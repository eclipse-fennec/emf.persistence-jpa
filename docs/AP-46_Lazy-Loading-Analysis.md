# AP-46 — Echtes Lazy-Loading für Non-Containment-Referenzen

## Ziel

Bei `resource.load()` soll pro geladene Entity nur **ein** SELECT auf die Eigentümer-Tabelle laufen. Die Ziel-Entities der Non-Containment-Referenzen werden **nicht** mitgeladen. Beim ersten `eGet` auf die Referenz löst EMF-Proxy-Resolution einen DB-Call genau für diese eine Zielentity aus.

Abgrenzung zu AP-45: AP-45 ist eine Proxy-Fassade auf eager geladenen Daten (gleiche DB-Last wie vorher, aber EMF-Semantik korrekt). AP-46 reduziert die DB-Last wirklich.

## 1. EclipseLink-Internals, die wir nutzen können

### 1.1 `QueryBasedValueHolder` — das Lazy-Holder-Primitiv

`/opt/git/eclipselink-4/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/internal/indirection/QueryBasedValueHolder.java`

Wichtige Punkte:

- **Konstruktor** (L64): `QueryBasedValueHolder(ReadQuery query, Object sourceObject, AbstractRecord row, AbstractSession session)` — speichert Query, Source-Row, Session; führt **nichts** aus.
- **`instantiate()`** (L129): ruft erst auf `.getValue()` die eigentliche Query. Bis dahin `isInstantiated()==false`.
- **`row` Feld** (L52 in `DatabaseValueHolder`) ist `protected AbstractRecord row` — Zugriff via public `getRow()` (L91 in `DatabaseValueHolder`). Die FK-Spaltenwerte liegen in dieser Row schon drin, bevor `instantiate()` je läuft.

### 1.2 FK aus der Row lesen — ohne DB-Call

`/opt/git/eclipselink-4/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/mappings/OneToOneMapping.java` L752:

```java
public Object extractPrimaryKeysForReferenceObjectFromRow(AbstractRecord row) {
    List<DatabaseField> primaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
    Object[] result = new Object[primaryKeyFields.size()];
    for (int index = 0; index < primaryKeyFields.size(); index++) {
        DatabaseField targetKeyField = primaryKeyFields.get(index);
        DatabaseField sourceKeyField = getTargetToSourceKeyFields().get(targetKeyField);
        if (sourceKeyField == null) return null;
        result[index] = row.get(sourceKeyField);
        if (getReferenceDescriptor().getCachePolicy().getCacheKeyType() == CacheKeyType.ID_VALUE) {
            return result[index];
        }
    }
    return new CacheId(result);
}
```

Das ist die Goldgrube: **public Methode, die aus der Source-Row die FK-Werte extrahiert und auf den Ziel-PK abbildet** — ohne jeden DB-Zugriff. `ManyToOneMapping` erbt diese Logik. Für `OneToMany`/`ManyToMany` ist Lazy sowieso schon der Default (Collection-Indirection, separate Query pro Traversal).

### 1.3 `BasicIndirectionPolicy.valueFromQuery` — der richtige Hook

`/opt/git/eclipselink-4/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/internal/indirection/BasicIndirectionPolicy.java` L522:

```java
@Override
public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
    return new QueryBasedValueHolder<>(query, sourceObject, row, session);
}
```

Diese Methode wird von `ForeignReferenceMapping.valueFromRow` aufgerufen, wenn `usesIndirection()`. Der Rückgabewert (= ValueHolder) landet via `mapping.setAttributeValueInObject(target, vh)` im Attribut-Slot. Das ist genau der Punkt, an dem wir stattdessen direkt einen EMF-Proxy reinlegen können.

Sichtbarkeit: `public` — kein Interna-Bruch.

### 1.4 `IndirectionPolicy.buildIndirectObject(vh)` — nicht der richtige Hook

Wird erst aufgerufen, **nachdem** `vh.getValue()` bereits lief. Für echtes Lazy zu spät. Ignorieren wir.

### 1.5 `ForeignReferenceMapping.valueFromRow` — der Einstieg

`/opt/git/eclipselink-4/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/mappings/ForeignReferenceMapping.java` um L2225.

Pfad bei `isLazy=true`, `usesIndirection()==true`, ohne JoinFetch/BatchRead:

1. Baut `ReadQuery targetQuery = prepareNestedQuery(sourceQuery)` — Ziel-Query mit FK-Bindung als Parameter
2. Ruft `indirectionPolicy.valueFromQuery(targetQuery, row, sourceObject, executionSession)`
3. Das Ergebnis geht via `setAttributeValueInObject` ins Feld

Wenn wir in (2) statt eines ValueHolders einen EMF-Proxy zurückgeben, ist der Gewinn sichtbar.

## 2. Wo im Fennec-Code eingegriffen wird

### 2.1 `ReferenceConfigurator.setMappingDefaults` — Lazy scharf schalten

`/opt/git/emf.persistence-jpa/org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/dynamic/ReferenceConfigurator.java` L321:

```java
mapping.setIsLazy(false);     // aktuell
// ändern zu:
mapping.setIsLazy(!reference.isContainment());
```

Containment → eager (korrekt, EMF-Semantik erfordert das); Non-Containment → lazy.

### 2.2 `EBasicIndirectionPolicy.valueFromQuery` — Proxy statt ValueHolder erzeugen

`/opt/git/emf.persistence-jpa/org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/indirection/EBasicIndirectionPolicy.java` — neue Überschreibung:

```java
@Override
public Object valueFromQuery(ReadQuery query, AbstractRecord row, Object sourceObject, AbstractSession session) {
    if (!usesIndirection()) {
        return super.valueFromQuery(query, row, sourceObject, session);
    }
    // 1) FK aus der Source-Row extrahieren — kein DB-Call
    Object targetPk = extractTargetPkFromRow(row);
    if (targetPk == null) {
        return super.valueFromQuery(query, row, sourceObject, session);  // Fallback
    }
    // 2) EclipseLink-managed Dynamic-Instanz als Proxy-Hülle erzeugen
    EClass targetEClass = reference.getEReferenceType();
    ClassDescriptor targetDesc = session.getDescriptor(type.getJavaClass());  // besser: aus Context
    EObject proxy = (EObject) targetTypeBuilder.getType().getDescriptor()
            .getInstantiationPolicy().buildNewInstance();
    // 3) PK auf den Proxy setzen, damit EcoreUtil.getID(proxy) ohne Resolution geht
    EAttribute idAttr = targetEClass.getEIDAttribute();
    proxy.eSet(idAttr, targetPk);
    // 4) eProxyURI auf das Target-Resource zeigen
    URI proxyURI = context.getBaseURI()
            .appendSegment(targetEClass.getName())
            .appendFragment("//" + reference.getName() + "/" + idAttr.getName() + "/" + targetPk);
    ((InternalEObject) proxy).eSetProxyURI(proxyURI);
    return proxy;   // <- statt ValueHolder landet der Proxy direkt im Feld
}
```

Hilfsmethode: `extractTargetPkFromRow(row)` — Dispatch auf `OneToOneMapping.extractPrimaryKeysForReferenceObjectFromRow(row)` (Zugriff via `this.mapping` in `BasicIndirectionPolicy`). Für `ManyToOneMapping` gleiches API.

**Wichtig:** Dieser Pfad umgeht `valueFromObject`-basierte Operationen, weil der ValueHolder wegfällt. Das hat Folgen für den Write-Pfad — siehe 3.3.

### 2.3 `EReferenceAccessor.unwrapValueHolder` — bleibt unverändert, aber Inhalt anders

`/opt/git/emf.persistence-jpa/org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/mappings/EReferenceAccessor.java` L111:

Im AP-46-Design kommen Proxies direkt rein (nicht mehr ValueHolder), also wird `unwrapValueHolder` auf dem Read-Pfad gar nicht mehr getriggert (weil EclipseLink keinen VH mehr liefert). Auf Write-/Cascade-Pfaden bleibt die Methode wie sie ist; wichtig: der `isInstantiated`-Gate aus den Experimenten wird nicht mehr gebraucht.

### 2.4 `JPAResourceImpl.proxifyNonContainmentRefs` — wird entfernt

`/opt/git/emf.persistence-jpa/org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/resource/JPAResourceImpl.java` L118–194:

Die gesamte Post-Load-Proxifizierung aus AP-45 entfällt, weil die Proxies schon im Mapping-Layer entstehen. Das vereinfacht `doLoad()` wieder auf den Pre-AP-45-Stand.

Konsequenz: auch `em.find`-Pfade geben Proxies zurück — siehe 3.5 für die Implikationen.

## 3. Die harten Stellen

### 3.1 FK-Extraktion aus der Row — ✅ trivial

`OneToOneMapping.extractPrimaryKeysForReferenceObjectFromRow(row)` ist public. Zugriff aus `EBasicIndirectionPolicy` via `this.mapping` (Feld in `BasicIndirectionPolicy`). Cast auf `OneToOneMapping` oder auf das gemeinsame Interface `ObjectReferenceMapping`.

### 3.2 Richtige IndirectionPolicy-Methode — ✅ klar

`valueFromQuery` ist der Hook. `buildIndirectObject` ist zu spät. `getRealAttributeValueFromObject`/`setRealAttributeValueInObject` arbeiten auf dem bereits im Feld liegenden Wert — nach unserer Änderung liegt dort ein EObject-Proxy, also greifen die Default-Impls aus `BasicIndirectionPolicy` nicht mehr (sie erwarten ValueHolder). **Wir müssen sie überschreiben**, um Proxy-EObjects zu akzeptieren:

```java
@Override
public Object getRealAttributeValueFromObject(Object object, Object attribute) {
    return attribute;   // bei uns IST das Attribut schon das echte Objekt (Proxy)
}

@Override
public void setRealAttributeValueInObject(Object target, Object attributeValue) {
    mapping.setAttributeValueInObject(target, attributeValue);
}
```

Plus eventuell `objectIsInstantiated`, `usesTransparentIndirection` (bereits überschrieben mit `true`), `reset`, `cloneAttribute`.

### 3.3 Write-Pfad — 🟠 muss explizit getestet werden

Bei `em.persist(a)` mit `a.dNonContainment = realD` muss EclipseLink die FK schreiben. Die Flow:

1. `mapping.getAttributeValueFromObject(a)` → EMF eGet → liefert D (das vom Client gesetzte Objekt, kein Proxy)
2. EclipseLink ruft `extractPrimaryKeyFromObject(D, session)` → liest D's PK
3. FK wird in die A-Row geschrieben

Solange der Client einen **echten** D setzt, unproblematisch. Problem entsteht, wenn der Client einen **Proxy** aus einem vorherigen Load setzt (`a.dNonContainment = proxyD_von_anderem_load`): `extractPrimaryKeyFromObject(proxyD, session)` muss den PK vom Proxy lesen können. Weil wir den PK-Attribut-Wert auf den Proxy setzen (Schritt 3 in 2.2), funktioniert das.

Cascade-merge: `em.merge(detachedA)` mit `detachedA.dNonContainment = proxyD` — EclipseLink muss den Proxy als „known identity" erkennen. Kritisch: Der Proxy muss über `descriptor.getObjectBuilder().extractPrimaryKeyFromObject()` einen gültigen PK liefern. Weil der PK auf dem Proxy gesetzt ist → ✅.

Risiko: Cascade-Flags. Wenn cascadeMerge=true und der Ref-Wert ein Proxy ist, versucht EclipseLink den Proxy zu mergen, was eine Resolution erzwingen könnte. **Test nötig.**

### 3.4 Bidirectional Non-Containment — 🟠 EMF-Opposite-Propagation

Bei bidi Non-Containment (`A.x ↔ E.xBack`): Beim Laden von A bekommt `a.x` einen Proxy von E. EMF setzt nicht automatisch `E.xBack = A`, weil der Proxy noch nicht aufgelöst ist. Erst bei `a.eGet(x)` wird der Proxy resolved → E wird geladen → EMF propagiert das Opposite.

Tests müssen prüfen:
- Nach `aResource.load()`: `a.eGet(x, false).eIsProxy() == true`
- Nach `a.eGet(x)`: Resolution → `e.eGet(xBack) == a`
- Persist von vollständig gesetzten Bidi-Objekten funktioniert weiterhin

### 3.5 `em.find`-Pfad ohne ResourceSet — 🔴 Breaking Change

**Problem:** Ein Client, der `em.find(A.class, pk)` ruft und direkt `a.eGet(dNonContainment)` macht, sieht den Proxy nicht aufgelöst (kein ResourceSet zum Traversieren).

**Optionen:**

1. **Akzeptieren und dokumentieren.** Alle JPA-API-Konsumenten müssen eine Resource/ResourceSet vorhalten. → inkompatibler Bruch mit bisherigen Non-OSGi-Tests (`NonOsgiOneToOneTest` etc. nutzen alle `em.find`).
2. **Fallback in einem EObject-Adapter.** Resolving via EclipseLink-Session direkt (statt ResourceSet): bei `eResolveProxy` auf dem Eigentümer selbst die Session nehmen (`JpaHelper.getEntityManager(...)` o. ä.). Braucht aber Zugriff auf die Session — ggf. als ThreadLocal beim Load gesetzt.
3. **Nur Resource-Loads proxifizieren.** Signal vom Query-Kontext runter zur IndirectionPolicy. Technisch: `ObjectBuildingQuery` hat eine `properties`-Map; `JPAResourceImpl.doLoad` setzt eine Flag, die Policy liest sie. Query-Properties sind öffentlich. Machbar, aber nicht trivial.

**Empfehlung:** Option 3. Nur wenn die Query ein Resource-Load ist, wird der Proxy-Pfad genommen; sonst fällt die Policy auf die Eager-Auflösung (aktueller Stand) zurück. So bleibt `em.find` für alle bestehenden Tests transparent.

### 3.6 Interna-Risiko — 🟡 überschaubar

| Aufruf | Sichtbarkeit | Stabil? |
|--------|--------------|---------|
| `OneToOneMapping.extractPrimaryKeysForReferenceObjectFromRow(row)` | public | ja |
| `BasicIndirectionPolicy.valueFromQuery(...)` | public (@Override) | ja |
| `DatabaseValueHolder.getRow()` | public | ja |
| `Session.executeQuery(q, row)` | public | ja |
| `ObjectBuildingQuery.getProperties()` | public | ja |
| `session.getDescriptor(class)` | public | ja |

Keine package-private Interna nötig. Das senkt das Upgrade-Risiko bei EclipseLink-Updates deutlich.

## 4. Implementierungsschritte (in Reihenfolge)

### Schritt 1 — Signal vom Resource-Load zur Policy plumben
Neues `JPAResourceImpl.doLoad`: vor dem Query-Aufruf eine Property auf der Query (oder als EM-Property) setzen, z. B. `fennec.jpa.proxify-refs=true`. Policy liest diese und entscheidet, ob Proxy-Pfad genommen wird.

Risiko: niedrig.

### Schritt 2 — `ReferenceConfigurator.setMappingDefaults` auf containment-aware `isLazy`
Einzeiler. Risiko: niedrig.

### Schritt 3 — `EBasicIndirectionPolicy.valueFromQuery` überschreiben
Kernstück. Baut FK aus Row → erzeugt Proxy via `descriptor.getInstantiationPolicy().buildNewInstance()` → setzt PK-Attribut + `eSetProxyURI`.

Risiko: mittel. Muss sowohl für Shared-Cache als auch für UnitOfWork korrekt arbeiten. Der zurückgegebene Proxy wandert durch UoW-Cloning — dort ist `EReferenceAccessor.setAttributeValueInObject` der Guard. Weil kein ValueHolder mehr im Spiel ist, greift kein `unwrapValueHolder` — Proxy bleibt Proxy.

### Schritt 4 — `getRealAttributeValueFromObject` / `setRealAttributeValueInObject` überschreiben
Damit EclipseLink-interne Operationen, die normalerweise den ValueHolder unwrappen würden, mit dem nackten Proxy arbeiten können.

Risiko: mittel. Betrifft Merge-/Change-Tracking-Pfade.

### Schritt 5 — Cascade-/Merge-Tests grün halten
Alle bestehenden bidi-Tests, alle OneToOne-/ManyToOne-Persist-Tests laufen lassen. Erwartung: grün, weil der Proxy den PK trägt.

Risiko: mittel.

### Schritt 6 — `JPAResourceImpl.proxifyNonContainmentRefs` entfernen
Die Post-Load-Proxifizierung ist redundant. `doLoad` kehrt auf den einfachen Stand zurück.

Risiko: niedrig.

### Schritt 7 — Neue Tests
- Query-Zähler-Test: `aResource.load()` löst genau 1 SELECT aus; `a.eGet(ref)` löst genau 1 weiteren aus
- `NonOsgiCrossResourceRefTest.testJpaNonContainmentRefIsProxyUntilAccessed` bleibt grün
- Regression-Suite grün (alle OneToOne/ManyToOne/OneToMany-Tests)

## 5. Teststrategie

### Query-Zählung via `SessionEventListener`

```java
int[] queryCount = {0};
server.getEventManager().addListener(new SessionEventAdapter() {
    @Override public void postExecuteQuery(SessionEvent event) {
        if (event.getQuery() instanceof ReadObjectQuery
                || event.getQuery() instanceof ReadAllQuery) {
            queryCount[0]++;
        }
    }
});
emf.getCache().evictAll();
aResource.load(null);
assertEquals(1, queryCount[0]);  // nur A, kein D

EObject a = aResource.getContents().get(0);
a.eGet(dNonContainmentFeature);
assertEquals(2, queryCount[0]);  // jetzt D
```

Das ist der Beweis für echtes Lazy.

### Weitere Tests

- `em.find(A, id).eGet(ref)` — muss noch Real-Objekt liefern (Option 3 aus 3.5)
- Bidi-Proxy: `a.eGet(x).eGet(xBack) == a`
- Persist mit Proxy: `a.dNonContainment = proxyD; em.persist(a)` — FK korrekt geschrieben
- Merge mit Proxy: `em.merge(detachedA)` funktioniert
- ManyToOne analog
- OneToMany: Non-Containment Collection bleibt lazy (EclipseLink-Default)

### JDBC-Spy alternativ

`eclipselink.logging.level=FINE` oder `net.sf.log4jdbc` einhängen; Build-Output nach `SELECT` greppen.

## 6. Abschätzung

- **Aufwand:** L (Large). Etwa 3–5 Tage fokussierte Arbeit inkl. Test-Debugging.
- **Risiko:** mittel. Kein Eingriff in package-private Interna, aber Cascade-/Merge-Pfade sind historisch tückisch.
- **Fallback-Pfad:** Bei Regressions feature-flag beibehalten (Property pro PU: `fennec.jpa.lazy-non-containment=true|false`), Default `false` bis Stabilität erreicht.

## 7. Designentscheidungen (getroffen)

1. **`em.find`-Verhalten:** Option A — Standard-EMF-Kontrakt. Ohne `ResourceSet` keine Resolution, Proxy bleibt unaufgelöst. ~30 bestehende Non-OSGi-Tests müssen auf Resource-Load umgeschrieben werden. Positionierung: „EMF-Resource, backed by JPA", nicht „JPA mit EMF-Beiwerk".

2. **Cascade-Merge:** Sichtweise 1 (Status Quo) — Non-Containment bleibt reine Assoziation. Änderungen am Referenz-Ziel müssen vom Client explizit persistiert werden. Kein Code-Change an `ReferenceConfigurator.setCascade`.

3. **Proxy-Inhalt:** Minimal — nur ID-Attribut + `eProxyURI`, korrekte EClass kommt automatisch via `descriptor.getInstantiationPolicy().buildNewInstance()`. Keine Attribut-Kopie, weil echtes Lazy bedeutet: wir haben das Ziel noch nie angefasst. Vor Resolution liefert `proxy.eGet(anyAttr)` `null` — Standard-EMF-Proxy-Verhalten.

4. **Scope:** Nur OneToOne und ManyToOne (singuläre Refs). OneToMany-/ManyToMany-Collections bleiben auf EclipseLinks `IndirectList` — Lazy pro Collection, voll-geladene Elemente nach Resolution. Element-Level-Proxies für Collections sind als AP-47 ausgelagert.

# AP-19: Change-Tracking-Brücke EMF↔JPA — Konzept & Evaluierung

> Evaluierung: Soll ein automatisches Change-Tracking via EContentAdapter implementiert werden?
> Stand: 2026-04-15 | Ergebnis: Bewusste Limitation — explizites `merge()` beibehalten

---

## 1. Ausgangslage

**Finding F8-01:** EMF-Änderungen an bereits persistierten Objekten werden nicht automatisch an JPA propagiert — `merge()` muss explizit aufgerufen werden.

### Aktueller Persistierungsfluss

```
EObject laden        →  em.find() / resource.load()
                         EObject ist detached (kein EntityManager-Kontext)

EObject ändern       →  eo.eSet(feature, value)
                         Änderung nur im Speicher, JPA weiß nichts davon

Änderung speichern   →  resource.save() / em.merge(eo)
                         JPAResourceImpl.doSave() iteriert getContents(), ruft merge() auf
                         EObjectBuilder.mergeIntoObject() → ECopier kopiert EMF-Attribute
                         EclipseLink vergleicht mit buildBackupClone() → SQL UPDATE
```

### Warum ist das so?

EMF und JPA haben unterschiedliche Change-Detection-Paradigmen:

| Aspekt | EMF | JPA/EclipseLink |
|--------|-----|-----------------|
| **Notification** | Push: `EContentAdapter`, `eAdapters()` — jeder `eSet()` feuert `Notification` | Pull: UnitOfWork vergleicht Snapshot mit aktuellem Zustand bei `flush()` |
| **Lifecycle** | Objekte leben beliebig lang, kein Session-Konzept | Managed → Detached → Merged — Lifecycle gebunden an EntityManager |
| **Granularität** | Pro Feature (Attribut/Referenz) | Pro Entity (Dirty-Flag auf Objekt-Ebene) |

Das Projekt nutzt `DynamicEObjectImpl` (EMF-basiert), nicht `DynamicEntityImpl` (EclipseLink-basiert). Damit greifen EclipseLink's interne Change-Tracking-Mechanismen nicht — der `EObjectBuilder` + `ECopier` übernimmt diese Aufgabe manuell beim `merge()`.

---

## 2. Evaluierte Optionen

### Option A: EContentAdapter als automatische Brücke

```java
public class JPADirtyTracker extends EContentAdapter {
    private final Set<EObject> dirtyObjects = new LinkedHashSet<>();

    @Override
    public void notifyChanged(Notification notification) {
        super.notifyChanged(notification);
        if (notification.getEventType() != Notification.REMOVING_ADAPTER) {
            dirtyObjects.add((EObject) notification.getNotifier());
        }
    }

    public Set<EObject> getDirtyObjects() { return dirtyObjects; }
    public void clear() { dirtyObjects.clear(); }
}
```

**Registrierung** bei `resource.load()`:
```java
JPADirtyTracker tracker = new JPADirtyTracker();
resource.eAdapters().add(tracker);  // EContentAdapter propagiert auf Containment-Baum
```

**Flush** bei `resource.save()`:
```java
for (EObject dirty : tracker.getDirtyObjects()) {
    em.merge(dirty);
}
tracker.clear();
```

| Pro | Contra |
|-----|--------|
| Erkennt alle Änderungen automatisch | Notification-Overhead bei jedem `eSet()` |
| Containment-Baum automatisch abgedeckt | Non-Containment-Referenzen nicht erfasst |
| Batch-fähig (Dirty-Set sammeln, bei save() flushen) | Detached-Objekte haben keinen EntityManager-Kontext |
| Opt-in möglich | Adapter-Lifecycle muss verwaltet werden (load/unload) |
| | Unterscheidung "echte Änderung" vs. "gleichwertiger Wert" nötig |

**Performance-Abschätzung:** Bei 10.000 geladenen Objekten mit je 5 Attributen erzeugt jeder Lade-Vorgang ~50.000 Notifications allein durch das initiale `eSet()`. Der Tracker müsste zwischen Laden und Benutzer-Änderungen unterscheiden.

### Option B: Explizites merge() beibehalten + Dokumentation

Aktuelle Funktionsweise als bewusste Design-Entscheidung dokumentieren.

| Pro | Contra |
|-----|--------|
| Kein Overhead | Entwickler muss `save()` explizit aufrufen |
| Volle Kontrolle über Zeitpunkt der Persistierung | Vergessenes `save()` = Datenverlust |
| Batch-Updates sind günstig (ein `save()` für viele Änderungen) | Kein automatischer Schutz |
| Standard-JPA-Pattern (vertraut für Java-Entwickler) | |
| Kein komplexer Adapter-Lifecycle | |

### Option C: Dirty-Flag auf Resource-Ebene

`JPAResourceImpl` setzt ein Dirty-Flag wenn `getContents()` modifiziert wird. `isModified()` signalisiert dem Aufrufer, dass ein `save()` nötig ist.

| Pro | Contra |
|-----|--------|
| Minimal-invasiv | Erkennt nur Content-Änderungen, nicht Attribut-Änderungen |
| Kein Notification-Overhead | Löst F8-01 nicht vollständig |
| Leicht testbar | |

---

## 3. Bewertung

### Warum Option A (EContentAdapter) nicht empfohlen wird

1. **Lade-Notification-Problem:** `EContentAdapter` feuert auch beim initialen Laden. Jeder `eSet()` durch EclipseLink während des Ladens erzeugt Notifications. Der Tracker müsste wissen, ob gerade geladen wird oder der Benutzer ändert — das erfordert einen Zustandsautomaten (`loading`/`active`/`flushing`).

2. **Detached-Entity-Problem:** Geladene EObjects sind detached. Der Tracker müsste wissen, welcher EntityManager zuständig ist. Bei mehreren PersistenceUnits oder parallelen EntityManagern wird das komplex.

3. **Non-Containment-Lücke:** `EContentAdapter` propagiert nur über Containment-Referenzen. Änderungen an Non-Containment-referenzierten Objekten werden nicht erkannt, es sei denn sie sind separat in `resource.getContents()`.

4. **Paradigmen-Mismatch:** JPA-Entwickler erwarten explizite Kontrolle (`persist`, `merge`, `flush`). Automatisches Tracking widerspricht diesem Muster und kann zu überraschenden Datenbank-Schreibvorgängen führen.

5. **Test-Komplexität:** Automatisches Tracking ist schwer testbar — "wann wurde was gemerged?" ist implizit statt explizit.

### Warum Option B (Explizites merge()) empfohlen wird

Die aktuelle Architektur ist **korrekt und vollständig**:

1. `JPAResourceImpl.doSave()` iteriert `getContents()` und ruft `merge()` auf
2. `EObjectBuilder.mergeIntoObject()` nutzt `ECopier` für typsichere Attribut-Kopie
3. EclipseLink vergleicht mit `buildBackupClone()` und erzeugt nur SQL für tatsächliche Änderungen
4. Cascade-Mappings propagieren Änderungen an Containment-Kinder automatisch

**Der einzige "Nachteil" ist, dass `save()` explizit aufgerufen werden muss — das ist aber Standard-JPA-Verhalten und kein Bug.**

---

## 4. Empfehlung

**Design-Entscheidung:** Explizites `merge()` via `resource.save()` beibehalten. Kein automatisches Change-Tracking via `EContentAdapter`.

**Begründung:** Der Overhead und die Komplexität eines automatischen Change-Trackings stehen in keinem Verhältnis zum Nutzen. Das explizite Pattern ist Standard-JPA, performant, und vollständig getestet.

**Maßnahmen:**

1. **Dokumentation:** README und Development-Guide um "Persistence Lifecycle" Abschnitt ergänzen:
   - Lade → Ändere → Speichere Muster explizit beschreiben
   - Hinweis dass `resource.save()` notwendig ist
   - Beispielcode für typische CRUD-Operationen

2. **Bewusste Limitation dokumentieren:** F8-01 ist kein Bug sondern eine Design-Entscheidung zugunsten von Einfachheit und Performance.

3. **Zukünftige Option:** Sollte ein konkreter Anwendungsfall für automatisches Tracking entstehen (z.B. UI-Binding mit Live-Persistierung), kann ein optionaler `JPADirtyTracker` als separate Komponente implementiert werden — ohne die Kern-Architektur zu ändern.

---

## 5. Abgrenzung

| Was ist abgedeckt | Was ist nicht abgedeckt |
|-------------------|------------------------|
| `resource.save()` persistiert alle Änderungen an Contents | Änderungen ohne `save()` gehen verloren (by design) |
| Cascade propagiert an Containment-Kinder | Änderungen an Objekten außerhalb von `getContents()` |
| `em.merge(eo)` funktioniert auch direkt | Automatische Erkennung geänderter Objekte |
| ECopier überbrückt EMF↔JPA Typ-Unterschiede | Live-Tracking für UI-Binding |

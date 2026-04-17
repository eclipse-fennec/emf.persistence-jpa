# AP-25: `@MappedSuperclass` Support — Design-Entscheidung

> Evaluierung: Soll `@MappedSuperclass` in der EMF→JPA-Mapping-Pipeline implementiert werden?
> Stand: 2026-04-17 | Ergebnis: **Bewusste Limitation — nicht implementiert. `TABLE_PER_CLASS` ist der empfohlene Weg für geteilte Attribute.**

---

## 1. Ausgangslage

**Finding F1-05 (Review 2026-04-14):** Das EORM-Metamodell enthält eine `MappedSuperclass`-EClass mit allen relevanten Attributen (`access`, `metadataComplete`, `attributes`, Listener-Callbacks, `class`-Referenz auf EClass) sowie eine Sammlung `EntityMappings.getMappedSuperclass()`. Die Processor-Pipeline und der `EDynamicTypeGenerator` ignorieren dieses Metamodell aktuell komplett — alle EClasses werden unconditional als `Entity` mit eigener Tabelle gemappt. Es gibt keinen Opt-in-Mechanismus (weder per EAnnotation noch per Konvention), um eine EClass als MappedSuperclass zu kennzeichnen.

### Kontext: Was ist `@MappedSuperclass` in JPA?

Ein MappedSuperclass ist ein **abstrakter Basistyp mit persistentem Mapping-Inhalt**, der aber **selbst keine Entity ist**:

- **Keine Tabelle** — die Basisklasse hat keine eigene Relation in der DB.
- **Attribute werden in jede Subklasse-Tabelle kopiert** — jede Subklasse-Tabelle enthält physisch die ID, `createdAt`, etc.
- **Keine Polymorphie auf der Basis** — `em.find(AuditableBase.class, id)` ist ungültig; es gibt kein polymorphes JPQL `SELECT a FROM AuditableBase a`.
- **Kein gemeinsamer Query-Namensraum** — jede Subklasse ist eine eigenständige Entity.

Typischer Use-Case: Gemeinsame Audit-Felder (`id`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`, `version`) in vielen unrelated Entities wiederverwenden, ohne sie per Copy-Paste in jede EClass zu schreiben.

---

## 2. Abgrenzung zu Inheritance-Strategien (AP-12)

`AP-12` hat die drei JPA-Inheritance-Strategien für **echte Polymorphie** implementiert — `SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`. Der Unterschied zu MappedSuperclass ist struktureller Natur:

| Aspekt | Inheritance (AP-12) | MappedSuperclass (AP-25) |
|--------|---------------------|--------------------------|
| Basis-EClass ist Entity | ✅ Ja (abstract möglich) | ❌ Nein |
| Basis hat Tabelle | SINGLE_TABLE/JOINED: Ja — TABLE_PER_CLASS: Nein | Nein |
| Polymorphes Query über Basis | ✅ Ja | ❌ Nein |
| Discriminator | SINGLE_TABLE/JOINED: Ja — TABLE_PER_CLASS: Nein | Nein (Subklassen teilen kein gemeinsames Typsystem) |
| Subklassen-FKs/Relationen | Können die Basis als Ziel haben | Können die Basis **nicht** als Ziel haben |
| Eigene Tabelle pro Subklasse | TABLE_PER_CLASS: Ja | ✅ Ja |

**`TABLE_PER_CLASS` und MappedSuperclass sehen auf DB-Ebene sehr ähnlich aus** — in beiden Fällen hat jede Subklasse ihre eigene Tabelle mit den vererbten Spalten. Der Unterschied liegt in der Polymorphie: `TABLE_PER_CLASS` erlaubt polymorphe Queries (via `UNION`), MappedSuperclass nicht.

---

## 3. Evaluierte Optionen

### Option A — Vollständige Implementierung

**Scope:**

1. **Detection** per EAnnotation analog zu AP-12/AP-13:
   `source="http://eclipse.org/fennec/jpa-persistence/1.0.0"`, key `mappedSuperclass`, value `"true"`.
2. **Neuer `MappedSuperclassProcessor`** — kopiert das Attribute-/ID-/Relations-Processing vom `EntityProcessor`, erzeugt aber eine `MappedSuperclass` statt einer `Entity` (kein Table-Mapping, keine Inheritance-Konfiguration).
3. **`MappingProcessor`-Routing** — annotierte EClasses gehen in `mappings.getMappedSuperclass()`, alle anderen wie gehabt in `mappings.getEntity()`.
4. **`EDynamicTypeGenerator`** — iteriert zusätzlich über `mapping.getMappedSuperclass()`. **Kniffligster Teil:** EclipseLink's Dynamic API hat keinen stabilen, getesteten Pfad für MappedSuperclass-Descriptoren mit `DynamicEObjectImpl`. Realistische Optionen:
   - (a) Alle MappedSuperclass-Attribute in jeden Subklassen-Descriptor replizieren (Simulation).
   - (b) Einen EclipseLink-Descriptor mit `descriptorIsMappedSuperclass()` erstellen und hoffen, dass die Dynamic-Layer-Integration trägt — hoher Risikofaktor, keine Referenzimplementierung im EclipseLink-4-Quellcode gefunden.

**Aufwand:** realistisch L (Detection + Processor + Generator + Tests + Fehlerbehandlung bei (b)).
**Risiko:** EclipseLink Dynamic + MappedSuperclass ist ein selten beschrittener Pfad — potenzielle Runtime-Überraschungen in Bereichen, die wir nicht testen (Entity-Listener, Lifecycle-Callbacks auf Basis, Derived-Attribute).
**Nutzen:** Ergonomischer Ausdruck von geteilten Audit-/Basis-Feldern **ohne** polymorphe Queries.

### Option B — Bewusste Limitation + `TABLE_PER_CLASS` empfehlen

**Scope:** Keine Implementierung. Wer gemeinsame Attribute wiederverwenden will, wählt zwischen:

1. **`TABLE_PER_CLASS` (AP-12)** — wenn polymorphe Queries erwünscht oder nicht stören.
2. **Attribute in jeder EClass duplizieren** — wenn keine gemeinsame Basis-EClass gewünscht ist.
3. **EClass-Inheritance in EMF mit `abstract="true"` auf der Basis + AP-12 Annotation** — die Basis wird heute als abstrakte Entity mit Table gemappt; bei `TABLE_PER_CLASS` entsteht sie faktisch als Nicht-Instanziierbare, ihre Attribute werden in die Subklassen-Tabellen kopiert.

**Aufwand:** Nur Dokumentation.
**Risiko:** Kein Framework-Risiko.
**Nutzen:** Klare API-Oberfläche, kein ungetesteter Dynamic-API-Pfad.

### Option C — Hybrid (verworfen)

EAnnotation detektieren und in Metamodell einspeisen, aber die Runtime-Seite offen lassen. Ergebnis: Subklassen hätten Attribute im Metamodell, aber ohne physisches Mapping — Silent-Failure-Risiko. **Nicht empfohlen.**

---

## 4. Entscheidung

**Option B — bewusste Limitation.**

### Begründung

1. **Kein konkreter Use-Case im Projekt.** Weder die OSM-, Citizen-, GLT- noch die Test-Modelle nutzen MappedSuperclass-Semantik. Eine Implementierung würde Code schreiben, der aktuell keinen Adressaten hat.
2. **`TABLE_PER_CLASS` deckt den 80 %-Fall ab.** Wer abstrakte Attribute teilen will, kann das schon heute über `AP-12`-Annotation ausdrücken. Die fehlende Polymorphie von MappedSuperclass ist selten der eigentliche Gewinn — in EMF-Modellen will man polymorphe Queries oft **sogar explizit**.
3. **EclipseLink Dynamic + MappedSuperclass ist ein dünnes Eis.** Unser Projekt fährt `DynamicEObjectImpl` (EMF-basiert, keine CGLIB-Weaving), und die Kombination mit MappedSuperclass-Descriptoren hat in EclipseLink keine etablierte Referenzimplementierung. Ein "funktioniert-in-der-Demo, crasht-bei-Edge-Case"-Zustand wäre schlimmer als keine Implementierung.
4. **Kleiner Return-on-Effort.** Option A ist L, Option B ist 1 Dokument. Andere offene APs (AP-29 Unit-Tests, AP-30 Cross-Resource, AP-38 Doku) haben klaren Nutzen.

### Explizite Konsequenz für Nutzer

Wer gemeinsame Attribute in mehreren Entities braucht:

```ecore
<!-- geht nicht: -->
<eClassifiers xsi:type="ecore:EClass" name="AuditableBase" abstract="true">
  <eAnnotations source="http://eclipse.org/fennec/jpa-persistence/1.0.0">
    <details key="mappedSuperclass" value="true"/>  <!-- wird ignoriert -->
  </eAnnotations>
  ...
</eClassifiers>
```

**Empfohlene Alternative 1 — `TABLE_PER_CLASS`** (polymorphe Queries möglich):

```ecore
<eClassifiers xsi:type="ecore:EClass" name="AuditableBase" abstract="true">
  <eAnnotations source="http://eclipse.org/fennec/jpa-persistence/1.0.0">
    <details key="inheritance" value="TABLE_PER_CLASS"/>
  </eAnnotations>
  <eStructuralFeatures xsi:type="ecore:EAttribute" name="id" iD="true" .../>
  <eStructuralFeatures xsi:type="ecore:EAttribute" name="createdAt" .../>
</eClassifiers>
<eClassifiers xsi:type="ecore:EClass" name="Customer" eSuperTypes="#//AuditableBase">
  <eStructuralFeatures xsi:type="ecore:EAttribute" name="name" .../>
</eClassifiers>
```

**Empfohlene Alternative 2 — Attribute duplizieren** (strikt keine Polymorphie):

Jede Entity definiert ihre Audit-Felder selbst. Ekliger im Ecore, aber in manchen EMF-Tooling-Chains (z.B. Xcore-Generatoren) einfach über Templates abbildbar.

---

## 5. Revisitierung

Diese Entscheidung sollte überprüft werden, wenn:

- Ein konkretes Anwendermodell MappedSuperclass-Semantik benötigt (gemeinsame Attribute **ohne** polymorphe Queries), und `TABLE_PER_CLASS` aus Performance- oder Schema-Gründen ausscheidet.
- EclipseLink einen klar dokumentierten Pfad für `descriptorIsMappedSuperclass()` mit Dynamic-API bereitstellt (z.B. in EclipseLink 5.x).
- Der Non-OSGi-Testpfad (AP-41) stabil genug ist, um ein solches Feature mit dedizierten Integrationstests abzusichern.

Bis dahin bleibt das `MappedSuperclass`-Element im EORM-Metamodell ungenutzt. **Es wird nicht aus dem Metamodell entfernt**, damit externe Produzenten von `.eorm`-Dateien kompatibel bleiben und eine spätere Reaktivierung ohne Metamodell-Migration möglich ist.

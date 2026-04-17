# AP-25: `@MappedSuperclass` Support — Design-Entscheidung

> Evaluierung: Soll `@MappedSuperclass` in der EMF→JPA-Mapping-Pipeline implementiert werden?
> Ursprünglicher Stand: 2026-04-17 | Ursprüngliches Ergebnis: Bewusste Limitation, nicht implementieren.
> **Revidiert: 2026-04-17 — siehe [§ 6 Revision](#6-revision-nach-ap-43).** Ergebnis nach Revision: Implementieren, zusammen mit dem TABLE_PER_CLASS-Fix in AP-44 (gemeinsame Mapping-Maschinerie).
> **Umgesetzt: 2026-04-17 (AP-44) — siehe [§ 7 Umsetzungsergebnis](#7-umsetzungsergebnis-ap-44).**

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

## 5. Revisitierung (ursprünglich)

Diese Entscheidung sollte überprüft werden, wenn:

- Ein konkretes Anwendermodell MappedSuperclass-Semantik benötigt (gemeinsame Attribute **ohne** polymorphe Queries), und `TABLE_PER_CLASS` aus Performance- oder Schema-Gründen ausscheidet.
- EclipseLink einen klar dokumentierten Pfad für `descriptorIsMappedSuperclass()` mit Dynamic-API bereitstellt (z.B. in EclipseLink 5.x).
- Der Non-OSGi-Testpfad (AP-41) stabil genug ist, um ein solches Feature mit dedizierten Integrationstests abzusichern.

---

## 6. Revision nach AP-43

**Anlass:** AP-43 hat Roundtrip-Tests für alle drei Inheritance-Strategien eingeführt und dabei aufgedeckt, dass **TABLE_PER_CLASS in der bisherigen Implementierung überhaupt nicht funktionierte** (Subklassen landeten auf der Root-Tabelle, falsche `InheritancePolicy` statt `TablePerClassPolicy`, fehlende Child-ID-Mappings). Ein substantieller Fix ist notwendig und wird als AP-44 verfolgt.

### Was sich am Kalkül geändert hat

Beim Aufsetzen von AP-44 wurde klar: die Mapping-Maschinerie, die TABLE_PER_CLASS zum Laufen bringt, ist **zu 80 % dieselbe**, die MappedSuperclass bräuchte:

| Gemeinsamer Baustein | TABLE_PER_CLASS | MappedSuperclass |
|---|---|---|
| Detection per EAnnotation auf dem Root | ✅ | ✅ |
| `getEffectiveAttributes()` muss geerbte Attribute zu Children spiegeln | ✅ | ✅ |
| `EntityProcessor.createIds()` erzeugt Child-spezifische IDs | ✅ | ✅ |
| `DirectToFieldMapping` muss auf die Child-Tabelle explizit qualifiziert werden | ✅ | ✅ |
| Abstrakter/kein Root-Entity-Descriptor | ✅ (abstract descriptor) | ✅ (gar kein Descriptor) |

Unterschiede bleiben nur auf der **Modellierungsseite**:

- **TPC** hat weiterhin einen abstrakten EclipseLink-Descriptor plus `TablePerClassPolicy`, damit polymorphe Queries via `UNION` möglich sind.
- **MappedSuperclass** hat **keinen** EclipseLink-Descriptor für die Basis; der Root landet in `EntityMappings.getMappedSuperclass()` statt `getEntity()`, und es gibt bewusst keine polymorphen Queries.

### Warum das Argument „dünnes Eis" damit entfällt

Die ursprüngliche Sorge war, dass EclipseLink's Dynamic-API-Pfad für MappedSuperclass-Descriptoren untestetes Terrain sei. **Diese Sorge war nicht zielführend**: für MappedSuperclass braucht man in unserer Architektur gar keinen EclipseLink-Descriptor für die Basis — wir backen die geerbten Attribute beim Children-Processing einfach mit ein. Genau das leistet die Maschinerie aus AP-44 ohnehin.

### Neue Entscheidung

**MappedSuperclass wird implementiert, gemeinsam mit dem TABLE_PER_CLASS-Fix in AP-44.**

Sub-Scope:

1. **Phase 1 (gemeinsam):** Field-Qualifikation auf Child-Tabellen, Child-ID-Mapping, Attribute-Spiegelung — die Bausteine, die AP-44 sowieso bauen muss.
2. **Phase 2a (TPC):** abstrakter Descriptor + `TablePerClassPolicy` + Child-Descriptoren verlinken + `setAbstract(true)` auf Root.
3. **Phase 2b (MappedSuperclass):** Detection per EAnnotation `mappedSuperclass="true"`, Root landet in `mappings.getMappedSuperclass()` und bekommt **keinen** EclipseLink-Descriptor, Root-`EClass` wird nicht in `EntityMapper.createMappings(...)` als Entity registriert.
4. **Tests:** Die Basisklasse `NonOsgiInheritanceRoundtripBase` lässt sich für MappedSuperclass weitgehend wiederverwenden; die beiden polymorphen Tests (Find-via-Basis, `SELECT FROM Basis`) müssten für MappedSuperclass entweder übersprungen oder so umgeschrieben werden, dass sie explizit belegen: polymorphe Queries **funktionieren nicht** (dokumentierte Limitation).

### EAnnotation-Konvention

- Source: `http://eclipse.org/fennec/jpa-persistence/1.0.0` (wie AP-12, AP-13)
- Key: `mappedSuperclass`
- Value: `"true"`
- Geht nur auf abstrakte EClasses; bei Konflikt mit `inheritance`-Annotation gewinnt `mappedSuperclass` (beide gleichzeitig ist ein Modellfehler und wird geloggt).

### Empfehlung für Nutzer während der Übergangszeit

Solange AP-44 nicht fertig ist, funktioniert weder MappedSuperclass noch TABLE_PER_CLASS. Wer heute gemeinsame Attribute braucht:

- **SINGLE_TABLE** oder **JOINED** nutzen (beide vollständig getestet nach AP-43).
- Alternativ Attribute in jeder EClass duplizieren.

### Was bleibt erhalten

- Das EORM-Metamodell bleibt unverändert — `MappedSuperclass`-EClass und `EntityMappings.getMappedSuperclass()` sind von Anfang an vorhanden und werden jetzt endlich genutzt.
- Die Abgrenzungstabelle zwischen Inheritance-Strategien und MappedSuperclass (§ 2) bleibt semantisch korrekt und für die Doku relevant.

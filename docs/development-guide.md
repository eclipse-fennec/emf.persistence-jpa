# Eclipse Fennec Persistence JPA — Entwicklungsdokumentation

## 1. Überblick

Eclipse Fennec Persistence JPA ist ein OSGi-basiertes Persistence-Framework, das EMF (Eclipse Modeling Framework) mit Jakarta Persistence (JPA) über EclipseLink verbindet. Es löst das Problem der **Impedance Mismatch zwischen EMF-Modellen und relationalen Datenbanken**: Domänenmodelle werden in ECore definiert, und das Framework erzeugt daraus automatisch JPA-Mappings — ohne manuellen Boilerplate-Code.

Zusätzlich unterstützt es den umgekehrten Weg: Aus einem bestehenden Datenbankschema kann ein ECore-Modell per JDBC-Metadata reverse-engineered werden.

## 2. Modularchitektur

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Anwendung / OSGi Container                    │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────┐    ┌──────────────────────────────┐    │
│  │   persistence.ecore     │    │   persistence.test           │    │
│  │   (Reverse-Engineering) │    │   (OSGi Integrationstests)   │    │
│  └─────────────────────────┘    └──────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │              persistence.eclipselink                         │    │
│  │   (EclipseLink JPA Provider, Descriptors, Dynamic Types)    │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                              │                                       │
│  ┌──────────────────────────┴──────────────────────────────────┐    │
│  │              persistence.orm                                 │    │
│  │   (ECore → ORM Mapping, Processor-Pipeline, MappingContext) │    │
│  └──────────────────────────┬──────────────────────────────────┘    │
│                              │                                       │
│  ┌──────────────────────────┴──────────────────────────────────┐    │
│  │              persistence (Core)                              │    │
│  │   (API, Engine, Converter, Helper, Processor-Interfaces)    │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌──────────────────────────┐    ┌──────────────────────────────┐    │
│  │   jpa.library.workspace  │    │   jpa.bom                    │    │
│  │   (bnd Library Template) │    │   (Bill of Materials)        │    │
│  └──────────────────────────┘    └──────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
```

### Modulbeschreibungen

| Modul | Verantwortung |
|-------|---------------|
| `org.eclipse.fennec.persistence` | Core API: `PersistenceEngine`, `ConverterService`, Type-Converter (UUID, BigDecimal, Instant, etc.), `EMFHelper`, Processor-Interfaces |
| `org.eclipse.fennec.persistence.orm` | ECore-basiertes ORM-Metadatenmodell (`eorm.ecore`, `epersistence.ecore`) + Processor-Pipeline für EClass→Entity Transformation |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink JPA Provider: `EPersistenceContext`, Descriptors (`EClassDescriptor`), Dynamic Type Builder, Object Builder, OSGi Classloader |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` — Reverse-Engineering von ECore-Modellen aus Datenbank-Schemata via JDBC |
| `org.eclipse.fennec.persistence.test` | OSGi-Integrationstests mit JUnit 5, H2-Datenbank, Test-ECore/EORM-Fixtures |
| `org.eclipse.fennec.persistence.jpa.library.workspace` | bnd Library Template für externe Workspace-Nutzung |
| `org.eclipse.fennec.persistence.jpa.bom` | Bill of Materials — re-exportiert den Workspace-Library-Buildpath |

## 3. Kern-Datenfluss: ECore → Datenbank

```
ECore-Modell (.ecore)
       │
       ▼
 ┌─────────────┐
 │ EntityMapper │  Einstiegspunkt: erzeugt EntityMappings aus EPackage/EClass-Listen
 └──────┬──────┘
        │
        ▼
 ┌──────────────────┐
 │ MappingProcessor  │  5-stufige Processor-Pipeline (orchestriert)
 └──────┬───────────┘
        │
        ├── Stage 1: EntityProcessor        EClass → Entity (Tabelle, Name, ID)
        ├── Stage 2: BasicProcessor          EAttribute → @Basic / @ElementCollection
        ├── Stage 3: Containment Refs        EReference (containment) → @OneToOne / @OneToMany
        ├── Stage 4: Non-Containment Refs    EReference (non-containment) → @O2O / @O2M / @M2O / @M2M
        └── Stage 5: Bidirektionale Opposites  mappedBy-Auflösung
        │
        ▼
 ┌──────────────────┐
 │  MappingContext   │  Zentrales Registry: EClass→Entity, EReference→Mapping, Opposites
 └──────┬───────────┘
        │
        ▼
 ┌──────────────────────┐
 │ EDynamicTypeBuilder   │  EORM Entity → EclipseLink Descriptors (2-Pass: erst Entities, dann Referenzen)
 └──────┬───────────────┘
        │
        ▼
 ┌──────────────────────┐
 │ EntityManagerFactory  │  JPA-Zugriff auf die Datenbank
 └──────────────────────┘
```

## 4. Processor-Pipeline im Detail

### 4.1 Architektur-Pattern

Das Framework nutzt ein **Processor-Pattern** mit generischen Interfaces:

- `Processor<T, S>` — Transformation von Source `S` zu Target `T`
- `ProcessorFactory<T, S, P>` — Factory zur Erzeugung von Processoren
- `ProcessorImpl<C, T, S>` — Abstrakte Basisimplementierung mit Template-Method-Pattern:
  `canProcess()` → `internalProcess()` → `doProcess()` → `doPostProcess()` → `registerMapping()`

### 4.2 Die 5 Pipeline-Stufen

**Stage 1 — Entity Mapping:**
- Filtert abstrakte EClasses heraus
- Erzeugt pro konkreter EClass einen `EntityProcessor`
- Setzt Tabellenname, Entity-Name, Access Type (FIELD)
- ID-Generierung über `CompositeIdProcessor` (4 Strategien: SINGLE_ID, EMBEDDED_ID, ID_CLASS, SYNTHETIC_ID)

**Stage 2 — Attribute Mapping:**
- Iteriert über alle EAttributes (exkl. transiente)
- Einwertige → `BasicProcessor` (@Basic, Column-Metadata)
- Mehrwertige → `ElementCollectionProcessor` (@ElementCollection mit CollectionTable)
- Temporal-Type-Erkennung (Date→TIMESTAMP, LocalDate→DATE, LocalTime→TIME)

**Stage 3 — Containment-Referenzen:**
- Einwertige Containment → `OneToOneProcessor` (JoinColumn, FK in Container-Tabelle)
- Mehrwertige Containment → `OneToManyProcessor` (JoinColumn, FK in Kind-Tabelle)
- Cascade: CASCADE_ALL, orphanRemoval bei Containment

**Stage 4 — Non-Containment-Referenzen:**
- Einwertige ohne Opposite → `OneToOneProcessor` (JoinTable)
- Mehrwertige ohne Opposite → `OneToManyProcessor` (JoinTable)
- Einwertige mit Opposite-many → `ManyToOneProcessor` (JoinColumn)
- Mehrwertige mit Opposite → `ManyToManyProcessor` (JoinTable)
- Cascade: nur DETACH + REFRESH (bewusst kein REMOVE)

**Stage 5 — Bidirektionale Opposites:**
- Iteriert über gesammelte Opposites aus dem MappingContext
- Setzt `mappedBy` auf der Inverse-Seite
- Markiert Inverse-Processor als Delegate

### 4.3 Containment vs. Non-Containment

| Aspekt | Containment | Non-Containment |
|--------|-------------|-----------------|
| JPA-Strategie | JoinColumn (FK in Kind-Tabelle) | JoinTable (separate Assoziationstabelle) |
| Cascade | ALL | DETACH + REFRESH |
| orphanRemoval | ja (bei OneToOne) | nein |
| Kardinalitäten | nur O2O, O2M | O2O, O2M, M2O, M2M |

## 5. EclipseLink-Integration

### 5.1 EDynamicTypeBuilder

Herzstück der EclipseLink-Anbindung (916 Zeilen). Transformiert EORM Entity-Metadaten in EclipseLink-Mappings:

- **2-Pass Referenz-Konfiguration**: Erst alle Entities, dann Referenzen, dann mappedBy-Referenzen (bidirektionale Mappings erfordern, dass die Owning-Seite zuerst existiert)
- **Converter Auto-Detection**: Erkennt automatisch passende TypeConverter für nicht-Standard-Typen (UUID, BigDecimal, Instant etc.)
- **ID-Konfiguration**: Single + Composite Keys mit UUID/Sequence-Generierung

### 5.2 Angepasste EclipseLink-Policies

| Klasse | Zweck |
|--------|-------|
| `EClassDescriptor` | Erweitert RelationalDescriptor; setzt EMF-spezifische Policies |
| `EInstantiationPolicy` | Erzeugt EObject-Instanzen via `EcoreUtil.create()` statt Reflection |
| `EObjectBuilder` | Backup/Clone/Merge für EObjects über ECopier |
| `ECopyPolicy` | EclipseLink CopyPolicy mit EMF-Semantik |
| `EFeatureAccessor` | Zugriff auf EAttribute/EReference über EMF-API statt Java-Reflection |
| `EReferenceAccessor` | Spezialisiert für bidirektionale EMF-Referenzen |
| `EBasicIndirectionPolicy` | EMF-Proxies für Lazy Loading bei Non-Containment-Referenzen |

### 5.3 ECopier

Performanter EMF-Kopierer (789 Zeilen) mit drei Szenarien:

- `forEclipseLinkInternal()` — Internes Cloning im UnitOfWork
- `forJPARepository()` — Kopie für Repository-Pattern (save/merge)
- `forGeneralEMF()` — Allgemeine EMF-Kopie

Optimierungen: 3-stufige Object-Creation-Priority (Context → Factory → Standard EMF), O(N) Shared-Mapping statt O(N²) für Cross-Referenzen.

## 6. Type-Converter-System

### 6.1 Architektur

```
ConverterService (Interface)
  └── DefaultConverterService (abstrakt, LinkedList<TypeConverter>)
        └── ConverterWhiteboard (@Component, dynamische OSGi-Registrierung)

Prioritätsreihenfolge:
  1. ComprehensiveTypeConverter (delegiert intern an 11 Spezial-Converter)
  2. ArrayConverter (Wrapper-Arrays: Double[], Integer[], String[])
  3. DefaultConverter (Fallback via EcoreUtil)
  4. XMLGregorianCalendarConverter
  5. BigDecimalConverter, BigIntegerConverter
  6. NonContainmentConverter (EObject Cross-References)
  7. Dynamisch registrierte Converter (via OSGi Whiteboard)
```

### 6.2 Unterstützte Typen

| Kategorie | Typen |
|-----------|-------|
| Zeit (java.time) | LocalDate, LocalDateTime, LocalTime, Instant, ZonedDateTime, Duration |
| Zahlen | BigDecimal, BigInteger, UUID |
| Arrays (Wrapper) | Double[], Integer[], Long[], Float[], String[] |
| Arrays (Primitiv) | int[], double[], float[], long[], boolean[], byte[], char[], short[] |
| Referenzen | EObject Cross-References (URI-basiert) |
| Legacy | XMLGregorianCalendar |
| Fallback | Alles via EcoreUtil.createFromString()/convertToString() |

## 7. Reverse-Engineering: Datenbank → ECore

Der `DatabaseEcoreParser` erzeugt ECore-Modelle aus bestehenden Datenbank-Schemata:

1. Verbindung über injizierte `DataSource` öffnen
2. Schema-Metadaten via `DatabaseService.createMetaInfo()` lesen
3. **Pass 1**: Tabellen → EClasses
4. **Pass 2**: Spalten → EAttributes/EReferences
   - Foreign Keys → `EReference` zur Ziel-EClass
   - Primary Keys → `EAttribute` mit `ID=true`
   - Reguläre Spalten → `EAttribute` mit JDBC→ECore Type-Mapping

JDBC-Type-Mapping: `int4`→EIntegerObject, `float8`→EBigDecimal, `varchar`/`text`→EString, `bool`→EBoolean.

## 8. OSGi-Integration

### 8.1 Declarative Services

Alle Komponenten nutzen OSGi Declarative Services:

- `@Component` für Service-Registrierung
- `@Reference` für Service-Injection (MANDATORY, OPTIONAL, MULTIPLE)
- `@ObjectClassDefinition` für Konfiguration
- Factory Configurations für mehrere Persistence-Units

### 8.2 Service-Registrierungsablauf

```
1. EPackage als OSGi Service registrieren (EPackageConfigurator)
2. EORM-Mapping laden (EORMMappingProvider / EORMMappingServiceComponent)
3. PersistenceUnit konfigurieren (Factory Configuration)
4. EntityManagerFactory wird automatisch erzeugt (EntityMappingPersistenceUnitConfigurator)
5. DataSource + EntityManager stehen als Services bereit
```

## 9. Teststrategie

### 9.1 Aktuelle Testabdeckung

| Modul | Unit-Tests | Integrationstests |
|-------|-----------|-------------------|
| `persistence` (Core) | Converter-Tests (2 Klassen, ~720 Zeilen) | — |
| `persistence.orm` | Helper-Tests (3 Klassen), **Processor-Pipeline (5 Klassen, 85 Tests)**, **Composite-ID (2 Klassen, 39 Tests)** | — |
| `persistence.eclipselink` | ECopier-Tests (5 Klassen) | — |
| `persistence.ecore` | Parser Unit-Tests (1 Klasse, Mockito) | — |
| `persistence.test` | — | OSGi-Tests (~30 Klassen, H2) |

**Gut abgedeckt:**
- Type Converter (alle Typen, Null-Handling, Edge Cases)
- **Processor-Pipeline** (alle Processoren: Basic, ElementCollection, O2O, O2M, M2O, M2M)
- **MappingContext** (Entity-Map, createMapping, registerRefMapping, Opposite-Tracking, MappedBy-Berechnung, calculateMappingType mit Opposites)
- **MappingProcessor 5-Stage-Pipeline** (Attribute, IDs, synthetische IDs, Containment/Non-Containment Refs, Bidirektional, Transient-Filterung, Strict Mode)
- **CascadeType-Berechnung** (containment=ALL, non-containment=DETACH+REFRESH)
- **CompositeIdAnalyzer** (alle 4 ID-Strategien: Single, Embedded, IdClass, Synthetic + IdClass-Erkennung via Annotation + Komponentenanalyse)
- **CompositeIdProcessor** (ID-Erzeugung mit Generierungsstrategien: UUID für String, Sequence für Numeric, Strict Mode)
- One-to-Many Beziehungen (5 Varianten: uni/bidi × containment/non-containment)
- One-to-One Beziehungen (mit und ohne Cache)
- End-to-End Type Converter Pipeline
- MappingHelper Utility-Methoden
- ECopier (Core, Context, Szenarien, Edge Cases)

### 9.2 Bekannte Testlücken

**P0 — Kritisch** (Kernlogik ohne Tests):

| Bereich | Beschreibung |
|---------|-------------|
| ~~Processor-Pipeline~~ | ~~keine Unit-Tests~~ — **erledigt (AP1)** |
| ~~MappingContext~~ | ~~keine Tests~~ — **erledigt (AP1)** |
| ~~CompositeIdAnalyzer~~ | ~~4 ID-Strategien keine getestet~~ — **erledigt (AP2)** |

**P1 — Hoch** (wichtig für Stabilität):

| Bereich | Beschreibung |
|---------|-------------|
| EDynamicTypeBuilder | Converter Auto-Detection, Relationship-Konfiguration, ID-Sequencing |
| EMFHelper | Map-Merging (Prioritäts-Semantik), EClass-Caching, Thread-Safety |
| Many-to-Many Integration | Tests existieren, sind aber `@Disabled` |
| ConverterService | Prioritätsreihenfolge, Thread-Safety, dynamische Registrierung |

**P2 — Mittel**:

| Bereich | Beschreibung |
|---------|-------------|
| DatabaseEcoreParser | `parse()` Integrationstest mit echtem H2 |
| EReferenceAccessor | Bidirektionale Synchronisation |
| EBasicIndirectionPolicy | EMF Proxy/Lazy Loading |
| BasicPersistenceEngine | Options-Normalisierung |

### 9.4 Gefundene und behobene Bugs

**Bug: `MappingContext.calculateMappingType()` — falsche MappingType-Berechnung für non-containment bidirektionale Referenzen**

- **Symptom:** Für non-containment bidirektionale single-to-single Referenzen wurde `MANY_TO_MANY` statt `ONE_TO_ONE` berechnet. Analog für single-to-many (`MANY_TO_MANY` statt `MANY_TO_ONE`).
- **Ursache:** Der `else`-Branch in `calculateMappingType()` nutzte `getMappingType()` für den Opposite. Diese Methode liefert aber für alle non-containment Referenzen pauschal `MANY_TO_MANY` (Zeile 189), unabhängig von der Kardinalität. Dadurch konnte die Verfeinerung in den Switch-Cases nie auf `ONE_TO_ONE` oder `MANY_TO_ONE` kommen.
- **Fix:** Direkte Kardinalitätsprüfung über `oppositeRef.isMany()` im non-containment `else`-Block, statt Umweg über `getMappingType()`. `getMappingType()` und `createMapping()` bleiben unverändert.
- **Auswirkung:** Die MappedBy-Berechnung für non-containment bidirektionale Referenzen ist jetzt korrekt. Die Processor-Pipeline war nicht betroffen, da diese eigene `canProcess()`-Guards nutzt.

### 9.3 Test-Infrastruktur

- **Framework**: JUnit 5.14 + Mockito 5.21 + AssertJ 3.27.7
- **OSGi-Tests**: `org.osgi.test.junit5` mit `@InjectService`, `@WithFactoryConfiguration`
- **Datenbank**: H2 (In-Memory und dateibasiert)
- **Test-Modelle**: `model.ecore` mit 25+ EClasses, zusätzlich citizen, glt, meter Domänen
- **Performance-Tests**: `@Tag("perf")`, separater Gradle-Task `perfTest`

## 10. Build & Konfiguration

```bash
./gradlew build                    # Alle Module bauen (ohne @Tag("perf") Tests)
./gradlew perfTest                 # Performance-Tests (@Tag("perf"))
./gradlew :MODULE_NAME:test        # Tests für einzelnes Modul
./gradlew codeCoverageReport       # JaCoCo Coverage (XML + HTML)
```

**Java-Version**: 17 (Source und Target, konfiguriert in `cnf/build.bnd`)

**Konfigurationsdateien**:
- `cnf/build.bnd` — Workspace-Level bnd-Konfiguration
- `cnf/ext/libraries.bnd` — bnd Library-Referenzen (Fennec, JaCoCo, OSGi Test, EMF)
- `cnf/ext/libraries.maven` — Maven Repository-Konfiguration
- `cnf/central.mvn` — Maven Central Dependency Index

## 11. Arbeitspakete zur Härtung

### Übersicht

| AP | Thema | Aufwand | Priorität | Teststrategie | Status |
|----|-------|---------|-----------|---------------|--------|
| 1 | Processor-Pipeline Unit-Tests | L | Kritisch | Unit | **Erledigt** — 85 Tests, 1 Bugfix |
| 2 | Composite-ID Tests & Stabilisierung | M | Kritisch | Unit + Integration | **Erledigt** — 39 Tests (Unit), printf noch offen |
| 3 | Many-to-Many aktivieren | M | Hoch | Integration | Offen |
| 4 | EDynamicTypeBuilder Tests | L | Hoch | Unit | Offen |
| 5 | Logging statt println/printStackTrace | S | Hoch | Refactoring | **Erledigt** |
| 6 | EMFHelper & ConverterService Tests | M | Mittel | Unit | Offen |
| 7 | Reserved-Words-Liste erweitern | S | Mittel | Unit | Offen |
| 8 | DatabaseEcoreParser Integrationstest | M | Mittel | Unit (H2 embedded) | Offen |
| 9 | Error-Handling-Strategie | M | Mittel | Unit | Offen |
| 10 | Accessor & Indirection Tests | M | Niedrig | Unit | Offen |

**Aufwand:** S = Small (1–2 Tage), M = Medium (3–5 Tage), L = Large (1–2 Wochen)

### Unit-Testbarkeit

Der Großteil der Arbeitspakete kann mit **Standard-JUnit + Mockito** getestet werden, ohne OSGi-Container. Der Schlüssel: ECore/EORM-Modellobjekte lassen sich komplett programmatisch erzeugen (via `EcoreFactory`, `DynamicEObjectImpl`, EORM-Factories). Die Processoren sind im Kern pure Transformationsfunktionen — ECore rein, EORM raus.

| AP | Unit-testbar? | Begründung |
|----|--------------|------------|
| 1 — Processor-Pipeline | **Ja, vollständig** | Processoren nehmen EClass/EAttribute/EReference und erzeugen EORM-Objekte. MappingContext ist ein POJO. |
| 2 — Composite-ID | **Ja, größtenteils** | `CompositeIdAnalyzer` und `CompositeIdProcessor` arbeiten auf EClass-Metadaten. Nur der Persist-Roundtrip braucht Integration. |
| 3 — Many-to-Many | **Nein** | JPA-Roundtrip mit echter Datenbank. Aber die Mapping-Erzeugung (`ManyToManyProcessor`) ist unit-testbar. |
| 4 — EDynamicTypeBuilder | **Ja, größtenteils** | Nimmt EORM Entity-Objekte, erzeugt EclipseLink-Descriptors. Nur `OSGiDynamicClassloader` braucht OSGi. |
| 5 — Logging | *Nicht testbar* | Refactoring-Aufgabe. |
| 6 — EMFHelper & ConverterService | **Ja, vollständig** | `mergeMaps()` ist pure Utility. `DefaultConverterService` ist eine LinkedList mit Lookup. |
| 7 — Reserved Words | **Ja, vollständig** | `MappingHelper.checkReservedName()` ist eine statische Methode. |
| 8 — DatabaseEcoreParser | **Ja, mit H2 embedded** | H2 als Embedded-DB direkt im Unit-Test, DataSource/DatabaseService mocken oder H2 instanziieren. |
| 9 — Error-Handling | **Ja, vollständig** | Fehlerfälle durch ungültige Inputs an Processoren/Context/Helper provozierbar. |
| 10 — Accessor & Indirection | **Ja, größtenteils** | `EFeatureAccessor`/`EReferenceAccessor` arbeiten auf EObject + EStructuralFeature. Minimales EclipseLink-Setup, kein OSGi. |

**Fazit:** 8 von 10 APs sind schwerpunktmäßig als Unit-Tests umsetzbar. OSGi-Integrationstests werden nur für AP3 (M2M JPA-Roundtrip) zwingend benötigt.

### AP1 — Processor-Pipeline Unit-Tests ✅
**Aufwand: L | Priorität: Kritisch | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** 85 neue Unit-Tests in 5 Testklassen, 1 Bug in `MappingContext.calculateMappingType()` gefunden und behoben.

**Testklassen:**

| Klasse | Tests | Abdeckung |
|--------|-------|-----------|
| `MappingContextTest` | 14 | Entity-Map, createMapping, registerRefMapping, Opposite-Tracking, MappedBy-Berechnung, MappingType mit Opposites |
| `BasicProcessorTest` | 11 | String/Int-Attribute, Optional/Required, alle Temporal-Types, Enums, Entity-Registrierung |
| `ElementCollectionProcessorTest` | 5 | Multi-valued Attribute, CollectionTable/Value-Column-Naming, JoinColumn, Entity-Registrierung |
| `ReferenceProcessorTest` | 18 | O2O, O2M, M2O, M2M (containment/non-containment, JoinColumn/JoinTable, canProcess-Guards, CascadeType) |
| `MappingProcessorPipelineTest` | 15 | Volle 5-Stage-Pipeline: Attribute, IDs, ElementCollections, Containment/Non-Containment Refs, Bidirektional, Transient-Filterung, Package-Metadata, Strict Mode |

**Bugfix:** `MappingContext.calculateMappingType()` — non-containment bidirektionale Referenzen lieferten immer `MANY_TO_MANY` statt korrekter Kardinalitäts-Bestimmung. Fix: direkte `isMany()`-Prüfung statt Umweg über `getMappingType()`. Siehe Abschnitt 9.4.

### AP2 — Composite-ID Tests & Stabilisierung ✅
**Aufwand: M | Priorität: Kritisch | Teststrategie: Unit + Integration | Status: Erledigt (Unit-Tests)**

**Umgesetzt:** 39 neue Unit-Tests in 2 Testklassen. Alle 4 ID-Strategien getestet.

**Testklassen:**

| Klasse | Tests | Abdeckung |
|--------|-------|-----------|
| `CompositeIdAnalyzerTest` | 21 | `analyzeIdStructure()` für alle 4 Strategien, `findDirectIdAttributes()`, `findIdClassReference()` (Reference- und Class-Level Annotation), `analyzeIdClassComponents()` (mit eKeys und ohne), IdConfiguration-Utilities |
| `CompositeIdProcessorTest` | 18 | Single ID (String/UUID, Long/Sequence, Strict), Embedded ID (2 und 3 Attribute, Mixed-Generierung, Strict), IdClass (Flattening, Column-Naming, Strict), Synthetic ID (Naming, Column-Setup, Sequence, Strict), EntityProcessor-Integration |

**Offen:** Debug-`printf` durch Logging ersetzen (wird in AP5 adressiert). `@Disabled` Integrationstests in `persistence.test` stehen noch aus.

### AP3 — Many-to-Many aktivieren
**Aufwand: M | Priorität: Hoch | Teststrategie: Integration**

`EPersistenceManyToManyTest` ist `@Disabled`. Das Feature existiert im Code, wird aber nicht validiert.

**Scope:**
- Root Cause für `@Disabled` analysieren und beheben
- Testfälle erweitern: uni/bidi, mit/ohne Opposite
- JoinTable-Konfiguration End-to-End verifizieren
- NoCacheVariante testen

**Teststrategie:** OSGi-Integrationstest mit H2 — hier geht es um den JPA-Roundtrip, nicht nur um Mapping-Erzeugung. Die Mapping-Erzeugung im `ManyToManyProcessor` kann vorab als Unit-Test in AP1 abgesichert werden.

**Ergebnis:** M2M-Beziehungen abgesichert.

### AP4 — EDynamicTypeBuilder Tests
**Aufwand: L | Priorität: Hoch | Teststrategie: Unit**

916 Zeilen ohne Tests — das Herzstück der EclipseLink-Anbindung.

**Scope:**
- Converter Auto-Detection (Typ-Erkennung, Fallback-Verhalten)
- ID-Sequencing (Single + Composite)
- Relationship-Mapping (OneToMany mit JoinColumns vs. JoinTable)
- Indirection Policy Auswahl (Containment vs. Non-Containment)
- Cascade-Type-Mapping

**Teststrategie:** EORM Entity-Objekte via Factory programmatisch erzeugen. `EDynamicTypeBuilder` instanziieren, `configureEntity()` aufrufen, resultierende EclipseLink-Descriptors und Mappings verifizieren. EclipseLink-Klassen (RelationalDescriptor, DirectToFieldMapping etc.) sind plain Java und ohne Container instanziierbar.

**Ergebnis:** Änderungen am Type Builder brechen nicht unbemerkt.

### AP5 — Logging statt println/printStackTrace ✅
**Aufwand: S | Priorität: Hoch | Teststrategie: Refactoring | Status: Erledigt**

**Umgesetzt:** Alle `System.out.printf`, `System.err.println` und `printStackTrace()` durch `java.util.logging` (JUL) ersetzt.

| Modul | Klasse | Änderung |
|-------|--------|----------|
| `persistence` | `ProcessorImpl` | 1× `printStackTrace()` → `Logger.log(Level.SEVERE, ...)` |
| `persistence.orm` | `CompositeIdProcessor` | 5× `System.out.printf` → `Logger.fine()` / `Logger.finer()` |
| `persistence.orm` | `MappingProcessor` | 1× `System.err.println` → `Logger.log(Level.SEVERE, ...)` |
| `persistence.orm` | `NamedBaseProcessor` | 2× `printStackTrace()` → `Logger.log(Level.SEVERE, msg, e)` |
| `persistence.eclipselink` | `EDynamicTypeBuilder` | 23× `System.out/err` + 2× `printStackTrace()` → `Logger.fine()`/`Logger.finer()`/`Logger.log(Level.WARNING/SEVERE, ...)` |
| `persistence.eclipselink` | `EntityMappingPersistenceUnitConfigurator` | 1× `Throwable::printStackTrace` + 1× `printStackTrace()` → `Logger.log(Level.SEVERE, ...)` |
| `persistence.eclipselink` | `PersistenceUnitConfigurator` | 1× `Throwable::printStackTrace` + 1× `printStackTrace()` → `Logger.log(Level.SEVERE, ...)` |
| `persistence.eclipselink` | `EDynamicPersistenceUnitInfo` | 1× `printStackTrace()` → `Logger.log(Level.WARNING, ...)` |

**Pattern:**
```java
private static final Logger LOG = Logger.getLogger(MyClass.class.getName());
```

**Ergebnis:** Kein `System.out/err` oder `printStackTrace()` mehr im Produkt-Code (exkl. `src-gen/`). Test-Code enthält weiterhin `System.out.println` — dort üblich und akzeptabel.

### AP6 — EMFHelper & ConverterService Tests
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit**

Utility-Code der überall genutzt wird, aber ungetestet ist.

**Scope:**
- `EMFHelper.mergeMaps()`: Prioritäts-Semantik (Options überschreiben Defaults)
- `EMFHelper.getEClass()`: Cache Hit/Miss, Thread-Safety
- `DefaultConverterService`: Converter-Reihenfolge, `getConverter(String name)`
- `ConverterWhiteboard`: Dynamische Add/Remove-Registrierung
- Thread-Safety unter Concurrent Access

**Teststrategie:** Pure Unit-Tests. `mergeMaps()` mit verschiedenen Map-Konstellationen. `getEClass()` mit programmatisch erzeugtem ResourceSet + EPackage. ConverterService mit definierten Converter-Listen, Reihenfolge über `isConverterForType()` verifizieren.

**Ergebnis:** Vertrauen in die Grundlagen.

### AP7 — Reserved-Words-Liste erweitern
**Aufwand: S | Priorität: Mittel | Teststrategie: Unit**

Nur 4 Einträge (`value`, `bigint`, `key`, `year`) — viele SQL-Keywords fehlen.

**Scope:**
- Standard SQL-Keywords ergänzen (`select`, `from`, `where`, `order`, `group`, `index`, `table`, `column`, `user`, `role`, `session`, `type` etc.)
- Datenbank-spezifische Keywords (H2, PostgreSQL, MySQL) berücksichtigen
- Tests für Escaping-Logik (`checkReservedName()`)

**Teststrategie:** Parametrisierte Unit-Tests mit allen Keywords → verifizieren, dass Escaping korrekt greift.

**Ergebnis:** Keine Laufzeitfehler bei Tabellen/Spalten die SQL-Keywords heißen.

### AP8 — DatabaseEcoreParser Integrationstest
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit (H2 embedded)**

`parse()` wird nur mit Mocks getestet — der eigentliche JDBC-Pfad ist ungetestet.

**Scope:**
- H2-Datenbank mit definiertem Schema aufsetzen (DDL im Test)
- `parse()` gegen echte JDBC-Metadaten testen
- Foreign Key → EReference Auflösung verifizieren
- JDBC-Type-Mapping erweitern (aktuell PostgreSQL-spezifisch: `int4`, `float8`, `bpchar`)
- Kommentierte Integrationstests in `persistence.test` aktivieren

**Teststrategie:** H2 als Embedded-DB im normalen JUnit-Test (kein OSGi nötig). `DataSource` via `JdbcDataSource` direkt erzeugen, `DatabaseService` mocken oder ebenfalls direkt instanziieren.

**Ergebnis:** Reverse-Engineering funktioniert nachweislich.

### AP9 — Error-Handling-Strategie
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit**

Aktuell inkonsistent: teilweise silent failure, teilweise Exception, teilweise IllegalStateException.

**Scope:**
- Definieren: Wann wird eine Exception propagiert, wann geloggt?
- `NamedBaseProcessor`: Catch-all durch gezielte Exception-Behandlung ersetzen
- `ProcessorImpl`: Failure-Semantik klären (processed=false reicht nicht)
- `MappingProcessor.createProcessor()`: Reflection-Fehler besser kommunizieren
- EMFHelper Cache: Verhalten bei fehlgeschlagener EClass-Auflösung definieren

**Teststrategie:** Unit-Tests mit ungültigen Inputs (null EClass, fehlende ID-Attribute, nicht auflösbare Referenzen) → erwartetes Exception-Verhalten verifizieren.

**Ergebnis:** Vorhersagbares Fehlerverhalten, keine versteckten Fehler.

### AP10 — Accessor & Indirection Tests
**Aufwand: M | Priorität: Niedrig | Teststrategie: Unit**

Spezial-Code für die EclipseLink-EMF-Brücke, aktuell ungetestet.

**Scope:**
- `EFeatureAccessor`: TypeConverter-Integration, Get/Set über EMF-API
- `EReferenceAccessor`: Bidirektionale Synchronisation, Parent-Child-Konsistenz
- `EBasicIndirectionPolicy`: Proxy-Erzeugung, Lazy Loading
- `ECopyPolicy`: Clone-Verhalten im UnitOfWork

**Teststrategie:** EObject-Instanzen programmatisch erzeugen. Accessor mit gemocktem TypeConverter testen. Indirection Policy mit minimalem EclipseLink-Descriptor-Setup (ohne Server/OSGi).

**Ergebnis:** EclipseLink-EMF-Brücke abgesichert.

## 12. Bekannte Code-Quality-Issues

| Issue | Ort | Empfehlung |
|-------|-----|------------|
| ~~`System.out.printf` Debug-Ausgaben~~ | ~~alle Module~~ | ~~erledigt (AP5)~~ |
| ~~`printStackTrace()` bei Exception-Handling~~ | ~~alle Module~~ | ~~erledigt (AP5)~~ |
| `EDynamicTypeContext extends ConcurrentHashMap` | `persistence.eclipselink` | Composition statt Inheritance |
| Reserved-Words-Liste unvollständig | `MappingHelper` (nur 4 Einträge) | SQL-Keyword-Liste erweitern |
| EMFHelper-Cache ohne Invalidierung | `persistence` Core | Cache-Eviction-Strategie einführen |
| `Options`-Klasse mit 60+ Konstanten | `persistence` Core | In thematische Interfaces aufteilen |

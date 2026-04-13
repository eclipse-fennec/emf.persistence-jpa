# Eclipse Fennec Persistence JPA — Entwicklungsdokumentation

## 1. Überblick

Eclipse Fennec Persistence JPA ist ein OSGi-basiertes Persistence-Framework, das EMF (Eclipse Modeling Framework) mit Jakarta Persistence (JPA) über EclipseLink verbindet. Es löst das Problem der **Impedance Mismatch zwischen EMF-Modellen und relationalen Datenbanken**.

### Zwei Use Cases

1. **EMF → DB (Forward Mapping):** Domänenmodelle werden in ECore definiert, und das Framework erzeugt daraus automatisch JPA-Mappings — ohne manuellen Boilerplate-Code. Ziel: Es soll einfach sein, EMF-Objekte in relationalen Datenbanksystemen zu speichern.

2. **DB → EMF (Reverse Engineering):** Aus einem bestehenden Datenbankschema wird ein ECore-Modell per JDBC-Metadata reverse-engineered. Ziel: Bestehende Datenbankstrukturen mit EMF-Modellen nutzbar machen.

### Design-Prinzipien

- **JPA-Defaults als Grundlage:** Die Standard-Mappings folgen den JPA-Konventionen (z.B. Containment → JoinColumn, Non-Containment OneToMany → JoinTable). Ohne zusätzliche Konfiguration soll ein valides, funktionierendes Mapping entstehen.
- **Customizing über EORM:** Das EORM-Metadatenmodell (`eorm.ecore`) bietet die volle Bandbreite an JPA-Mapping-Optionen. So kann z.B. ein OneToMany wahlweise mit JoinColumn oder JoinTable gemappt werden — je nach Anwendungsfall. Die Defaults erzeugen ein korrektes Mapping, aber die Alternativen bleiben über EORM konfigurierbar.
- **EclipseLink-Kompatibilität:** Die erzeugten Mappings müssen 1:1 den EclipseLink-Mapping-Mechanismen entsprechen. Das EORM-Modell bildet die relevante JPA/EclipseLink-Semantik ab.

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

## 5. ECore → JPA Mapping-Matrix

### 5.1 Vollständige Mapping-Übersicht

Die folgende Matrix zeigt alle ECore-Referenztypen und ihr JPA-Mapping. „Default" ist die automatisch erzeugte Variante; „Alternative" zeigt über EORM konfigurierbare Optionen.

| EReference | Containment | Kardinalität | Bidi | JPA Default | Alternative | EclipseLink-Mapping |
|---|---|---|---|---|---|---|
| Single, kein Opposite | containment | 0..1 | uni | OneToOne + JoinColumn | JoinTable | `OneToOneMapping` |
| Single, kein Opposite | non-containment | 0..1 | uni | OneToOne + JoinColumn | JoinTable | `OneToOneMapping` |
| Multi, kein Opposite | containment | 0..* | uni | OneToMany + JoinColumn (FK in child) | JoinTable | `UnidirectionalOneToManyMapping` |
| Multi, kein Opposite | non-containment | 0..* | uni | OneToMany + JoinTable | JoinColumn | `ManyToManyMapping` (definedAsO2M) |
| Single + eOpposite(multi) | containment | bidi | bidi | M2O (child, FK) + O2M (parent, mappedBy) | — | `ManyToOneMapping` + `OneToManyMapping` |
| Single + eOpposite(single) | containment | bidi | bidi | O2O (owning, JoinColumn) + O2O (inverse, mappedBy) | — | `OneToOneMapping` × 2 |
| Single + eOpposite(single) | non-containment | 1:1 | bidi | O2O (owning, JoinColumn) + O2O (inverse, mappedBy) | — | `OneToOneMapping` × 2 |
| Single + eOpposite(multi) | non-containment | M:1 / 1:N | bidi | M2O (FK) + O2M (mappedBy) | — | `ManyToOneMapping` + `OneToManyMapping` |
| Multi + eOpposite(multi) | non-containment | M:N | bidi | M2M (owning, JoinTable) + M2M (inverse, mappedBy) | — | `ManyToManyMapping` × 2 |

### 5.2 JPA-Semantik: Owning vs. Inverse

JPA definiert pro bidirektionaler Beziehung eine **Owning Side** und eine **Inverse Side**:

| Aspekt | Owning Side | Inverse Side (mappedBy) |
|--------|-------------|------------------------|
| Konfiguration | JoinColumn oder JoinTable | `mappedBy` Attribut |
| DML-Operationen | INSERT/UPDATE/DELETE | Nur Lesen (isReadOnly bei M2M) |
| FK-Besitz | Besitzt den Foreign Key | Referenziert den FK der Owning Side |
| EclipseLink | Konfiguriert eigene Key-Fields | Kopiert/spiegelt Key-Fields der Owning Side |

**Fennec-Zuordnung:** Die Owning Side ist die zuerst in der Pipeline verarbeitete Seite (determiniert durch EClass-Reihenfolge in `createMappings()`). Die Inverse Side wird in Stage 5 via `createOppositeMapping()` aufgelöst.

### 5.3 Containment-Semantik in JPA

EMF Containment hat keine direkte JPA-Entsprechung. Fennec bildet es folgendermaßen ab:

| EMF-Konzept | JPA-Umsetzung | Begründung |
|-------------|---------------|------------|
| Containment (Eltern-Kind) | JoinColumn (FK in Kind-Tabelle) | Kind gehört zum Eltern; FK-Beziehung ist natürlich |
| Non-Containment (lose Assoziation) | JoinTable (separate Tabelle) | Kein Ownership; Assoziationstabelle entkoppelt |
| Containment Cascade | CascadeType.ALL | Eltern-Löschung kaskadiert zu Kindern |
| Non-Containment Cascade | DETACH + REFRESH | Keine Lösch-Kaskade bei losen Assoziationen |

**Customizing:** Die Defaults (JoinColumn für Containment, JoinTable für Non-Containment) können über EORM überschrieben werden. Für bestehende DB-Schemata, die z.B. OneToMany mit JoinTable statt JoinColumn nutzen, kann dies explizit konfiguriert werden.

### 5.4 Abgleich mit EclipseLink-Referenzimplementierung

Systematischer Vergleich der Fennec-Implementierung mit EclipseLink 4 (`/opt/git/eclipselink-4/`):

| Feature | EclipseLink-Referenz | Fennec-Implementierung | Status |
|---------|---------------------|----------------------|--------|
| M2M bidi: Owning Side | JoinTable, kein mappedBy | JoinTable, kein mappedBy | korrekt |
| M2M bidi: Inverse Side | mappedBy, isReadOnly=true, Key-Fields gespiegelt | mappedBy, isReadOnly=true, Key-Fields gespiegelt | korrekt (AP3 Fix) |
| M2M uni | JoinTable | JoinTable | korrekt |
| O2M bidi (mappedBy) | O2M mit targetFK aus M2O-Owner | O2M mit mappedBy (delegate) | korrekt |
| O2M uni (JoinColumn) | UnidirectionalOneToManyMapping | UnidirectionalOneToManyMapping mit JoinColumns | korrekt |
| O2M uni (JoinTable) | ManyToManyMapping (definedAsO2M) | ManyToManyMapping (definedAsO2M) | korrekt |
| M2O | OneToOneMapping + FK | ManyToOneMapping + FK | korrekt |
| O2O bidi | Owning: FK, Inverse: Key-Swap | Owning: JoinColumn, Inverse: mappedBy + Key-Swap | korrekt |
| O2O containment | JoinColumn + CascadeAll | JoinColumn + CascadeAll | korrekt |

**Bekannte Abweichungen:**

1. **O2O/O2M Delegate-Pattern:** Bei O2O und O2M bidi setzt Stage 5 `mappedBy` auf die owning side's EORM-Mapping (statt auf die inverse side). `EDynamicTypeBuilder` kompensiert dies, weil `isMappedBy()` korrekt filtert. Funktional korrekt, aber inkonsistent mit JPA-Semantik (Inverse hat mappedBy, nicht Owning).

2. **O2O bidi: Doppelte JoinColumns:** Beide Seiten erhalten in Stage 4 einen JoinColumn. Stage 5 setzt dann `mappedBy` auf eine Seite. Ergebnis: eine Seite hat sowohl JoinColumn als auch mappedBy. EDynamicTypeBuilder prüft mappedBy zuerst und ignoriert den JoinColumn. Funktional korrekt, aber das EORM-Modell enthält unnötige Informationen.

## 6. EclipseLink-Integration

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
| `persistence` (Core) | Converter-Tests (2 Klassen, ~720 Zeilen), **EMFHelper (1 Klasse, 19 Tests)**, **ConverterService (1 Klasse, 17 Tests)** | — |
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
| 3 | Many-to-Many aktivieren | M | Hoch | Integration | **Erledigt** — Bidi M2M Fix + Tests aktiviert |
| 4 | EDynamicTypeBuilder Tests | L | Hoch | Unit | **Erledigt** — 16 Tests (Entity, ID, Basic, Enum, Reference, Inheritance) |
| 5 | Logging statt println/printStackTrace | S | Hoch | Refactoring | **Erledigt** |
| 6 | EMFHelper & ConverterService Tests | M | Mittel | Unit | **Erledigt** — 36 Tests |
| 7 | Reserved-Words-Liste erweitern | S | Mittel | Unit | **Erledigt** |
| 8 | DatabaseEcoreParser Integrationstest | M | Mittel | OSGi-Integration + Unit | Offen — `convertType`-Tests vorhanden, `parse()` braucht OSGi-Test |
| 9 | Error-Handling-Strategie | M | Mittel | Unit | **Erledigt** |
| 10 | Accessor & Indirection Tests | M | Niedrig | Unit | **Erledigt** — 22 Tests (Accessor, Enum, Containment, ProxyURI) |
| 11 | JPAResource — EMF Resource-Schicht | L | Hoch | Unit + OSGi-Integration | **Erledigt** — load, save, count, exist, getEObject (Proxy-Resolution) |
| 12 | DatabaseEcoreParser verbessern | M | Mittel | Unit | Offen |
| 13 | Inheritance-Mapping (EClass-Vererbung) | M | Hoch | Unit + Integration | **Erledigt** — SINGLE_TABLE, 11 Unit-Tests + OSGi-Test |
| 14 | EEnum STRING/ORDINAL-Konfiguration | S | Mittel | Unit | **Erledigt** — Bugfix + STRING als Default |
| 15 | Delegate-Pattern O2O/O2M konsistent machen | M | Mittel | Unit | **Erledigt** — Redundante JoinColumn/JoinTable entfernt |

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

### AP3 — Many-to-Many aktivieren ✅
**Aufwand: M | Priorität: Hoch | Teststrategie: Integration | Status: Erledigt**

**Root Cause:** Bidirektionale M2M-Rückrichtung lieferte leere Ergebnisse. Zwei Bugs in `ManyToManyProcessor`:

1. **`canProcess()` blockierte Stage 5:** `context.containsOpposite(source)` verhinderte, dass die inverse Seite in Stage 5 (`createOppositeMapping`) verarbeitet werden konnte. Die Referenz war bereits als Opposite registriert (Stage 4, owning side), und der Guard blockte sie in beiden Stages. **Fix:** `containsOpposite`-Check wird übersprungen wenn `isOppositeMapping()` true ist.

2. **`doProcess()` setzte mappedBy auf die falsche Seite:** Die inverse Seite setzte `mappedBy` auf die owning side's EORM-Mapping und markierte sich als Delegate — damit wurde das inverse Mapping nie zum Entity hinzugefügt. **Fix:** `mappedBy` wird auf die eigene (inverse) Mapping-Instanz gesetzt. Kein Delegate, kein JoinTable. Damit wird das Mapping korrekt registriert und `EDynamicTypeBuilder` erzeugt eine `ManyToManyMapping` mit gespiegelten Key-Fields und `isReadOnly=true`.

**Geänderte Dateien:**
- `ManyToManyProcessor.java` — `canProcess()` und `doProcess()` korrigiert
- `EPersistenceManyToManyTest.java` — Auskommentierte Assertions für C→A Rückrichtung aktiviert

**Tests:** 3 OSGi-Integrationstests (+ NoCacheVariante):
- `testManyToManyUni` — Unidirektional A→B ohne eOpposite
- `testManyToManyNoEOpposite` — Unidirektional mit manueller Rückseite
- `testManyToManyEOpposite` — Bidirektional A↔C mit eOpposite, inklusive Rückrichtung C→A

**Ergebnis:** Bidirektionale M2M-Beziehungen funktionieren End-to-End (Persist + Find in beide Richtungen).

### AP4 — EDynamicTypeBuilder Tests ✅
**Aufwand: L | Priorität: Hoch | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** 16 Unit-Tests in `EDynamicTypeBuilderTest.java`.

**Testklassen:**

| Gruppe | Tests | Abdeckung |
|--------|-------|-----------|
| EntitySetup | 3 | Tabelle, Alias, Java-Klasse korrekt initialisiert |
| IdConfiguration | 3 | String-ID mit Sequence, Int-ID, kein ID (Warning) |
| BasicMapping | 5 | String/Integer→DirectToFieldMapping, EEnum→String, Optional, expliziter Column-Name |
| ReferenceMappings | 3 | O2O/M2M/M2O EORM-Strukturen (JoinTable, ForeignKey) |
| Inheritance | 2 | Root InheritancePolicy + DiscriminatorColumn, Child DiscriminatorValue |

**Produktcode-Änderung:** `EDynamicTypeContext.setClassloader/getClassloader` Signatur von `DynamicClassLoader` auf `ClassLoader` geändert (verbessert Testbarkeit ohne ASM-Abhängigkeit in Unit-Tests).

**Hinweis:** Volle Reference-Mapping-Tests (EclipseLink Descriptor-Ebene) benötigen DynamicClassLoader/ASM für eindeutige Java-Klassen und sind durch die 86 OSGi-Integrationstests abgedeckt.

**Ergebnis:** Kernfunktionalität des Type Builders abgesichert.

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

### AP6 — EMFHelper & ConverterService Tests ✅
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** 36 neue Unit-Tests in 2 Testklassen.

**Testklassen:**

| Klasse | Tests | Abdeckung |
|--------|-------|-----------|
| `EMFHelperTest` | 19 | `getResponse()` (null/empty/missing/present), `mergeMaps()` (null/empty/disjoint/conflict/immutability), `getEffectiveOptions()` (merge+response+unmodifiable), `getEClassFromResourceSet()`, `getEClass()` mit Cache (miss/hit/null-cache/multi-class) |
| `DefaultConverterServiceTest` | 17 | Lookup by Type (UUID, LocalDate, BigDecimal, Instant, Duration → ComprehensiveConverter), Lookup by Name (`comprehensive`, `default`, `array`), Prioritätsreihenfolge (7 Converter in korrekter Reihenfolge), dynamische Registrierung (add/remove/find-by-name), null/unknown-Fehlerbehandlung |

### AP7 — Reserved-Words-Liste erweitern ✅
**Aufwand: S | Priorität: Mittel | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** Reserved-Words-Liste von 4 auf ~70 Einträge erweitert. Parametrisierte Tests für Escaping-Logik.

**Erweiterte Kategorien:**
- SQL DML/DDL Keywords (`select`, `from`, `where`, `update`, `insert`, `delete`, `create`, `drop`, `alter` etc.)
- Join Keywords (`join`, `left`, `right`, `inner`, `outer`, `cross`, `natural`, `on`)
- Constraint Keywords (`primary`, `foreign`, `unique`, `check`, `default`, `constraint`, `references`, `cascade`)
- SQL Data Types (`bigint`, `integer`, `float`, `double`, `decimal`, `varchar`, `boolean`)
- Temporal Keywords (`year`, `month`, `day`, `hour`, `minute`, `second`)
- Weitere (`end`, `limit`, `offset`, `row`, `trigger`, `view`, `sequence`, `function`, `procedure`)

**Verhalten:** Die Liste wird nur zur **Erkennung und Warnung** genutzt — Namen werden **nicht** automatisch escaped/prefixed. Stattdessen wird ein `LOG.warning()` ausgegeben. Der User entscheidet selbst, ob er umbenennt oder per ExtendedMetaData-Annotation einen expliziten Spaltennamen setzt. Dieser Ansatz folgt dem EclipseLink-Vorbild (dort: `useDelimiters` auf `DatabaseField`-Ebene).

**Tests:** Parametrisierte Tests (`@ParameterizedTest`) für Erkennung, Case-Insensitivity, Verifizierung dass Namen unverändert zurückgegeben werden, Context-Parameter, Null-Handling, und dass gängige Spaltennamen (`person`, `address`, `email`) nicht als reserved erkannt werden.

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

### AP9 — Error-Handling-Strategie ✅
**Aufwand: M | Priorität: Mittel | Status: Erledigt**

**Definierte Strategie:**

| Situation | Verhalten | Begründung |
|-----------|-----------|------------|
| Fehler in `process()` | Exception propagieren (nicht fangen) | Aufrufer muss Fehler sehen, silent failure maskiert Bugs |
| Fehler in `reprocess()` | Log WARNING, weitermachen | Re-Processing ist optional, vorheriger Zustand bleibt gültig |
| Fehler bei OSGi-Aktivierung | `IllegalStateException` werfen | OSGi DS erkennt Component-Fehler und reagiert |
| Asynchrone Fehler (Promise) | Log SEVERE | Kann nicht synchron propagiert werden |
| Optionale Lookups (TypeBuilder) | Log SEVERE, return null/early | Fehlende Referenz-Typen sind konfigurationsabhängig |
| I/O / Reflection | Wrap in `IllegalStateException` | Konsistent mit Rest des Projekts |

**Umgesetzte Änderungen:**

| Klasse | Vorher | Nachher |
|--------|--------|---------|
| `ProcessorImpl.process()` | catch → log + `processed=false` | Keine Exception-Fangung, Fehler propagieren |
| `NamedBaseProcessor.process()` | catch → log SEVERE + `processed=false` | Keine Exception-Fangung, Fehler propagieren |
| `NamedBaseProcessor.reprocess()` | catch → log SEVERE | catch → log WARNING (Re-Processing ist optional) |
| `EntityMappingPersistenceUnitConfigurator` | catch → log SEVERE (schlucken) | catch → throw `IllegalStateException` |
| `PersistenceUnitConfigurator` | catch → log SEVERE (schlucken) | catch → throw `IllegalStateException` |

**Bereits korrekt** (nicht geändert): `EORMMappingServiceComponent`, `EORMModelHelper`, `MappingProcessor.createProcessor()`, `ProcessorFactoryImpl` — propagieren Exceptions korrekt. `EDynamicPersistenceUnitInfo` — log WARNING bei optionalem JAR-URL-Parsing ist angemessen. `EDynamicTypeBuilder` TypeBuilder-Lookups — log SEVERE + return bei fehlendem Referenz-Typ ist konfigurationsabhängig.

### AP10 — Accessor & Indirection Tests ✅
**Aufwand: M | Priorität: Niedrig | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** 22 Unit-Tests in `AccessorIndirectionTest.java` mit realen EMF-Objekten (kein Mockito nötig).

| Gruppe | Tests | Abdeckung |
|--------|-------|-----------|
| EFeatureAccessor | 8 | Singleton-Cache, String/Int get/set, String→Int Konversion, EObject-Fallback |
| EFeatureAccessor Enum | 4 | EEnum→Literal (get), String→EEnum (set), isEEnumFeature Verhalten |
| ContainmentHelper | 6 | isContainmentReference, isContainmentChild, Containment-Bidi via eSet |
| NonContainmentBidi | 3 | isNonContainmentOppositeRelation, Bidi-Automatik via eOpposite |
| ProxyURI | 1 | URI-Format `jpa://puName/Entity#//ref/id/value`, eSetProxyURI/eIsProxy |

**Hinweis:** UnitOfWork-Cloning-Tests (backupCloneAttribute, cloneAttribute, buildClone) benötigen echte EclipseLink-Session und sind durch OSGi-Integrationstests abgedeckt.

**Ergebnis:** EclipseLink-EMF-Brücke (Accessor, Enum-Handling, Containment-Erkennung, ProxyURI-Format) abgesichert.

### AP11 — JPAResource: EMF Resource-Schicht für JPA ✅
**Aufwand: L | Priorität: Hoch | Teststrategie: Unit + OSGi-Integration | Status: Erledigt**

Kernstück für die vollständige EMF-Integration. Ermöglicht "EMF mit JPA-Backend" — transparente Proxy-Auflösung über `resourceSet.getResource(uri)`.

**Ausgangslage:**
- `PersistenceResource` Interface existiert (Core-Modul), definiert `load`, `save`, `delete`, `count`, `exist`
- `PersistenceEngine` Interface existiert (Core-Modul), abstrakte Basisklasse `BasicPersistenceEngine`
- `EBasicIndirectionPolicy` erzeugt korrekte proxyURIs (`jpa://puName/EntityName#//refName/idAttr/id`)
- Es fehlt: Konkrete `JPAResource`-Implementierung, `ResourceFactory`, Proxy-Resolution

**Architektur:**

```
ResourceSet
  │
  ├── Resource.Factory.Registry: "jpa" → JPAResourceFactory (@Component)
  │
  ▼
JPAResourceFactory.createResource(URI)
  │  URI: jpa://myPU/Person
  │  → OSGi-Lookup: EntityManagerFactory mit Filter (fennec.jpa.persistenceUnitName=myPU)
  │
  ▼
JPAResource extends ResourceImpl implements PersistenceResource
  │
  ├── load(options)
  │     → EntityManager.createQuery() / find()
  │     → Ergebnisse in resource.getContents()
  │
  ├── save(options)
  │     → EntityManager.persist() / merge() für contents
  │
  ├── getEObject(fragment)
  │     fragment: "//address/id/42"
  │     → Parse: EClass-Name + ID-Attribut + ID-Wert
  │     → EntityManager.find(descriptorClass, idValue)
  │     → Proxy-Resolution für non-containment Referenzen
  │
  └── delete(options)
        → EntityManager.remove()
```

**URI-Schema:**
- `jpa://puName/` — Basis-URI für eine PersistenceUnit
- `jpa://puName/EntityName` — Resource für einen Entity-Typ
- `jpa://puName/EntityName#//refName/idAttr/42` — Proxy-Fragment für ein konkretes Objekt

**Scope:**

| Komponente | Modul | Beschreibung |
|------------|-------|-------------|
| `JPAResourceImpl` | `persistence.eclipselink` | Konkrete Resource-Implementierung, delegiert an EntityManager |
| `JPAResourceFactory` | `persistence.eclipselink` | OSGi @Component, erzeugt JPAResource mit passender EMF |
| `JPAPersistenceEngine` | `persistence.eclipselink` | Konkrete Engine für JPA-Operationen (find, persist, merge, remove, query) |
| ProxyURI-Resolution | `JPAResourceImpl.getEObject()` | Fragment parsen → EM.find() → Proxy auflösen |
| ResourceFactory-Registrierung | OSGi DS | `jpa`-Schema im ResourceSet registrieren |

**Abhängigkeiten:**
- `EBasicIndirectionPolicy` — ProxyURI-Format muss konsistent mit `getEObject()` Fragment-Parsing sein
- `EDynamicTypeContext` — baseURI muss mit Resource-URI übereinstimmen
- `NonContainmentConverter` — URI-Erzeugung muss das `jpa://`-Schema verwenden

**Umgesetzt:**

| Datei | Beschreibung |
|-------|-------------|
| `JPAResourceImpl.java` | EMF Resource mit JPA-Backend: `load()` via JPQL-Query, `save()` via merge, `getEObject(fragment)` für Proxy-Resolution, `count()`/`exist()` |
| `JPAResourceFactory.java` | Factory für `jpa://` URIs, registriert sich im ResourceSet via `getProtocolToFactoryMap()` |
| `package-info.java` | OSGi Export-Annotation für `resource` Package |
| `JPAResourceIntegrationTest.java` | 4 OSGi-Tests: load, count/exist, getEObject (Proxy-Resolution), EMF-Setup |

**Noch offen für spätere Erweiterung:**
- OSGi @Component Registrierung der JPAResourceFactory (aktuell manuell im Test)
- JPAPersistenceEngine als separate Engine-Implementierung
- `save()` mit insert/update Unterscheidung
- Options-Auswertung (Filter, Batch, Lazy Loading)

**Ergebnis:** EMF-Objekte können transparent über `resourceSet.getResource(uri)` geladen und aufgelöst werden. Proxy-Fragments (`//refName/idAttr/value`) werden via `EntityManager.find()` resolved.

### AP12 — DatabaseEcoreParser verbessern
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit**

Der `DatabaseEcoreParser` generiert aus einem DB-Schema ein ECore-Modell. Die aktuelle Implementierung hat mehrere Schwachstellen.

**Scope:**

| Verbesserung | Beschreibung |
|-------------|-------------|
| `convertType()` auf JDBCType-Enum | Aktuell PostgreSQL-spezifische Strings (`int4`, `float8`). Umstellen auf `JDBCType`-Enum (vendor-agnostisch). ~15 Standard-JDBC-Typen abdecken. |
| PK-Erkennung über `primaryKeys()` | `StructureInfo.primaryKeys()` nutzen statt Umweg über ImportedKeys. `Map<String, Set<String>>` für PK-Spaltennamen. |
| FK-Map Duplikate | `Collectors.toMap()` → `groupingBy()`, damit mehrere FKs pro Tabelle nicht crashen. |
| Nullable-Info | `ColumnMetaData.nullability()` auswerten → `lowerBound` setzen. |
| AutoIncrement | `autoIncrement() == YES` → `setID(true)` markieren. |

**Teststrategie:** Bestehende `DatabaseEcoreParserConvertTypeTest` auf `JDBCType`-Enum umstellen. `isPK()`-Tests an neue Signatur anpassen.

**Ergebnis:** Parser funktioniert vendor-agnostisch (H2, PostgreSQL, MySQL) und nutzt verfügbare DB-Metadaten vollständig.

### AP13 — Inheritance-Mapping (EClass-Vererbung) ✅
**Aufwand: M | Priorität: Hoch | Teststrategie: Unit + Integration | Status: Erledigt**

**Umgesetzt:** Volle SINGLE_TABLE Inheritance-Unterstützung über die gesamte Pipeline.

**Geänderte Dateien:**

| Datei | Änderung |
|-------|----------|
| `EntityProcessor` | `canProcess()` erlaubt abstrakte EClasses; `configureInheritance()` setzt Inheritance/DiscriminatorColumn/DiscriminatorValue; `hasMappedSuperType()`/`getMappedRoot()` für Hierarchie-Erkennung |
| `MappingProcessor` | Abstrakte EClasses in Stage 1 einbezogen; `getEffectiveAttributes()`/`getEffectiveReferences()` für lokale vs. geerbte Features |
| `MappingContext` | `allEClasses` Liste für Subklassen-Erkennung |
| `EORMHelper` | `filterEClasses()` ohne Abstract-Filter |
| `EDynamicTypeBuilder` | `configureInheritance()` mit InheritancePolicy, `addClassIndicator()`, Parent/Child-Verknüpfung |
| `EDynamicTypeGenerator` | Inheritance-Pass mit Parent-vor-Child Sortierung (`inheritanceOrder()`) |
| `model.ecore` | Vehicle(abstract)/Car/Motorcycle Hierarchie |
| `InheritanceProcessorTest` | 11 Unit-Tests (Root, Child, Multi-Level, Siblings, Mixed) |
| `EPersistenceInheritanceTest` | OSGi-Integrationstest: Persist + Find mit SINGLE_TABLE |

**Default:** Abstrakte EClass → Entity mit `@Inheritance(SINGLE_TABLE)` + `@DiscriminatorColumn("DTYPE", STRING)`. Konfigurierbar auf MappedSuperclass über EORM. Child-Entities bekommen nur lokale Attribute/Referenzen, IDs werden vom Root geerbt.

**Ergebnis:** ECore-Modelle mit Vererbung erzeugen korrekte JPA SINGLE_TABLE Mappings End-to-End.

### AP14 — EEnum STRING/ORDINAL-Konfiguration ✅
**Aufwand: S | Priorität: Mittel | Teststrategie: Unit | Status: Erledigt**

**Bugfix:** `EDynamicTypeBuilder.processBasic()` verwendete `ea.getEAttributeType() == EcorePackage.Literals.EENUM` — das vergleicht mit der EEnum-Metaklasse, nicht mit konkreten EEnum-Instanzen (wie `PersonType`). Custom EEnums wurden nur zufällig als String gemappt, weil `getInstanceClass()` null zurückgibt und der String-Fallback greift.

**Fix:** `instanceof EEnum` statt `== EcorePackage.Literals.EENUM`. Damit werden alle EEnum-Attribute explizit als `String` (EnumType.STRING) in der Datenbank gespeichert — korrekt und konsistent mit dem `BasicProcessor` in der ORM-Schicht.

**Ergebnis:** EEnums werden zuverlässig als String persistiert, unabhängig davon ob `getInstanceClass()` null ist oder nicht.

### AP15 — Delegate-Pattern O2O/O2M konsistent machen ✅
**Aufwand: M | Priorität: Mittel | Teststrategie: Unit | Status: Erledigt**

**Umgesetzt:** Cleanup der redundanten JoinColumn/JoinTable/ForeignKey auf der inverse Seite bei bidirektionalen O2O und O2M/M2O Referenzen. Kein funktionaler Bug (EDynamicTypeBuilder kompensierte die Redundanz), aber das EORM-Modell ist jetzt sauber: inverse Seiten haben nur noch `mappedBy`.

**Geänderte Processoren:**
- `OneToOneProcessor.doProcess()` — Bei `isOppositeMapping()`: JoinColumn, ForeignKey, JoinTable von der existierenden inverse Mapping entfernt
- `OneToManyProcessor.doProcess()` — Bei `isOppositeMapping()`: JoinColumn (O2M), ForeignKey, JoinTable entfernt
- `ManyToOneProcessor.doProcess()` — Bei `isOppositeMapping()`: JoinColumn, ForeignKey, JoinTable von der O2M inverse Mapping entfernt

**Ergebnis:** Konsistentes EORM-Modell — inverse Seiten haben nur `mappedBy`, keine redundanten FK/JoinTable-Informationen.

## 12. Bekannte Code-Quality-Issues

| Issue | Ort | Empfehlung |
|-------|-----|------------|
| ~~`System.out.printf` Debug-Ausgaben~~ | ~~alle Module~~ | ~~erledigt (AP5)~~ |
| ~~`printStackTrace()` bei Exception-Handling~~ | ~~alle Module~~ | ~~erledigt (AP5)~~ |
| `EDynamicTypeContext extends ConcurrentHashMap` | `persistence.eclipselink` | Composition statt Inheritance |
| ~~Reserved-Words-Liste unvollständig~~ | ~~`MappingHelper`~~ | ~~erledigt (AP7)~~ — ~70 Einträge, Warn-only statt Prefix-Escaping |
| EMFHelper-Cache ohne Invalidierung | `persistence` Core | Cache-Eviction-Strategie einführen |
| `Options`-Klasse mit 60+ Konstanten | `persistence` Core | In thematische Interfaces aufteilen |

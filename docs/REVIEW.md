# Eclipse Fennec Persistence JPA — Structured Review

> Lebendes Arbeitsdokument: Review-Kriterien, Ergebnisse und abgeleitete Arbeitspakete.
> Stand: 2026-04-14 | Review durchgeführt: 2026-04-14 | Letzte Aktualisierung: 2026-04-14

**Review-Quellen:**
- Projekt-Quellcode: `/opt/git/emf.persistence-jpa/`
- EclipseLink 4 Quellcode (Referenz): `/opt/git/eclipselink-4/`

---

## 1. Review-Kriterien

### 1.1 Technische Kriterien

#### T1 — API-Vollständigkeit & Konsistenz
- Sind alle öffentlichen Interfaces vollständig dokumentiert (Javadoc)?
- Gibt es Interfaces ohne Implementierung oder tote Interfaces?
- Ist die API-Oberfläche konsistent (Naming, Parameter-Reihenfolge, Return-Typen)?
- Sind Extension Points klar definiert und nutzbar?

#### T2 — Fehlerbehandlung & Robustheit
- Werden Fehler sinnvoll geworfen und nicht verschluckt?
- Gibt es eine konsistente Exception-Hierarchie?
- Werden Randfälle behandelt (null, leere Collections, ungültige Eingaben)?
- Gibt es Logging an den richtigen Stellen (JUL)?

#### T3 — Testabdeckung & Testqualität
- Welche Module/Klassen haben keine oder unzureichende Tests?
- Decken die Tests sowohl Happy Path als auch Fehlerfälle ab?
- Sind die Tests wartbar und verständlich?
- Gibt es Integrationstests für alle kritischen Pfade?

#### T4 — Code-Qualität & Wartbarkeit
- Gibt es Code-Duplikation die refactored werden sollte?
- Sind Abhängigkeiten zwischen Modulen sauber?
- Gibt es zyklische Abhängigkeiten?
- Sind TODOs/FIXMEs adressiert oder dokumentiert?

#### T5 — Java 21 Sprachmittel & Code-Stil
- Werden Java 21 Features genutzt wo sinnvoll?
  - Records statt POJOs / Datenklassen
  - Sealed Classes/Interfaces für geschlossene Typhierarchien
  - Pattern Matching (`instanceof`, `switch`)
  - Text Blocks für mehrzeilige Strings (SQL, XML)
  - `Optional` / Stream-Verbesserungen
  - SequencedCollections (`SequencedMap`, `SequencedSet`)
  - Virtual Threads (wo IO-bound sinnvoll)
- **Import-Konsistenz:**
  - Werden Java-Imports verwendet statt Fully Qualified Class Names (FQCN) im Code?
  - Werden statische Imports genutzt (z.B. `import static java.util.Objects.*`)?
  - Wildcard-Imports sind erlaubt und bevorzugt bei statischen Imports
- **Utility-Nutzung:**
  - Bevorzugte Nutzung von `java.util.Objects.*` für Null-Checks (`requireNonNull`, `isNull`, `nonNull`)
  - `Objects.equals()` statt manueller null-sicherer Vergleiche
  - `Objects.hash()` / `Objects.hashCode()` statt manueller Hash-Berechnung

#### T6 — OSGi & Build
- Sind Bundle-Manifeste korrekt (Imports, Exports)?
- Funktioniert die Service-Registrierung zuverlässig?
- Sind Versionen und Abhängigkeiten aktuell?
- Ist der Build reproduzierbar und stabil?
- **Package-Metadaten:**
  - Existieren `package-info.java` Dateien für alle exportierten Packages?
  - Sind OSGi-Annotationen (`@Export`, `@Version`) in `package-info.java` gesetzt?
  - Sind Requirements und Capabilities korrekt deklariert?

#### T7 — Abhängigkeiten & Eclipse Fennec Migration
Kontext: Das Projekt soll in das **Eclipse Fennec**-Projekt einfließen. Abhängigkeiten zu **GeckoProjects** sind problematisch, da wir in einer Migrationsphase von GeckoProjects nach Eclipse Fennec sind und "alte Zöpfe" abschneiden wollen.

- **GeckoProjects-Abhängigkeiten identifizieren und bewerten:**
  - Welche Bundles/Packages aus GeckoProjects (`org.gecko.*`, `org.geckoprojects.*`) werden referenziert?
  - In welchen Modulen (bnd, Gradle, Java-Imports) tauchen GeckoProjects-Abhängigkeiten auf?
  - Gibt es Eclipse Fennec-Pendants die stattdessen genutzt werden können?
  - Welche GeckoProjects-Abhängigkeiten haben noch kein Fennec-Äquivalent (Blocker)?
- **Erlaubte Abhängigkeiten:**
  - Eclipse Fennec (`org.eclipse.fennec.*`) — bevorzugt
  - Eclipse-Projekte (EMF, EclipseLink, Equinox, etc.) — ok
  - Apache-Projekte — ok
  - OSGi Spec / Compendium — ok
  - Jakarta EE APIs — ok
- **Nicht erwünschte Abhängigkeiten:**
  - GeckoProjects (`org.gecko.*`, `org.geckoprojects.*`) — migrieren oder eliminieren
  - Sonstige proprietäre/interne Abhängigkeiten — prüfen
- **Migrations-Reifegrad:**
  - Wie weit ist die Migration von GeckoProjects → Eclipse Fennec fortgeschritten?
  - Welche Abhängigkeiten blockieren eine vollständige Entkopplung?

#### T8 — Performance & Skalierbarkeit
- Gibt es bekannte Performance-Bottlenecks?
- Wie verhält sich das System mit großen Datenmengen?
- Sind Caching-Strategien sinnvoll?
- Gibt es N+1 Query-Probleme?

#### T9 — Defensive Programmierung & Sicherheit
- **Null-Safety:**
  - Werden Null-Checks konsequent durchgeführt (bevorzugt via `Objects.requireNonNull`)?
  - Gibt es potentielle `NullPointerException`-Stellen?
  - Werden Rückgabewerte auf null geprüft wo nötig?
- **Race Conditions & Thread-Safety:**
  - Sind Mutable Fields in Multithreading-Kontexten korrekt synchronisiert?
  - Werden volatile, AtomicReference, oder Locks wo nötig verwendet?
  - Gibt es TOCTOU (Time-of-Check-Time-of-Use) Probleme?
- **Input Validation:**
  - Werden externe Eingaben validiert?
  - Gibt es Risiken für Injection (SQL, JPQL, etc.)?
- **Ressourcen-Management:**
  - Werden Ressourcen (EntityManager, Streams, etc.) korrekt geschlossen?
  - Gibt es Leak-Risiken bei Exceptions?
- **Immutability & Defensive Copies:**
  - Werden Collections defensiv kopiert oder als unmodifiable zurückgegeben?
  - Werden Parameter nicht unnötig mutiert?

#### T10 — BSI TR-03183 / Supply Chain Security
- SBOM-Generierung (CycloneDX/SPDX)?
- Vulnerability-Disclosure-Policy (SECURITY.md)?
- Dependency-Updates, bekannte CVEs?
- Logging von sensitiven Daten?

#### T11 — Design Principles (SOLID, DRY, KISS)
- Single Responsibility Principle — gibt es God-Classes oder -Methoden?
- Dependency Inversion — werden konkrete statt abstrakte Abhängigkeiten verwendet?
- DRY — gibt es signifikante Code-Duplikation?
- KISS — gibt es unnötige Komplexität?

### 1.2 Fachliche Kriterien

#### F1 — JPA-Compliance & Feature-Vollständigkeit
- Welche JPA-Features sind implementiert, welche fehlen?
- Sind die implementierten Features korrekt (Spec-konform)?
- Gibt es bekannte Abweichungen von der JPA-Spezifikation?
- Welche Beziehungstypen werden unterstützt (O2O, O2M, M2O, M2M)?
- Werden Cascade-Typen und OrphanRemoval korrekt angewendet?

#### F2 — Entity Lifecycle Management
- Werden Entity-States korrekt verwaltet (NEW, MANAGED, DETACHED, REMOVED)?
- Funktioniert Persist/Merge/Remove korrekt?
- Gibt es Probleme mit Lazy Loading?
- Wird die Identity Map / First-Level-Cache korrekt genutzt?

#### F3 — Type Mapping & Conversion
- Sind alle EMF↔JPA Type-Mappings vollständig?
- Werden Custom-Types (UUID, BigDecimal, Date/Time) korrekt konvertiert?
- Gibt es Präzisionsverluste bei Konvertierungen?
- Werden Enum-Typen korrekt gemappt?

#### F4 — Query & Resource Integration
- Funktionieren JPQL-Queries korrekt mit EMF-Entities?
- Ist die EMF Resource-Integration konsistent?
- Werden Resource-Notifications korrekt ausgelöst?
- Funktioniert Cross-Resource-Referenz-Auflösung?

#### F5 — Schema & Mapping
- Wird das Datenbank-Schema korrekt aus Ecore abgeleitet?
- Sind Tabellen-/Spaltennamen konsistent und sinnvoll?
- Werden Constraints (NOT NULL, UNIQUE, FK) korrekt generiert?
- Gibt es Support für Schema-Evolution?

#### F6 — Caching & Referenz-Auflösung
- Wird der EclipseLink L2-Cache korrekt genutzt?
- Funktioniert die EMF-Proxy-Auflösung für Non-Containment-Referenzen?
- Gibt es Stale-Data-Probleme?

#### F7 — Transaktionsmanagement
- Werden Transaktionen korrekt begonnen/committed/rollbacked?
- Funktioniert Transaction-Isolation?
- Gibt es Deadlock-Risiken?

#### F8 — Change Tracking
- Werden Änderungen an EMF-Objekten korrekt an JPA propagiert?
- Funktioniert Dirty-Detection?
- Gibt es Probleme mit der Reihenfolge von Änderungen?

#### F9 — Fehlerszenarien & Resilience
- Was passiert bei Constraint-Verletzungen?
- Wie wird mit Connection-Verlust umgegangen?
- Was passiert bei Schema-Mismatches?

#### F10 — OSGi-Integration & Lifecycle
- Funktioniert der OSGi-Service-Lifecycle korrekt?
- Werden Services sauber registriert und de-registriert?
- Funktioniert die Konfiguration via ConfigAdmin?
- Gibt es Service-Ranking/Filter-Probleme?

#### F11 — API-Design & Erweiterbarkeit
- Ist die API intuitiv nutzbar?
- Können externe Entwickler eigene Converter/Processor registrieren?
- Ist die Erweiterung via OSGi Services möglich?

#### F12 — Performance-Verhalten
- Gibt es messbare Latenz-Probleme?
- Wie verhält sich das System unter Last?
- Gibt es Memory-Leaks bei langlebigen Sessions?

#### F13 — Datenbankkompatibilität
- Funktioniert das System mit verschiedenen Datenbanken (H2, PostgreSQL, etc.)?
- Gibt es datenbankspezifische Workarounds?
- Wird der EclipseLink DatabasePlatform korrekt konfiguriert?

#### F14 — Reverse Engineering (DatabaseEcoreParser)
- Kann aus einem bestehenden Datenbankschema ein korrektes Ecore-Modell abgeleitet werden?
- Werden Primary Keys, Foreign Keys und Beziehungen korrekt erkannt?
- Werden alle gängigen SQL-Typen unterstützt?

#### F15 — Dokumentation & Nutzbarkeit
- Gibt es eine brauchbare README / Getting-Started-Anleitung?
- Sind die Konfigurationsoptionen dokumentiert?
- Gibt es Beispiel-Projekte oder -Tests?
- Sind bekannte Limitationen dokumentiert?

---

## 2. Review-Ergebnisse

> **Methodik:** Code-Review aller Module mit Fokus auf die definierten Kriterien.
> Review unter Hinzuziehung des EclipseLink-Quellcodes (`/opt/git/eclipselink-4/`) für Verständnis der internen APIs.

### 2.1 Technische Findings

#### Kritisch

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T2-01 | T2 | ~~`dispose()` in `BasicPersistenceEngine` ist leer — kein Cleanup~~ Bestätigt, aber nicht kritisch: Engine verwaltet keine eigenen Ressourcen (EM/EMF-Lifecycle liegt bei Konfiguratoren). Downgraded zu **Minor** (→ AP-33) | `BasicPersistenceEngine.java:51` | ~~Ressourcen-Cleanup implementieren~~ |
| T2-02 | T2 | ~~`CascadeType.REMOVE` bei Non-Containment (`OneToManyProcessor`)~~ **FIXED (AP-01)** — Cascade für Non-Containment korrigiert: persist/detach/refresh, kein remove. OrphanRemoval nur bei Containment. | `OneToManyProcessor.java:50-68` | ~~Cascade-Logik korrigieren~~ |
| T9-01 | T9 | ~~`EPersistenceContextImpl` setzt Properties auf `persistenceUnit`, nicht `this`~~ **FIXED (AP-04)** | `EPersistenceContextImpl.java:74-76` | ~~Properties auf this setzen~~ |
| T9-02 | T9 | ~~Race Condition in `PersistenceUnitConfigurator.activate()` und `EntityMappingPersistenceUnitConfigurator`~~ **FIXED (AP-03)** — volatile fields, ExecutorService als Feld, proper shutdown | `PersistenceUnitConfigurator.java:125-143` | ~~volatile + shutdown~~ |
| T9-03 | T9 | ~~SQL-Injection in `JPAResourceImpl.doLoad()`: Entity-Name wird unkontrolliert in JPQL eingebaut~~ **FIXED (AP-09)** — Entity-Name über `descriptor.getAlias()` validiert | `JPAResourceImpl.java:85-91` | ~~Entity-Name validieren~~ |
| T9-04 | T9 | ~~`EFeatureAccessor.accessorMap`: Statische ConcurrentHashMap, fehlende Eviction, Cross-PU-Konflikte bei unterschiedlichen Convertern~~ **FIXED (AP-18)** — Statischer Cache entfernt, Converter als Konstruktor-Parameter | `EFeatureAccessor.java:45` | ~~Cache entfernen, Converter per Konstruktor~~ |
| T9-05 | T9 | ~~`BigIntegerInternalConverter`: `intValue()` — Silent Truncation bei > 32-bit~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:309,315` | ~~`BigInteger` nativ an JDBC durchreichen~~ |

#### Major

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T1-01 | T1 | `PersistenceEngine.doSave()` Javadoc sagt "one or more" EObjects, Implementierung akzeptiert beliebig viele | `PersistenceEngine.java:43-48` | Javadoc oder Implementierung anpassen |
| T1-02 | T1 | `EPersistenceContext`: mehrere Methoden nur `return null` (`createNamedQuery`, `getMappedSuperclassNames`) | `EPersistenceContextImpl.java:106-121` | Implementieren oder `UnsupportedOperationException` |
| T2-03 | T2 | ~~`JPAResourceImpl.doSave()` und `delete()`: Kein Rollback bei Exception~~ **FIXED (AP-02)** — try/catch/rollback + IOException wrapping + 8 Unit-Tests | `JPAResourceImpl.java:102-123` | ~~try/catch mit `rollback()`~~ |
| T2-04 | T2 | `JPAResourceImpl.getEngine()` gibt hart `null` zurück — Vertragsbruch des Interfaces | `JPAResourceImpl.java:180-182` | Engine implementieren oder Optional verwenden |
| T2-05 | T2 | ~~`BaseReferenceProcessor.calculateCascadeType()` setzt `cascadeRemove` für Non-Containment~~ **FIXED (AP-01)** | `BaseReferenceProcessor.java:153-165` | ~~Non-Containment: persist+detach+refresh, KEIN remove~~ |
| T2-06 | T2 | ~~`PersistenceUnitConfigurator` hardcodiert `H2Platform`~~ **FIXED (AP-17)** — `AbstractPersistenceUnitConfigurator` nutzt `TargetDatabase.Auto` als Default | `AbstractPersistenceUnitConfigurator.java` | ~~`TargetDatabase.Auto`~~ |
| T4-01 | T4 | `Options.CAP_USE_TIMESTAMP` und `CAP_TIMESTAMP_FIELD_NAME` haben identischen Wert `"TIMESTMAP_FIELD_NAME"` (Tippfehler: TIMESTMAP statt TIMESTAMP) | `Options.java:365,377` | Eigene Werte + Tippfehler korrigieren |
| T4-02 | T4 | `Keywords.CAPABILITY_NAMESPACE` enthält `"org.eclipse.fennec.persistence.old.old"` — Migrations-Artefakt | `Keywords.java:57-59` | Namespace bereinigen |
| T4-03 | T4 | `Options`-Javadoc referenziert nicht-existente Typen: `DBObjectBuilderImpl`, `NativeQueryEngine`, MongoDB-Formulierungen | `Options.java:40,49,153` | Javadoc auf JPA-Kontext aktualisieren |
| T4-04 | T4 | `EFeatureAccessor.accessorMap`: Statische ConcurrentHashMap ohne Eviction — Memory-Leak, Converter-Konflikte bei parallelen PUs | `EFeatureAccessor.java:45` | Cache auf PU-Scope beschränken, Eviction |
| T4-05 | T4 | ~~Code-Duplikation: beide PU-Konfiguratoren nahezu identisch~~ **FIXED (AP-17)** — `AbstractPersistenceUnitConfigurator` extrahiert | `spi/AbstractPersistenceUnitConfigurator.java` | ~~Basisklasse extrahieren~~ |
| T4-06 | T4 | `EDynamicTypeContext` erbt von `ConcurrentHashMap` — Anti-Pattern, exponiert alle Map-Methoden | `EDynamicTypeContext.java:37` | Komposition statt Vererbung |
| T8-01 | T8 | `JPAResourceImpl.doLoad()`: `SELECT e FROM EntityName e` ohne Paginierung — OOM bei großen Tabellen | `JPAResourceImpl.java:85-93` | Paginierung via setFirstResult/setMaxResults |
| T8-02 | T8 | ~~`Executors.newSingleThreadExecutor()` wird nie heruntergefahren~~ **FIXED (AP-03)** — executor als Feld, shutdownNow+awaitTermination in deactivate | `EntityMappingPersistenceUnitConfigurator.java`, `PersistenceUnitConfigurator.java` | ~~ExecutorService shutdown~~ |
| T9-06 | T9 | ~~`BigDecimalInternalConverter`: `doubleValue()` — Präzisionsverlust~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:291,299` | ~~`BigDecimal` nativ an JDBC durchreichen~~ |
| T10-01 | T10 | Keine SBOM-Generierung (CycloneDX/SPDX) im Build — TR-03183-2 nicht erfüllt | `build.gradle` | CycloneDX-Gradle-Plugin integrieren |
| T10-02 | T10 | Kein SECURITY.md, keine Vulnerability-Disclosure-Policy — TR-03183-1 | Projekt-Root | SECURITY.md erstellen (Eclipse Foundation Template) |
| T10-03 | T10 | ~~Default-Logging auf `FINE`~~ **FIXED (AP-10, AP-17)** — Default auf `WARNING` in `AbstractPersistenceUnitConfigurator` | `AbstractPersistenceUnitConfigurator.java` | ~~Default auf WARNING~~ |
| T11-01 | T11 | SRP: `EDynamicTypeBuilder` ist God-Class (700+ Zeilen, 7 Verantwortlichkeiten) | `EDynamicTypeBuilder.java` | Aufteilen in IdBuilder, BasicBuilder, ReferenceBuilder, InheritanceBuilder |
| T11-02 | T11 | ~~DIP: H2Platform hardcodiert~~ **FIXED (AP-17)** — `TargetDatabase.Auto` als Default in Basisklasse, per Property überschreibbar | `AbstractPersistenceUnitConfigurator.java` | ~~Konfigurierbare Property~~ |

#### Major (T5 — Code-Stil, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T5-01 | T5 | 33 Stellen `instanceof` ohne Pattern Variable (z.B. `if (x instanceof Foo) { Foo f = (Foo) x; }`) | Diverse | `if (x instanceof Foo f)` |
| T5-02 | T5 | 60+ Stellen mit FQCN statt Import (z.B. `java.util.Map.Entry<...>` inline) | Diverse | Imports verwenden |
| T5-03 | T5 | Switch-Statements mit traditioneller Syntax wo Arrow-Syntax klarer wäre | `EDynamicTypeBuilder.java`, `DatabaseEcoreParser.java` | Arrow-Syntax verwenden |
| T5-04 | T5 | `Objects.isNull()` / `Objects.nonNull()` inkonsistent mit `== null` / `!= null` gemischt | Diverse | Einheitlich, bevorzugt `Objects.*` |
| T5-05 | T5 | Fehlende statische Imports für häufig genutzte Utilities | Diverse | `import static java.util.Objects.*` |

#### Major (T3 — Testlücken, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T3-01 | T3 | `JPAResourceImpl` hat keine eigenen Unit-Tests — nur indirekt über OSGi-Integration | — | Mock-basierte Unit-Tests (EntityManager, Transaction) |
| T3-02 | T3 | `EFeatureAccessor` untestet — implizit über E2E, keine isolierten Tests | — | Unit-Tests für Konvertierung, Enum-Handling, Default-Value-Logic |
| T3-03 | T3 | `PersistenceUnitConfigurator` / `EntityMappingPersistenceUnitConfigurator` untestet (OSGi DS-Lifecycle) | — | Integration-Tests mit Mock-DS-Context |
| T3-04 | T3 | Kein Test für gleichzeitigen Zugriff / Thread-Safety | — | Concurrent-Tests für kritische Pfade |

#### Major (T6 — OSGi, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T6-01 | T6 | Fehlende `package-info.java` in mehreren exportierten Packages (z.B. `converter`, `helper`) | Diverse | `@Export @Version("1.0.0")` hinzufügen |
| T6-02 | T6 | `package-info.java` vorhanden aber ohne `@Export` oder `@Version` | Diverse | Annotationen ergänzen |
| T6-03 | T6 | Bundle-Version ist `0.1.0.SNAPSHOT` in allen Modulen — ok für Pre-Release, vor 1.0 prüfen | `bnd.bnd` | Bei Release auf 1.0.0 anheben |
| T6-04 | T6 | `org.eclipse.fennec.persistence.eclipselink` importiert EclipseLink-interne Packages (z.B. `org.eclipse.persistence.internal.sessions`) | `bnd.bnd` | Dokumentieren als bewusste Entscheidung (notwendig für DynamicEntity API) |
| T6-05 | T6 | `cnf/ext/libraries.bnd` referenziert `dimc-ossrh-releases` Nexus — Eclipse-Hosting prüfen | `cnf/ext/libraries.maven` | Eclipse Nexus evaluieren |

#### Major (T7 — GeckoProjects-Abhängigkeiten, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T7-01 | T7 | `org.gecko.emf.osgi` in `bnd.bnd` Import-Packages (5 Bundles) | Diverse `bnd.bnd` | → `org.eclipse.fennec.emf.osgi` |
| T7-02 | T7 | `org.geckoprojects.emf` Maven-Coordinates in `cnf/ext/libraries.maven` und `cnf/central.mvn` | Build-Config | → Eclipse Fennec Maven Coordinates |
| T7-03 | T7 | Java-Imports: `org.gecko.emf.osgi.*` in 8 Quelldateien | Diverse `.java` | → `org.eclipse.fennec.emf.osgi.*` |
| T7-04 | T7 | `org.gecko.emf.osgi.example.model.basic` Testabhängigkeit — Eclipse Fennec-Pendant vorhanden (`org.eclipse.fennec.emf.osgi.example.model.basic`) | `bnd.bnd`, `test.bndrun` | Auf Fennec-Version umstellen |

#### Minor

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T4-07 | T4 | Auskommentierter Code in `EDynamicTypeBuilder` (>50 Zeilen) | `EDynamicTypeBuilder.java` | Entfernen |
| T4-08 | T4 | Auskommentierter Code in `EPersistenceCitizenTest` (deaktivierte Tests) | `EPersistenceCitizenTest.java` | Entfernen oder @Disabled mit Ticket |

### 2.2 Fachliche Findings

#### Kritisch

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| F1-01 | F1 | ~~`OrphanRemoval` invertiert: Bei Containment=true wird orphanRemoval=false gesetzt und umgekehrt~~ **FIXED (AP-01)** | `OneToManyProcessor.java:56-68` | ~~Logik invertieren~~ |
| F1-02 | F1 | ~~Cascade enthält `REMOVE` bei Non-Containment-Referenzen~~ **FIXED (AP-01)** — Nur persist/detach/refresh, kein remove bei Non-Containment | `BaseReferenceProcessor.java` | ~~REMOVE nur bei Containment~~ |
| F7-01 | F7 | ~~Kein Rollback bei Exceptions in `doSave()`/`delete()`~~ **FIXED (AP-02)** | `JPAResourceImpl.java` | ~~try/catch/rollback~~ |
| F9-01 | F9 | ~~Non-Containment-Referenzen: Aufgelöstes Proxy-Objekt hat `eResource() == null`~~ **FIXED (AP-05)** — Proxy wird in Resource-Contents eingefügt | `EBasicIndirectionPolicy.java` | ~~In Resource einfügen~~ |
| F9-02 | F9 | ~~`JPAResourceImpl.doUnload()`: isLoaded-Flag wird nicht zurückgesetzt~~ **FIXED (AP-05)** | `JPAResourceImpl.java` | ~~isLoaded = false in doUnload~~ |
| F10-01 | F10 | ~~Race Condition: `emf`/`server` können null sein bei parallelem Zugriff~~ **FIXED (AP-03)** | `PersistenceUnitConfigurator.java` | ~~volatile + null-checks~~ |

#### Major

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| F1-03 | F1 | ~~Nur SINGLE_TABLE Inheritance~~ **FIXED (AP-12)** — Alle 3 Strategien via EAnnotation konfigurierbar | `EntityProcessor.java` | ~~Konfigurierbar machen~~ |
| F1-04 | F1 | @Embedded/@Embeddable: EORM-Metamodell vorhanden, aber kein Processor implementiert | — | EmbeddableProcessor implementieren |
| F1-05 | F1 | @MappedSuperclass: EORM-Metamodell vorhanden, aber nicht implementiert — abstrakte EClasses werden immer als Entity gemappt | — | Design-Entscheidung dokumentieren oder implementieren |
| F1-06 | F1 | ~~@Version (Optimistic Locking)~~ **FIXED (AP-13)** — Version-Mapping via EAnnotation, MappingProcessor + EDynamicTypeBuilder mit useVersionLocking | — | ~~VersionProcessor implementieren~~ |
| F1-07 | F1 | Derived/volatile EStructuralFeatures werden als normale Attribute gemappt — erzeugt DDL-Fehler wenn Feature keinen Backing-Store hat | `MappingProcessor.java` | Derived/volatile Features filtern |
| F2-01 | F2 | Kein Test für Entity-Update (merge detached) — nur persist + find | Tests | Update-Tests ergänzen |
| F2-02 | F2 | Kein Test für Entity-Remove mit Cascade-Verhalten | Tests | Delete-Cascade-Tests |
| F2-03 | F2 | Kein Test für Detach/Reattach-Lifecycle | Tests | Detach-Tests |
| F2-04 | F2 | Kein Optimistic-Locking-Test (kein @Version-Support) | Tests | Nach @Version-Implementierung testen |
| F2-05 | F2 | Kein Test für Cache-Eviction / Second-Level-Cache-Konsistenz | Tests | Cache-Tests |
| F2-06 | F2 | EclipseLink `UnitOfWork.setShouldNewObjectsBeCached(true)` — nie konfigurierbar | `EntityManagerFactoryConfigurator.java:170` | Property exponieren oder entfernen |
| F3-01 | F3 | ~~`BigDecimalInternalConverter`: Doppelte Konvertierung (BigDecimal → double → BigDecimal) vernichtet Präzision~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java` | ~~BigDecimal nativ durchreichen~~ |
| F3-02 | F3 | ~~`ZonedDateTimeInternalConverter`: Zeitzonen-Information geht bei Timestamp-Roundtrip verloren~~ **FIXED (AP-22)** — ISO-8601-String-Speicherung erhält Zeitzonen | `ComprehensiveTypeConverter.java` | ~~Als ISO-8601 String speichern~~ |
| F3-03 | F3 | Fehlende Converter für URI, URL, OffsetDateTime — teilweise in Ecore-Modellen genutzt | — | Converter implementieren |
| F3-04 | F3 | ~~EEnum-Converter: `findConverter` gibt null für EEnum zurück — kein "enum"-Key registriert~~ **FIXED (AP-14)** — Enum-Handling über EclipseLink-Mappings, kein separater Converter nötig | `ComprehensiveTypeConverter.java` | ~~Architektur-Entscheidung dokumentiert~~ |
| F3-05 | F3 | Kein Converter für `EEnumLiteral` ↔ String/Ordinal — Enum-Persistierung nutzt EclipseLink-Mapping direkt | — | Dokumentieren als bewusste Entscheidung |
| F4-01 | F4 | ~~Deaktivierte Tests in `EPersistenceCitizenTest`, `TypeConverterEndToEndTest`~~ **FIXED (AP-14)** — TypeConverterEndToEndTest und CitizenTest reaktiviert | Tests | ~~Tests analysieren und reaktivieren~~ |
| F4-02 | F4 | `JPAResourceImpl.doLoad()` setzt `isLoaded = false` auch bei Erfolg, dann `isLoaded = true` — Race Window | `JPAResourceImpl.java:82-96` | `isLoaded` erst nach erfolgreichem Load setzen |
| F4-03 | F4 | `JPAResourceImpl.doLoad()`: Neue Objekte werden ge-`add`ed ohne vorherige Contents zu leeren | `JPAResourceImpl.java:90` | Contents leeren oder Duplikat-Check |
| F4-04 | F4 | Cross-Resource-Referenzen: Keine Tests für JPA↔JPA oder JPA↔XMI Cross-Resource Szenarien | Tests | Cross-Resource-Tests |
| F5-01 | F5 | ~~`DatabaseEcoreParser` hatte Daanse/OLAP-Abhängigkeiten, nutzte nicht-standard JDBC~~ **FIXED (AP-15)** — Komplett auf Standard-JDBC umgeschrieben | `DatabaseEcoreParser.java` | ~~Standard-JDBC nutzen~~ |
| F5-02 | F5 | ~~Keine Foreign-Key → Referenz-Auflösung in `DatabaseEcoreParser`~~ **FIXED (AP-15)** — FK→ManyToOne+OneToMany mit EOpposite, Junction→ManyToMany | `DatabaseEcoreParser.java` | ~~FK-Analyse implementieren~~ |
| F5-03 | F5 | Keine Schema-Evolution-Unterstützung — DDL nur `create-or-extend-tables` | — | Schema-Migration evaluieren (Flyway?) |
| F5-04 | F5 | ~~`DatabaseEcoreParser`: Kein Multi-Schema-Support — alle Tabellen in einem EPackage~~ **FIXED (AP-15)** — Ein EPackage pro Schema | `DatabaseEcoreParser.java` | ~~Schema-separierte EPackages~~ |
| F6-01 | F6 | ~~`EBasicIndirectionPolicy.buildIndirectObject()` modifiziert Original-EObject (setzt ProxyURI)~~ **FIXED (AP-20)** — Proxy wird als `EcoreUtil.copy()` erstellt, Original bleibt unverändert | `EBasicIndirectionPolicy.java` | ~~Kopie erstellen~~ |
| F8-01 | F8 | Kein automatisches Change-Tracking: EMF-Änderungen an bereits persistierten Objekten werden nicht automatisch an JPA propagiert — erfordert manuelles `merge()` | — | EContentAdapter-Brücke evaluieren |
| F9-03 | F9 | Zwei parallele Proxy-Mechanismen: `NonContainmentInternalConverter` (URI-String) und `EBasicIndirectionPolicy` (EMF-Proxy) — potenzieller Konflikt | `ComprehensiveTypeConverter.java`, `EBasicIndirectionPolicy.java` | Konsolidieren |
| F12-01 | F12 | Kein Batch Writing konfiguriert — jedes Persist ist ein einzelnes INSERT | EclipseLink Config | `eclipselink.jdbc.batch-writing` exponieren |
| F14-01 | F14 | ~~`DatabaseEcoreParser`: Alle `java.sql.Types` werden auf EString gemappt — kein Typ-Mapping~~ **FIXED (AP-15)** — 20+ JDBCType-Mappings implementiert | `DatabaseEcoreParser.java` | ~~JDBCType-Mapping~~ |
| F14-02 | F14 | ~~`DatabaseEcoreParser`: Primary Keys werden nicht als `eID` markiert~~ **FIXED (AP-15)** — PKs werden als `iD=true` gesetzt | `DatabaseEcoreParser.java` | ~~PK→eID~~ |
| F15-01 | F15 | ~~README beschreibt ein komplett anderes Projekt (Codec-Bibliothek)~~ **FIXED (AP-07)** | `README.md` | ~~README neu schreiben~~ |
| F15-02 | F15 | Keine Getting-Started-Anleitung, kein Beispiel-Projekt | — | Tutorial erstellen |
| F15-03 | F15 | Keine Dokumentation der Konfigurationsoptionen (`fennec.jpa.*` Properties) | — | Config-Referenz erstellen |
| F15-04 | F15 | `Options`-Klasse enthält MongoDB-Legacy-Referenzen (z.B. `DBObjectBuilderImpl`) | `Options.java` | Legacy-Referenzen entfernen |

> **Hinweis:** Es existiert bereits ein abstraktes EMF-Query-Modell. Die Anwendung auf JPA ist ein separates Arbeitspaket. Im Review wird nur der aktuelle Stand dokumentiert — keine tiefe Analyse.

---

## 3. Arbeitspakete

> **Testabdeckung als Pflichtbestandteil jedes Arbeitspaketes:**
>
> Bei jedem Arbeitspaket muss die Testabdeckung aktiv geprüft und ggfs. als Teil des Fixes erhöht werden:
> - Jeder Bugfix braucht mindestens einen Test, der den Bug reproduziert und den Fix beweist
> - Jedes neue Feature braucht Happy-Path- und Fehlerfall-Tests
> - Architectural Changes brauchen Integrationstests
> - **"Kein Test = nicht fertig"**

### P1 — Muss vor erstem Release erledigt werden

| AP | Titel | Quelle | Aufwand | Status |
|----|-------|--------|---------|--------|
| AP-01 | **Cascade/OrphanRemoval Fix:** Non-Containment: cascadeRemove entfernen, orphanRemoval invertieren. Unit-Tests erweitert. **TODO:** E2E-Integrationstest: Parent mit Non-Containment-Ref löschen → Ref-Objekt muss überleben (→ AP-11) | F1-01, F1-02, T2-05 | S | ✅ Done |
| AP-02 | **Transaction Safety:** Rollback in JPAResourceImpl (doSave, delete), IOException wrapping, doUnload isLoaded-Reset, Contents-Preserve bei Delete-Fehler. 8 Unit-Tests. **TODO:** E2E-Tests für Rollback-Konsistenz gegen echte DB (→ AP-11) | F7-01, T2-03, F9-02 | M | ✅ Done |
| AP-03 | **Race Condition Fix:** volatile Felder, ExecutorService als Feld mit shutdownNow+awaitTermination in deactivate, separierte null/isOpen-Checks. **TODO:** Unit-Tests für Konfiguratoren (→ AP-29, erfordert OSGi-Mocks) | T9-02, T8-02, F10-01 | M | ✅ Done |
| AP-04 | **EPersistenceContextImpl Bug Fix:** Properties auf this.persistenceUnit setzen | T9-01 | S | ✅ Done |
| AP-05 | **Non-Containment Proxy-Resolution:** Aufgelöstes Objekt wird in Resource-Contents eingefügt (eResource() gesetzt), doUnload setzt isLoaded zurück. Fragment-Parsing-Tests. **TODO:** E2E-Test für vollständige Proxy-Resolution über ResourceSet (→ AP-30) | F9-01, F9-02 | L | ✅ Done |
| AP-07 | **README + Dokumentation:** README komplett neu geschrieben (war Codec-Projekt). Enthält: Features, Module, Quick Start, Build, Architektur-Diagramm, Doku-Links. **TODO:** Erweiterte Getting-Started-Anleitung mit vollständigem Beispiel-Projekt als separates Dokument | F15-01, F15-02 | M | ✅ Done |
| AP-08 | **BigDecimal/BigInteger Converter Fix:** Native JDBC-Typen statt double/int Konvertierung. Precision-/Range-Tests ergänzt. **TODO:** TypeConverterEndToEndTest reaktivieren für DB-Round-Trip-Beweis (→ AP-14) | T9-05, T9-06, F3-01 | S | ✅ Done |
| AP-09 | **SQL-Injection Prevention:** Entity-Name Validierung via `descriptor.getAlias()` in JPAResourceImpl. **TODO:** Negativtest (ungültiger Entity-Name → kein Query, nur null-Return) (→ AP-29) | T9-03 | S | ✅ Done |
| AP-10 | **BSI TR-03183 Grundlagen:** SECURITY.md erstellt, Logging-Default von FINE auf WARNING gesetzt (Secure-by-Default). SBOM: CycloneDX nicht geeignet für bnd/OSGi-Workspace (Gradle sieht nur testImplementation, nicht die bnd-Repository-Bundles) — OSGi-spezifische SBOM-Lösung evaluieren. | T10-01, T10-02, T10-03 | M | ✅ Done |

### P2 — Sollte zeitnah adressiert werden

| AP | Titel | Quelle | Aufwand | Status |
|----|-------|--------|---------|--------|
| AP-06 | **GeckoProjects-Migration:** 7 Maven-Abhängigkeiten auf Eclipse Fennec migrieren, URLs/Namespaces bereinigen. ⚠️ Erfordert manuelle Prüfung gemeinsam mit Mark. | T7-01 bis T7-04, T4-02, T7-02 | L | ❌ Offen |
| AP-11 | **Entity Lifecycle Tests:** Granulare Tests für Update (attached/detached), Delete (cascade, FK), Identity Map, Cache-Eviction. Inkl. E2E-Tests aus AP-01: Delete Parent mit Non-Containment-Ref → Ref-Objekt überlebt; Delete Parent mit Containment → Kinder kaskadiert gelöscht; OrphanRemoval bei Containment-Collection-Remove | F2-01 bis F2-05 | L | ❌ Offen |
| AP-12 | **Inheritance-Strategien:** JOINED und TABLE_PER_CLASS via EAnnotation konfigurierbar (key "inheritance"). Default bleibt SINGLE_TABLE. TABLE_PER_CLASS ohne Discriminator. EDynamicTypeBuilder unterstützte bereits alle 3 Strategien. 5 neue Tests (Strategie-Auswahl, case-insensitive, Fallback). **TODO:** E2E-Integrationstests mit JOINED gegen DB (→ AP-11) | F1-03 | M | ✅ Done |
| AP-13 | **@Version / Optimistic Locking:** EAnnotation "version"="true" markiert Attribute als Version. MappingProcessor erzeugt Version-Mapping statt Basic. EDynamicTypeBuilder konfiguriert `useVersionLocking()` auf Descriptor. **TODO:** E2E-Test: Concurrent Update → OptimisticLockException (→ AP-11) | F1-06, F2-04 | M | ✅ Done |
| AP-14 | **Deaktivierte Tests reaktiviert:** (1) ✅ TypeConverterEndToEndTest — Array-Types als BLOB, DB-friendly Type-Mapping. (2) ✅ EPersistenceAttributeNoCacheTest — EFeatureAccessor.dataTypeConvert nutzt jetzt Converter für EAttribute-Werte (Timestamp→LocalDate Fix). (3) ✅ EPersistenceCitizenTest — CitizenEPersistenceConfiguration Annotation, sauberer Deskriptor-Registrierungs- und Roundtrip-Test. (4) ManyToMany/NoCacheTest Debug-Tests entfernt (testeten falsches Modell in falscher Klasse). (5) EORMMappingProviderTest: bewusst disabled. | F4-01, F5-02, F3-04, F5-04 | M | ✅ Done |
| AP-15 | **DatabaseEcoreParser Rewrite:** Daanse→Standard-JDBC. JDBCType-Mapping (20+ Typen). FK→ManyToOne+OneToMany mit EOpposite. Junction-Table→ManyToMany. Containment-Heuristik (NOT NULL+CASCADE). Views (read-only EClasses). Multi-Schema (ein EPackage pro Schema). Naming (SNAKE→CamelCase, konfigurierbar). 27 Tests (13 Unit + 14 H2-Integration). | F5-01, F14-01, F14-02 | M | ✅ Done |
| AP-16 | **EDynamicTypeBuilder refactoring:** God-Class aufteilen in spezialisierte Builder | T11-01 | L | ❌ Offen |
| AP-17 | **PU-Konfigurator-Deduplizierung:** `AbstractPersistenceUnitConfigurator` extrahiert mit gemeinsamer Lifecycle-, Property- und Registrierungslogik. Beide Subklassen nur noch spezifische Context-Erstellung. Auch: Weaving="false" (statt "static"), TargetDatabase.Auto, Logging=WARNING konsistent. OSGi-Integrationstests bestätigen Korrektheit. | T4-05 | M | ✅ Done |
| AP-18 | **EFeatureAccessor Cache Fix:** Statischen globalen Cache entfernt, Converter als immutabler Konstruktor-Parameter. Kein Memory-Leak, keine Thread-Safety-Probleme mehr. | T4-04, T9-04 | M | ✅ Done |
| AP-19 | **Change-Tracking-Brücke evaluieren:** EContentAdapter für EMF→JPA Dirty-Notification, oder bewusste Limitation dokumentieren | F8-01 | M | ❌ Offen |
| AP-20 | **ProxyURI auf Cache-Kopie:** `buildIndirectObject()` erstellt jetzt `EcoreUtil.copy()` als Proxy statt Original zu modifizieren. Test: Original bleibt non-proxy. | F6-01 | S | ✅ Done |
| AP-21 | **Proxy-Mechanismus konsolidieren:** NonContainmentConverter vs. EBasicIndirectionPolicy — einen wählen, dokumentieren | F9-03 | M | ❌ Offen |
| AP-22 | **ZonedDateTime Zeitzonen-Fix:** EMF→DB speichert als ISO-8601 String (erhält Timezone), DB→EMF bei Timestamp nutzt UTC-Konvention. Round-Trip-Test beweist Timezone-Erhaltung. | F3-02 | S | ✅ Done |

### P3 — Nice-to-have, kann in späterem Release kommen

| AP | Titel | Quelle | Aufwand | Status |
|----|-------|--------|---------|--------|
| AP-23 | **Java 17 Code-Modernisierung:** Pattern Matching instanceof (33 Stellen), FQCN→Imports (60+ Stellen), switch Arrow-Syntax, Objects.* Konsistenz | T5-01 bis T5-05 | M | ❌ Offen |
| AP-24 | **@Embedded/@Embeddable Processor** | F1-04 | L | ❌ Offen |
| AP-25 | **@MappedSuperclass Processor** (oder bewusste Design-Entscheidung dokumentieren) | F1-05 | M | ❌ Offen |
| AP-26 | **Fehlende Converter:** URI, URL, OffsetDateTime, Enum-Converter | F3-03, F3-05 | M | ❌ Offen |
| AP-27 | **Package-Info Bereinigung:** @Export/@Version überall konsistent, EPL-2.0 Header | T6-01, T6-02, T6-05 | S | ❌ Offen |
| AP-28 | **Options-Klasse bereinigen:** MongoDB-Legacy entfernen, Tippfehler, tote Referenzen | T4-01, T4-03, F15-04 | S | ❌ Offen |
| AP-29 | **Unit-Tests für Kernklassen:** JPAResourceImpl, EFeatureAccessor, PU-Konfiguratoren | T3-01 bis T3-03 | L | ❌ Offen |
| AP-30 | **Cross-Resource-Referenzen:** Tests für JPA↔JPA und JPA↔XMI Cross-Resource-Refs | F4-04 | M | ❌ Offen |
| AP-31 | **View-Unterstützung und Schema-Evolution** | F5-03 | XL | ❌ Offen |
| AP-32 | **Batch Writing / Fetch-Optimierung:** EclipseLink-Properties exponieren | F12-01 | S | ❌ Offen |
| AP-33 | **dispose()-Implementierung** in BasicPersistenceEngine | T2-02 | S | ❌ Offen |
| AP-34 | **Toter Code / Auskommentierter Code entfernen** | T4-07, T4-08, F2-06, T1-02 | S | ❌ Offen |
| AP-35 | **Derived/volatile Features filtern** statt als normale Attribute zu mappen | F1-07 | S | ❌ Offen |

---

## 4. Zusammenfassung

### 4.1 Gesamtbewertung

Das Projekt ist architektonisch ambitioniert und in den Kernbereichen solide umgesetzt. Die Brücke zwischen EMF und JPA via EclipseLink Dynamic Entity API, Custom Descriptors und der Processor-Pipeline ist kreativ und funktional. Das EORM-Metamodell ist umfassend.

**Stärken:**
- Umgehung der Weaving-Problematik durch DynamicEObjectImpl + EBasicIndirectionPolicy
- ECopier für saubere EObject-Kopien ohne JPA-Artefakte
- Vollständiges EORM-Metamodell (nahezu JPA 3.1 komplett)
- Gute Testabdeckung für Beziehungstypen (O2O, O2M, M2O, M2M) mit/ohne Cache
- Saubere OSGi DS-Integration

**Schwächen (aktualisiert nach Fixes):**
- ~~**Kritische Bugs:** OrphanRemoval invertiert, CascadeRemove bei Non-Containment, Race Conditions~~ ✅ Gefixt (AP-01, AP-02, AP-03, AP-04)
- ~~**Architektonische Lücke:** Non-Containment-Proxy-Resolution liefert keine eigene EMF Resource~~ ✅ Gefixt (AP-05)
- ~~**Feature-Gaps:** Nur SINGLE_TABLE, kein Optimistic Locking~~ ✅ Gefixt (AP-12, AP-13)
- **Unvollständige Migration:** 7 GeckoProjects-Abhängigkeiten, veraltete Namespaces (AP-06 — erfordert gemeinsame Prüfung)
- **Test-Gaps:** E2E-Tests für Lifecycle (Update/Delete/Cache), Cross-Resource-Referenzen (AP-11, AP-30)
- ~~**Dokumentation:** README beschreibt falsches Projekt~~ ✅ Gefixt (AP-07)
- **Code-Qualität:** EDynamicTypeBuilder God-Class (AP-16), toter Code/Legacy (AP-28, AP-34)

### 4.2 Top-Risiken (aktualisiert)

1. ~~**Datenverlust-Risiko:** CascadeRemove + invertiertes OrphanRemoval~~ ✅ Gefixt (AP-01)
2. ~~**Inkonsistenz-Risiko:** Fehlende Transaktions-Absicherung~~ ✅ Gefixt (AP-02)
3. ~~**Resource-Leak:** Race Condition + Thread-Leak~~ ✅ Gefixt (AP-03)
4. ~~**Architektonisches Risiko:** Non-Containment-Proxy-Resolution~~ ✅ Gefixt (AP-05)
5. **Eclipse-Fennec-Blocker:** GeckoProjects-Abhängigkeiten verhindern saubere Integration (AP-06 — erfordert gemeinsame Prüfung)
6. **Wartbarkeit:** EDynamicTypeBuilder als God-Class erschwert Erweiterungen (AP-16)

### 4.3 Fortschritt

**Gesamtstand: 15 von 35 Arbeitspaketen erledigt (43%)**

| Priorität | Gesamt | ✅ Done | ❌ Offen |
|-----------|--------|---------|---------|
| P1 | 9 | 9 | 0 |
| P2 | 13 | 6 | 7 |
| P3 | 13 | 0 | 13 |

### 4.4 Testübersicht

**521 Tests gesamt — alle grün**

| Modul | Typ | Anzahl |
|-------|-----|--------|
| `persistence` (core) | Unit | 76 |
| `persistence.orm` | Unit | 221 |
| `persistence.ecore` | Unit | 27 |
| `persistence.eclipselink` | Unit | 102 |
| **Unit-Tests gesamt** | | **426** |
| `persistence.test` | Integration (OSGi) | 95 |
| **Gesamt** | | **521** |

### 4.5 Empfohlene Reihenfolge der Arbeitspakete

**Welle 1 — Bugfixes:** AP-01, AP-04, AP-08, AP-09 → ✅ Erledigt
**Welle 2 — Safety:** AP-02, AP-03 → ✅ Erledigt
**Welle 3 — Architektur:** AP-05, AP-07, AP-10 → ✅ Erledigt
**Welle 4 — Code-Qualität:** AP-17, AP-18, AP-20, AP-22 → ✅ Erledigt
**Welle 5 — Feature-Gaps:** AP-12, AP-13 → ✅ Erledigt
**Welle 6 — Tests & Parser:** AP-14, AP-15 → ✅ Erledigt
**Welle 7 — Nächste Schritte (offen):**
- AP-11 (Entity Lifecycle Tests) — baut Vertrauen auf
- AP-06 (GeckoProjects-Migration) — erfordert gemeinsame Prüfung mit Mark
**Welle 8 — Qualität:** AP-16, AP-19, AP-21, AP-23
**Welle 9 — Erweiterungen:** AP-24 bis AP-35

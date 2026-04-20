# Eclipse Fennec Persistence JPA — Structured Review

> Lebendes Arbeitsdokument: Review-Kriterien, Ergebnisse und abgeleitete Arbeitspakete.
> Stand: 2026-04-14 | Review durchgeführt: 2026-04-14 | Letzte Aktualisierung: 2026-04-16

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
| T2-01 | T2 | ~~`dispose()` in `BasicPersistenceEngine` ist leer — kein Cleanup~~ **FIXED (AP-33)** — `dispose()` leert mergedOptions, engineProperties und setzt resource=null. Javadoc auf PersistenceEngine.dispose() verbessert. 4 Unit-Tests. | `BasicPersistenceEngine.java` | ~~Ressourcen-Cleanup implementieren~~ |
| T2-02 | T2 | ~~`CascadeType.REMOVE` bei Non-Containment (`OneToManyProcessor`)~~ **FIXED (AP-01)** — Cascade für Non-Containment korrigiert: persist/detach/refresh, kein remove. OrphanRemoval nur bei Containment. | `OneToManyProcessor.java:50-68` | ~~Cascade-Logik korrigieren~~ |
| T9-01 | T9 | ~~`EPersistenceContextImpl` setzt Properties auf `persistenceUnit`, nicht `this`~~ **FIXED (AP-04)** | `EPersistenceContextImpl.java:74-76` | ~~Properties auf this setzen~~ |
| T9-02 | T9 | ~~Race Condition in `PersistenceUnitConfigurator.activate()` und `EntityMappingPersistenceUnitConfigurator`~~ **FIXED (AP-03)** — volatile fields, ExecutorService als Feld, proper shutdown | `PersistenceUnitConfigurator.java:125-143` | ~~volatile + shutdown~~ |
| T9-03 | T9 | ~~SQL-Injection in `JPAResourceImpl.doLoad()`: Entity-Name wird unkontrolliert in JPQL eingebaut~~ **FIXED (AP-09)** — Entity-Name über `descriptor.getAlias()` validiert | `JPAResourceImpl.java:85-91` | ~~Entity-Name validieren~~ |
| T9-04 | T9 | ~~`EFeatureAccessor.accessorMap`: Statische ConcurrentHashMap, fehlende Eviction, Cross-PU-Konflikte bei unterschiedlichen Convertern~~ **FIXED (AP-18)** — Statischer Cache entfernt, Converter als Konstruktor-Parameter | `EFeatureAccessor.java:45` | ~~Cache entfernen, Converter per Konstruktor~~ |
| T9-05 | T9 | ~~`BigIntegerInternalConverter`: `intValue()` — Silent Truncation bei > 32-bit~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:309,315` | ~~`BigInteger` nativ an JDBC durchreichen~~ |

#### Major

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T1-01 | T1 | ~~`PersistenceEngine.doSave()` Javadoc sagt "one or more" EObjects~~ Nicht mehr zutreffend: `doSave()` existiert nicht mehr im Interface (wurde in früheren APs bereinigt) | `PersistenceEngine.java` | ~~Entfällt~~ |
| T1-02 | T1 | ~~`EPersistenceContext`: mehrere Methoden nur `return null`~~ Nicht mehr zutreffend: `createNamedQuery`/`getMappedSuperclassNames` existieren nicht mehr in der aktuellen Implementierung (wurden in früheren APs bereinigt) | `EPersistenceContextImpl.java` | ~~Entfällt~~ |
| T2-03 | T2 | ~~`JPAResourceImpl.doSave()` und `delete()`: Kein Rollback bei Exception~~ **FIXED (AP-02)** — try/catch/rollback + IOException wrapping + 8 Unit-Tests | `JPAResourceImpl.java:102-123` | ~~try/catch mit `rollback()`~~ |
| T2-04 | T2 | ~~`JPAResourceImpl.getEngine()` gibt hart `null` zurück — Vertragsbruch des Interfaces~~ **FIXED (AP-34)** — Wirft jetzt `UnsupportedOperationException` mit Begründung | `JPAResourceImpl.java` | ~~`UnsupportedOperationException`~~ |
| T2-05 | T2 | ~~`BaseReferenceProcessor.calculateCascadeType()` setzt `cascadeRemove` für Non-Containment~~ **FIXED (AP-01)** | `BaseReferenceProcessor.java:153-165` | ~~Non-Containment: persist+detach+refresh, KEIN remove~~ |
| T2-06 | T2 | ~~`PersistenceUnitConfigurator` hardcodiert `H2Platform`~~ **FIXED (AP-17)** — `AbstractPersistenceUnitConfigurator` nutzt `TargetDatabase.Auto` als Default | `AbstractPersistenceUnitConfigurator.java` | ~~`TargetDatabase.Auto`~~ |
| T4-01 | T4 | ~~`Options.CAP_USE_TIMESTAMP` und `CAP_TIMESTAMP_FIELD_NAME` haben identischen Wert `"TIMESTMAP_FIELD_NAME"` (Tippfehler)~~ **FIXED (AP-28)** — Beide Konstanten entfernt (unbenutzt) | `Options.java` | ~~Tippfehler korrigieren~~ |
| T4-02 | T4 | ~~`Keywords.CAPABILITY_NAMESPACE` enthält `"org.eclipse.fennec.persistence.old.old"`~~ Bereits bereinigt (AP-06) — aktueller Wert: `"org.eclipse.fennec.persistence"` | `Keywords.java:57` | ~~Entfällt~~ |
| T4-03 | T4 | ~~`Options`-Javadoc referenziert nicht-existente Typen: `DBObjectBuilderImpl`, `NativeQueryEngine`, MongoDB-Formulierungen~~ **FIXED (AP-28)** — Options komplett bereinigt (525→105 Zeilen) | `Options.java` | ~~Javadoc aktualisieren~~ |
| T4-04 | T4 | ~~`EFeatureAccessor.accessorMap`: Statische ConcurrentHashMap ohne Eviction~~ **FIXED (AP-18)** — Statischer Cache entfernt, Converter als Konstruktor-Parameter | `EFeatureAccessor.java` | ~~Cache entfernen~~ |
| T4-05 | T4 | ~~Code-Duplikation: beide PU-Konfiguratoren nahezu identisch~~ **FIXED (AP-17)** — `AbstractPersistenceUnitConfigurator` extrahiert | `spi/AbstractPersistenceUnitConfigurator.java` | ~~Basisklasse extrahieren~~ |
| T4-06 | T4 | ~~`EDynamicTypeContext` erbt von `ConcurrentHashMap` — Anti-Pattern, exponiert alle Map-Methoden~~ **FIXED (AP-34)** — Komposition: `private final ConcurrentHashMap` als Feld, nur benötigte Methoden (`get`, `put`, `computeIfAbsent`, `remove`, `clear`) exponiert. Bug in `remove()` behoben (entfernte aus falscher Map). 8 Unit-Tests. | `EDynamicTypeContext.java` | ~~Komposition statt Vererbung~~ |
| T8-01 | T8 | `JPAResourceImpl.doLoad()`: `SELECT e FROM EntityName e` ohne Paginierung — OOM bei großen Tabellen (→ AP-32) | `JPAResourceImpl.java:85-93` | Paginierung via setFirstResult/setMaxResults |
| T8-02 | T8 | ~~`Executors.newSingleThreadExecutor()` wird nie heruntergefahren~~ **FIXED (AP-03)** — executor als Feld, shutdownNow+awaitTermination in deactivate | `EntityMappingPersistenceUnitConfigurator.java`, `PersistenceUnitConfigurator.java` | ~~ExecutorService shutdown~~ |
| T9-06 | T9 | ~~`BigDecimalInternalConverter`: `doubleValue()` — Präzisionsverlust~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:291,299` | ~~`BigDecimal` nativ an JDBC durchreichen~~ |
| T10-01 | T10 | Keine SBOM-Generierung (CycloneDX/SPDX) im Build — TR-03183-2 nicht erfüllt | `build.gradle` | CycloneDX-Gradle-Plugin integrieren |
| T10-02 | T10 | ~~Kein SECURITY.md, keine Vulnerability-Disclosure-Policy — TR-03183-1~~ **FIXED (AP-10)** | Projekt-Root | ~~SECURITY.md erstellen~~ |
| T10-03 | T10 | ~~Default-Logging auf `FINE`~~ **FIXED (AP-10, AP-17)** — Default auf `WARNING` in `AbstractPersistenceUnitConfigurator` | `AbstractPersistenceUnitConfigurator.java` | ~~Default auf WARNING~~ |
| T11-01 | T11 | ~~SRP: `EDynamicTypeBuilder` ist God-Class (700+ Zeilen, 7 Verantwortlichkeiten)~~ **FIXED (AP-16)** — Delegate-Pattern: 1061→265 Zeilen, 4 Konfiguratoren extrahiert | `EDynamicTypeBuilder.java` | ~~Aufteilen~~ |
| T11-02 | T11 | ~~DIP: H2Platform hardcodiert~~ **FIXED (AP-17)** — `TargetDatabase.Auto` als Default in Basisklasse, per Property überschreibbar | `AbstractPersistenceUnitConfigurator.java` | ~~Konfigurierbare Property~~ |

#### Major (T5 — Code-Stil, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T5-01 | T5 | ~~33 Stellen `instanceof` ohne Pattern Variable~~ **FIXED (AP-23)** — 18 Stellen modernisiert | Diverse | ~~Pattern Matching~~ |
| T5-02 | T5 | ~~60+ Stellen mit FQCN statt Import~~ **FIXED (AP-23)** — ~25 Stellen bereinigt | Diverse | ~~Imports verwenden~~ |
| T5-03 | T5 | Switch-Statements mit traditioneller Syntax wo Arrow-Syntax klarer wäre — verbleibend | `DatabaseEcoreParser.java` | Arrow-Syntax verwenden |
| T5-04 | T5 | `Objects.isNull()` / `Objects.nonNull()` inkonsistent mit `== null` / `!= null` gemischt — verbleibend | Diverse | Einheitlich, bevorzugt `Objects.*` |
| T5-05 | T5 | Fehlende statische Imports für häufig genutzte Utilities — verbleibend | Diverse | `import static java.util.Objects.*` |

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
| T6-05 | T6 | ~~`cnf/ext/libraries.bnd` referenziert `dimc-ossrh-releases` Nexus~~ **FIXED (AP-06)** — `cnf/ext/` komplett entfernt | `cnf/ext/` | ~~Entfernt~~ |

#### Major (T7 — GeckoProjects-Abhängigkeiten, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T7-01 | T7 | ~~`org.gecko.emf.osgi` in `bnd.bnd` Import-Packages~~ **FIXED (AP-06)** — Runtime-Code auf Fennec umgestellt | Diverse `bnd.bnd` | ~~Fennec-Namespace~~ |
| T7-02 | T7 | ~~`org.geckoprojects.emf` Maven-Coordinates in `cnf/ext/libraries.maven`~~ **FIXED (AP-06)** — `cnf/ext/` komplett entfernt | Build-Config | ~~Fennec Maven Coordinates~~ |
| T7-03 | T7 | ~~Java-Imports: `org.gecko.emf.osgi.*` in 8 Quelldateien~~ **FIXED (AP-06)** — Auskommentierte Imports entfernt | Diverse `.java` | ~~Fennec-Imports~~ |
| T7-04 | T7 | ~~`org.gecko.emf.osgi.example.model.basic` Testabhängigkeit~~ **FIXED (AP-06)** — Auf Fennec-Version umgestellt | `bnd.bnd`, `test.bndrun` | ~~Fennec-Version~~ |

#### Minor

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T4-07 | T4 | ~~Auskommentierter Code in `EDynamicTypeBuilder` (>50 Zeilen)~~ **FIXED (AP-16)** — Kompletter Rewrite via Delegate-Pattern | `EDynamicTypeBuilder.java` | ~~Entfernt~~ |
| T4-08 | T4 | ~~Auskommentierter Code in `EPersistenceCitizenTest` (deaktivierte Tests)~~ **FIXED (AP-34)** — Debug-Methode `testConverterDebugOrig` (auskommentiertes `@Test`, `Thread.sleep`) entfernt. `@Disabled testEMFAvailable` beibehalten (notwendig: ererbter Test funktioniert nicht mit Citizen-Modell). Unbenutzte Imports bereinigt. | `EPersistenceCitizenTest.java` | ~~Entfernt~~ |

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
| F1-05 | F1 | ~~@MappedSuperclass: EORM-Metamodell vorhanden, aber nicht implementiert — abstrakte EClasses werden immer als Entity gemappt~~ **Evaluiert (AP-25), Entscheidung revidiert nach AP-43**: wird gemeinsam mit TPC-Fix in AP-44 implementiert (80 % gemeinsame Mapping-Pipeline). Konzept inkl. Revision: `docs/AP-25_MappedSuperclass-Decision.md` § 6 | — | ~~Design-Entscheidung dokumentieren oder implementieren~~ — jetzt: implementieren (AP-44) |
| F1-06 | F1 | ~~@Version (Optimistic Locking): Runtime-Cast-Fehler `ValuesAccessor` → `DynamicEntityImpl`~~ **FIXED (AP-13 + AP-36)** — Lock-Mapping-Accessor durch `EFeatureAccessor` ersetzt, Version-Typ aus EStructuralFeature abgeleitet. 2 E2E-Tests aktiv. | `EDynamicTypeBuilder.java` | ~~Custom LockingPolicy~~ |
| F1-07 | F1 | ~~Derived/volatile EStructuralFeatures werden als normale Attribute gemappt — erzeugt DDL-Fehler wenn Feature keinen Backing-Store hat~~ **FIXED (AP-35)** — `isDerived()` und `isVolatile()` in allen 4 Filter-Stufen des MappingProcessor ergänzt | `MappingProcessor.java` | ~~Derived/volatile Features filtern~~ |
| F2-01 | F2 | ~~Kein Test für Entity-Update (merge detached)~~ **FIXED (AP-11)** — `testUpdateAttribute` | Tests | ~~Update-Tests~~ |
| F2-02 | F2 | ~~Kein Test für Entity-Remove mit Cascade-Verhalten~~ **FIXED (AP-11)** — `testDeleteParentContainmentCascade`, `testDeleteParentNonContainmentRefSurvives` | Tests | ~~Delete-Cascade-Tests~~ |
| F2-03 | F2 | Kein Test für Detach/Reattach-Lifecycle — verbleibend (→ AP-29) | Tests | Detach-Tests |
| F2-04 | F2 | ~~Kein Optimistic-Locking-Test~~ **FIXED (AP-36)** — `testOptimisticLockingConflict`, `testVersionIncrementOnUpdate` | Tests | ~~Version-Tests~~ |
| F2-05 | F2 | Kein Test für Cache-Eviction / Second-Level-Cache-Konsistenz (→ AP-29) | Tests | Cache-Tests |
| F2-06 | F2 | EclipseLink `UnitOfWork.setShouldNewObjectsBeCached(true)` — nie konfigurierbar (→ AP-32) | `EntityManagerFactoryConfigurator.java:170` | Property exponieren oder entfernen |
| F3-01 | F3 | ~~`BigDecimalInternalConverter`: Doppelte Konvertierung (BigDecimal → double → BigDecimal) vernichtet Präzision~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java` | ~~BigDecimal nativ durchreichen~~ |
| F3-02 | F3 | ~~`ZonedDateTimeInternalConverter`: Zeitzonen-Information geht bei Timestamp-Roundtrip verloren~~ **FIXED (AP-22)** — ISO-8601-String-Speicherung erhält Zeitzonen | `ComprehensiveTypeConverter.java` | ~~Als ISO-8601 String speichern~~ |
| F3-03 | F3 | ~~Fehlende Converter für URI, URL, OffsetDateTime~~ **FIXED (AP-26)** — 3 InternalConverter in ComprehensiveTypeConverter, 8 Tests | `ComprehensiveTypeConverter.java` | ~~Converter implementieren~~ |
| F3-04 | F3 | ~~EEnum-Converter: `findConverter` gibt null für EEnum zurück — kein "enum"-Key registriert~~ **FIXED (AP-14)** — Enum-Handling über EclipseLink-Mappings, kein separater Converter nötig | `ComprehensiveTypeConverter.java` | ~~Architektur-Entscheidung dokumentiert~~ |
| F3-05 | F3 | Kein Converter für `EEnumLiteral` ↔ String/Ordinal — Enum-Persistierung nutzt EclipseLink-Mapping direkt | — | Dokumentieren als bewusste Entscheidung |
| F4-01 | F4 | ~~Deaktivierte Tests in `EPersistenceCitizenTest`, `TypeConverterEndToEndTest`~~ **FIXED (AP-14)** — TypeConverterEndToEndTest und CitizenTest reaktiviert | Tests | ~~Tests analysieren und reaktivieren~~ |
| F4-02 | F4 | ~~`JPAResourceImpl.doLoad()` setzt `isLoaded = false` auch bei Erfolg, dann `isLoaded = true` — Race Window~~ **FIXED (AP-37)** — `isLoaded` wird erst nach erfolgreichem `doLoad()` gesetzt | `JPAResourceImpl.java` | ~~`isLoaded` erst nach erfolgreichem Load setzen~~ |
| F4-03 | F4 | ~~`JPAResourceImpl.doLoad()`: Neue Objekte werden ge-`add`ed ohne vorherige Contents zu leeren~~ **FIXED (AP-37)** — `getContents().clear()` vor Query (mit Guard wegen EMF `ContentsEList.didClear()`) | `JPAResourceImpl.java` | ~~Contents leeren oder Duplikat-Check~~ |
| F4-04 | F4 | Cross-Resource-Referenzen: Keine Tests für JPA↔JPA oder JPA↔XMI Cross-Resource Szenarien | Tests | Cross-Resource-Tests |
| F5-01 | F5 | ~~`DatabaseEcoreParser` hatte Daanse/OLAP-Abhängigkeiten, nutzte nicht-standard JDBC~~ **FIXED (AP-15)** — Komplett auf Standard-JDBC umgeschrieben | `DatabaseEcoreParser.java` | ~~Standard-JDBC nutzen~~ |
| F5-02 | F5 | ~~Keine Foreign-Key → Referenz-Auflösung in `DatabaseEcoreParser`~~ **FIXED (AP-15)** — FK→ManyToOne+OneToMany mit EOpposite, Junction→ManyToMany | `DatabaseEcoreParser.java` | ~~FK-Analyse implementieren~~ |
| F5-03 | F5 | Keine Schema-Evolution-Unterstützung — DDL nur `create-or-extend-tables` | — | Schema-Migration evaluieren (Flyway?) |
| F5-04 | F5 | ~~`DatabaseEcoreParser`: Kein Multi-Schema-Support — alle Tabellen in einem EPackage~~ **FIXED (AP-15)** — Ein EPackage pro Schema | `DatabaseEcoreParser.java` | ~~Schema-separierte EPackages~~ |
| F6-01 | F6 | ~~`EBasicIndirectionPolicy.buildIndirectObject()` modifiziert Original-EObject (setzt ProxyURI)~~ **FIXED (AP-20)** — Proxy wird als `EcoreUtil.copy()` erstellt, Original bleibt unverändert | `EBasicIndirectionPolicy.java` | ~~Kopie erstellen~~ |
| F8-01 | F8 | ~~Kein automatisches Change-Tracking~~ **Evaluiert (AP-19)** — Bewusste Design-Entscheidung: Explizites `merge()` via `resource.save()` beibehalten. Konzept: `docs/AP-19_Change-Tracking-Konzept.md` | — | ~~EContentAdapter-Brücke~~ |
| F9-03 | F9 | ~~Zwei parallele Proxy-Mechanismen: potenzieller Konflikt~~ **Evaluiert (AP-21)** — Kein Konflikt: `EBasicIndirectionPolicy` für FK-Relationen, `NonContainmentConverter` für String-URI-Spalten. Komplementäre Mechanismen. | — | ~~Konsolidieren~~ |
| F12-01 | F12 | Kein Batch Writing konfiguriert — jedes Persist ist ein einzelnes INSERT | EclipseLink Config | `eclipselink.jdbc.batch-writing` exponieren |
| F14-01 | F14 | ~~`DatabaseEcoreParser`: Alle `java.sql.Types` werden auf EString gemappt — kein Typ-Mapping~~ **FIXED (AP-15)** — 20+ JDBCType-Mappings implementiert | `DatabaseEcoreParser.java` | ~~JDBCType-Mapping~~ |
| F14-02 | F14 | ~~`DatabaseEcoreParser`: Primary Keys werden nicht als `eID` markiert~~ **FIXED (AP-15)** — PKs werden als `iD=true` gesetzt | `DatabaseEcoreParser.java` | ~~PK→eID~~ |
| F15-01 | F15 | ~~README beschreibt ein komplett anderes Projekt (Codec-Bibliothek)~~ **FIXED (AP-07)** | `README.md` | ~~README neu schreiben~~ |
| F15-02 | F15 | Keine Getting-Started-Anleitung, kein Beispiel-Projekt (→ AP-38) | — | Tutorial erstellen |
| F15-03 | F15 | Keine Dokumentation der Konfigurationsoptionen (`fennec.jpa.*` Properties) (→ AP-38) | — | Config-Referenz erstellen |
| F15-04 | F15 | ~~`Options`-Klasse enthält MongoDB-Legacy-Referenzen~~ **FIXED (AP-28)** — Options komplett bereinigt | `Options.java` | ~~Legacy entfernt~~ |

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
| AP-06 | **GeckoProjects-Migration:** EAnnotation-Source auf Fennec-Namespace umgestellt (`http://eclipse.org/fennec/jpa-persistence/1.0.0`). Obsolete `person.eorm` (Gecko-nsURI) gelöscht. Auskommentierte Gecko-Imports entfernt. `cnf/ext/libraries.bnd` + `cnf/ext/libraries.maven` (7 Gecko-bnd-Libraries) komplett entfernt. Build läuft ohne Gecko-Abhängigkeiten. | T7-01 bis T7-04, T4-02, T7-02 | L | ✅ Done |
| AP-11 | **Entity Lifecycle Tests:** Update-Roundtrip, Delete mit Containment-Cascade (Kinder werden gelöscht), Delete mit Non-Containment (Ref-Objekt überlebt — AP-01 E2E-Beweis). Citizen-Modell bereinigt (reservierte SQL-Wörter: Year→YearEntity, year→yearValue, key→genderId). Version-Locking-Tests vorbereitet aber `@Disabled` (→ AP-36). **Verbleibend:** Detach/Reattach, Cache-Eviction, OrphanRemoval (→ AP-11b) | F2-01 bis F2-05 | L | ✅ Done |
| AP-12 | **Inheritance-Strategien:** JOINED und TABLE_PER_CLASS via EAnnotation konfigurierbar (key "inheritance"). Default bleibt SINGLE_TABLE. TABLE_PER_CLASS ohne Discriminator. EDynamicTypeBuilder unterstützte bereits alle 3 Strategien. 5 neue Tests (Strategie-Auswahl, case-insensitive, Fallback). **TODO:** E2E-Integrationstests mit JOINED gegen DB (→ AP-11) | F1-03 | M | ✅ Done |
| AP-13 | **@Version / Optimistic Locking (Mapping-Pipeline):** EAnnotation "version"="true" markiert Attribute als Version. MappingProcessor erzeugt Version-Mapping statt Basic. EDynamicTypeBuilder konfiguriert `useVersionLocking()` auf Descriptor. Runtime-Fix via AP-36: Lock-Mapping-Accessor gepatcht. E2E-Tests aktiv (Conflict + Version-Increment). | F1-06, F2-04 | M | ✅ Done |
| AP-14 | **Deaktivierte Tests reaktiviert:** (1) ✅ TypeConverterEndToEndTest — Array-Types als BLOB, DB-friendly Type-Mapping. (2) ✅ EPersistenceAttributeNoCacheTest — EFeatureAccessor.dataTypeConvert nutzt jetzt Converter für EAttribute-Werte (Timestamp→LocalDate Fix). (3) ✅ EPersistenceCitizenTest — CitizenEPersistenceConfiguration Annotation, sauberer Deskriptor-Registrierungs- und Roundtrip-Test. (4) ManyToMany/NoCacheTest Debug-Tests entfernt (testeten falsches Modell in falscher Klasse). (5) EORMMappingProviderTest: bewusst disabled. | F4-01, F5-02, F3-04, F5-04 | M | ✅ Done |
| AP-15 | **DatabaseEcoreParser Rewrite:** Daanse→Standard-JDBC. JDBCType-Mapping (20+ Typen). FK→ManyToOne+OneToMany mit EOpposite. Junction-Table→ManyToMany. Containment-Heuristik (NOT NULL+CASCADE). Views (read-only EClasses). Multi-Schema (ein EPackage pro Schema). Naming (SNAKE→CamelCase, konfigurierbar). 27 Tests (13 Unit + 14 H2-Integration). | F5-01, F14-01, F14-02 | M | ✅ Done |
| AP-16 | **EDynamicTypeBuilder Refactoring:** God-Class (1061 Zeilen) via Delegate-Pattern in Orchestrator (265 Zeilen) + 4 Konfiguratoren aufgeteilt: `IdConfigurator`, `AttributeConfigurator`, `CollectionConfigurator`, `ReferenceConfigurator`. Package-privates `BuilderOperations`-Interface für Callbacks. Keine API-Änderung. 24 neue Unit-Tests (ID/Composite, Version-Attribute, Type-Mapping). | T11-01 | L | ✅ Done |
| AP-17 | **PU-Konfigurator-Deduplizierung:** `AbstractPersistenceUnitConfigurator` extrahiert mit gemeinsamer Lifecycle-, Property- und Registrierungslogik. Beide Subklassen nur noch spezifische Context-Erstellung. Auch: Weaving="false" (statt "static"), TargetDatabase.Auto, Logging=WARNING konsistent. OSGi-Integrationstests bestätigen Korrektheit. | T4-05 | M | ✅ Done |
| AP-18 | **EFeatureAccessor Cache Fix:** Statischen globalen Cache entfernt, Converter als immutabler Konstruktor-Parameter. Kein Memory-Leak, keine Thread-Safety-Probleme mehr. | T4-04, T9-04 | M | ✅ Done |
| AP-19 | **Change-Tracking-Brücke evaluiert:** EContentAdapter-Ansatz analysiert und verworfen (Notification-Overhead beim Laden, Detached-Entity-Problem, Non-Containment-Lücke, Paradigmen-Mismatch). Bewusste Design-Entscheidung: Explizites `merge()` via `resource.save()` beibehalten — Standard-JPA-Pattern, performant, vollständig getestet. Konzeptdokument: `docs/AP-19_Change-Tracking-Konzept.md` | F8-01 | M | ✅ Done |
| AP-20 | **ProxyURI auf Cache-Kopie:** `buildIndirectObject()` erstellt jetzt `EcoreUtil.copy()` als Proxy statt Original zu modifizieren. Test: Original bleibt non-proxy. | F6-01 | S | ✅ Done |
| AP-21 | **Proxy-Mechanismus evaluiert:** Kein Konsolidierungsbedarf — zwei komplementäre Mechanismen für unterschiedliche Persistierungsstrategien: `EBasicIndirectionPolicy` für FK-basierte Referenz-Mappings (Lazy Loading via ValueHolder), `NonContainmentConverter` für String-URI-basierte Referenzen in Basic-Mappings. Siehe Analyse unten. | F9-03 | S | ✅ Done |
| AP-22 | **ZonedDateTime Zeitzonen-Fix:** EMF→DB speichert als ISO-8601 String (erhält Timezone), DB→EMF bei Timestamp nutzt UTC-Konvention. Round-Trip-Test beweist Timezone-Erhaltung. | F3-02 | S | ✅ Done |

### P1b — Neue höchste Priorität (aus aktueller Entwicklung)

| AP | Titel | Quelle | Aufwand | Status |
|----|-------|--------|---------|--------|
| AP-39 | **SINGLE_TABLE Inheritance Fix:** Zwei Bugs in `EDynamicTypeBuilder.configureInheritance()` gefixt: (1) Discriminator-Registrierung auf Root statt direktem Parent — behebt "Cannot find value in class indicator mapping". (2) `EClassDescriptor.convertClassNamesToClasses()` unterstützt jetzt auch `DynamicClassLoader` (nicht nur OSGi-Variante). ID-Mappings werden NICHT manuell kopiert — EclipseLink erbt sie automatisch via `InheritancePolicy.initialize()` (concatenateVectors). `inheritIdMappings()` entfernt. Non-OSGi-Integrationstest `InheritanceIntegrationTest` (H2, 7 Tests inkl. 3-Level-Hierarchie Persist/Find-Roundtrip). | F1-03, AP-12 | M | ✅ Done |
| AP-40 | **XMI→DB→XMI Roundtrip-Test (OSM-Modell):** Non-OSGi `OsmRoundtripTest` mit in-memory H2. **Small-Test** (500 Objekte): 5.763 Attribute deep-verglichen, 0 Mismatches. **Perf-Test** (`@Tag("perf")`, 10.225 Objekte): 102.205 Attribute deep-verglichen, 0 Mismatches. Timing: Load XMI 561ms, Persist 12.6s, Read-Back 2.0s, Deep-Compare 142ms, Write XMI 537ms. `domain_full.xmi` als `.gz` im Repo (22MB→981KB, zur Laufzeit entpackt). Reserved-Word-Handling (`user`, `natural`, `key`, `value`, `interval`) via `ExtendedMetaData`-Annotation. `domain.ecore`: `GeoFeature.id` als `iD=true`, DomainTag/WikiInfo/FieldMapping mit eigener `id`. Batch-Writing (`JDBC`, size=500) konfiguriert. | — | L | ✅ Done |
| AP-41 | **Non-OSGi-Testinfrastruktur:** `test/`-Ordner im `persistence.test`-Modul für Non-OSGi-Tests eingerichtet. `-testpath` mit H2, EclipseLink ASM/JPA/JPQL, Fennec-Modulen. Blaupause: `EDynamicPersistenceUnitInfo` + `PersistenceProvider` + `EDynamicTypeGenerator` + `EDynamicHelper.addETypes()` ohne OSGi-Container. Wiederverwendbare Basisklasse `NonOsgiPersistenceTestBase` extrahiert (Ecore-Load, H2-EMF-Bootstrap, `ConverterService`-Wiring via `DefaultConverterService`, Dynamic-Type-Registrierung, Teardown; `defaultProperties()` und `configureMapper()` als Hooks für Cache/Strict-Varianten). **Migration + Cleanup abgeschlossen**: 18 Non-OSGi-Testklassen mit 64 Tests (alle grün) decken Attribute/NoCache, O2O/NoCache, O2M/NoCache, M2M/NoCache, Inheritance, Lifecycle, CompositeId, CompositeIdEclipseLink, Citizen, GltContainment, TypeConverterEndToEnd, TypeConverterIntegration, JPAResourceIntegration und OsmRoundtripTest ab. 21 migrierte bzw. komplett auskommentierte OSGi-Testdateien gelöscht. Verbleibende OSGi-Tests (22 Tests): ServiceIsolationTest, H2DataSourceTest, DynamicModelRegistrationTest, EORMLoaderTest, EORMMappingProviderTest, copier/ECopierIntegrationTest — alle testen genuin OSGi-spezifisches Verhalten (Service-Injection, ConfigAdmin-Lifecycle, ORMMappingProvider-Service) und bleiben bewusst OSGi. Bei Non-OSGi-Tests wird das Schema immer per EclipseLink-DDL generiert, nicht per JDBC pre-seeded — damit Setup-Fehler (falsche Mappings, falsche ID-Strategie) früh auffallen. Build grün (22 OSGi + 64 Non-OSGi Tests). | T3-01 bis T3-04 | L | ✅ Done |
| AP-42 | **Detached DynamicEObjectImpl persist/merge:** `JPAResourceImpl.toManagedEntity()` konvertiert fremde `DynamicEObjectImpl`-Objekte (z.B. aus XMI geladen) zu EclipseLink-Entities via Descriptor-Lookup per `eClass().getName()` + ECopier mit Factory-Function. `JPAResourceImpl.doSave()` nutzt dies transparent. `getServer()` defensiv (gibt null bei Non-EclipseLink EMF). Durch OSM-Roundtrip-Test mit 500 Objekten E2E verifiziert. | — | M | ✅ Done |

### P3 — Nice-to-have, kann in späterem Release kommen

| AP | Titel | Quelle | Aufwand | Status |
|----|-------|--------|---------|--------|
| AP-23 | **Java 17 Code-Modernisierung:** Pattern Matching instanceof (18 Stellen in 14 Dateien modernisiert), FQCN→Imports (~25 Stellen in 7 Dateien). **Verbleibend:** switch Arrow-Syntax, Objects.* Konsistenz | T5-01 bis T5-05 | M | ✅ Done |
| AP-24 | **@Embedded/@Embeddable Processor** | F1-04 | L | ❌ Offen |
| AP-25 | **@MappedSuperclass — Design evaluiert, Entscheidung revidiert:** Erste Analyse schlug „nicht implementieren, TABLE_PER_CLASS deckt ab" vor. Nach AP-43 zeigte sich: (1) TABLE_PER_CLASS war bisher gar nicht funktional, (2) die Fix-Maschinerie dafür deckt 80 % dessen ab, was MappedSuperclass bräuchte. Revidierte Entscheidung: **implementieren, gemeinsam mit AP-44**. Konzept inkl. Revision: `docs/AP-25_MappedSuperclass-Decision.md` (§ 6). Implementierungs-Arbeit wird in AP-44 verfolgt. | F1-05 | M | ✅ Done (Analyse + Revision) |
| AP-26 | **Fehlende Converter:** `URIInternalConverter` (java.net.URI↔String), `URLInternalConverter` (java.net.URL↔String), `OffsetDateTimeInternalConverter` (java.time.OffsetDateTime↔ISO-8601-String). Alle in `ComprehensiveTypeConverter`. Enum-Converter bewusst nicht implementiert — EEnum wird bereits korrekt über `EFeatureAccessor` + `EcoreUtil.createFromString()` gehandhabt. 8 neue Tests. | F3-03, F3-05 | M | ✅ Done |
| AP-27 | **Package-Info + License-Header vereinheitlicht:** Alle 26 `package-info.java` (Nicht-generated) auf m2m-Stil umgestellt — einheitlicher Header „Copyright (c) 2026 Contributors to the Eclipse Foundation" mit Stern-Bar-Rahmen, Beitragszeile „Data In Motion Consulting - initial implementation". Doppelte Javadoc-Blöcke in 5 Dateien entfernt; 2 Dateien ohne Header (`ecore`, `eclipselink/resource`) nachgerüstet. `@Export`/`@Version`/`@RequireEMF`/`@RequireConfigurationAdmin`/`@RequireConfigurator` unverändert an ihren Stellen — OSGi-Tests bleiben grün (22 OSGi + 490 Unit/Integration). `.licenserc.yaml` aus m2m übernommen (`comment: always`, Copyright-Content 2026) plus emf-spezifische `paths-ignore`-Einträge (`.txt`, `.eorm`, `.sql`, `.xmi`, `.db`). Regular `.java`-Files hatten den neuen Header bereits. | T6-01, T6-02, T6-05 | S | ✅ Done |
| AP-28 | **Options-Klasse bereinigt:** 525→105 Zeilen. MongoDB-Legacy-Javadoc entfernt (`DBObjectBuilderImpl`, `NativeQueryEngine`, MongoDB-Terminologie). Tippfehler `TIMESTMAP_FIELD_NAME` behoben. 17 unbenutzte Konstanten + 4 tote Utility-Methoden entfernt. Behalten: `READ_FILTER_ECLASS`, `OPTION_TABLE_NAME` + genutzte Hilfsmethoden. | T4-01, T4-03, F15-04 | S | ✅ Done |
| AP-29 | **Unit-Tests für Kernklassen:** `JPAResourceImpl` +16 Tests (count/exist, doLoad-Guards, convertId Integer/Long/String-Fallback, cache-new-objects-Options inkl. UoW-Unwrap-Fehlerpfad, close, updateDefaultOptions). `EFeatureAccessor` via `AccessorIndirectionTest` +7 Tests (non-EObject set-noop, Default-Value skip, Many-valued Collection-Conversion, EReference mit Converter get+set, EAttribute mit Converter, Fast-Path ohne Converter). `AbstractPersistenceUnitConfigurator` +6 Tests (empty/ignored-only Maps, setEMFProperties Defaults, No-Override bei DDL_GENERATION/TARGET_DATABASE, null-Guard). Neu: `PersistenceUnitConfiguratorTest` 6 Tests für `createPersistenceContext`-Branches (PU-File-Pfad, mapping+name, ConfigurationException bei missing name/mappingFile/invalid URIs) — `createPersistenceContext` Visibility auf package-private. Neu: `EntityMappingPersistenceUnitConfiguratorTest` 2 Tests (null-Name → ConfigurationException, valid-Config delegiert an doActivate) mit Subklasse-Seam um EMF-Bootstrap zu umgehen. Annotation-Proxy-Helper für @interface-Stubs. Insgesamt 37 neue Tests, alle grün, Full-Build grün. | T3-01 bis T3-03 | L | ✅ Done |
| AP-30 | **Cross-Resource-Referenzen:** Neue `NonOsgiCrossResourceRefTest` mit 6 Tests: (1) JPA↔JPA unidirektional (`ClassAO2O.dNonContainment → ClassDO2O` über getrennte `jpa://xref/*`-Resources), (2) JPA↔JPA bidirektional via EOpposite (`ClassAO2O.eNonContainmentBidi` ↔ `ClassEO2O.eClassA`), (3) `ResourceSet.getEObject(URI)` löst `jpa://puName/Entity#//refName/idAttr/idValue`-Fragmente korrekt auf, (4) unbekannte ID ergibt null (keine Exception), (5) gemischter ResourceSet mit JPA- und XMI-Factory — beide Resource-Typen koexistieren, (6) **XMI→JPA Proxy-Resolution**: XMI-Datei enthält `ClassDO2O` mit `dClassA`-Proxy auf `jpa://xref/ClassAO2O#//dClassA/id/{aId}`; beim Zugriff auf `d.dClassA` in frischem ResourceSet (mit JPA-Factory) wird der Proxy aufgelöst → `JPAResourceImpl.getEObject()` → `em.find()` → vollständig initialisiertes JPA-EObject. Bestätigt: `JPAResourceFactory` + `XMIResourceFactoryImpl` lassen sich parallel an eine `ResourceSet` binden; `JPAResourceImpl.getEObject()` integriert sich korrekt in den EMF-Proxy-Resolution-Path, auch aus XMI-Quellen. | F4-04 | M | ✅ Done |
| AP-31 | **View-Unterstützung und Schema-Evolution** | F5-03 | XL | ❌ Offen |
| AP-32 | **Batch Writing / Fetch-Optimierung:** (1) Batch-Writing (`eclipselink.jdbc.batch-writing=JDBC`, size=500) im OSM-Perf-Test konfiguriert und gemessen — bei in-memory H2 mit SINGLE_TABLE (435 Spalten) ist der Commit/Flush ~74 % der Zeit, Batch-Writing bringt ~6 %. `toManagedEntity()` mit 3 % kein Flaschenhals; Entity-Factory-Function wird pro `doSave()` einmal erstellt. (2) **Pagination in `doLoad()`**: `Options.OPTION_PAGE_SIZE` (Integer). Bei > 0 iteriert `JPAResourceImpl.doLoad()` per `setFirstResult`/`setMaxResults`, andernfalls Single-Query-Default. `Options.getPageSize()` akzeptiert Integer/Number/String. 3 neue Tests (Pagination mit 25/10, page-size=total, page-size=0 deaktiviert). (3) **`setShouldNewObjectsBeCached`**: `Options.OPTION_CACHE_NEW_OBJECTS` (Boolean). Bei `FALSE` wird nach `em.getTransaction().begin()` per `em.unwrap(UnitOfWork.class).setShouldNewObjectsBeCached(false)` das Caching neuer Objekte deaktiviert — Memory-Entlastung bei Bulk-Insert. 1 neuer Test. (4) **Batch-Writing als PU-Property**: typisierte OCD-Einträge `batchWriting` (String) + `batchSize` (int) in beiden PUConfigs (`PersistenceUnitConfigurator`, `EntityMappingPersistenceUnitConfigurator`). `AbstractPersistenceUnitConfigurator.createForwardedProperties()` mappt sie auf `PersistenceUnitProperties.BATCH_WRITING`/`BATCH_WRITING_SIZE`; `putIfAbsent` — explizite `fennec.jpa.ext.*`-Overrides gewinnen. 6 neue Unit-Tests. Insgesamt: 13 OptionsTest (neu) + 6 AbstractPersistenceUnitConfiguratorTest (neu) + 4 neue Non-OSGi-Tests in JPAResource. | F12-01, T8-01, F2-06 | M | ✅ Done |
| AP-33 | **dispose()-Implementierung:** `BasicPersistenceEngine.dispose()` leert `mergedOptions`, `engineProperties` und setzt `resource=null`. Auskommentierter Legacy-Code (ConverterService, QueryEngine, PrimaryKeyFactory, handlerList) und veralteter Javadoc entfernt. `PersistenceEngine.dispose()` Javadoc verbessert. 4 Unit-Tests (set+get, dispose clears, idempotent, fresh engine). | T2-01 | S | ✅ Done |
| AP-34 | **Code-Bereinigung:** (1) ✅ T2-04: `JPAResourceImpl.getEngine()` wirft `UnsupportedOperationException`. (2) ✅ T4-06: `EDynamicTypeContext` Komposition statt Vererbung — `ConcurrentHashMap` als Feld, nur benötigte Methoden exponiert, Bug in `remove()` behoben, 8 Tests. (3) ✅ T4-08: Debug-Test `testConverterDebugOrig` entfernt, Imports bereinigt. (4) T1-01/T1-02: Entfällt (bereits in früheren APs bereinigt). (5) T4-02: Entfällt (bereits in AP-06 bereinigt). | T1-01, T1-02, T2-04, T4-02, T4-06, T4-08 | M | ✅ Done |
| AP-35 | **Derived/volatile Features filtern:** `MappingProcessor` filtert jetzt `isDerived()` und `isVolatile()` in allen 4 Mapping-Stufen (Attributes, Containment-Refs, Non-Containment-Refs, Opposites) — analog zum bestehenden `isTransient()`-Filter. 7 neue Tests in `MappingProcessorPipelineTest` (Attribute, Multi-valued, Containment-Ref, Non-Containment-Ref, Bidirektional). | F1-07 | S | ✅ Done |
| AP-36 | **Version-Locking Accessor Fix:** Statt eigener `EVersionLockingPolicy` (Option A) reichte Option C: Nach `useVersionLocking()` wird der `ValuesAccessor` des Lock-Mappings durch `EFeatureAccessor` ersetzt. Zusätzlich: Version-Typ wird aus EStructuralFeature abgeleitet statt hartkodiert `Long.class`. 2 E2E-Tests aktiviert (Conflict-Detection + Version-Increment). | F1-06, AP-13 | S | ✅ Done |
| AP-37 | **JPAResourceImpl.doLoad() Robustheit:** (1) `isLoaded` wird erst nach erfolgreichem `doLoad()` gesetzt — bei Exception bleibt Flag `false`, erneuter Load möglich (kein "stuck"-Zustand). (2) `getContents().clear()` vor Query verhindert Duplikate bei Reload nach `unload()`. Guard `!isEmpty()` nötig wegen EMF `ContentsEList.didClear()` das bei leerer Liste `setLoaded(true)` triggert. 3 neue Tests (Failure-Recovery, Reload-ohne-Duplikate, Idempotenz). | F4-02, F4-03 | S | ✅ Done |
| AP-38 | **Erweiterte Dokumentation:** Getting-Started-Anleitung mit Beispiel-Projekt, Konfigurationsreferenz (`fennec.jpa.*` Properties) | F15-02, F15-03 | M | ❌ Offen |
| AP-43 | **Inheritance-Roundtrip-Tests (SINGLE_TABLE + JOINED):** Wiederverwendbare Basisklasse `NonOsgiInheritanceRoundtripBase` mit 5 Tests pro Strategie (Persist+Find-by-Subklasse, Find-via-abstrakte-Basis, polymorphes Query, polymorpher Filter auf geerbtem Attribut, voller Attribut-Roundtrip). Modell erweitert: `VehicleJ/CarJ/MotorcycleJ` (JOINED) und `VehicleTpc/CarTpc/MotorcycleTpc` (TABLE_PER_CLASS) parallel zur bestehenden SINGLE_TABLE-Hierarchie. 10 Tests grün (SINGLE_TABLE + JOINED). Dabei aufgedeckt: AP-12 TABLE_PER_CLASS war nicht funktionsfähig (Subklassen auf Root-Tabelle, `InheritancePolicy` statt `TablePerClassPolicy`, fehlende child-ID-Mappings) — Vollständigkeit in AP-44 geliefert. | F1-03 vertieft | L | ✅ Done |
| AP-44 | **TABLE_PER_CLASS + MappedSuperclass komplett implementiert** (zusammengelegt mit AP-25). **Phase 1 — gemeinsame Maschinerie:** (a) `NamedBaseProcessor.registerMapping()` nutzt `MappingContext.getCurrentEntity()` statt der deklarierenden EClass — inherited Attribute landen auf dem aktuell iterierten Child statt auf dem Parent; (b) `MappingProcessor` setzt `currentEntity` vor jeder Stage und räumt im `finally` auf; (c) `getEffectiveAttributes/Refs` spiegelt inherited Features in TPC/MS-Children; (d) `EntityProcessor.childOwnsTable()` + `createIds()` gelten bei TPC- UND MS-Children; (e) inherited Attrs aus nicht-gemappten Super-Types werden dropped (Back-Compat). **Phase 2a (TABLE_PER_CLASS):** `TablePerClassPolicy` statt `InheritancePolicy` auf TPC-Descriptoren, `addParentDescriptor`/`addChildDescriptor` verlinken Parent↔Children, kein Class-Indicator, polymorphe UNION-Queries funktionieren. **Phase 2b (MappedSuperclass):** Detection via EAnnotation `mappedSuperclass="true"` auf dem Root; `MappingProcessor` Stage 1 überspringt Root-EntityProcessor; Children werden als unabhängige Entities gemappt; Root hat keinen EclipseLink-Descriptor; `configureInheritance()` überspringt MS-Children bewusst (keine Inheritance-Linkage auf JPA-Ebene). **Tests:** `NonOsgiInheritanceTablePerClassTest` aktiviert (5 grün), neue `NonOsgiMappedSuperclassTest` (5 grün — 2 davon als Negativ-Assertion: Root hat keinen Descriptor, Filterung per konkretem Subklassen-Query). Modell-Fixture `VehicleMs/CarMs/MotorcycleMs`. Build: 83 Non-OSGi + 22 OSGi Tests, alles grün. | F1-03, F1-05 | L | ✅ Done |
| AP-45 | **EMF-Proxy-Fassade für Non-Containment-Refs auf Resource-Ebene (Eager-Fetch unverändert):** **Wichtig:** Dies ist *keine* Lazy-Loading-Optimierung — EclipseLink fetched weiterhin eager beim `resource.load()`. Der Gewinn ist die EMF-API-Semantik, nicht die DB-Last. Architekturentscheidung — Proxy-Erzeugung ist ein Resource-Layer-Concern, nicht ein Mapping-Layer-Concern. `EReferenceAccessor` und `ReferenceConfigurator` bleiben unverändert, weil sie in zwei Kontexten gleichzeitig aufgerufen werden: Resource-gestützte Reads (da wollen wir Proxy-Semantik) und direkte `em.find`-Pfade (da müssen voll aufgelöste Entities rauskommen, damit der Aufrufer auch ohne `ResourceSet` navigieren kann). `JPAResourceImpl.doLoad()` ersetzt jetzt nach dem Query-Ergebnis alle Non-Containment-Referenz-Werte der geladenen Entities durch EclipseLink-managed Dynamic-Instanzen (via `ClassDescriptor.getInstantiationPolicy().buildNewInstance()`) mit `eSetProxyURI(jpa://puName/TargetEClass#//refName/idAttr/idValue)`. Attribute werden auf den Proxy kopiert (keine Referenzen), damit scalar-Reads auch ohne aufgelöste Resolution funktionieren. Proxy-Auflösung beim späteren `eGet` geht über `ResourceSet.getEObject` → `JPAResourceImpl.getEObject` → `em.find` — **Cache-Hit ohne zusätzlichen DB-Call**, weil EclipseLink das Ziel in Phase 1 schon geladen hat. Test-Beweis: `NonOsgiCrossResourceRefTest.testJpaNonContainmentRefIsProxyUntilAccessed` — `eIsProxy()==true` vor `eGet`, korrekt aufgelöstes Ziel nach `eGet`, Ziel-URI `jpa://xref/ClassDO2O#...`. Alle 94 Non-OSGi-Tests + 22 OSGi-Tests grün. Echtes Lazy (FK lesen ohne das Ziel zu fetchen) ist separat in AP-46 zu tracken. | F4-04 Anschluss | M | ✅ Done |
| AP-46 | **Echtes Lazy-Loading für Non-Containment-Refs (DB-Fetch deferred):** Implementiert wie in `docs/AP-46_Lazy-Loading-Analysis.md` geplant. (1) `ReferenceConfigurator.setMappingDefaults` → `setIsLazy(!reference.isContainment())`. (2) `EBasicIndirectionPolicy.valueFromQuery(..., sourceObject, session)` + `(..., session)` überschrieben: liest FK aus der Source-`AbstractRecord` via `ObjectReferenceMapping.extractPrimaryKeysForReferenceObjectFromRow(row)` — kein DB-Call. Erzeugt EclipseLink-managed Dynamic-Proxy via `targetDescriptor.getInstantiationPolicy().buildNewInstance()`, setzt ID-Attribut und `eSetProxyURI(jpa://puName/TargetEClass#//refName/idAttr/idValue)`, gibt den Proxy direkt zurück statt eines `ValueHolder`s. CacheId-Unwrap für Single-PK. (3) `validateAttributeOfInstantiatedObject` wickelt EMF-Proxies nicht mehr in VHs ein (würde die `eIsProxy()`-Semantik kaputt machen). `getRealAttributeValueFromObject`, `cloneAttribute`, `extractPrimaryKeyForReferenceObject` akzeptieren jetzt nackte Proxy-EObjects statt nur VH. (4) AP-45-Post-Load-Proxifizierung (`JPAResourceImpl.proxifyNonContainmentRefs`) komplett entfernt — Proxies kommen jetzt direkt aus der Indirection-Pipeline. (5) `em.find`-basierte Tests (OneToOne/OneToMany Non-Containment in NoCache-Varianten) auf Resource-Load umgeschrieben; neuer Test-Base-Helper `findViaResource(rs, entityName, id)` teilt ResourceSet für Cross-Resource-Identity; Bidi-Assertions nutzen `EcoreUtil.getID(...)` statt Java-Identität (da jeder `em.find` in `JPAResourceImpl.getEObject` eine frische EM öffnet). D's aus Lazy-OneToMany-Collections haben keine `eResource()`, deshalb wird `EcoreUtil.resolve(proxy, rs)` explizit für ihre Back-Refs verwendet. (6) Neuer **`testLazyNonContainmentDefersTargetQuery`**: `SessionEventAdapter` zählt ReadObject/ReadAll-Queries. Beweis: `aResource.load()` löst genau **1** Query aus (nur ClassAO2O), `a.eGet(dNonContainment)` löst die Target-Query aus, zweiter `eGet` tut **nichts** (Proxy ist resolved). Build: **94 Non-OSGi + 22 OSGi Tests grün**. | Performance/Skalierung | L | ✅ Done |
| AP-47 | **Element-Level-Proxies für Non-Containment-Collections:** Erweitert AP-46 auf OneToMany- und ManyToMany-Non-Containment. Aktuell nutzen Collections EclipseLinks `IndirectList` — lazy auf Collection-Ebene (erste `eGet(collection)` löst einen SELECT aus), aber die zurückgelieferten Elemente sind voll geladen, nicht EMF-Proxies. Ziel: Auch Collection-Elemente als EMF-Proxy mit `eProxyURI` ausliefern, damit Resolution erst beim Zugriff auf das Einzel-Element einen (weiteren) DB-Call auslöst (über Cache-Hit nach dem ersten SELECT). **Scope:** Eigene Collection-IndirectionPolicy (analog zu `TransparentIndirectionPolicy`), die beim Build der Liste die FK-Row-Einträge der Join-/Target-Rows in EMF-Proxies umwandelt statt voll-geladene Objekte zu liefern. Oder alternativ: post-hoc in `EReferenceAccessor` beim Collection-Set die Elemente proxifizieren. **Risiko:** hoch — EclipseLinks Collection-Indirection ist battle-tested, Eingriff kann in UoW-Clone/Merge-Pfaden brechen. **Nutzen:** fraglich — die erste Query läuft sowieso (Join-Table bei M:N, Target-Scan bei 1:N), Nutzen ist nur die Verzögerung des pro-Element-Buildings. Wirklich relevant erst bei Collections mit sehr vielen Elementen, von denen selten auf Attribute zugegriffen wird. | Performance-Variante | XL | ❌ Offen |

---

### AP-36: VersionLockingPolicy / DynamicEObjectImpl — Analyse & Lösung

**Status:** ✅ Done — Option C (Lock-Mapping nachträglich patchen) hat funktioniert.

#### Problem

EclipseLink's `VersionLockingPolicy` war inkompatibel mit `DynamicEObjectImpl`-basierten Entities. Der intern gesetzte `ValuesAccessor` castete zu `DynamicEntityImpl`, unsere Entities basieren aber auf `DynamicEObjectImpl` (EMF) → `ClassCastException` zur Laufzeit.

#### Lösung (Option C)

In `EDynamicTypeBuilder.configureVersionAttributes()`:
1. **Version-Typ aus EStructuralFeature ableiten** statt hartkodiertem `Long.class` — damit passt der DB-Mapping-Typ zum EMF-Modelltyp (z.B. `EInt` → `int`)
2. **Lock-Mapping-Accessor patchen** — nach `useVersionLocking()` wird der automatisch gesetzte `ValuesAccessor` durch `EFeatureAccessor` ersetzt, der mit `EObject.eGet()/eSet()` arbeitet

```java
descriptor.useVersionLocking(colName, false);
DatabaseMapping lockMapping = descriptor.getMappingForAttributeName(version.getName());
if (nonNull(lockMapping) && nonNull(feature)) {
    lockMapping.setAttributeAccessor(EFeatureAccessor.create(feature));
}
```

#### E2E-Tests (aktiviert)
- **testOptimisticLockingConflict:** Concurrent Modification → `RollbackException`
- **testVersionIncrementOnUpdate:** Version-Feld wird bei Update automatisch inkrementiert

---

### AP-21: Proxy-Mechanismus — Analyse & Design-Entscheidung

**Status:** ✅ Done — Bewusste Design-Entscheidung: Beide Mechanismen beibehalten.

#### Ausgangsfrage

Es existieren zwei parallele Proxy-Mechanismen für Non-Containment-Referenzen. Die Frage war, ob einer entfernt oder beide konsolidiert werden sollten.

#### Die zwei Mechanismen

**1. `EBasicIndirectionPolicy` (ORM-Mapping-Ebene)**
- **Wo:** `ForeignReferenceMapping` (OneToOne, OneToMany, ManyToOne, ManyToMany)
- **Wann:** Referenz wird als FK-Relation in der DB abgebildet
- **Wie:** EclipseLink ValueHolder → Lazy Loading → `buildIndirectObject()` erstellt EMF-Proxy-Kopie mit `jpa://`-URI
- **Proxy-Resolution:** `JPAResourceImpl.getEObject()` parst Fragment, ruft `em.find()` auf
- **Cache-Schutz:** AP-20 Fix — `EcoreUtil.copy()` statt Original zu modifizieren

**2. `NonContainmentConverter` (TypeConverter-Ebene)**
- **Wo:** `EFeatureAccessor` für `Basic`-Mappings (DirectToFieldMapping) auf `EReference`-Features
- **Wann:** Referenz wird als String-URI-Spalte in der DB gespeichert (kein FK)
- **Wie:** `EObject` ↔ `String` (URI-Serialisierung), setzt `eSetProxyURI()` beim Laden

#### Ergebnis

**Kein Konflikt, kein Konsolidierungsbedarf.** Die Mechanismen bedienen unterschiedliche Persistierungsstrategien:

| Aspekt | `EBasicIndirectionPolicy` | `NonContainmentConverter` |
|--------|--------------------------|--------------------------|
| DB-Repräsentation | FK-Spalte (Relation) | String-Spalte (URI) |
| EclipseLink-Mapping | `ForeignReferenceMapping` | `DirectToFieldMapping` |
| Lazy Loading | Ja (ValueHolder) | Nein |
| Cache-Schutz | Ja (Proxy-Kopie) | Nein (direkt auf Objekt) |
| Proxy-Format | `jpa://puName/Entity#//ref/idAttr/id` | Beliebige EMF-URI |

Die Mechanismen ergänzen sich und werden nie für dieselbe Referenz gleichzeitig aktiv.

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
- ~~**Feature-Gaps:** Nur SINGLE_TABLE~~ ✅ Gefixt (AP-12). ~~**Optimistic Locking:** Runtime-Cast `DynamicEntityImpl` vs. `DynamicEObjectImpl`~~ ✅ Gefixt (AP-13 + AP-36 — Lock-Mapping-Accessor gepatcht)
- ~~**Unvollständige Migration:** 7 Gecko-bnd-Libraries in Build-Tooling verbleibend~~ ✅ Gefixt (AP-06 — `cnf/ext/` komplett entfernt, Build läuft ohne Gecko-Abhängigkeiten)
- ~~**Test-Gaps:** E2E-Tests für Lifecycle~~ ✅ Gefixt (AP-11). ~~Cross-Resource-Referenzen~~ ✅ Gefixt (AP-30). Verbleibend: Detach/Cache (AP-11b)
- ~~**Dokumentation:** README beschreibt falsches Projekt~~ ✅ Gefixt (AP-07)
- ~~**Code-Qualität:** EDynamicTypeBuilder God-Class~~ ✅ Gefixt (AP-16). ~~Options MongoDB-Legacy~~ ✅ Gefixt (AP-28). ~~Toter Code, Anti-Patterns~~ ✅ Gefixt (AP-34 — EDynamicTypeContext Komposition, getEngine UOE, Debug-Tests entfernt)

### 4.2 Top-Risiken (aktualisiert)

1. ~~**Datenverlust-Risiko:** CascadeRemove + invertiertes OrphanRemoval~~ ✅ Gefixt (AP-01)
2. ~~**Inkonsistenz-Risiko:** Fehlende Transaktions-Absicherung~~ ✅ Gefixt (AP-02)
3. ~~**Resource-Leak:** Race Condition + Thread-Leak~~ ✅ Gefixt (AP-03)
4. ~~**Architektonisches Risiko:** Non-Containment-Proxy-Resolution~~ ✅ Gefixt (AP-05)
5. ~~**Eclipse-Fennec-Blocker:** 7 Gecko-bnd-Libraries in Build-Tooling~~ ✅ Gefixt (AP-06 — `cnf/ext/` komplett entfernt)
6. ~~**Wartbarkeit:** EDynamicTypeBuilder als God-Class erschwert Erweiterungen~~ ✅ Gefixt (AP-16 — Delegate-Pattern, 1061→265 Zeilen)

### 4.3 Fortschritt

**Gesamtstand: 42 von 47 Arbeitspaketen erledigt (89 %)**

| Priorität | Gesamt | ✅ Done | ⚠️ In Arbeit | ❌ Offen |
|-----------|--------|---------|-------------|---------|
| P1 | 9 | 9 | 0 | 0 |
| P1b | 4 | 4 | 0 | 0 |
| P2 | 13 | 13 | 0 | 0 |
| P3 | 21 | 17 | 0 | 4 |

### 4.4 Testübersicht

**591 Tests gesamt — alle grün, 0 @Disabled** (+ 1 Perf-Test separat)

| Modul | Typ | Anzahl |
|-------|-----|--------|
| `persistence` (core) | Unit | 88 |
| `persistence.orm` | Unit | 228 |
| `persistence.ecore` | Unit | 27 |
| `persistence.eclipselink` | Unit + Integration (H2) | 146 |
| `persistence.test` | Non-OSGi Integration (H2) | 1 |
| **Unit-/Integrationstests gesamt** | | **490** |
| `persistence.test` | Integration (OSGi) | 101 |
| **Gesamt** | | **591** |
| `persistence.test` | Perf (`@Tag("perf")`, separat via `perfTest`) | 1 |

**Perf-Test-Ergebnis** (10.225 Objekte, 435 EClasses, SINGLE_TABLE, H2 in-memory):

| Phase | Zeit |
|-------|------|
| Load Ecore + Mappings + EMF + DDL | 3,5s |
| Load XMI (22 MB, .gz komprimiert) | 561ms |
| Persist (toManagedEntity + merge) | 12,6s |
| Read back (JPQL per Typ) | 2,0s |
| Deep comparison (102.205 Attribute) | 142ms |
| Write XMI (24 MB) | 537ms |
| **Total** | **~16s** |

### 4.5 Empfohlene Reihenfolge der Arbeitspakete

**Welle 1 — Bugfixes:** AP-01, AP-04, AP-08, AP-09 → ✅ Erledigt
**Welle 2 — Safety:** AP-02, AP-03 → ✅ Erledigt
**Welle 3 — Architektur:** AP-05, AP-07, AP-10 → ✅ Erledigt
**Welle 4 — Code-Qualität:** AP-17, AP-18, AP-20, AP-22 → ✅ Erledigt
**Welle 5 — Feature-Gaps:** AP-12, AP-13 → ✅ Erledigt
**Welle 6 — Tests & Parser:** AP-14, AP-15 → ✅ Erledigt
**Welle 7 — Lifecycle & Modernisierung:** AP-11, AP-23 → ✅ Erledigt
**Welle 8 — Migration & Locking:** AP-06, AP-36 → ✅ Erledigt
**Welle 9 — Qualität:** AP-16, AP-19, AP-21 → ✅ Erledigt
**Welle 10 — Robustheit & Mapping:** AP-35, AP-37 → ✅ Erledigt
**Welle 11 — Cleanup & Lifecycle:** AP-33, AP-34 → ✅ Erledigt
**Welle 12 — Inheritance & XMI-Roundtrip:** AP-39, AP-40, AP-42 → ✅ Erledigt
**Welle 13 — Non-OSGi-Testinfrastruktur:** AP-41 → ✅ Done (Basisklasse + 18 Non-OSGi-Testklassen mit 64 Tests; 21 migrierte OSGi-Testdateien gelöscht; 22 genuin OSGi-Tests bleiben)
**Welle 14 — MappedSuperclass-Design:** AP-25 → ✅ Evaluiert + revidiert (siehe `docs/AP-25_MappedSuperclass-Decision.md` § 6) — MappedSuperclass wird jetzt gemeinsam mit AP-44 implementiert
**Welle 15 — Inheritance-Roundtrip + TPC/MappedSuperclass-Pipeline:** AP-43 → ✅ Done (SINGLE_TABLE + JOINED Roundtrip-Tests grün, TPC-Bugs aufgedeckt), AP-44 → ✅ Done (TPC und MappedSuperclass auf geteilter Pipeline vollständig implementiert)
**Welle 16 — Cleanup:** AP-27 → ✅ Done (package-info + .licenserc.yaml auf m2m-Stil angeglichen)
**Welle 17 — Fetch/Write-Tuning:** AP-32 → ✅ Done (Pagination in `doLoad`, cache-new-objects-Option in `doSave`, typisierte `batchWriting`/`batchSize`-OCD-Properties)
**Welle 18 — Unit-Test-Verbreiterung:** AP-29 → ✅ Done (37 neue Tests für JPAResourceImpl, EFeatureAccessor, AbstractPersistenceUnitConfigurator, PersistenceUnitConfigurator, EntityMappingPersistenceUnitConfigurator)
**Welle 19 — Cross-Resource-Tests:** AP-30 → ✅ Done (6 Tests: JPA↔JPA uni/bidi, ResourceSet-Fragment-Resolution, Unknown-ID-null, Mixed-JPA+XMI-ResourceSet, XMI→JPA-Proxy-Resolution)
**Welle 20 — EMF-Proxy-Fassade:** AP-45 → ✅ Done (Resource-Layer proxifiziert Non-Containment-Ref-Werte mit `eSetProxyURI`; Eager-Fetch bleibt — reine EMF-API-Semantik, keine DB-Optimierung)
**Welle 21 — Echtes Lazy-Loading:** AP-46 → ✅ Done (IndirectionPolicy.valueFromQuery liest FK aus Source-Row und erzeugt EclipseLink-managed Proxy direkt — echte DB-Latenz-Reduktion, Query-Zähler-Test beweist 1+1 statt 1+N)
**Welle 22 — Erweiterungen:** AP-24, AP-31, AP-38, AP-47 (Element-Level Lazy für Collections)

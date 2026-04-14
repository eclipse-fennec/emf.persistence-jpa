# Eclipse Fennec Persistence JPA — Structured Review

> Lebendes Arbeitsdokument: Review-Kriterien, Ergebnisse und abgeleitete Arbeitspakete.
> Stand: 2026-04-14 | Review durchgeführt: 2026-04-14

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
  - Sind shared-mutable-state Zugriffe synchronisiert?
  - Gibt es Race Conditions bei OSGi Service-Registrierung/-Deregistrierung?
  - Werden Collections thread-safe genutzt (z.B. `ConcurrentHashMap`)?
- **Memory Leaks:**
  - Werden Ressourcen (Streams, Connections, EntityManager) korrekt geschlossen?
  - Gibt es Listener/Observer die nicht deregistriert werden?
  - Werden starke Referenzen gehalten wo WeakReferences angemessen wären?
- **Sichere Programmierung (OWASP, CWE):**
  - Werden Credentials/Connection-Strings sicher gehandhabt (nicht in Logs, nicht im Klartext)?
  - Werden SQL-Injections verhindert (Parameterized Queries)?
  - Werden sensible Daten in Exception-Messages vermieden?
  - Ist das Logging frei von sensiblen Daten (Passwörter, personenbezogene Daten)?

#### T10 — BSI TR-03183 (Cyber Resilience)
Prüfung gegen die BSI Technische Richtlinie TR-03183 — relevant im Kontext des EU Cyber Resilience Act (CRA).

- **TR-03183-1 — Allgemeine Anforderungen:**
  - Secure Development Lifecycle: Gibt es dokumentierte Prozesse für sichere Entwicklung?
  - Vulnerability Handling: Gibt es einen Prozess für Schwachstellenbehandlung und Disclosure?
  - Sicherheitsupdates: Ist das Projekt in der Lage, zeitnah Patches bereitzustellen?
  - Default-Konfiguration: Sind Standardkonfigurationen sicher (Secure by Default)?
  - Minimierung der Angriffsfläche: Werden nur notwendige APIs/Services exponiert?
- **TR-03183-2 — Software Bill of Materials (SBOM):**
  - Existiert eine SBOM (CycloneDX oder SPDX)?
  - Sind alle transitiven Abhängigkeiten erfasst (Gradle Dependencies)?
  - Sind Lizenzen aller Abhängigkeiten dokumentiert und kompatibel (EPL-2.0)?
  - Werden bekannte Vulnerabilities in Abhängigkeiten geprüft (CVE-Scanning)?
- **TR-03183-3 — Open Source Software:**
  - Governance: Gibt es klare Contribution-Richtlinien und Rollen?
  - Code-Herkunft: Ist die Herkunft aller Code-Beiträge nachvollziehbar (Signed Commits, DCO)?
  - Abhängigkeitsmanagement: Werden Third-Party-Dependencies bewusst ausgewählt und gepflegt?
  - Lizenz-Compliance: Ist die EPL-2.0 Lizenzierung konsistent über alle Module?
  - Dokumentation: Sind Sicherheitsrelevante Entscheidungen dokumentiert?

#### T11 — SOLID-Prinzipien
- **Single Responsibility (SRP):**
  - Hat jede Klasse genau eine Verantwortlichkeit?
  - Gibt es God-Classes die zu viel tun?
- **Open/Closed (OCP):**
  - Ist das System erweiterbar ohne bestehenden Code zu ändern?
  - Werden Processor/Converter-Patterns konsequent für Erweiterbarkeit genutzt?
- **Liskov Substitution (LSP):**
  - Verhalten sich Subtypen konform zu ihren Basistypen?
  - Werden Verträge (Pre-/Post-Conditions) eingehalten?
- **Interface Segregation (ISP):**
  - Sind Interfaces schlank und fokussiert?
  - Gibt es zu breite Interfaces die aufgeteilt werden sollten?
- **Dependency Inversion (DIP):**
  - Hängen Module von Abstraktionen ab statt von konkreten Implementierungen?
  - Wird OSGi Declarative Services konsequent für Dependency Injection genutzt?

### 1.2 Fachliche Kriterien

#### F1 — JPA↔Ecore Mapping (Kernthema)
Das zentrale fachliche Kriterium: Welche JPA/ORM-Konzepte mappen auf welche Ecore-Konzepte — und wo gibt es Gaps oder Mismatches?

- **Mapping-Matrix JPA → Ecore:**

  | JPA-Konzept | Ecore-Äquivalent | Status prüfen |
  |-------------|------------------|---------------|
  | `@Entity` | `EClass` | |
  | `@Basic` / Column | `EAttribute` | |
  | `@OneToOne` | `EReference` (upper=1) | |
  | `@OneToMany` | `EReference` (upper=-1) | |
  | `@ManyToOne` | `EReference` (upper=1) + eOpposite | |
  | `@ManyToMany` | `EReference` (upper=-1) + eOpposite | |
  | `@Embedded` / `@Embeddable` | `EClass` (inline) oder `EDataType`? | |
  | `@Inheritance` (SINGLE_TABLE, JOINED, TABLE_PER_CLASS) | EClass-Hierarchie (`eSuperTypes`) | |
  | `@MappedSuperclass` | Abstract `EClass` | |
  | `@ElementCollection` | `EAttribute` (upper=-1) oder `EReference`? | |
  | `@Id` / `@GeneratedValue` | `EAttribute` (id=true) | |
  | `@EmbeddedId` / `@IdClass` | Composite aus `EAttribute`s | |
  | `@Version` | `EAttribute` (changeable, volatile?) | |
  | `@Transient` | `EStructuralFeature` (transient=true) | |
  | `@Enumerated` | `EEnum` | |
  | `@Lob` | `EAttribute` (EDataType=EByteArray?) | |
  | `@JoinTable` / `@JoinColumn` | kein direktes Ecore-Äquivalent → EORM | |
  | `@NamedQuery` / `@NamedNativeQuery` | kein Ecore-Äquivalent → EORM | |
  | `@EntityListeners` / Callbacks | kein Ecore-Äquivalent → EORM | |

- **Gaps auf JPA-Seite:** Welche JPA-Features können nicht auf Ecore abgebildet werden und müssen über EORM-Metadaten gelöst werden?
- **Gaps auf Ecore-Seite:** Welche Ecore-Features haben kein JPA-Pendant?
  - `containment=true` → Cascade + Orphan Removal? Aber semantisch tiefer (Lifecycle-Ownership)
  - `eOpposite` → bidirektionale Referenzen, aber JPA erfordert explizite `mappedBy`
  - `derived` / `volatile` Features → transient oder computed?
  - `EOperation` → keine JPA-Entsprechung
  - `EAnnotation` → als Extension-Punkt nutzbar?
- **Mismatches / semantische Lücken:**
  - EMF Containment ≠ JPA Cascade (Containment impliziert Lifecycle-Ownership, JPA Cascade ist konfigurierbar)
  - EMF EOpposite ist immer bidirektional, JPA erlaubt unidirektionale Referenzen
  - EMF orderedReferences vs. JPA `@OrderColumn` / `@OrderBy`
  - EMF unique-Constraint auf EReference vs. JPA UniqueConstraint
  - EMF default-Values vs. JPA `@Column(columnDefinition)`

#### F2 — Entity Lifecycle: Attaching, Detaching & Caching (CRUD)
Systematische Prüfung des Entity-Lifecycle über alle CRUD-Operationen. Bevorzugt kleine, nachvollziehbare Tests von denen auf das Gesamtverhalten geschlossen werden kann.

- **Create (Persist):**
  - Neues EObject → persist → ist es attached?
  - Persist mit Containment-Hierarchie (Parent + Children in einem Schritt)
  - Persist mit Referenzen auf bereits persistierte Objekte
  - Persist mit bidirektionalen Referenzen — werden beide Seiten korrekt gesetzt?
  - Persist-Verhalten bei Duplikaten / bereits existierenden IDs
- **Read (Find/Query):**
  - Geladenes Objekt: attached oder detached?
  - Caching: Wird dasselbe Objekt bei erneutem Find zurückgegeben (Identity Map)?
  - Lesen nach Cache-Eviction: frische DB-Daten?
  - Lesen mit Lazy-Referenzen: Wird bei Zugriff nachgeladen?
  - Lesen einer kompletten Containment-Hierarchie (Eager vs. Lazy)
- **Update:**
  - Änderung an attached EObject → flush → DB-Update?
  - Änderung an detached EObject → merge → korrekt re-attached?
  - Änderung an Containment-Kind: wird Parent als dirty markiert?
  - Concurrent Update: Optimistic Locking / Version-Check?
  - Partial Update: Nur geänderte Felder oder volles Update?
- **Delete:**
  - Delete eines attached Objekts
  - Delete eines Objekts mit Containment-Kindern (Cascade?)
  - Delete eines Objekts das von anderen referenziert wird (FK-Constraints)
  - Delete + Cache-Eviction: Ist das Objekt nach Delete aus dem Cache entfernt?
- **Cache-Verhalten über CRUD:**
  - First-Level-Cache (EntityManager/Session): Konsistenz innerhalb einer Transaktion
  - Second-Level-Cache: Konsistenz über Transaktionsgrenzen
  - Cache-Invalidierung bei Create/Update/Delete
  - Cache + OSGi Service Lifecycle (Service restart → Cache-Zustand?)

#### F3 — ID-Generierung & Value-Konvertierung
- **ID-Strategien:**
  - Auto-generierte IDs (UUID, Sequence, Table, Identity)
  - Manuell gesetzte IDs (Application-assigned)
  - Composite IDs (@IdClass, @EmbeddedId) — bekanntes Gap, Status prüfen
  - ID-Generierung bei Batch-Inserts
  - ID-Typ-Mapping: Java UUID ↔ DB varchar/binary, Long ↔ bigint, etc.
- **Value-Konvertierung (TypeConverter):**
  - Alle 18 vorhandenen Converter: Korrektheit und Vollständigkeit prüfen
  - Fehlende Konvertierungen identifizieren (z.B. `java.net.URI`, `java.net.URL`, `Currency`, `Locale`)
  - Enum-Konvertierung: Ordinal vs. String, Custom Mapping
  - Custom EDataType-Konvertierung: Ist der Extension-Point nutzbar?
  - Null-Handling bei Konvertierungen: nullable Spalten ↔ Optional/Default
  - Konvertierung bei Collections (List<String>, Set<Integer>)
  - Round-Trip-Sicherheit: Java → DB → Java ergibt identisches Objekt?

#### F4 — Use-Case 1: Automatisches EORM für EObject CRUD
EObjects persistieren mit automatisch generiertem EORM — der "einfache" Pfad.

- Funktioniert CRUD für einfache EObjects (flache Attribute)?
- Funktioniert CRUD für verschachtelte Containment-Strukturen?
  - Tiefe Hierarchien (3+ Ebenen): Root → Child → Grandchild
  - Mehrere Kinder unterschiedlichen Typs
  - EObject in mehreren Containment-Referenzen
- Funktioniert CRUD mit Non-Containment-Referenzen?
  - Referenz auf Objekt in gleicher Resource
  - Cross-Resource-Referenzen (Proxy-basiert)
- Funktioniert CRUD mit Vererbung?
  - Abstrakte Basisklasse + konkrete Subklassen
  - Polymorphe Queries (findAll auf Basistyp)
- Automatische Schema-Generierung: Werden Tables/Columns korrekt abgeleitet?
- Einschränkungen: Was geht explizit NICHT automatisch?

#### F5 — Use-Case 2: Custom EORM für bestehende Datenbank
Bestehendes DB-Schema auf Ecore mappen via Custom EORM — der "anspruchsvolle" Pfad.

- Mapping von DB-Tabellen auf EClasses mit abweichenden Namen
- Mapping von DB-Spalten auf EAttributes mit abweichenden Namen/Typen
- Mapping von Foreign Keys auf EReferences
- Zusammengesetzte Primärschlüssel
- Views auf EClasses mappen
- Stored Procedures / Functions nutzbar?
- Schema-Evolution: Wie wird mit DB-Änderungen umgegangen?
- Reverse Engineering (DatabaseEcoreParser): Wie gut ist die automatische Erkennung?
- Delta zwischen auto-generiertem und custom EORM: Welche Zusatzinfos sind nötig?

#### F6 — Proxy-Mechanismus: EMF vs. JPA (Weaving-Problematik)
Kritisches Querschnittsthema — EMF und JPA haben fundamental unterschiedliche Proxy-Konzepte.

- **EMF-Proxies (URI-basiert):**
  - Non-Containment-Referenzen werden als URI-Proxy aufgelöst (`EcoreUtil.resolve`)
  - Lazy: Proxy-Objekt mit URI, Resolve bei erstem Zugriff
  - Eager: Objekt direkt aufgelöst und eingebettet
  - Proxy-Auflösung geht über `ResourceSet` → `Resource` → Load
- **JPA/EclipseLink-Proxies (Weaving-basiert):**
  - Lazy Loading via Bytecode-Weaving (Getter-Interception)
  - ValueHolder-Pattern bei EclipseLink
  - Weaving erfordert Classloader-Zugriff → **problematisch in OSGi**
- **Mismatch-Analyse:**
  - Kann ein JPA-Proxy als EMF-Proxy fungieren und umgekehrt?
  - Was passiert wenn ein EclipseLink-geweavtes Objekt über EMF-API traversiert wird?
  - Werden LazyInitializationExceptions korrekt in EMF-Kontext übersetzt?
- **Weaving in OSGi — Problemanalyse:**
  - Funktioniert EclipseLink Static Weaving mit bnd/OSGi?
  - Funktioniert Dynamic Weaving mit dem `OSGiDynamicClassloader`?
  - Welche Weaving-Strategie wird aktuell verwendet? Ist sie stabil?
- **Lösungsansatz: Ungeweavte Kopien:**
  - Kann `EcoreUtil.copy()` genutzt werden um "saubere" (ungeweavte) EObjects aus der Resource zurückzuliefern?
  - Performance-Impact: Ist Copy bei jedem Read akzeptabel?
  - Alternativen: Detach + Serialisierung? EMF-eigener Proxy-Mechanismus statt JPA-Weaving?
  - Konfigurierbarkeit: Sollte der Consumer entscheiden (Lazy/Eager/Copy)?
- **Testabdeckung:**
  - Gibt es Tests die das Verhalten von Proxies über die Grenze EMF↔JPA prüfen?
  - Wird getestet was passiert wenn auf eine Lazy-Referenz außerhalb einer Session zugegriffen wird?
  - Wird getestet ob kopierte Objekte tatsächlich frei von JPA-Artefakten sind?

#### F7 — Transaktionsmanagement
- **Transaktionsmodell:**
  - JTA vs. Resource-Local: Welches Modell wird verwendet / unterstützt?
  - Transaktionsgrenzen: Wer öffnet/schließt Transaktionen (Framework oder Consumer)?
  - Transaktionspropagierung im OSGi-Kontext (Services sind langlebig)
- **Rollback-Semantik:**
  - Was passiert mit EMF-Objekten bei einem Rollback? Werden in-memory Änderungen zurückgesetzt?
  - Konsistenz zwischen JPA-Zustand und EMF-Objektgraph nach Rollback
  - Werden Containment-Hierarchien bei Rollback korrekt wiederhergestellt?
- **Transaktionen über CRUD-Operationen:**
  - Implizite vs. explizite Transaktionen pro Operation
  - Batch-Operationen: Mehrere Persist/Update/Delete in einer Transaktion
  - Read innerhalb vs. außerhalb einer Transaktion (Isolation Level)

#### F8 — EMF Change Notification ↔ JPA Dirty Tracking
EMF und JPA haben jeweils eigene Change-Detection-Mechanismen — ein potentieller Mismatch ähnlich der Proxy-Problematik.

- **EMF → JPA Richtung:**
  - Wird `eSet()` / `eUnset()` als JPA-Änderung erkannt (Dirty-Marking)?
  - Werden EMF-Notifications (ADD, REMOVE, SET) in JPA-ChangeTracking übersetzt?
  - Funktioniert das bei Collections (`EList.add()`, `EList.remove()`)?
- **JPA → EMF Richtung:**
  - Werden JPA-seitige Änderungen (Merge, Refresh) als EMF-Notifications propagiert?
  - Bekommen EMF-Adapter die auf dem EObject registriert sind die Änderungen mit?
- **Konflikt-Szenarien:**
  - Was passiert wenn EMF und JPA unterschiedliche "Wahrheiten" über den Objektzustand haben?
  - Cache-Refresh (JPA) vs. EMF-Objektgraph: Wird der Graph konsistent aktualisiert?

#### F9 — EMF Resource/ResourceSet Integration & URI-Mechanismus
Der URI-basierte Mechanismus von EMF muss konsequent genutzt werden. Cross-Resource-Referenzen sind in EMF normal und müssen mit JPA funktionieren.

- **JPAResource als EMF Resource:**
  - Integriert sich `JPAResource` korrekt in den EMF `ResourceSet`-Lifecycle?
  - Semantik von `resource.load()` / `resource.save()` — was lösen diese aus (JPA-seitig)?
  - Können mehrere `JPAResource`-Instanzen in einem `ResourceSet` koexistieren?
  - URI-Schema: Wie sieht eine JPA-Resource-URI aus? Ist das Schema konsistent und erweiterbar?
- **Cross-Resource-Referenzen:**
  - Funktionieren Referenzen zwischen zwei JPA-backed Resources?
  - Funktionieren Referenzen zwischen einer JPA-Resource und einer XMI-Resource?
  - Werden Cross-Resource-Referenzen als URI-Proxies aufgelöst (EMF-Mechanismus)?
- **Proxy-Resolve über ResourceSet (→ Verbindung zu F6):**
  - Non-Containment-Referenzen: Wird das referenzierte Objekt als EMF-URI-Proxy angelegt?
  - Beim Resolve: Bekommt das aufgelöste Objekt eine **eigene EMF Resource**?
  - Konsequenz: EclipseLink-Weaving/Lazy-Loading wird für Non-Containment-Proxies **nicht benötigt**, da EMF-Proxy-Auflösung über ResourceSet → eigene Resource → eigener Load geht
  - Vorteil: Aufgelöste Objekte sind "sauber" (nicht geweaved), da sie frisch aus einer neuen Resource geladen werden
  - Ist dieser Mechanismus implementiert und getestet?

#### F10 — EntityManager Lifecycle & Thread-Safety
`EntityManager` ist nicht thread-safe — im OSGi-Kontext mit langlebigen Services ein besonderes Risiko.

- Wie wird der EntityManager-Lifecycle verwaltet? (per-Request, per-Transaction, langlebig?)
- Connection Pooling: Ist ein Pool konfiguriert und sinnvoll dimensioniert?
- Werden EntityManager korrekt geschlossen (kein Leak bei Exceptions)?
- Concurrent Access: Was passiert wenn zwei Threads denselben Service/dieselbe Resource nutzen?
- OSGi Service Lifecycle: Was passiert mit offenen EntityManagern bei Service-Deaktivierung?

#### F11 — Query-API (Future TODO)
> **Hinweis:** Es existiert bereits ein abstraktes EMF-Query-Modell. Die Anwendung auf JPA ist ein separates Arbeitspaket. Im Review wird nur der aktuelle Stand dokumentiert — keine tiefe Analyse.

- Gibt es aktuell eine Query-Möglichkeit jenseits von `find(id)`?
- Wie weit ist das EMF-Query-Modell und welche Lücken bestehen zur JPA-Anwendung?
- Welche Vorarbeiten im aktuellen Code existieren bereits?

#### F12 — EclipseLink-spezifische Features
Review unter Hinzuziehung des EclipseLink-Quellcodes (`/opt/git/eclipselink-4/`).

- Werden EclipseLink-spezifische Features sinnvoll genutzt?
- Dynamic Entity API, Descriptor Customization, ClassLoader-Integration
- Cache Coordination
- Batch Writing / Fetch Optimization
- Multi-Datenbank-Kompatibilität: EclipseLink DatabasePlatform-Abstraktion — wird sie korrekt genutzt?

#### F13 — EORM-Metamodell (eorm.ecore)
- Bildet das EORM-Modell alle benötigten JPA-Konzepte ab?
- Ist das Modell erweiterbar für zukünftige JPA-Features?
- Sind die Prozessoren vollständig für alle EORM-Elemente?
- Lücken in EORM die für F1-Mapping-Matrix benötigt werden?

#### F14 — Reverse Engineering (Database → Ecore)
- Welche Datenbanken werden unterstützt?
- Werden alle Spaltentypen korrekt auf EDataTypes gemappt?
- Werden Beziehungen (Foreign Keys) korrekt erkannt?
- Werden Indizes und Constraints berücksichtigt?

#### F15 — Usability & Developer Experience
- Wie einfach ist es, das Framework in ein neues Projekt einzubinden?
- Gibt es ausreichend Dokumentation und Beispiele?
- Sind Fehlermeldungen hilfreich und verständlich?
- Gibt es eine Migration-Strategie für Schema-Änderungen?

---

## 2. Review-Ergebnisse

**Severity-Stufen:**
- **Critical** — Blockiert produktiven Einsatz oder verursacht Datenverlust
- **Major** — Signifikante Einschränkung, Workaround möglich
- **Minor** — Verbesserungspotential, kein Blocker
- **Info** — Beobachtung, kein unmittelbarer Handlungsbedarf

**Hinweis:** Java-Version ist aktuell 17 (nicht 21). Java 21-exklusive Features (SequencedCollections, Record Patterns) sind nicht anwendbar. T5-Bewertung bezieht sich auf Java 14-17 Features.

> **Arbeitsregel: Testabdeckung bei jedem Fix kritisch hinterfragen!**
> Bei jedem Arbeitspaket muss die Testabdeckung aktiv geprüft und ggfs. als Teil des Fixes erhöht werden:
> - Reichen die vorhandenen Unit-Tests, um die Korrektheit des Fixes zu belegen?
> - Gibt es Edge-Cases die getestet werden müssen (Null, Grenzwerte, Fehlerfälle)?
> - Fehlen Integrations-/E2E-Tests? Falls ja: als TODO im verlinkten AP dokumentieren.
> - Sind deaktivierte Tests (@Disabled) betroffen? Falls ja: Reaktivierung als Teil des Fixes prüfen.
> - **Kein Fix ist fertig, solange die Testabdeckung nicht ausreichend ist.**

### 2.1 Technische Findings

#### Critical

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T2-01 | T2 | Falscher Import: `org.eclipse.persistence.internal.cache.Processor` (EclipseLink-interner Typ) im ORM-Modul — erzeugt unnötige Abhängigkeit auf EclipseLink-Interna | `NamedBaseProcessor.java:35` | Import entfernen |
| T2-02 | T2 | `BasicPersistenceEngine` implementiert `PersistenceEngine.dispose()` nicht — Ressourcen (EntityManager, Connections) können leaken | `BasicPersistenceEngine.java` | `dispose()` implementieren |
| T9-01 | T9 | ~~Bug: `EPersistenceContextImpl` zweiter Konstruktor setzt Properties auf Parameter statt Kopie~~ **FIXED (AP-04)** | `EPersistenceContextImpl.java:64` | ~~`this.persistenceUnit.setProperties(props)`~~ |
| T9-02 | T9 | ~~Race Condition: `emf`/`emfRegistration` ohne Synchronisation~~ **FIXED (AP-03)** — volatile Felder, ExecutorService shutdown, separierte deactivate-Logik | `EntityMappingPersistenceUnitConfigurator.java`, `PersistenceUnitConfigurator.java` | ~~volatile + shutdown~~ |

#### Major

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T2-03 | T2 | ~~`JPAResourceImpl.doSave()` und `delete()`: Kein Rollback bei Exception~~ **FIXED (AP-02)** — try/catch/rollback + IOException wrapping + 8 Unit-Tests | `JPAResourceImpl.java:102-123` | ~~try/catch mit `rollback()`~~ |
| T2-04 | T2 | `JPAResourceImpl.getEngine()` gibt hart `null` zurück — Vertragsbruch des Interfaces | `JPAResourceImpl.java:180-182` | Engine implementieren oder Optional verwenden |
| T2-05 | T2 | ~~`BaseReferenceProcessor.calculateCascadeType()` setzt `cascadeRemove` für Non-Containment~~ **FIXED (AP-01)** | `BaseReferenceProcessor.java:153-165` | ~~Non-Containment: persist+detach+refresh, KEIN remove~~ |
| T2-06 | T2 | `PersistenceUnitConfigurator` hardcodiert `H2Platform` als Target-Database — andere DBs nicht nutzbar | `PersistenceUnitConfigurator.java:208` | `TargetDatabase.Auto` wie in EntityMapping-Variante |
| T4-01 | T4 | `Options.CAP_USE_TIMESTAMP` und `CAP_TIMESTAMP_FIELD_NAME` haben identischen Wert `"TIMESTMAP_FIELD_NAME"` (Tippfehler: TIMESTMAP statt TIMESTAMP) | `Options.java:365,377` | Eigene Werte + Tippfehler korrigieren |
| T4-02 | T4 | `Keywords.CAPABILITY_NAMESPACE` enthält `"org.eclipse.fennec.persistence.old.old"` — Migrations-Artefakt | `Keywords.java:57-59` | Namespace bereinigen |
| T4-03 | T4 | `Options`-Javadoc referenziert nicht-existente Typen: `DBObjectBuilderImpl`, `NativeQueryEngine`, MongoDB-Formulierungen | `Options.java:40,49,153` | Javadoc auf JPA-Kontext aktualisieren |
| T4-04 | T4 | `EFeatureAccessor.accessorMap`: Statische ConcurrentHashMap ohne Eviction — Memory-Leak, Converter-Konflikte bei parallelen PUs | `EFeatureAccessor.java:45` | Cache auf PU-Scope beschränken, Eviction |
| T4-05 | T4 | Code-Duplikation: `PersistenceUnitConfigurator` und `EntityMappingPersistenceUnitConfigurator` nahezu identisch | `spi/PersistenceUnitConfigurator.java`, `spi/EntityMappingPersistenceUnitConfigurator.java` | Gemeinsame Basisklasse extrahieren |
| T4-06 | T4 | `EDynamicTypeContext` erbt von `ConcurrentHashMap` — Anti-Pattern, exponiert alle Map-Methoden | `EDynamicTypeContext.java:37` | Komposition statt Vererbung |
| T8-01 | T8 | `JPAResourceImpl.doLoad()`: `SELECT e FROM EntityName e` ohne Paginierung — OOM bei großen Tabellen | `JPAResourceImpl.java:85-93` | Paginierung via setFirstResult/setMaxResults |
| T8-02 | T8 | ~~`Executors.newSingleThreadExecutor()` wird nie heruntergefahren~~ **FIXED (AP-03)** — executor als Feld, shutdownNow+awaitTermination in deactivate | `EntityMappingPersistenceUnitConfigurator.java`, `PersistenceUnitConfigurator.java` | ~~ExecutorService shutdown~~ |
| T9-03 | T9 | ~~SQL-Injection-Risiko: `entityName` aus URI direkt in JPQL konkateniert~~ **FIXED (AP-09)** — nutzt jetzt `descriptor.getAlias()` | `JPAResourceImpl.java:85-87,164` | ~~Entity-Name validieren~~ |
| T9-04 | T9 | `NonContainmentConverter.toEObject`: Mutable State in Singleton — nicht thread-safe | `NonContainmentConverter.java:36` | State entfernen oder pro Konvertierung neue Instanz |
| T9-05 | T9 | ~~`BigIntegerInternalConverter.convertEMFToValue()`: `intValue()` — Überlauf~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:317` | ~~Native JDBC BigInteger~~ |
| T9-06 | T9 | ~~`BigDecimalInternalConverter`: `doubleValue()` — Präzisionsverlust~~ **FIXED (AP-08)** | `ComprehensiveTypeConverter.java:291,299` | ~~`BigDecimal` nativ an JDBC durchreichen~~ |
| T10-01 | T10 | Keine SBOM-Generierung (CycloneDX/SPDX) im Build — TR-03183-2 nicht erfüllt | `build.gradle` | CycloneDX-Gradle-Plugin integrieren |
| T10-02 | T10 | Kein SECURITY.md, keine Vulnerability-Disclosure-Policy — TR-03183-1 | Projekt-Root | SECURITY.md erstellen (Eclipse Foundation Template) |
| T10-03 | T10 | Default-Logging auf `FINE` — kann sensitive SQL/Parameter in Produktion loggen | `PersistenceUnitConfigurator.java:211` | Default auf `WARNING` oder `INFO` |
| T11-01 | T11 | SRP: `EDynamicTypeBuilder` ist God-Class (700+ Zeilen, 7 Verantwortlichkeiten) | `EDynamicTypeBuilder.java` | Aufteilen in IdBuilder, BasicBuilder, ReferenceBuilder, InheritanceBuilder |
| T11-02 | T11 | DIP: H2Platform hardcodiert, `TargetDatabase.Auto` nur in einem der zwei Konfiguratoren | `PersistenceUnitConfigurator.java:207` | Konfigurierbare Property |

#### Major (T5 — Code-Stil, gebündelt)

| ID | Kriterium | Finding | Umfang | Empfehlung |
|----|-----------|---------|--------|------------|
| T5-01 | T5 | FQCN statt Imports in Produktionscode (ECopier, EDynamicTypeBuilder, EORMModelHelper, BaseProcessor, EDynamicPersistenceUnitInfo) und Tests (60+ Stellen) | 11 Dateien, 60+ Stellen | Java-Imports verwenden, statische Imports für Utility-Methoden |
| T5-02 | T5 | Pattern Matching for `instanceof` nicht genutzt — überall alter Cast-Stil | 33 Stellen in 15 Dateien | `if (x instanceof Type t)` verwenden |
| T5-03 | T5 | Alte switch-Syntax (case/break) statt Arrow-Syntax | 5 Stellen | Switch-Expressions mit `->` |
| T5-04 | T5 | Inkonsistente `Objects.*`-Nutzung (mal static import, mal qualifiziert) | 8 Stellen | Einheitlich `import static java.util.Objects.*` |

#### Major (T6-T7 — OSGi & GeckoProjects, gebündelt)

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| T6-01 | T6 | 4 package-info.java ohne `@Export`/`@Version` (converter, helper, orm.loader) | Diverse package-info.java | Prüfen ob export gewollt, ggf. annotieren |
| T6-02 | T6 | `eclipselink.copying` und `eclipselink.dynamic` haben @Export aber KEIN @Version → Default 0.0.0 | copying/package-info.java, dynamic/package-info.java | `@Version("1.0.0")` ergänzen |
| T6-03 | T6 | SNAPSHOT-Abhängigkeiten (4 von 7 GeckoProjects-Libs) — Build nicht reproduzierbar | `cnf/ext/libraries.maven` | Release-Versionen oder Fennec-Äquivalente |
| T7-01 | T7 | **7 GeckoProjects-Maven-Abhängigkeiten** in libraries.maven, davon 2 vermutlich überflüssig (jakartars, search) | `cnf/ext/libraries.maven` | Auf Eclipse Fennec migrieren, überflüssige entfernen |
| T7-02 | T7 | GeckoProjects-URL hardcodiert: `PERSISTENCE_ANNOTATION_SOURCE = "http://org.geckoprojects.com/..."` | `Keywords.java:51` | Eclipse Fennec URL |
| T7-03 | T7 | SETUP.MD enthält durchgehend GeckoProjects-Referenzen — nicht migrierte Template-Datei | `cnf/SETUP.MD` | Aktualisieren oder entfernen |
| T7-04 | T7 | Private DIM-Nexus-Repository-URLs — für Eclipse-Projekt nur öffentliche Repos | `cnf/ext/libraries.bnd:3-4` | Nach Migration auf Fennec: Nexus-URLs entfernen |
| T6-04 | T6 | SonarQube-projectKey Tippfehler: `emf.peristence-jpa` statt `persistence` | `build.gradle:52` | Korrigieren |

#### Minor

| ID | Kriterium | Finding | Datei |
|----|-----------|---------|-------|
| T1-01 | T1 | `EPersistenceContext` (zentrales SPI) hat keinerlei Javadoc | `EPersistenceContext.java` |
| T1-02 | T1 | `EMFEntityManagerProvider` Interface — keine bekannte Implementierung, vermutlich tot | `EMFEntityManagerProvider.java` |
| T2-07 | T2 | `EORMMappingProvider.activate()` castet ohne null/instanceof Check | `EORMMappingProvider.java:86` |
| T3-01 | T3 | Keine Unit-Tests für JPAResourceImpl (doLoad, doSave, delete, count, getEObject) | — |
| T3-02 | T3 | Keine Unit-Tests für EntityManagerFactoryConfigurator, beide PU-Konfiguratoren | — |
| T3-03 | T3 | Keine Unit-Tests für EFeatureAccessor / EReferenceAccessor | — |
| T3-04 | T3 | Leerer catch-Block mit TODO in EPersistenceOneToManyTest | `EPersistenceOneToManyTest.java:92` |
| T4-07 | T4 | Auskommentierter Code in BasicPersistenceEngine (Zeilen 36-84) | `BasicPersistenceEngine.java` |
| T4-08 | T4 | Toter Code: MappingHelper innere Klassen MappingType/MappedBy unbenutzt | `MappingHelper.java:51-62` |
| T5-05 | T5 | Redundanter null-Check vor instanceof (`eClassifier != null && eClassifier instanceof EClass`) | `EMFHelper.java:82` |
| T6-05 | T6 | `ecore`-Modul: Kein EPL-2.0 Lizenz-Header in package-info.java | `ecore/package-info.java` |
| T10-04 | T10 | Nur teilweise Signed-off-by in Commits, kein DCO-Enforcement | — |
| T11-03 | T11 | `PersistenceEngine` Interface mischt Business- und Lifecycle-Methoden | `PersistenceEngine.java` |
| T8-03 | T8 | Alle DirectToFieldMappings mit `setIsLazy(false)` — kein Lazy für große Attribute (LOBs) | `EDynamicTypeBuilder.java:482` |

### 2.2 Fachliche Findings

#### Critical

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| F1-01 | F1 | ~~**OrphanRemoval invertiert**~~ **FIXED (AP-01)** — Containment=true, Non-Containment=false, mit Tests | `OneToOneProcessor.java:76`, `OneToManyProcessor.java:98,106` | ~~Invertieren~~ |
| F1-02 | F1 | ~~**CascadeRemove bei Non-Containment**~~ **FIXED (AP-01)** — Non-Containment hat jetzt persist+detach+refresh, kein remove. Tests prüfen explizit dass cascadeRemove null ist | `BaseReferenceProcessor.java:153-165` | ~~Nur cascadePersist+Detach+Refresh~~ |
| F9-01 | F9 | ~~**Non-Containment Proxy-Resolution unvollständig**~~ **FIXED (AP-05)** — Aufgelöste Objekte werden in Resource-Contents eingefügt. **TODO:** E2E-Test über ResourceSet (→ AP-30) | `JPAResourceImpl.java:147-175` | ~~Aufgelöstes EObject in Resource einfügen~~ |
| F7-01 | F7 | ~~**Kein Rollback bei Exceptions**~~ **FIXED (AP-02)** — Rollback bei Exceptions, Contents bei delete erst nach commit gecleart. **TODO:** E2E-Test für Rollback-Konsistenz (→ AP-11) | `JPAResourceImpl.java:102-123` | ~~try/catch/rollback~~ |

#### Major

| ID | Kriterium | Finding | Datei | Empfehlung |
|----|-----------|---------|-------|------------|
| F1-03 | F1 | Nur SINGLE_TABLE Inheritance implementiert — JOINED und TABLE_PER_CLASS fehlen, obwohl EORM-Modell sie unterstützt | `EntityProcessor.java:143` | InheritanceType konfigurierbar machen |
| F1-04 | F1 | @Embedded/@Embeddable: EORM-Metamodell vorhanden, aber kein Processor implementiert | — | EmbeddableProcessor implementieren |
| F1-05 | F1 | @MappedSuperclass: EORM-Metamodell vorhanden, aber nicht implementiert — abstrakte EClasses werden immer als Entity gemappt | — | Design-Entscheidung dokumentieren oder implementieren |
| F1-06 | F1 | @Version (Optimistic Locking): EORM-Metamodell vorhanden, kein Processor, kein Runtime-Support | — | VersionProcessor implementieren |
| F1-07 | F1 | Derived/volatile EFeatures werden als normale Attribute gemappt statt gefiltert | `MappingProcessor.java` | Filtern wie transient |
| F1-08 | F1 | Doppelte Mapping-Erzeugung in Stage 4: Single-valued Non-Containment mit Opposite bekommt O2O UND M2O Mapping | `MappingProcessor.java:234-268` | Filter-Logik korrigieren |
| F2-01 | F2 | **Update nur über JPAResource getestet**, nicht auf EntityManager-Ebene (attached eSet→flush, detached merge) | Tests | Granulare Update-Tests auf EM-Ebene |
| F2-02 | F2 | **Delete nur über JPAResource getestet** — kein Test für Cascade-Delete bei Containment, FK-Constraints | Tests | Delete-Szenarien einzeln testen |
| F2-03 | F2 | **Kein Identity-Map-Test:** Wird `em.find(id)` im selben EM dasselbe Java-Objekt zurückgegeben? | Tests | `assertSame`-Test |
| F2-04 | F2 | **Kein Optimistic-Locking-Test** — @Version weder implementiert noch getestet | Tests + Code | Erst F1-06 implementieren, dann testen |
| F2-05 | F2 | Tests zu groß: Ein Test deckt Setup+Persist+Find ab — widerspricht Prinzip "kleine, nachvollziehbare Tests" | Tests | Aufteilen in Einzel-Aspekt-Tests |
| F3-01 | F3 | ~~**BigDecimal/BigInteger Converter: Präzisionsverlust/Überlauf**~~ **FIXED (AP-08)** — Native Durchreichung, Precision- und Range-Tests ergänzt | `ComprehensiveTypeConverter.java:291,299,317,318` | ~~Native JDBC-Typen~~ |
| F3-02 | F3 | **ZonedDateTime: Zeitzonen-Information geht verloren** — nutzt `ZoneId.systemDefault()` bei Rückkonvertierung | `ZonedDateTimeConverter.java` | Zeitzone separat speichern oder ISO-8601 |
| F3-03 | F3 | **Enum-Converter nicht registriert:** `converters.get("enum")` in findConverter referenziert, aber keiner vorhanden | `ComprehensiveTypeConverter.java:141-143` | Enum-Converter registrieren oder dokumentieren |
| F3-04 | F3 | **TypeConverterEndToEndTest ist @Disabled** — "Tables are not created appropriately" | `TypeConverterEndToEndTest.java` | Test fixen und aktivieren |
| F3-05 | F3 | Fehlende Converter: URI, URL, Currency, Locale, OffsetDateTime, OffsetTime, Year, YearMonth | — | Converter ergänzen nach Bedarf |
| F4-01 | F4 | **Kein Test für 3+-Ebenen Containment** — MeterTargetPersistenceTest (Plant→MeteringPoint→MeterReading) komplett auskommentiert | `MeterTargetPersistenceTest.java` | Test reaktivieren |
| F4-02 | F4 | OneToMany Containment erzeugt JoinTable statt JoinColumn — dokumentierter Bug in `testGltContainmentNoCache` | `EPersistenceGltContainmentTest.java` | OneToManyProcessor Fix |
| F4-03 | F4 | Polymorphe Queries nicht getestet (findAll auf Basis-EClass) | Tests | Test ergänzen |
| F4-04 | F4 | Cross-Resource-Referenzen nicht getestet | Tests | Test ergänzen |
| F5-01 | F5 | **DatabaseEcoreParser nur PostgreSQL-Typen** — `int4`, `float8`, `bpchar` etc. Standard-JDBC-Typen (INTEGER, BIGINT, TIMESTAMP) fehlen komplett | `DatabaseEcoreParser.java:219-257` | JDBC-Standard-Typen ergänzen |
| F5-02 | F5 | **Citizen-Test komplett auskommentiert** — kein aktiver E2E-Test für Custom-EORM | `EPersistenceCitizenTest.java` | Test reaktivieren |
| F5-03 | F5 | Keine View-Unterstützung, keine Schema-Evolution | — | Als Feature planen |
| F6-01 | F6 | **ProxyURI wird auf gecachtem Objekt gesetzt** statt auf Kopie — kann EclipseLink-Cache korrumpieren | `EBasicIndirectionPolicy.java:86` | Kopie erstellen vor ProxyURI-Setzung |
| F6-02 | F6 | Weaving="static" konfiguriert aber irrelevant für DynamicEObjectImpl — irreführend | `PersistenceUnitConfigurator.java:204` | Auf "false" setzen |
| F8-01 | F8 | **Kein bidirektionales Change-Tracking:** `eSet()` ohne EntityManager wird von EclipseLink nicht erkannt — nur Deferred-Detection via Backup-Clone | Code | EContentAdapter als Brücke evaluieren |
| F9-02 | F9 | ~~`doUnload()` setzt `isLoaded` nicht zurück~~ **FIXED (AP-02)** — `isLoaded = false` in doUnload gesetzt | `JPAResourceImpl.java:127-129` | ~~`isLoaded = false`~~ |
| F9-03 | F9 | NonContainmentConverter und EBasicIndirectionPolicy sind zwei parallele Proxy-Mechanismen ohne klare Abgrenzung | Architektur | Einen Mechanismus wählen und dokumentieren |
| F10-01 | F10 | ~~Race Condition + Executor-Leak bei EMF-Erstellung~~ **FIXED (AP-03)** (doppelt zu T9-02/T8-02) | Konfiguratoren | ~~volatile + shutdown~~ |
| F12-01 | F12 | Kein Batch Writing, kein Fetch-Size konfiguriert | Konfiguratoren | EclipseLink-Properties exponieren |
| F14-01 | F14 | DatabaseEcoreParser: FK-Kollision bei `Collectors.toMap` ohne Merge-Funktion → `IllegalStateException` | `DatabaseEcoreParser.java:103,107` | Merge-Funktion angeben |
| F15-01 | F15 | ~~**README.md beschreibt falsches Projekt**~~ **FIXED (AP-07)** — README komplett neu geschrieben | `README.md` | ~~Komplett neu schreiben~~ |
| F15-02 | F15 | ~~Keine Anwendungsdokumentation~~ **FIXED (AP-07)** — Quick Start, Architektur, Module Overview in README. **TODO:** Erweitertes Getting-Started mit vollständigem Beispiel | — | ~~Dokumentation erstellen~~ |

#### Minor

| ID | Kriterium | Finding | Datei |
|----|-----------|---------|-------|
| F1-09 | F1 | @Lob nicht automatisch erkannt für byte[]-Attribute | `BaseProcessor.java` |
| F1-10 | F1 | EmbeddedId/IdClass nur als Workaround (multiple @Id statt echte Annotation) | `CompositeIdProcessor.java:97,126` |
| F1-11 | F1 | SequenceGenerator + UUID-Generator gleichzeitig im Legacy-Pfad | `EntityProcessor.java:239-250` |
| F2-06 | F2 | Viel auskommentierter Code in Tests (EPersistenceAttributeTest, ManyToManyTest) | Tests |
| F2-07 | F2 | Hardcoded Pfad `/home/mark/test.eorm` in Test | `EPersistenceOneToManyTest.java:86` |
| F3-06 | F3 | PrimitiveArrayConverter: Nur int[], double[], boolean[] — andere Primitiv-Arrays geben null zurück | `ComprehensiveTypeConverter.java:449` |
| F3-07 | F3 | DefaultConverter: Kein Null-Handling — NPE bei null-Werten | `DefaultConverter.java` |
| F5-04 | F5 | EPersistenceRepositoryTest komplett auskommentiert | Test |
| F9-04 | F9 | Fragment-Auflösung ignoriert `refName` — nur idValue wird für DB-Lookup genutzt | `JPAResourceImpl.java:138-140` |
| F10-02 | F10 | Connection-Pool-Maximum nicht konfiguriert (EclipseLink Default: 32) | Konfiguratoren |
| F12-02 | F12 | OSGiDynamicClassloader minimal — nur DynamicEObjectImpl, keine Consumer-Bundle-Klassen | `OSGiDynamicClassloader.java` |
| F13-01 | F13 | EORM-Metamodell vollständig, aber 6+ Konzepte ohne Processor (Embedded, MappedSuperclass, Version, Index, NamedQuery, Converter) | — |
| F14-02 | F14 | Indexes, UniqueConstraints, NOT NULL werden nicht ausgelesen | `DatabaseEcoreParser.java` |
| F15-03 | F15 | Kritische Fehler in EDynamicTypeBuilder nur geloggt, nicht geworfen → subtile Laufzeitfehler | `EDynamicTypeBuilder.java:595-597,645-647` |
| F15-04 | F15 | Options-Klasse enthält MongoDB-Legacy-Konstanten und -Kommentare | `Options.java` |

#### Info / Positiv

| ID | Kriterium | Finding |
|----|-----------|---------|
| F6-P1 | F6 | **Stärke:** ECopier — durchdachte Lösung für saubere EObjects ohne JPA-Artefakte. Priority-Chain-Konzept architektonisch sauber. |
| F6-P2 | F6 | **Stärke:** Architektur umgeht Weaving-Problem geschickt durch DynamicEObjectImpl + EBasicIndirectionPolicy statt Bytecode-Weaving |
| F8-P1 | F8 | **Stärke:** EFeatureAccessor/EReferenceAccessor als bidirektionale EMF↔JPA-Brücke konzeptionell richtig |
| F12-P1 | F12 | **Stärke:** Dynamic Entity API kreativ genutzt — Runtime-JPA-Entities aus Ecore-Modellen |
| F12-P2 | F12 | **Stärke:** EClassDescriptor, EObjectBuilder, EInstantiationPolicy, ECopyPolicy — alle EclipseLink-Erweiterungspunkte korrekt überschrieben |
| F13-P1 | F13 | **Stärke:** EORM-Metamodell (eorm.ecore) ist sehr umfassend und deckt nahezu alle JPA 3.1 Konzepte ab |
| F7-I1 | F7 | Nur RESOURCE_LOCAL, kein JTA — bewusste Design-Entscheidung für OSGi, aber nirgends dokumentiert |
| F11-I1 | F11 | Query-API: Keine dedizierte API. Options-Klasse enthält MongoDB-Legacy-Konstanten. EORM hat NamedQuery/NamedNativeQuery, aber kein Runtime-Code. Grundlage für EMF-Query-Integration vorhanden. |

---

## 3. Arbeitspakete

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
| AP-12 | **Inheritance-Strategien:** JOINED und TABLE_PER_CLASS implementieren (Processor + EDynamicTypeBuilder) | F1-03 | M | ❌ Offen |
| AP-13 | **@Version / Optimistic Locking:** Processor + Runtime + Tests | F1-06, F2-04 | M | ❌ Offen |
| AP-14 | **Deaktivierte Tests reaktivieren:** MeterTargetPersistenceTest, EPersistenceCitizenTest, TypeConverterEndToEndTest (inkl. DB-Round-Trip für BigDecimal/BigInteger aus AP-08), EPersistenceRepositoryTest | F4-01, F5-02, F3-04, F5-04 | M | ❌ Offen |
| AP-15 | **DatabaseEcoreParser erweitern:** JDBC-Standard-Typen, FK-Kollisions-Fix, NOT NULL, Constraints | F5-01, F14-01, F14-02 | M | ❌ Offen |
| AP-16 | **EDynamicTypeBuilder refactoring:** God-Class aufteilen in spezialisierte Builder | T11-01 | L | ❌ Offen |
| AP-17 | **PU-Konfigurator-Deduplizierung:** Gemeinsame Basisklasse für PersistenceUnitConfigurator und EntityMappingPersistenceUnitConfigurator | T4-05 | M | ❌ Offen |
| AP-18 | **EFeatureAccessor Cache Fix:** Static Cache auf PU-Scope, Eviction, Thread-Safety für Converter | T4-04, T9-04 | M | ❌ Offen |
| AP-19 | **Change-Tracking-Brücke evaluieren:** EContentAdapter für EMF→JPA Dirty-Notification, oder bewusste Limitation dokumentieren | F8-01 | M | ❌ Offen |
| AP-20 | **ProxyURI auf Cache-Kopie:** EBasicIndirectionPolicy darf nicht direkt auf gecachtem Objekt setzen | F6-01 | S | ❌ Offen |
| AP-21 | **Proxy-Mechanismus konsolidieren:** NonContainmentConverter vs. EBasicIndirectionPolicy — einen wählen, dokumentieren | F9-03 | M | ❌ Offen |
| AP-22 | **ZonedDateTime Zeitzonen-Fix:** Zeitzone separat speichern oder ISO-8601 String | F3-02 | S | ❌ Offen |

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

**Schwächen:**
- ~~**Kritische Bugs:** OrphanRemoval invertiert, CascadeRemove bei Non-Containment, Race Conditions~~ ✅ Gefixt (AP-01, AP-02, AP-03, AP-04)
- **Architektonische Lücke:** Non-Containment-Proxy-Resolution liefert keine eigene EMF Resource
- **Unvollständige Migration:** 7 GeckoProjects-Abhängigkeiten, veraltete Namespaces
- **Test-Gaps:** Update/Delete nur über JPAResource, viele deaktivierte Tests, keine Cache/Lifecycle-Tests
- **Dokumentation:** README beschreibt falsches Projekt, keine Anwendungsdokumentation

### 4.2 Top-Risiken

1. ~~**Datenverlust-Risiko:** CascadeRemove + invertiertes OrphanRemoval~~ ✅ Gefixt (AP-01)
2. ~~**Inkonsistenz-Risiko:** Fehlende Transaktions-Absicherung~~ ✅ Gefixt (AP-02)
3. ~~**Resource-Leak:** Race Condition + Thread-Leak~~ ✅ Gefixt (AP-03)
4. **Architektonisches Risiko:** Ohne eigene EMF Resource für aufgelöste Non-Containment-Proxies ist das EMF-Proxy-Pattern nicht vollständig — Weaving-Freiheit nicht garantiert (AP-05)
5. **Eclipse-Fennec-Blocker:** GeckoProjects-Abhängigkeiten verhindern saubere Integration in Eclipse Fennec (AP-06)

### 4.3 Empfohlene Reihenfolge der Arbeitspakete

**Welle 1 — Bugfixes (sofort):** AP-01, AP-04, AP-08, AP-09 (alle S) → ✅ Erledigt
**Welle 2 — Safety (kurzfristig):** AP-02, AP-03 (beide M) → ✅ Erledigt
**Welle 3 — Architektur (mittelfristig):** AP-05, AP-07, AP-10 → Eclipse Fennec Readiness (AP-06 nach P2 verschoben — erfordert gemeinsame Prüfung)
**Welle 4 — Testabdeckung:** AP-11, AP-14 → Vertrauen in Korrektheit aufbauen
**Welle 5 — Feature-Gaps:** AP-12, AP-13, AP-15 → JPA-Vollständigkeit
**Welle 6 — Qualität:** AP-16, AP-17, AP-23 → Wartbarkeit und Code-Modernisierung
**Welle 7 — Erweiterungen:** AP-24 bis AP-35 → Nice-to-have Features

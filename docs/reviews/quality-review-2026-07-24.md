# Quality review — emf.persistence-jpa — 2026-07-24

Mode: quick · Scope: whole repo · Rule sets: SOLID/OSGi + Eclipse Foundation (see skill references)

## Summary

| Severity | api-hygiene | api-versioning | release-readiness | bnd-metadata | docs | Total |
|----------|-------------|----------------|-------------------|--------------|------|-------|
| blocker  | 0 | 0 | 0 | 0 | 0 | **0** |
| major    | 2 | 1 | 3 | 0 | 0 | **6** |
| minor    | 0 | 0 | 2 | 1 | 0 | **3** |
| info     | 1 | 0 | 1 | 0 | 2 | **4** |

Overall the codebase is in very good shape. License headers are present on **every** hand-written `.java` file (src and test), DS lifecycle discipline is exemplary (every `deactivate()` undoes its `activate()` — registrations unregistered, Mongo clients closed, liveness gates closed), the three sibling `QueryProcessor` implementations (JPA/Mongo/memory) are contract-consistent (`backend()`/`capabilities()`/`validate()`/`translate()` with identical exception discipline — no LSP drift), and the whiteboard/target-filter patterns are used exactly where Open/Closed calls for them (pluggable `QueryProcessor` by `persistence.query.backend`, `JPAUnit` whiteboard by `osgi.unit.name`). No SOLID violations worth reporting were found in hand-written code.

The findings cluster in two areas instead: **API surface hygiene** (implementation classes and DS components live in exported packages; two exported packages are unversioned; one exported package couples to EclipseLink internals) and **release-readiness** (missing Dash/DEPENDENCIES tooling, missing license-check workflow, missing CODE_OF_CONDUCT.md). Nothing blocks day-to-day development, but the release-readiness items must be closed before a first Eclipse release.

**Status update (2026-07-24, same day):** F4–F8 were addressed after the review. F4: `CODE_OF_CONDUCT.md` (Eclipse Community CoC 2.0) added from emf.osgi. F5: `tools/dash-licenses.sh`/`.bat` and `.github/workflows/dash-licenses.yml` added (adapted: PMI id `modeling.fennec`, `-batch 50` because the Foundation endpoint 504s on the default batch size); `DEPENDENCIES` generated and committed — 280/290 approved, **10 `restricted` entries remain** (bnd/Felix/report-tooling artifacts pulled in via the fennec workspace libraries) and need IP review via `tools/dash-licenses.sh --review --project modeling.fennec` with an IPLab token before a release. F6: resolved as no-change — the pinned `eclipse-fennec/.github` `reusable-verify.yml` already runs apache/skywalking-eyes as a license gate on every push/PR, so a separate `license.yml` would duplicate it. F7: PMI id confirmed as `modeling.fennec` (projects.eclipse.org; `technology.fennec` does not exist) — `NOTICE.md` fixed. F8: GitHub Security Advisories URL added to `SECURITY.md` as the preferred reporting channel; MongoDB driver and Jackson added to `NOTICE.md`'s notable dependencies while at it. F12: Java version corrected to 21 in `CLAUDE.md`, `README.md`, `docs/getting-started.md` and `docs/mongo-user-guide.md`. F13: the three leftover directories deleted and `pushstreams/test/.keep` staged — with the correction that the directory only held a `.keep` placeholder, not test sources as originally claimed. F3: `@Export` removed from `descriptors/package-info.java` (the package is now in `Private-Package`); this surfaced one genuine API leak — `ECopyPolicy.getDescriptor()`'s covariant `EClassDescriptor` return type in the still-exported `copying` package — fixed by removing the unnecessary override (all call sites work against EclipseLink's public `ClassDescriptor`). The `copying` package itself must stay exported: the OSGi integration test in `persistence.test` consumes it. F9: `Bundle-Name`/`Bundle-Description` added to the ecore `bnd.bnd`.

## Findings

### F1 · major · api-hygiene · eclipselink / mongo / query (systemic, see below)
- **Where:** representative: `org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/query/package-info.java:13` exporting the package that contains the DS component `JpaQueryProcessor.java:82`
- **What:** Six exported packages contain DS component classes and/or `*Impl` classes that no other bundle references as a type: `eclipselink.query` (`JpaQueryProcessor`), `eclipselink.resource` (`JPAResourceFactoryComponent`, `JPAResourceImpl`), `eclipselink.spi` (`PersistenceUnitConfigurator`, `EntityMappingPersistenceUnitConfigurator`, `EPersistenceContextImpl`), `mongo.query` (`MongoQueryProcessor`), `mongo.resource` (`MongoResourceFactory`, `MongoResourceImpl`), `query.memory` (`MemoryQueryProcessor`).
- **Why it matters:** Everything public in an exported package is API: once released, these classes fall under binary-compatibility rules (§5 of the Eclipse guidelines) and can no longer be refactored freely. Consumers bind the processors as `QueryProcessor` *services*, so the component classes never need to be visible as types outside their bundle (verified: the only cross-package references are same-bundle). The 1180-line `JPAResourceImpl` and 1045-line `MongoResourceImpl` would become frozen API surface.
- **Suggested fix:** Split each package into an exported API part (plan types like `JpaQueryPlan`/`MongoQueryPlan`/`MemoryQueryPlan`, interfaces like `EPersistenceContext`/`JPAUnit`, backend-id constants) and a private impl part holding the DS components and `*Impl` classes (drop `@Export` or move classes to an unexported sibling package). Rated major, not blocker, because the packages are not impl-only — they deliberately mix genuine API (plan types must be visible to the resource executing the plan) with implementation.

### F2 · major · api-versioning · org.eclipse.fennec.persistence.eclipselink
- **Where:** `org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/copying/package-info.java:15` and `.../dynamic/package-info.java:15`
- **What:** Both packages carry `@org.osgi.annotation.bundle.Export` but no `@Version` annotation; the built manifest falls back to the bundle version (`0.1.0`), while every other exported package in the workspace is explicitly versioned (`1.0.0`/`2.0.0`).
- **Why it matters:** The package version silently changes with every bundle version bump, breaking semantic-versioning contracts for importers and making future baselining (§5/§6) report spurious changes.
- **Suggested fix:** Add `@org.osgi.annotation.versioning.Version("1.0.0")` to both `package-info.java` files — or, per F1, reconsider whether `copying` and `dynamic` (EclipseLink integration internals) should be exported at all.

### F3 ✅ · major · api-hygiene · org.eclipse.fennec.persistence.eclipselink
- **Where:** `org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/descriptors/EObjectBuilder.java:37` (`public class EObjectBuilder extends ObjectBuilder` — an `org.eclipse.persistence.internal.descriptors` type); package exported via `descriptors/package-info.java`
- **What:** The exported `descriptors` package (v1.0.0) exposes EclipseLink **internal** types in its public API surface — the manifest `uses:` clause carries `org.eclipse.persistence.internal.descriptors`, `internal.identitymaps`, `internal.sessions`; `EInstantiationPolicy` likewise extends an internal EclipseLink class.
- **Why it matters:** EclipseLink internal packages have no versioning or compatibility guarantee; exporting API whose type hierarchy transits them means any EclipseLink upgrade can break this bundle's *published* API, not just its implementation. No other bundle in the workspace references `eclipselink.descriptors` (verified against `mongo`, `test`, `tck`).
- **Suggested fix:** Remove `@Export` from `descriptors/package-info.java` (make the package private like the sibling `mappings`/`indirection`/`helper` packages, which correctly stayed unexported despite the same internal coupling).

### F4 ✅ · major · release-readiness · repo root
- **Where:** repo root (file absent; expected `CODE_OF_CONDUCT.md`)
- **What:** `CODE_OF_CONDUCT.md` is missing; the other five required root documents (`LICENSE`, `NOTICE.md`, `README.md`, `CONTRIBUTING.md`, `SECURITY.md`) exist and are adapted.
- **Why it matters:** All six root documents are required for Eclipse Foundation projects; release review will flag its absence.
- **Suggested fix:** Copy `CODE_OF_CONDUCT.md` (Eclipse Community Code of Conduct 2.0) from `eclipse-fennec/emf.osgi` — no adaptation needed.

### F5 ✅ · major · release-readiness · repo root (tooling + DEPENDENCIES in place; 10 restricted entries still pending IP review)
- **Where:** repo root (files absent; expected `DEPENDENCIES`, `tools/dash-licenses.sh`, `.github/workflows/dash-licenses.yml`)
- **What:** No Dash IP-check tooling exists: no `DEPENDENCIES` file, no `tools/` scripts, no dash workflow. `.github/workflows/dependency-review.yml` exists but is GitHub's vulnerability review, not Eclipse IP review.
- **Why it matters:** Eclipse IP cleanliness requires a generated, reviewed, committed `DEPENDENCIES` file with zero `restricted` entries at release time; without the workflow, third-party additions (e.g. the new MongoDB driver and Jackson dependencies) are never IP-checked.
- **Suggested fix:** Copy `tools/dash-licenses.sh`/`.bat` and `.github/workflows/dash-licenses.yml` from `eclipse-fennec/emf.osgi`, generate `DEPENDENCIES` from the bnd workspace (`bnd repo deps` based script), review and commit it. Restricted entries need `dash-licenses.sh --review --project <PMI id>` (dotted PMI id — see F8: confirm whether that is `technology.fennec` or `modeling.fennec`).

### F6 ✅ · major · release-readiness · repo root (no change needed — covered by reusable-verify)
- **Where:** `.github/workflows/` (file absent; expected `license.yml`); `.licenserc.yaml:1` exists and is adapted
- **What:** `.licenserc.yaml` is present and well-adapted, but no `license.yml` workflow (apache/skywalking-eyes) runs it — header compliance is currently unenforced in CI.
- **Why it matters:** Headers are 100 % compliant today (verified by scan), but nothing keeps them that way; the guideline treats missing enforcement as major even when the headers themselves are fine.
- **Suggested fix:** Add `.github/workflows/license.yml` from `eclipse-fennec/emf.osgi`. Caveat: if the shared `eclipse-fennec/.github` `reusable-verify.yml` already runs skywalking-eyes, this finding reduces to "make that visible" — worth confirming once, since the repo pins that reusable workflow.

### F7 ✅ · minor · release-readiness · repo root
- **Where:** `NOTICE.md:5` (`technology.fennec`) vs `SECURITY.md:5` (`modeling.fennec`)
- **What:** The two documents name different Eclipse PMI project ids for the same project.
- **Why it matters:** One of them is wrong; the PMI id is also the parameter for Dash IP review (F5), so the wrong id propagates into tooling.
- **Suggested fix:** Confirm the project's PMI id on projects.eclipse.org and align both files.

### F8 ✅ · minor · release-readiness · repo root
- **Where:** `SECURITY.md:8-13`
- **What:** The vulnerability-reporting section points to the Eclipse security team page and `security@eclipse.org`, but not to this repository's GitHub Security Advisories URL.
- **Why it matters:** The fennec release guide expects the repo-specific advisories URL (`https://github.com/eclipse-fennec/emf.persistence-jpa/security/advisories`) as the primary private-reporting channel.
- **Suggested fix:** Add the GitHub advisories link as reporting option 1, keeping the Eclipse security team as the alternative.

### F9 ✅ · minor · bnd-metadata · org.eclipse.fennec.persistence.ecore
- **Where:** `org.eclipse.fennec.persistence.ecore/bnd.bnd:1`
- **What:** The only bundle in the workspace without `Bundle-Name`/`Bundle-Description` headers.
- **Why it matters:** The generated manifest falls back to the BSN; repository browsers and OBR indexes show no human-readable description.
- **Suggested fix:** Add the two headers, e.g. `Bundle-Name: Eclipse Fennec Ecore Reverse Engineering`.

### F10 · info · release-readiness · cnf
- **Where:** `cnf/build.bnd:15` (`#fennec-baselining: true`)
- **What:** Baselining is commented out. The repo has no git tags/releases yet, so this is currently correct — flagged only so it isn't forgotten.
- **Suggested fix:** Enable `fennec-baselining: true` together with the first release (release-OBR orphan branch from the same release run), otherwise the API-evolution rules (§5) are unenforced from release two onward.

### F11 · info · api-hygiene · org.eclipse.fennec.persistence.tck
- **Where:** `org.eclipse.fennec.persistence.tck/bnd.bnd:1`; sources under `test/` only
- **What:** The TCK's reusable base classes (`AbstractPersistenceTCK`, `JpaTckSupport`, `MongoTestSupport`) live in the `test/` source tree, so the built bundle contains only an empty private package — the "Backend-agnostic compatibility tests" the description promises are not consumable by an external backend implementation.
- **Suggested fix:** If external backends should be able to run the TCK, move the abstract TCK classes to `src/` and export the package; if the TCK is intentionally repo-internal, adjust the `Bundle-Description`.

### F12 ✅ · info · docs · repo root
- **Where:** `cnf/build.bnd:24` (`javac.source: 21`) vs `CLAUDE.md` ("Java version: 17")
- **What:** The workspace compiles for Java 21; the contributor documentation still says 17.
- **Suggested fix:** Update `CLAUDE.md` (and check README/docs-site for the same drift).

### F13 ✅ · info · housekeeping · working tree
- **Where:** `org.eclipse.fennec.persistence.eorm.converter/`, `org.eclipse.fennec.persistence.jpa.bom/`, `org.eclipse.fennec.persistence.jpa.library.workspace/` (untracked, contain only `bin`/`generated` build leftovers); `org.eclipse.fennec.persistence.pushstreams/test/` (untracked)
- **What:** Three orphan directories are local build leftovers from the project renames (no `bnd.bnd`, nothing tracked by git) — stale artifacts like `org.eclipse.fennec.persistence.eorm.converter.jar` can still be picked up from local repos. Separately, `pushstreams/test/` holds only a `.keep` placeholder for the bnd test source directory, not yet added to git. (Correction: the original finding said this directory contained real test sources — it does not.)
- **Suggested fix:** Delete the three leftover directories; `git add` the `pushstreams/test/.keep` placeholder.

## Systemic issues

- **Exported implementation surface (F1)** spans three bundles (`eclipselink`, `mongo`, `query`) and follows one root cause: the package-per-feature layout puts API types (query plans, SPI interfaces, constants) and their implementing DS components in the *same* package, so exporting the API drags the implementation into the public surface. A workspace-wide convention — components/`*Impl` in a private sibling package (as `orm.loader`, `mongo.config`, `persistence.converter`, `persistence.liveness.impl` already do correctly) — fixes all six occurrences the same way and is much cheaper before the first release than after.

## Skipped / not reviewed

- **EMF-generated code** (`src-gen/` in the six model bundles, `generated/`): structural checks suppressed per fennec idiom; their exported `impl`/`util` packages are the allowed EMF convention. Header checks ran (via `.licenserc.yaml` ignores, generated code is exempt).
- **Full reads:** all 16 `bnd.bnd` files, all hand-written `package-info.java` exports, generated manifests of every bundle, `JPAResourceFactoryComponent`, `MongoClientComponent`, `PersistenceUnitConfigurator`, repo root documents and workflows.
- **Skimmed** (structure, lifecycle hooks, and signatures only): `JPAResourceImpl` (1180 lines), `MongoResourceImpl` (1045 lines), `ECopier`, the ORM processors, `AbstractPersistenceUnitConfigurator`, liveness components, the three `QueryProcessor` implementations (compared as siblings), converter components.
- **Not reviewed:** `docs-site/`, `org.eclipse.fennec.persistence.test` beyond header/naming checks (test bundle), `bom`/`workspace.library` (no Java), external reusable workflows in `eclipse-fennec/.github` (pinned by hash, content not fetched).

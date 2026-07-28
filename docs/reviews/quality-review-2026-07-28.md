# Quality review — emf.persistence-jpa — 2026-07-28

Mode: quick (followup) · Scope: whole repo · Rule sets: SOLID/OSGi + Eclipse Foundation (see skill references) · Previous report: [quality-review-2026-07-24.md](quality-review-2026-07-24.md)

## Summary

| Severity | api-hygiene | api-versioning | release-readiness | docs | Total |
|----------|-------------|----------------|-------------------|------|-------|
| blocker  | 0 | 0 | 0 | 0 | **0** |
| major    | 1 | 1 | 1 | 0 | **3** |
| minor    | 0 | 0 | 1 | 0 | **1** |
| info     | 1 | 0 | 1 | 1 | **3** |

The codebase is essentially unchanged since the 2026-07-24 review — the only delta is the `org.eclipse.fennec.model.metadata` → `model.metadata` + `model.metadata.api` dependency split (bnd.bnd/bndrun updates), a `.licenserc.yaml` ignore for `DEPENDENCIES`, and a handover doc. The delta itself is clean: no new Java code, no export changes, license headers remain 100 % compliant (re-scanned), workflow triggers remain push-based (no PR-triggered publishing), and the root documents fixed on 2026-07-24 are all in place and correct.

Of the 13 previous findings, **9 are resolved, 2 are partially resolved, and 2 are still open** — and both still-open ones (exported implementation surface, two unversioned exported packages) are the same two majors as last time; they are now recurring. One new minor emerged from the dependency split: the committed `DEPENDENCIES` file was not regenerated and no longer reflects the buildpath. The 10 `restricted` Dash entries flagged last time are still pending IP review and remain the main release blocker-in-waiting.

**Status update (2026-07-28, same day):** F3 largely defused and F4 fixed. An audit of the 10 `restricted` entries showed none is actually used by any `-buildpath`/`-testpath` or checked-in `-runbundles`: 7 were unused lines in the repo-owned `cnf/central.mvn` (`aQute.libg`, `biz.aQute.gogo.commands.provider`, `biz.aQute.wrapper.hamcrest`, `felix.http.jetty` 4.2.8, `felix.threaddump`, `felix.webconsole.plugins.useradmin`, `jackson-dataformat-properties`) and were deleted; `DEPENDENCIES` was regenerated (`tools/dash-licenses.sh`, exit code 3). Restricted count: **10 → 3**. The remaining three (`SODS`, `fastcsv`, `servicemix.bundles.poi`) are injected by the external fennecCodec workspace library's Maven index (backing the tabular codecs `codec.csv`/`codec.ods`/`codec.xlsx`, unused here) and cannot be removed from this repo — they need either an upstream split of the fennecCodec index or IP review via `--review --project modeling.fennec`. The regeneration also fixed F4 as a side effect: the stale `org.eclipse.fennec.codec/org.eclipse.fennec.model.metadata/0.1.0-SNAPSHOT` entry was replaced by the current split artifacts (`org.eclipse.fennec.metadata/org.eclipse.fennec.model.metadata` + `...metadata.api`, 1.0.0-SNAPSHOT), so `DEPENDENCIES` reflects the buildpath again.

## Previous findings status

| Prev id | Status | Note |
|---------|--------|------|
| F1 (major, exported impl surface) | **still open** | All six packages still exported with DS components/`*Impl` inside — re-verified per package. Carried over as F1 below. |
| F2 (major, unversioned exports) | **still open** | `copying`/`dynamic` still `@Export` without `@Version`. Carried over as F2 below. |
| F3 (descriptors export) | resolved | `descriptors/package-info.java` has no `@Export`; package is private. |
| F4 (CODE_OF_CONDUCT.md) | resolved | Present at repo root (Eclipse Community CoC 2.0). |
| F5 (Dash/DEPENDENCIES) | **partial** | Tooling, workflow and `DEPENDENCIES` committed — but the 10 `restricted` entries remain unreviewed. Residual carried over as F3 below. |
| F6 (license workflow) | resolved | No change needed — skywalking-eyes runs via the pinned `reusable-verify.yml`. |
| F7 (PMI id mismatch) | resolved | `NOTICE.md` and `SECURITY.md` both say `modeling.fennec`. |
| F8 (advisories URL) | resolved | `SECURITY.md:10` links this repo's GitHub Security Advisories. |
| F9 (ecore bnd headers) | resolved | `Bundle-Name`/`Bundle-Description` present. |
| F10 (baselining off) | **still open** | Still correct (no tags, no release-obr branch yet) — tracked as F5 below. |
| F11 (TCK bundle empty) | **partial** | New `src/.../tck/package-info.java` documents that the suite deliberately lives in `test/`; the `Bundle-Description` still overpromises — see F6 below. |
| F12 (Java version docs) | resolved | CLAUDE.md/README/docs say 21. (New, unrelated doc drift found — see F7 below.) |
| F13 (leftover dirs) | resolved | All three directories gone; working tree clean. |

Resolved/n-a: 9 · Partially resolved: 2 · Still open: 2.

## Findings

### F1 · major · api-hygiene · eclipselink / mongo / query — *carried over from 2026-07-24 (F1), 2nd consecutive review*
- **Where:** representative: `org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/query/package-info.java:13` exports the package containing the DS component `JpaQueryProcessor.java:82`
- **What:** Six exported packages still contain DS component and/or `*Impl` classes (re-verified 2026-07-28): `eclipselink.query` (`JpaQueryProcessor`), `eclipselink.resource` (`JPAResourceFactoryComponent`, `JPAResourceImpl`), `eclipselink.spi` (`PersistenceUnitConfigurator`, `EntityMappingPersistenceUnitConfigurator`, `EPersistenceContextImpl`), `mongo.query` (`MongoQueryProcessor`), `mongo.resource` (`MongoResourceFactory`, `MongoResourceImpl`), `query.memory` (`MemoryQueryProcessor`).
- **Why it matters:** Everything public in an exported package is API; once released, the 1180-line `JPAResourceImpl` and 1045-line `MongoResourceImpl` freeze under binary-compatibility rules. Consumers bind the processors as `QueryProcessor` services and never need the component types.
- **Suggested fix:** Split each package into an exported API part (plan types, SPI interfaces, backend-id constants) and a private impl sibling holding components and `*Impl` classes — the convention `orm.loader`, `mongo.config`, `persistence.liveness.impl` already follow.
- **Recurring:** open across 2 consecutive reviews. The proposed package split hasn't started; if it is deliberately deferred, consider recording that decision (and the intended pre-release deadline) so it stops resurfacing — the cost of the split only grows once anything is released.

### F2 · major · api-versioning · org.eclipse.fennec.persistence.eclipselink — *carried over from 2026-07-24 (F2), 2nd consecutive review*
- **Where:** `org.eclipse.fennec.persistence.eclipselink/src/org/eclipse/fennec/persistence/eclipselink/copying/package-info.java:15` and `.../dynamic/package-info.java:15`
- **What:** Both packages carry `@org.osgi.annotation.bundle.Export` but no `@Version` annotation (re-verified — every other exported package in the workspace is explicitly versioned at 1.0.0/2.0.0).
- **Why it matters:** The manifest falls back to the bundle version, so the package version silently drifts with every bundle bump, breaking semantic versioning for importers and future baselining.
- **Suggested fix:** Add `@org.osgi.annotation.versioning.Version("1.0.0")` to both files — a two-line change. `copying` must stay exported (consumed by the OSGi integration test); for `dynamic`, first check whether it needs to be exported at all.
- **Recurring:** open across 2 consecutive reviews despite being the cheapest major in the report — worth folding into whatever commit next touches the eclipselink bundle.

### F3 🟡 · major · release-readiness · repo root — *carried over from 2026-07-24 (F5 residual); largely defused same day (10 → 3)*
- **Where:** `DEPENDENCIES:1` (first of 10 lines ending `restricted, clearlydefined`)
- **What:** 10 `restricted` entries remain in the committed `DEPENDENCIES` (bnd/Felix/report-tooling artifacts: `aQute.libg`, `biz.aQute.gogo.commands.provider`, `biz.aQute.wrapper.hamcrest`, `SODS`, `fastcsv`, `felix.http.jetty`, `felix.threaddump`, `felix.webconsole.plugins.useradmin`, `servicemix poi`, `jackson-dataformat-properties`).
- **Why it matters:** Zero `restricted` entries are required at release time (blocker then; major now). These are build/test-scope artifacts pulled in via the fennec workspace libraries, so review is likely a formality — but it must actually happen.
- **Suggested fix:** Run `tools/dash-licenses.sh --review --project modeling.fennec` with an IPLab token to file the review requests; re-generate and re-commit `DEPENDENCIES` once they clear.
- **Status 2026-07-28:** 7 of the 10 were verified unused and deleted from `cnf/central.mvn`; `DEPENDENCIES` regenerated — **3 restricted entries remain** (`SODS`, `fastcsv`, `servicemix.bundles.poi`), all injected by the external fennecCodec workspace library and not removable from this repo. Next step: upstream index split in the codec project, or IP review for those three (see Summary status update).

### F4 ✅ · minor · release-readiness · repo root — *new; fixed same day*
- **Where:** `DEPENDENCIES:107` (only `org.eclipse.fennec.model.metadata/0.1.0-SNAPSHOT` listed)
- **What:** Commit `09694bd` switched the workspace to the split `org.eclipse.fennec.model.metadata` + `org.eclipse.fennec.model.metadata.api` artifacts at 1.0.0 (`mongo`/`tck` bnd.bnd, `test.bndrun`, `required.bndrun`), but `DEPENDENCIES` was not regenerated: it has no `model.metadata.api` entry and still records `model.metadata` at `0.1.0-SNAPSHOT`.
- **Why it matters:** The committed IP record no longer reflects the actual buildpath. Harmless in substance here (both artifacts are project-own `modeling.fennec` code and auto-approve), but the "regenerate DEPENDENCIES when dependencies change" habit is exactly what keeps release reviews cheap; the dash workflow will also flag the drift on the next push to `snapshot`/`main`.
- **Suggested fix:** Re-run the `DEPENDENCIES` generation (`tools/dash-licenses.sh`) and commit the result alongside the dependency change.
- **Status 2026-07-28:** Fixed — `DEPENDENCIES` regenerated during the F3 cleanup; it now lists `org.eclipse.fennec.metadata/org.eclipse.fennec.model.metadata` and `...metadata.api` at 1.0.0-SNAPSHOT, matching the buildpath.

### F5 · info · release-readiness · cnf — *carried over from 2026-07-24 (F10)*
- **Where:** `cnf/build.bnd:15` (`#fennec-baselining: true`)
- **What:** Baselining is still commented out. Still correct today — the repo has no tags and no `release-obr` branch (re-checked) — flagged so it isn't forgotten.
- **Suggested fix:** Enable `fennec-baselining: true` together with the first release (release-OBR orphan branch from the same release run).

### F6 · info · api-hygiene · org.eclipse.fennec.persistence.tck — *carried over from 2026-07-24 (F11), partially resolved*
- **Where:** `org.eclipse.fennec.persistence.tck/bnd.bnd:2`
- **What:** The new `src/.../tck/package-info.java` now documents that the TCK suite deliberately lives in the `test/` source folder ("this package only anchors the bundle") — resolving the ambiguity half of the finding. The `Bundle-Description` ("Backend-agnostic compatibility tests for EMF persistence backends") still describes contents the built bundle does not ship.
- **Suggested fix:** Reword the description to match, e.g. "Backend-agnostic compatibility test suite (runs in-repo; not shipped as consumable TCK)".

### F7 · info · docs · repo root — *new*
- **Where:** `CLAUDE.md:26` (module table)
- **What:** The contributor doc's module table lists 7 modules, but the workspace has 16 bundles — `mongo`, `query`, `pushstreams`, `tck` and the five model bundles (`command.model`, `expression.model`, `expression.ocl`, `query.model`, `stream.model`) are missing, i.e. the entire query/Mongo/pushstream feature set added since the table was written.
- **Why it matters:** CLAUDE.md is loaded as ground truth for AI-assisted work on this repo; a module map missing half the workspace misdirects exactly the audience it exists for.
- **Suggested fix:** Extend the module table (one row per bundle, mirroring the existing style); check `README.md`/`docs-site` for the same drift.

## Skipped / not reviewed

- **Unchanged code:** apart from the delta listed in the Summary, no Java source, `package-info.java`, or export changed since the 2026-07-24 review, so its full-read/skim coverage (all bnd.bnd files, DS lifecycle audit, `QueryProcessor` sibling LSP comparison, ORM processors) was not repeated. This review independently re-verified: the six F1 package contents, all `@Export`/`@Version` annotations workspace-wide (28 exported packages), license headers on every hand-written `.java` (clean), root documents, workflow triggers (`build`/`snapshot`/`release`/`dash-licenses` — all push-based, no PR-triggered publishing), `cnf/build.bnd` publishing config (`maven-central: true` inherited from the fennec bnd library — fine), and `DEPENDENCIES`.
- **EMF-generated code** (`src-gen/`, model bundles' `impl`/`util` exports): suppressed per fennec idiom.
- **Not reviewed:** `docs-site/`, `docs/handover-2026-07-28.md` (prose), pinned external reusable workflows in `eclipse-fennec/.github`.

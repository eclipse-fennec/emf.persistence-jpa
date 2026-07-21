# GitHub CI

The repository builds through the **org-central reusable workflows** in
[`eclipse-fennec/.github`](https://github.com/eclipse-fennec/.github) — the entire
build logic and all action versions live there; this repo only contains thin caller
workflows under [`.github/workflows`](../.github/workflows), pinned by commit SHA
(bumped automatically by Dependabot). See the CI/CD design document in the
`eclipse-fennec` org docs for the full reference; `emf.util` uses the identical setup.

## Trigger model

| Trigger | Verify (license → build/test/osgiTest/perfTest, JDK 21+25) | Release | Docs |
|---|---|---|---|
| PR (any target branch) | ✅ | – | – |
| Push to a feature branch | ✅ | – | – |
| Push to `snapshot` | ✅ | Maven **Snapshot** (`do-release=false`) | ✅ |
| Push to `main` | ✅ | Maven **Central** (`do-release=true`) | ✅ |

- `main` is the release branch, `snapshot` the development branch. Both release paths
  run the same Gradle `release` task; only the `DO_RELEASE` flag differs.
- The license-header check (Apache SkyWalking Eyes, `.licenserc.yaml`) is the first,
  gating job inside `reusable-verify` — there is no separate license workflow anymore.

## Caller workflows in this repo

| File | `on` | Calls |
|---|---|---|
| `build.yml` | push (all branches except `main`/`snapshot`) + PR | `reusable-verify` |
| `snapshot.yml` | push `snapshot` | `reusable-verify` → `reusable-release` (snapshot) → `reusable-docs` |
| `release.yml` | push `main` | `reusable-verify` → `reusable-release` (central) → `reusable-docs` |
| `docs.yml` | `workflow_dispatch` | `reusable-docs` (manual site rebuild) |
| `scorecard.yml` | schedule / push `main` / branch protection | `reusable-scorecard` |
| `dependency-review.yml` | PR | `reusable-dependency-review` |

`dependabot.yml` keeps three ecosystems current: the pinned action SHAs
(`github-actions`), Gradle test-scope dependencies (`gradle`), and the VitePress site
(`npm` in `/docs-site`). Runtime/OSGi dependencies are managed by bnd via
`cnf/central.mvn`, not by Dependabot.

## Credential scoping

Only the release job receives the publishing secrets — passed via `secrets: inherit`
into `reusable-release.yml`, which is the only reusable declaring them. The verify
matrix and the docs build never see credentials, and publishing runs with exactly one
JDK (21). Required org/repo secrets:

- `CENTRAL_SONATYPE_TOKEN_USERNAME` / `CENTRAL_SONATYPE_TOKEN_PASSWORD`
- `GPG_PASSPHRASE` / `GPG_KEY_ID` / `GPG_PRIVATE_KEY`

## Documentation site

`reusable-docs` builds the VitePress site in `docs-site/` (which syncs an allowlist of
pages from `docs/` — see `docs-site/guides.mjs`) and deploys it to GitHub Pages under
`https://eclipse-fennec.github.io/emf.persistence-jpa/<branch>/`. The branch name is
passed as `DOCS_BRANCH`; the snapshot branch publishes to `/snapshot/`.

## Test specifics

The OSGi integration tests include MongoDB liveness tests that resolve a
`docker`/`podman` CLI at runtime: on GitHub's `ubuntu-latest` runners docker is
available, so the container tests actually run in CI; in environments without a
container CLI they are skipped via JUnit assumptions and never fail the build.
Performance tests (`@Tag("perf")`) run in the verify step via the `perfTest` task with
`ignoreFailures=true`.

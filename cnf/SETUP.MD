# Initial Setup

A one-off checklist for bringing a fresh Eclipse Fennec repository online.
The steps below describe how **this** repository
([`eclipse-fennec/emf.persistence-jpa`](https://github.com/eclipse-fennec/emf.persistence-jpa))
was set up; reuse the pattern when creating a new Fennec component.

## Basics

The Maven group-id is set to **`org.eclipse.fennec.persistence.jpa`** in
[`cnf/build.bnd`](build.bnd) via the bnd **`-groupid`** instruction. For a
new Fennec repo, replace this with `org.eclipse.fennec.<your-component>`
following the convention used by the other repositories
(`org.eclipse.fennec.emf`, `org.eclipse.fennec.codec`, etc.).

## GitHub — create project

* Create an empty repository under the
  [`eclipse-fennec` organisation](https://github.com/organizations/eclipse-fennec/repositories/new).
  * Name: use the canonical lower-case form (e.g. `emf.persistence-jpa`).
  * Do **not** initialise with a default README / `.gitignore` / licence —
    the workspace template provides all three.

## Branch protection

* Project → Settings → Branches → *Add rule*.
  * Branch name pattern: `[ms][an][ia][np]*` (covers `main*` and `snapshot*`).
  * Require pull request reviews before merging (1 reviewer).
  * Dismiss stale approvals when new commits are pushed.
  * Require status checks to pass — at minimum `CI Build` and `license`.
  * Require branches to be up to date before merging.
  * Require linear history.
  * Include administrators.
  * **Do not** allow force pushes.
  * **Do not** allow branch deletion.

## Repository secrets (for publishing)

Project → Settings → Secrets and variables → Actions → *New repository secret*.
Add the same set used by every Fennec publishing pipeline:

| Secret name                          | Purpose                                  |
|--------------------------------------|------------------------------------------|
| `CENTRAL_SONATYPE_TOKEN_USERNAME`    | Sonatype Central user token              |
| `CENTRAL_SONATYPE_TOKEN_PASSWORD`    | Sonatype Central token password          |
| `GPG_PRIVATE_KEY`                    | ASCII-armored GPG private key            |
| `GPG_PASSPHRASE`                     | Passphrase for the private key           |
| `GPG_KEY_ID`                         | Long-form key id used by the build       |

See [`docs/ci.md`](../docs/ci.md) for what each workflow does with them.

## Gradle init

On Linux make `gradlew` executable, then verify:

```bash
chmod +x gradlew
./gradlew --version
```

## Git init and push

Run from the project root:

```bash
git init
git add .
git commit -s -m "initial commit"
git branch -M main
git remote add origin git@github.com:eclipse-fennec/<repo-name>.git
git push -u origin main
git checkout -b snapshot
git push --set-upstream origin snapshot
```

> `-s` adds the DCO `Signed-off-by:` trailer required for every commit. See
> [`CONTRIBUTING.md`](../CONTRIBUTING.md#sign-your-work).

## Branch model

This repository follows the Eclipse Fennec convention:

* **`snapshot`** — active development. PRs target this branch; every push
  publishes a `-SNAPSHOT` artifact.
* **`main`** — latest release. Code here matches what is on Maven Central.

See [`docs/ci.md`](../docs/ci.md) for the full pipeline.

## License header check

The license-header workflow runs on every push and pull request via
[apache/skywalking-eyes](https://github.com/apache/skywalking-eyes). It is
driven by [`.licenserc.yaml`](../.licenserc.yaml) at the repository root.

To run it locally:

```bash
docker run --rm -v $(pwd):/github/workspace \
  ghcr.io/apache/skywalking-eyes/license-eye header check
```

To exclude or include files, edit `.licenserc.yaml`. To customise the
GitHub Action itself, edit `.github/workflows/license.yml`.

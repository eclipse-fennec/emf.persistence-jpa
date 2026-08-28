# Contributing to Eclipse Fennec — Persistence JPA

Thank you for your interest in this project. Eclipse Fennec is an open-source
project hosted by the [Eclipse Foundation](https://www.eclipse.org) and
operated under the [Eclipse Development Process](https://www.eclipse.org/projects/dev_process/).
Contributions are welcome from the whole community.

* Project home: https://projects.eclipse.org/projects/modeling.fennec
* This repository: https://github.com/eclipse-fennec/emf.persistence-jpa
* Issue tracker: https://github.com/eclipse-fennec/emf.persistence-jpa/issues
* Developer mailing list: https://accounts.eclipse.org/mailing-list/fennec-dev

## Eclipse Development Process

All contributions are governed by the Eclipse Foundation Development Process.
The most important points for new contributors:

* **Eclipse Contributor Agreement (ECA).** Every contributor must have a
  signed ECA on file at the Eclipse Foundation before any contribution can be
  merged. Sign it once at https://www.eclipse.org/legal/eca.html — it covers
  all your future contributions to any Eclipse project.
* **Sign your commits (DCO).** Every commit must carry a `Signed-off-by:`
  trailer that matches the email on your Eclipse Foundation account. This is
  the project's *Developer Certificate of Origin* declaration; see
  ["Sign your work"](#sign-your-work) below.
* **License.** All contributions are licensed under the
  [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/).
* **Intellectual Property.** Third-party dependencies introduced by a
  contribution must clear Eclipse IP review. Run the
  [Eclipse Dash License Tool](https://github.com/eclipse-dash/dash-licenses)
  before opening a pull request that touches dependencies (see
  ["Adding dependencies"](#adding-dependencies)).

Background reading:

* [Eclipse Development Process](https://www.eclipse.org/projects/dev_process/)
* [Eclipse Foundation Contributor Guide](https://www.eclipse.org/projects/handbook/#contributing)
* [Eclipse Code of Conduct](https://www.eclipse.org/org/documents/Community_Code_of_Conduct.php)

## Reporting issues

* Search the [issue tracker](https://github.com/eclipse-fennec/emf.persistence-jpa/issues)
  first — your problem may already be reported.
* When filing a new issue, include the Fennec Persistence JPA version, Java
  version, JPA provider (EclipseLink) version, JDBC driver, and a minimal
  reproducer.
* Security issues must **not** be reported as public GitHub issues. See
  [SECURITY.md](SECURITY.md) for the disclosure process, or email
  [security@eclipse.org](mailto:security@eclipse.org).

## Contributing code

We use a fork-and-pull-request workflow:

1. **Check the ECA.** Sign the Eclipse Contributor Agreement if you have not
   yet done so. The CI bot will block any PR without a signed ECA.
2. **Fork** this repository and create a topic branch off `snapshot`.
3. **Make focused commits.** Each commit should do one thing and keep the
   build green. Prefer several small, reviewable commits over a single large
   one. Use descriptive commit messages with a short subject line (≤ 72
   chars) and a body explaining *why* the change is needed.
4. **Add or update tests** for any behavior change. The project keeps an
   OSGi integration test module (`org.eclipse.fennec.persistence.test`) with
   JUnit 5 against an in-memory H2 database — extend it for cross-cutting
   changes.
5. **Run the build locally:**
   ```bash
   ./gradlew clean build
   ```
6. **Push** to your fork and open a Pull Request against the `snapshot`
   branch. Link the PR to an existing issue when possible.
7. **Wait for CI.** All status checks (build matrix on JDK 21 + 25, license
   header check) must be green before review. See
   [docs/ci.md](docs/ci.md) for what each workflow does.

### Sign your work

Every commit must include a `Signed-off-by:` line that matches the email
registered with your Eclipse Foundation account. This is the project's DCO
sign-off — it declares that you wrote the change or otherwise have the right
to contribute it under the project's license.

The easiest way is to commit with `-s`:

```bash
git commit -s -m "Support ZonedDateTime in EclipseLink converter"
```

This appends a trailer like:

```
Signed-off-by: Jane Developer <jane@example.org>
```

To sign off all commits in an existing branch, use `git rebase` with
`--signoff`:

```bash
git rebase --signoff snapshot
```

### License headers

Every new source file (`.java`, `.gradle`, etc.) must start with the
following header. The license-header workflow rejects PRs that introduce
files without one.

```
/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
```

Excluded paths and supported file types are configured in
[`.licenserc.yaml`](.licenserc.yaml). The check is run locally with:

```bash
docker run --rm -v $(pwd):/github/workspace ghcr.io/apache/skywalking-eyes/license-eye header check
```

### Adding dependencies

Adding a new third-party library requires Eclipse IP clearance:

1. Run the Eclipse Dash License Tool over the project's dependencies.
2. Add any newly cleared dependencies to a `DEPENDENCIES` file at the
   repository root.
3. For dependencies that Dash marks as "restricted", file a Contribution
   Questionnaire (CQ) with the Eclipse IP team before merging the PR.

Note: this project has finished migrating off the legacy `org.gecko.*` and
`org.geckoprojects.*` namespaces; new PRs introducing dependencies from those
groups will be rejected. See [`docs/REVIEW.md`](docs/REVIEW.md) for the
migration history.

## Coding style

* **Java:** 4-space indent (no tabs), opening braces on the same line, no
  wildcard imports.
* **API stability:** Public packages are tracked by BND baselining; bump
  `Bundle-Version` for any public-API change.
* **Javadoc:** Public API classes and methods require Javadoc.
* **No hand edits in `src-gen/`.** Regenerate the EMF sources from the
  matching `.genmodel` instead.

## Build prerequisites

* Java 17 minimum (project source/target). CI builds on Java 21 and 25.
* No separate Gradle install needed — the project ships the Gradle Wrapper.

```bash
./gradlew clean build       # core build + unit tests
./gradlew perfTest          # performance tests (skipped by default)
```

## Project leads & committers

Current committers are listed on the
[Eclipse Fennec project page](https://projects.eclipse.org/projects/modeling.fennec/who).
Becoming a committer follows the standard Eclipse process — sustained,
high-quality contributions over time, followed by a committer election.

## Contact

* Mailing list: [fennec-dev@eclipse.org](mailto:fennec-dev@eclipse.org)
  ([subscribe](https://accounts.eclipse.org/mailing-list/fennec-dev))
* Issues: https://github.com/eclipse-fennec/emf.persistence-jpa/issues

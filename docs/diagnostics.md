# Diagnostics

How the persistence framework reports problems: EMF `Diagnostic`s are the contract,
JUL logging is only ever *derived* from them at well-defined boundaries. This page
documents the severity rules, the diagnostic sources, and where logging happens
(issue #19).

## The contract

Every pipeline that can encounter recoverable problems collects
`org.eclipse.emf.common.util.Diagnostic`s instead of logging ad hoc:

- **Result APIs** return the produced artifact *plus* the collected diagnostics and
  never log and never throw for model-level problems — a `RuntimeException` inside
  the run becomes an `ERROR` diagnostic carrying the exception:
  - `EntityMapper.createMappingsWithDiagnostics(...)` /
    `createMappingsFromEPackageWithDiagnostics(...)` → `MappingResult`
  - `DatabaseEcoreParser.parseAllWithDiagnostics()` → `ParseResult`

  Both results are records with the same shape: the artifact, `diagnostics()`,
  `isSuccess()` (no `ERROR` or worse) and `getSeverity()` (highest reported).
- **Legacy APIs** (`EntityMapper.createMappings(...)`, `DatabaseEcoreParser.parseAll()`
  / `parse()`) keep their historical behavior and act as the logging boundary: they
  run the result API and pass every diagnostic through `Diagnostics.log(...)`.
- **EMF resources** (`JPAResourceImpl`, `MongoResourceImpl`) surface problems on the
  standard `Resource#getErrors()` / `Resource#getWarnings()` lists as
  `PersistenceDiagnostic`s.

### Diagnostic shape

| Field | Convention |
|---|---|
| `severity` | `ERROR` or `WARNING` (see rules below); `OK`/`INFO` are not reported |
| `source` | the reporting **bundle's namespace** (source catalog below) |
| `code` | always `0` — codes are not used, the message and data carry the context |
| `message` | human-readable, names the affected table/column/feature |
| `data[0]` | the **affected model element** (EMF-validation style), e.g. the `EClass` or `EStructuralFeature` |
| `exception` | present when a `RuntimeException` was converted into an `ERROR` diagnostic |

### Severity rules

- **ERROR** — the artifact is unusable or the run could not complete: an EClass that
  is not EMF-configurable, a `RuntimeException` during mapping. On the deploy path an
  `ERROR` **breaks the deployment** (see boundary table).
- **WARNING** — the run continued with a silent correction that the user should know
  about: a containment reference forced to `EAGER`, a batch hint without effect, a
  reference skipped because its target is not part of the persistence unit, an
  unmapped JDBC type falling back to `EString`, an FK whose target table is not in
  the parsed schema mapped as plain attribute.

Rule of thumb: WARNING = "we changed your intent to keep going", ERROR = "we could
not keep this element at all".

## Source catalog

The `source` of a diagnostic is the namespace of the bundle that detected the
problem:

| Source | Reported by |
|---|---|
| `org.eclipse.fennec.persistence.orm` | eorm mapping pipeline (`MappingContext`, processors) |
| `org.eclipse.fennec.persistence.eclipselink` | EclipseLink descriptor/type generation (`EDynamicTypeContext`) and `JPAResourceImpl` load/save |
| `org.eclipse.fennec.persistence.mongo` | `MongoResourceImpl` load/save/query |
| `org.eclipse.fennec.persistence.ecore` | `DatabaseEcoreParser` reverse engineering |

## PersistenceDiagnostic (resource layer)

`org.eclipse.fennec.persistence.diagnostic.PersistenceDiagnostic` is the one
`Resource.Diagnostic` implementation shared by all backends (it replaced the
per-backend `JPADiagnostic`/`MongoDiagnostic`). It adds what the plain EMF interface
lacks:

```java
resource.getErrors().add(PersistenceDiagnostic.error(
        DIAGNOSTIC_SOURCE, "Save failed: " + e.getMessage(), getURI(), e));
```

- `getSeverity()` — EMF constant matching the list it is surfaced on
  (`ERROR` → `getErrors()`, `WARNING` → `getWarnings()`)
- `getSource()` — bundle namespace, same catalog as above
- `getLocation()` — the resource URI
- `getCause()` — the originating exception, for callers that want the backend stack

## Logging boundaries

`Diagnostics.log(Logger, Diagnostic)` (package
`org.eclipse.fennec.persistence.diagnostic`) is the **only** way diagnostics become
JUL log records (`ERROR` → `Level.SEVERE`, `WARNING` → `Level.WARNING`). It is called
exclusively at boundaries where diagnostics would otherwise be lost:

| Boundary | Behavior |
|---|---|
| `EntityManagerFactoryConfigurator` (deploy path) | logs all diagnostics; any `ERROR` → `PersistenceException`, the EMF is closed before the throw |
| `EntityMapper.createMappings(...)` (legacy API) | logs in `finally`, throws as before |
| `DatabaseEcoreParser.parseAll()` / `parse()` (legacy API) | logs all diagnostics, returns the packages |

Result APIs (`…WithDiagnostics`, `MappingResult`, `ParseResult`) never log — the
caller owns the diagnostics. Consumers embedding the framework should either use the
result APIs and route diagnostics into their own reporting, or rely on the boundary
logging of the legacy APIs.

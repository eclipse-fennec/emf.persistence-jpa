# Issue #8 — Honor `delimitedIdentifiers` in `EDynamicTypeGenerator`

GitHub: <https://github.com/eclipse-fennec/emf.persistence-jpa/issues/8>

## Problem

The eorm metamodel exposes
`EntityMappings.persistenceUnitMetadata.persistenceUnitDefaults.delimitedIdentifiers`
(the JPA `<delimited-identifiers/>` flag), but the `eclipselink` bundle never
consults it. As a result, EclipseLink emits unquoted SQL identifiers for
dynamic types. When an attribute name matches a SQL reserved word
(e.g. `order`, `year`, `user`, `key`), both DDL and runtime queries fail
against strict dialects like H2.

## Investigation

- EclipseLink does **not** expose a project-wide "use delimiters" toggle; the
  flag lives per `DatabaseTable` / `DatabaseField` (`setUseDelimiters(boolean)`
  on `org.eclipse.persistence.internal.helper.DatabaseField` and
  `…DatabaseTable`).
- `DatabaseField` / `DatabaseTable` instances are created in many places during
  descriptor assembly (`EDynamicTypeBuilder.configureDatabase`,
  `IdConfigurator`, `AttributeConfigurator`, `ReferenceConfigurator`,
  `CollectionConfigurator`, plus EclipseLink internals that materialise fields
  from `setFieldName(...)` calls). Touching every construction site is
  invasive and easy to miss.
- A first attempt did the sweep at the end of
  `EDynamicTypeGenerator.createFromMappings`, but at that point descriptors
  have not yet been initialized: `mapping.getFields()` returns `null` for
  several mapping types (only `getField()` is populated, lazily), so basic
  columns like a `Book.order` attribute were silently skipped. DDL then
  produced `CREATE TABLE "BOOK" (..., ORDER INTEGER, ...)` and H2 failed
  silently (the dynamic-schema flow has `setIgnoreDatabaseException(true)`),
  leaving the table unmaterialised and surfacing as a confusing
  *"Table BOOK not found"* on the subsequent INSERT.

## Decision

Sweep **after** `session.addDescriptors(...)` in
`EDynamicHelper.addETypes`. By then mapping initialisation has populated
every `getFields()` / `getAllFields()` and the sweep can be exhaustive.

The flag itself is detected in
`EDynamicTypeGenerator.createFromMappings(List<EntityMappings>)` and stored
on the shared `EDynamicTypeContext` (`useDelimitedIdentifiers`). Each
`EDynamicType` already exposes that context via `getContext()`, so the
helper can read it without any extra plumbing. JPA scopes
`<persistence-unit-metadata>` to the persistence unit, so the flag is
turned on for **all** descriptors when **any** `EntityMappings` in the list
declares it (`anyMatch` semantics).

## Behavior

| `delimitedIdentifiers` in any mapping | `setUseDelimiters(true)` on descriptor tables/fields |
|---------------------------------------|-------------------------------------------------------|
| absent                                | no (default — identifiers stay unquoted)              |
| present                               | yes (DDL + SQL emit quoted identifiers)               |

The DDL run by `EDynamicHelper.addETypes` happens after `createFromMappings`
returns, so the sweep is visible to both DDL generation and runtime SQL.

## Changes

- `org.eclipse.fennec.persistence.eclipselink/src/.../dynamic/EDynamicTypeContext.java`
  — new `useDelimitedIdentifiers` boolean (package-private setter, public getter)
  that flows the flag from the generator down to the helper.
- `org.eclipse.fennec.persistence.eclipselink/src/.../dynamic/EDynamicTypeGenerator.java`
  — in `createFromMappings`, detect the flag via `hasDelimitedIdentifiers`
  (`anyMatch` over the supplied `EntityMappings`) and set it on the shared
  `EDynamicTypeContext`. No sweep happens here.
- `org.eclipse.fennec.persistence.eclipselink/src/.../dynamic/EDynamicHelper.java`
  — in `addETypes`, after `session.addDescriptors(descriptors)` and after the
  `fqClassnameToDescriptor` map is updated, check if any incoming
  `EDynamicType` has the flag on its context and, if so, run the new private
  `applyDelimitedIdentifiers(eTypes)` sweep. The sweep walks every
  descriptor's `getTables()`, `getPrimaryKeyFields()`, `getAllFields()`,
  `getFields()` and `mapping.getFields()` (with a null guard), flipping
  `useDelimiters` to `true`.
- `org.eclipse.fennec.persistence.test/test/.../NonOsgiDelimitedIdentifiersTest.java`
  — regression test: a `Book` EClass with a column named `order`
  (SQL reserved word) is wrapped in `EntityMappings` whose
  `persistenceUnitMetadata.persistenceUnitDefaults.delimitedIdentifiers` is
  set, then the full DDL + persist + find chain must succeed without
  exception. Confirmed failing before the fix.

## Open

- Existing tests that don't use reserved identifiers should be unaffected (the
  sweep only runs when the flag is set), but the many-to-many and join-table
  tests are worth a sanity run to confirm.

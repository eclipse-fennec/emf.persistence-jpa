# Conformance and capabilities — the persistence contract

**Status:** target picture, approved 2026-08-13. The A/B split itself is not implemented yet; §4a
is, and describes shipped behaviour (#138–#140). Defines what a Fennec
persistence backend must do unconditionally (the *conformance core*) versus what it may
declare (*capabilities*), and how the TCK proves both. Companions: `concept.md`,
`query-ir-redesign.md` (the `QueryFeature` vocabulary), `query-processor-spi.md` (the SPI
that carries the declarations). The harness that implements this is the follow-up topic.

---

## 1. Why

Today one word carries two jobs. `MongoFlavorCapabilities`, `QueryFeature` (51 literals),
`CommandFeature` (4) and the seven `supports*()` hooks in `AbstractPersistenceTCK` all
answer "can this backend do X?" — but X is sometimes *store-dependent power* (geo indexes,
multi-document transactions) and sometimes *EMF semantics* (does a containment child come
back). Those are not the same kind of question, and merging them costs the API its central
promise.

The failure mode is concrete. If EMF semantics are declarable, a backend can declare
`MULTIVALUED_ATTRIBUTES=false`, its tests skip, CI is green, and whoever writes portable
code against the API never learns. A backend that cannot round-trip a multi-valued
attribute does not have a missing capability. It is broken.

This matters more for EMF than it would for plain objects. With POJOs, "how does this
store map an aggregate?" is a legitimate design conversation between mapping philosophies.
With EMF, the metamodel already fixes the constraints: containment *is* ownership,
`eOpposite` *is* a maintained invariant, `resolveProxies` *is* what permits a child to
live in another resource. The backends do not get a vote on those. That is precisely what
the core is for.

## 2. The split

### A — Conformance core

EMF semantics on the write and read path. **Not declarable, not skippable, no flags.**
Every backend runs all of it or it is not a conformant Fennec persistence backend.

Structurally enforced: core items **do not exist as declarable values**. There is no
`CoreFeature` enum to consult and no way to spell "I do not support containment". If the
value cannot be spelled, the question never arises — for the implementor or for the
consumer reading the declaration.

Verified as a round trip **without cache**: written through one resource, read back through
a resource that cannot have the answer in memory. This is not test-author discipline; it is
the only read path the harness offers (§6).

### B — Capabilities

Store-dependent power: geospatial vocabulary, multi-document transactions, aggregation
pipelines, full-text, streaming, indexes. Declared per backend/flavor, and every
declaration must be **test-backed in both directions**:

- **declared** → the tests carrying that capability execute and pass;
- **not declared** → the corresponding request is *refused with a diagnostic*.

The second half is the one usually missing, and it is the more important one. Refusal is
covered in §5.

### What separates them

A capability answers "can this store do it at all". Core answers "does this backend
implement EMF". A useful test: if the answer for a given item could reasonably be *no* for
a mature, well-implemented backend on a capable store, it is a capability. If *no* would
mean the backend is defective, it is core.

## 3. Where the boundary runs

### Core

| Area | Contract |
|---|---|
| Attributes | single- and multi-valued, all `EDataType`s the converter layer covers, enums, dates, defaults, `eUnset`/unsettable |
| References, non-containment | single/many, order preserved, object identity, resolution through the `ResourceSet` |
| Bidirectional references | `eOpposite` consistent after reload, both directions |
| Containment | single/many, order preserved, ownership |
| Cross-document containment | child owned by the parent **and** resident in its own resource (#130, #133) |
| Resolution transparency | no consumer-facing API hands out an unresolved proxy — `eGet`, `eContents`, `EcoreUtil.getAllContents` (#116) |
| Cascade-delete | dropping a containment subtree deletes what it owned, transitively, across document/table boundaries. Implemented on both backends (#138/#139 for Mongo, #142/#143 for JPA); the timing guarantee is qualified in §4a |
| Inheritance | polymorphic write and read of a subtype through a supertype resource |
| Composite ids | **core** — every store can key on a concatenation, so absence is a defect, not a limitation |
| Command verbs | Insert / Update / Delete exist (§4) |
| Refusal | anything not supported is refused with a diagnostic, never silently mis-answered (§5) |

### Capabilities

| Capability | Why store-dependent |
|---|---|
| Geospatial | needs real geo indexes and operators; splits further — a bounding box is not 2dsphere |
| Transaction bracket | the PostgreSQL wire gateways provably cannot; MongoDB needs a replica set |
| Aggregation / pipeline | genuinely absent or crippled on some stores |
| Full-text / score | index-dependent |
| Streaming / cursors | server-side cursor support varies |
| Index (future) | see §7 |
| Query expression vocabulary | the `QueryFeature` set, granular (§5) |

### The seven hooks, sorted

`AbstractPersistenceTCK` currently gates on seven overridable predicates. Under this split:

| Hook | Becomes |
|---|---|
| `supportsGeo` | capability, split finer |
| `supportsCommandTransactions` | capability |
| `supportsCompositeIds` | **core** — hook removed |
| `supportsTypePredicates` | capability (query vocabulary), but polymorphic round-trip moves to core |
| `supportsSortExpressions` | capability (query vocabulary) |
| `supportsExpand` | capability (query vocabulary) |
| `supportsFilteredCollectionCounts` | capability (query vocabulary) |

Removing `supportsCompositeIds` is a real tightening: a backend that skips it today would
become non-conformant.

## 4. Commands: verb, selector, template

A command is not one capability. It decomposes, and the parts land in different categories:

| Part | Category |
|---|---|
| Object-level mutation (persist / remove an `EObject` via the resource) | core, unconditional |
| Set-based verb (`DELETE WHERE …`, `UPDATE WHERE …`, `INSERT`) | **core** — every backend has all three |
| Selector vocabulary | **capability**, granular |
| Update template rules (no update on a containment reference — ownership is lifecycle) | **core**, identical everywhere |

### The composition rule

> If a backend declares verb *V* and expression capability *X*, then *V* over a selector
> using *X* must work.

The cross product is **derived, never declared**. Without this rule you get
`DELETE_WITH_GEO_SELECTOR`, `UPDATE_WITH_SUBQUERY_SELECTOR` and thirty more flags — which
is exactly where "no doubt what a capability means" dies. With it, the TCK generates the
cross-product cases mechanically from the declaration, which is what makes the test set the
specification rather than an illustration of it (§5).

The negative direction stays core: a selector using vocabulary the backend does not declare
must be refused with a diagnostic.

### Two contract statements that must be explicit

These are consequences of set-based mutation, identical in **every** backend. They are not
defects to fix; they are the price of bulk. They must be stated in the contract rather than
discovered by a user later.

**Update-by-selector does not carry EMF change semantics.** It mutates rows/documents that
were never in a `ResourceSet`, so no notifications fire, no `eOpposite` is maintained, no
derived feature recomputes. A consumer expecting opposite consistency after a bulk update
will not get it anywhere.

**Cascade-delete converges everywhere; the transient window does not.** See §4a — the
guarantee is about the state after an operation completes, not about every instant during it.

**Delete-by-selector does not clean inverse references.** `EcoreUtil.delete` removes
non-containment references *pointing at* the deleted object; a bulk `DELETE`/`deleteMany`
cannot, and dangling references result. Containment cascade is still required (ownership is
core). The decision: bulk delete is documented as not cleaning inverses, and the
**object-level** delete path must do it properly. Core tests must distinguish the two paths.

### 4a. What cascade-delete guarantees, per flavor

Containment is ownership on every backend and every flavor: dropping a containment subtree
deletes what it owned, transitively, across document and table boundaries. That is core, not a
capability. What differs is not *whether* it happens but *when*, and the difference has to be
stated rather than implied.

**Where `TRANSACTION_BRACKET` is available** — MongoDB as a replica set, and the JPA backend
inside its unit of work — the owner write and the removal of what it released are atomic. No
window exists.

**Where it is not** — the PostgreSQL-backed wire gateways (FerretDB, DocumentDB) — the owner is
written first and the release follows, deliberately in that order: a crash then leaves a
recoverable orphan rather than an owner referencing documents that no longer exist. Inside that
window a query over the child's own collection **returns the orphan**. A reader who queries a
cross-document containment child's collection directly can therefore observe an object whose
owner has already let go of it.

The state converges regardless, because the ownership records make the orphan re-derivable: the
record still names an owner that no longer claims the child. The next save of that owner
reconciles it; for an owner that is never saved again, `OwnershipMaintenance.sweepOwnership()`
is the explicit backstop (#140). It is idempotent, scoped to one collection's owners, and finds
nothing on a healthy store or a transactional deployment.

**Selector-based deletes cascade too.** A `DeleteCommand` removes the owned cross-document
children of every match, on the same terms as an object-level delete. This is the one place
where the §4 statement about bulk paths does *not* apply: bulk delete may leave dangling
*non-containment* references, but it may not leave owned children behind.

**One limit, deliberately not hidden.** Moving a containment child into a *different* resource
and then saving only the **old** resource deletes the child: that save sees a subtree it once
owned and no longer does, and it cannot know another resource has taken it over in memory.
Cross-resource changes require saving both resources — standard EMF practice — but here the cost
of not doing so is silent data loss rather than a stale reference. Re-parenting *within* one
save, and re-parenting where the new owner is saved first, are both correct.

## 5. The rules that make it work

**A capability *is* its test set.** Not its javadoc. Declaring a capability means "these
tests must pass"; the tests are the specification, so ambiguity is structurally impossible
and drift detection is mechanical. This inverts the usual order — no prose to keep in sync.

**Granularity is where honesty is won or lost.** `QUERY=true` is worthless. The existing
`CommandFeature` grain (INSERT, DELETE_BY_SELECTOR, UPDATE_BY_SELECTOR,
TRANSACTION_BRACKET) is about right; the `QueryFeature` grain is already good. Geo needs
splitting. Coarse capabilities are self-deception dressed as documentation.

**Skip means exactly one thing: an undeclared capability.** "Backend unreachable" is an
**error**, never a skip. If both states share a mechanism the whole construction degenerates
into the silent green we already hit (#132): a suite that skipped itself is
indistinguishable from one that passed. The backend must therefore be booted and proven
live *before* any capability condition is evaluated.

**Refuse, never lie.** A backend may lack features. It may never answer a request it cannot
serve with a plausible wrong result. This is the single non-negotiable rule and it is
backend-independent. `CODE_NON_EMBEDDED_PATH` (Mongo refusing cross-document query paths)
is the pattern done right.

**Runtime introspection over documentation.** A backend answers what it supports when
asked, and refuses what it does not. Then documentation is convenience, not contract.

## 6. Topology: backend × flavor

Two axes, not one. Today "flavor" means "which mongo-wire server", which stops working the
moment JPA joins the matrix.

| Backend | Flavor | Notes |
|---|---|---|
| `jpa` | `h2` | in-memory, the fast default |
| | `postgres` | EclipseLink target-database/dialect property set accordingly |
| | `oracle` | dialect property |
| | `mssql` | dialect property |
| `mongo` | `mongo` | replica set — the only one with transactions |
| | `ferretdb` | PostgreSQL-backed wire gateway |
| | `postgres-docdb` | DocumentDB emulator, PostgreSQL-backed |

The backend chooses the implementation; the flavor is *configuration* of the same
implementation. On the JPA side that is literally true already — `JpaTckSupport.bootstrap`
takes driver, URL and dialect as properties, so `jpa × postgres` is a property change plus
a container. Flavors of one backend must not differ in core conformance; if they do, it is
a bug in that flavor's mapping, not a capability.

**How a backend realises containment is not the consumer's business.** JPA gives the child
a row with a foreign key, Mongo embeds it. Same semantics, different shape. Where the shape
must be influenced deliberately, the **eorm** is the knob — and its *default* has to satisfy
the core.

## 7. Index — a capability with a different test shape

Pure performance, zero semantics: results with and without an index are identical. So it
cannot be tested by comparing results. Proving it requires inspecting the plan — Mongo's
`explain`, EclipseLink's SQL plus the database's plan — which loads the harness differently
from every other capability. Worth knowing before the harness is designed rather than after.

## 8. What is not covered today

The TCK has 73 tests. Nine touch EMF semantics: `saveAndLoadAttributes`,
`containmentManyRoundtrip`, `nonContainmentSingleResolvesViaResourceSet`,
`nonContainmentManyResolvesViaResourceSet`, `bidirectionalReferenceBothSidesResolve`,
`countAndExist`, `deleteRemovesPersistedObjects`, `idGenerationOnSaveAssignsAndWritesBackId`
and the two `compositeId*` cases, plus `streamAllObjects` / `pushStreamDeliversAllObjects`.
The remaining sixty are query, command, geo and derived-reference cases.

So the part that is about to become mandatory is the thinnest part. Gaps, each verifiable:

- **Multi-valued attributes are never round-tripped on JPA.** The model's only one is
  `GeoPoint.coordinates`, exercised solely inside the geo tests; `supportsGeo()` is
  overridden true only in `MongoPersistenceTckTest`, and `Place`/`GeoPoint` are not even in
  the JPA bootstrap.
- **Single-valued containment is untested on JPA** — `Place.location` is the only one, same
  missing bootstrap.
- **Cascade-delete is asserted nowhere, for any flavor.** `deleteRemovesPersistedObjects`
  uses a childless `Person`; `commandDeleteBySelector` deletes a `Person` that does have
  addresses but never inspects the `Address` side (#133).
- **Cross-document containment**: JPA cannot write it in one save order and loses residency
  (#130); Mongo orphans the child document on delete (#133).
- **Polymorphic round-trip.** `Car`/`Motorcycle` are used thirteen times, but only through
  query type predicates, which are capability-gated — never as a plain write/read of a
  subtype through a supertype resource.
- **`eUnset` / unsettable features** only via `commandUpdateUnsetClearsTheValue`, i.e. the
  command path, not the EMF write path.
- **Untested entirely:** `EMap`, containment order preservation, object identity across
  repeated loads, default values, `resolveProxies=false` (which must *forbid* cross-resource
  containment), abstract/interface types, `EcoreUtil.delete` inverse cleanup, feature maps.

## 9. Open decisions

1. **Query-vocabulary hooks.** `supportsTypePredicates`, `supportsSortExpressions`,
   `supportsExpand`, `supportsFilteredCollectionCounts` are listed as capabilities above.
   Arguable: they are not store limits so much as translator gaps, and a translator gap is
   a defect. Deciding them as core would put real pressure on both backends.
2. **`EMap` and feature maps in core.** Both are EMF semantics, so core by the §2 test —
   but neither has a mapping today, which makes this the most expensive single item.
3. **Geo capability split.** Where exactly: `GEO_WITHIN`/`GEO_DISTANCE` exist; is a
   bounding box its own capability below 2dsphere?
4. **Streaming as capability or core.** `stream()` and `pushStream` are tested today
   without gating, i.e. de facto core, but they hand out EObjects attached to no resource —
   which needs a contract statement either way.

## 10. Getting there

1. Freeze the boundary — §3 and the four decisions in §9.
2. Introduce the declaration surface with core items *absent by construction*, and reduce
   `MongoFlavorCapabilities` / `CommandCapabilities` onto it.
3. Replace the seven hooks with an `ExecutionCondition` and a capability annotation, so
   gating is declarative and a skip reason is a capability statement in the report.
4. Make the boot fail loudly: undeclared capability → skip, unreachable backend → error.
   Retires the XML-parsing guard in `mongo-flavors.yml` (#132).
5. Close the core gaps of §8 as mandatory tests — the two known defects (#130, #133) become
   blocking rather than `@Disabled`.
6. Generate the command × selector cross product from the declarations (§4).
7. Extend the matrix to `backend × flavor` (§6): `jpa × h2` first, then `jpa × postgres`,
   with the container harness of `MongoTestSupport` factored out for reuse.
8. Index capability last, once the plan-inspection harness exists (§7).

Steps 1–4 are structure and change no test outcome. Step 5 is where conformance starts
biting.

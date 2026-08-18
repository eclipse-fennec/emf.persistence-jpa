# Conformance and capabilities — the persistence contract

**Status:** target picture, approved 2026-08-13; third category and declaration surface added
2026-08-14 (§2C, §5a). Partly implemented since: §4a and §4b describe shipped behaviour
(#138–#140, #150), the declaration surface of §5a exists as its own bundle with
`PersistenceResource.capabilities()` as its carrier, and the `backend × flavor` matrix of §6 runs
in CI (§10 records what is done and what is left). Defines what a Fennec
persistence backend must do unconditionally (the *conformance core*), what it may
declare (*capabilities*), what merely differs in shape (*form divergence*), and how the TCK
proves each. Companions: `concept.md`,
`query-ir-redesign.md` (the `QueryFeature` vocabulary), `query-processor-spi.md` (the SPI
that carries the declarations). The harness that implements this is the follow-up topic.

---

## 1. Why

Today one word carries two jobs. `MongoFlavorCapabilities`, `QueryFeature` (50 literals),
`CommandFeature` (3 since `TRANSACTION_BRACKET` became a `StoreFeature`, §5a) and the seven
`supports*()` hooks in `AbstractPersistenceTCK` all
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

Both directions are mechanical since #160. Every non-core TCK case names the features it
exercises in a `@RequiresCapabilities` annotation; an `ExecutionCondition` reads the
binding's declaration and **skips** the case when a required feature is undeclared, with the
undeclared features as the skip reason — a skip in the report is a capability statement,
never a silent hole. The refusal direction is one generic case:
`undeclaredFeaturesAreRefusedWithADiagnostic` probes every undeclared query feature with a
minimal query and asserts the diagnostic names it. A case *without* the annotation is core —
"skip this core case" cannot be spelled, which is §2A's structural enforcement carried into
the suite.

### C — Form divergence

Neither of the two above, and it exists because §4b happened: the semantics are required of
every backend, honoured by every backend, and the *shape* still differs. Residency is the
case — the child is resolved, owned, addressable and save-order independent everywhere, but
which resource object it reports follows from how the store represents containment.

Form divergence is **not declarable and not optional**. It is not a capability, because
nothing is missing and nothing gets refused; it is not a gap in the core, because the
semantics are complete. What it gets instead is a contract statement naming both behaviours
and their consequences, plus a test that **asserts** the divergence rather than skipping it —
so a future change that removes it has to come here and update the contract.

Deliberately absent: a runtime flag for it. A `reportsOwnResource()` predicate would invite
portable code to branch on the shape, which is exactly what §4b tells consumers not to do.
The two cases today are §4a (behaviour uniform, timing not) and §4b (semantics uniform, form
not).

### What separates them

A capability answers "can this store do it at all". Core answers "does this backend
implement EMF". A useful test: if the answer for a given item could reasonably be *no* for
a mature, well-implemented backend on a capable store, it is a capability. If *no* would
mean the backend is defective, it is core.

Form divergence is what remains when the answer is *yes everywhere* and the observable shape
still differs. The three are distinguished by what they produce: core produces a mandatory
test, a capability produces a declaration plus tests in both directions, a form divergence
produces a contract statement plus an asserting test.

## 3. Where the boundary runs

### Core

| Area | Contract |
|---|---|
| Attributes | single- and multi-valued, all `EDataType`s the converter layer covers, enums, dates, defaults, `eUnset`/unsettable |
| References, non-containment | single/many, order preserved, object identity, resolution through the `ResourceSet` |
| Bidirectional references | `eOpposite` consistent after reload, both directions |
| Containment | single/many, order preserved, ownership |
| Cross-document containment | child owned by the parent, resolved, addressable, save-order independent; residency itself is form, not semantics — see §4b |
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

### 4b. Residency is form, not semantics — the one documented divergence

Cross-document containment (a child owned by a parent *and* a root of its own `Resource`) is
core, and both backends honour it: the child comes back resolved, owned by its parent,
addressable by reference, with its ownership round-tripping in either save order and its
lifecycle cascading. One thing differs, deliberately.

**On Mongo the child reports its own resource; on JPA it reports the parent's.** Loading the
child's own resource explicitly hands it over as a root there on both backends, so the child is
reachable either way — the difference only shows when the parent alone was loaded.

The reason is in the storage model, not in the implementation. A Mongo document records the
shape: the parent carries `{"$ref": …}` where an embedded child would sit. **A JPA row does
not** — the foreign key to the parent is identical whether the object was an ordinary
containment child or additionally a root of its own resource. Reconstructing the difference on
load would mean persisting it in a side table, and that was built and then withdrawn: the shape's
substance does not need it. What the record bought was the `eResource()` identity and the shape
surviving a load/save round trip, at the price of schema the model does not describe.

So this is the precedent for the boundary drawn in §2: **semantics are uniform, form is not.**
Uniform and required of every backend are the resolved child, the container link, addressability,
save-order independence and the ownership cascade. Which resource object the child reports is
form, and it follows from how the store represents containment.

What this costs a consumer, stated plainly:

- `child.eResource()` on JPA is the parent's resource, so `child.eResource().save(null)` saves the
  parent's resource — with the child in it, but not only the child.
- On JPA the cross-document shape lives in memory for as long as the objects do. Load the parent
  and save it again and the child is an ordinary containment child; nothing recorded that it had
  been a resource root. Re-establish it by attaching the child to its own resource again.
- Code that must work identically on both backends should not ask a containment child which
  resource it belongs to. Ask its container, or address it by reference.

Pinned by `JpaCrossDocumentContainmentTest.crossDocumentChildReportsTheParentsResource` — as an
assertion rather than a skipped test, so that a future change which removes the divergence has to
come here and update the contract.

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

### 5a. The declaration surface

Decided 2026-08-14. The rules above say what a declaration must do; this says where it lives
and who answers it. Today it lives in the wrong place, and the symptom is visible: everything
declarable sits in `query-api.ecore` inside the query bundle, so anything that wants to
declare has to depend on the query model — while the save path, `StreamingResource`, liveness,
index and schema generation have no place to declare at all. The liveness concept made the
point by building its own runtime model (`PersistenceLivenessRuntime` + DTOs) rather than
using this one.

**One model, three roles, one name.** `query-api.ecore` carries the query execution API
(`QueryProcessor`, `QueryPlan`, `QueryContext`, `QueryResult`, `QueryableResource`), the
command execution API (`CommandResource`, `CommandTransaction`) *and* the declaration surface.
The name describes the first third. Split it by role, one name per role:

| Type | Where | Note |
|---|---|---|
| `PersistenceCapabilities` | own bundle `org.eclipse.fennec.persistence.capabilities` | the root a backend answers; query, command and store are views on it |
| `QueryCapabilities` / `QueryFeature` | moved there unchanged | this really *is* query expression vocabulary — the prefix is correct here |
| `CommandCapabilities` / `CommandFeature` | moved there, minus one literal | see below |
| `StoreCapabilities` / `StoreFeature` | new | the home for power outside query and command; today `TRANSACTION_BRACKET` and `SERVER_CURSORS` (below), with `CHANGE_STREAMS`, `INDEXES`, `FULL_TEXT`, `TIMESERIES` as the expected neighbours — each added when something declares it, not before |
| `StoreLimits` | **not built yet** | scalars, not flags — identifier length, timestamp precision, LOB bounds, NULL ordering. Waits for the flavor axis, which is what produces them; `maxFeaturePathDepth` stays on `QueryCapabilities` until then, since it limits the translator rather than the store |
| query / command execution types | stay in the query bundle, `...query.api` and `...command.api` | `CommandResource` is not a query |

The bundle is its own, not a package inside the core bundle: the core has no EMF codegen, and the
vocabulary needs it. It depends on nothing but EMF, so every other bundle can depend on it.

`TRANSACTION_BRACKET` moves from `CommandFeature` to `StoreFeature`. It was never a command
capability: §4a already uses it to explain the cascade-delete window on the **save** path, and
`OwnershipMaintenance` refers to it — reaching that statement through a `CommandResource` is
an accident of where the literal was first needed.

`SERVER_CURSORS` is the `QueryResult` lifetime contract (#162, first declarer: the Lucene
backend, which holds the unit's searcher lease from `query(...)` to `close()`). *Declared*
means the streams of a `QueryResult` remain valid until `close()` and fetch incrementally out
of a live store handle — a server-side cursor or its embedded analogue. *Undeclared* means
results may be fully materialized at call time and `close()` is a no-op. Both are conforming;
the feature only tells the consumer which resource-lifetime contract `query(...)` hands out.
A TCK case pinning it (reading a stream past a subsequent write, or bounding memory) can
follow once a second declarer makes the shape comparable.

**The carrier is `PersistenceResource`, in the core bundle.** That is what fixes the finding
rather than papering over it: `PersistenceResource.capabilities()` returns the aggregate, so a
capability statement needs no optional query or command role. `CommandResource.capabilities()` is
gone — one way to reach the answer, not two. What a resource returns is the **effective** set,
with the deployment probe already applied; what the declaration service returns (below) is what
the flavor can do at all.

**Not named `contract` or `conformance`.** Both were considered and rejected: the conformance
core is precisely what is *not* declared (§2A — core items do not exist as declarable values).
A model that by construction contains only B must not be named after A.

**Scalars, not just booleans.** `maxFeaturePathDepth` is already the exception among the flags,
and the flavor axis produces more of them: Oracle's identifier length, timestamp precision,
collation and case sensitivity, NULL sort order. Those are not can/cannot questions, and a
boolean cannot say "works, differently" — which is the normal case across flavors.

**Two levels, because there are two kinds of truth.** The existing Mongo implementation is
already built this way and it stays that way:

1. **Declaration** — static, readable *without opening a connection*, so tests, the documented
   matrix and a consumer choosing a server can all read it. `MongoFlavorCapabilities` is the
   pattern: a `BASELINE` minus per-flavor gaps, derived by exclusion, so a newly supported
   feature is available everywhere by default and a genuine gap has to be discovered and
   declared deliberately.
2. **Probe** — per resource instance, for what only the running deployment knows: replica set
   or standalone, session-capable client, database version, actual dialect. FerretDB and
   DocumentDB-PG carry *identical* query declarations and differ exactly here.

> **The probe may only narrow, never widen.** A probe that would add a capability the
> declaration does not carry is a startup error, not a silent upgrade. Otherwise no one can
> say what actually holds.

**Declared in Java; the vocabulary stays in Ecore.** The feature enums remain `EEnum`s and
stay closed — the vocabulary is our contract, and no one extends it from outside. The
per-flavor declarations are Java constants. XMI instance data was considered and deferred: its
real benefit is a *foreign* flavor without a fork, which nothing needs today, while its costs
land immediately — an unknown enum literal in XMI is a `Resource` error but silently the
default value, which reproduces the #132 failure mode one level up; renaming a literal breaks
Java at compile time and XMI silently; and EMF joins the core bundle's startup path.

What makes this reversible is the surface, not the format: `PersistenceCapabilities` is to be
registered as an **OSGi service with `backend` and `flavor` properties** (still to build — see
§10.2), so one provider may build it from Java constants and another from an XMI it ships —
consumers and the TCK cannot tell. The irreversible decision is the type and the registration;
where the data comes from is per backend and can change later. That is also the answer for JPA's
open flavor set (h2, postgres, oracle, mssql, and whatever a deployment brings): a third party
registers its own declaration as a bundle instead of forking ours.

**Every gap carries its evidence.** The valuable part of `MongoFlavorCapabilities` is not the
list but the reasoning: `FERRETDB_GAPS` is empty, and above it stand the container tag it was
measured against, the issue, the features suspected up front and then disproved, and the
scope of the claim — "no gap in what the TCK exercises", not "no gap". A declaration without
that is unreviewable. So evidence — issue reference plus how it was measured — is required
per gap, and a declaration lacking it must fail a test rather than merely look thin.

## 6. Topology: backend × flavor

Two axes, not one. Today "flavor" means "which mongo-wire server", which stops working the
moment JPA joins the matrix.

| Backend | Flavor | Notes |
|---|---|---|
| `jpa` | `h2` | in-memory, one database per persistence unit, no container — the fast default |
| | `postgres` | container, one schema per persistence unit, `TARGET_DATABASE=PostgreSQL` |
| `mongo` | `mongo` | replica set — the only one with transactions |
| | `ferretdb` | PostgreSQL-backed wire gateway |
| | `postgres-docdb` | DocumentDB emulator, PostgreSQL-backed |

Oracle and MSSQL are deliberately **not** flavors: they cost proprietary containers and licence
questions, and the dialect is the layer EclipseLink itself tests. `h2` plus `postgres` is the
honest matrix — one lax database and one strict one, which is what actually finds things (below).

The backend chooses the implementation; the flavor is *configuration* of the same
implementation. Selected with `-Djpa.test.flavor` / `-Dmongo.test.flavor`, and both axes must be
listed in `backendSelectionProperties` in `build.gradle`: Gradle does not forward `-D` to the test
JVM, so an unlisted property means the suite silently runs the *default* flavor and reports green
for something it never measured. Flavors of one backend must not differ in core conformance; if
they do, it is a bug in that flavor's mapping, not a capability.

**What the second flavor found immediately.** The first `jpa × postgres` run failed 14 of 164
cases, in four groups, every one a real defect that H2's permissiveness had hidden: a
hand-written `CLOB` column type in the saved-query catalog (#154), `SUBSTR` offsets bound as
`bigint` (#155), a `GROUP BY` clause that re-renders an expression containing bind parameters,
which PostgreSQL cannot match to the select list (#156), and — the substantial one — `EDate`
persisted as `varchar`, so `EXTRACT(YEAR FROM …)` is applied to a string (#157). That is the
argument for the axis in one paragraph: not "does PostgreSQL work too", but "which of our
translations were only ever correct by the grace of one database".

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

Decided since, and moved out of this list: the third category (§2C, form divergence) and the
declaration surface with its naming, its two levels and Java-versus-XMI (§5a) — both
2026-08-14. What remains:

1. **Query-vocabulary capabilities.** `TYPE_CHECK`/`TYPE_CAST`, `SORT_EXPRESSION`,
   `EXPAND`, `COLLECTION_COUNT_FILTERED` are gated as capabilities above (since #160 via
   `@RequiresCapabilities`, no longer as `supports*()` hooks). Arguable: they are not store
   limits so much as translator gaps, and a translator gap is a defect. Deciding them as
   core would put real pressure on both backends.
2. **`EMap` and feature maps in core.** Both are EMF semantics, so core by the §2 test —
   but neither has a mapping today, which makes this the most expensive single item.
3. **Geo capability split.** Where exactly: `GEO_WITHIN`/`GEO_DISTANCE` exist; is a
   bounding box its own capability below 2dsphere?
4. **Streaming as capability or core.** `stream()` and `pushStream` are tested today
   without gating, i.e. de facto core, but they hand out EObjects attached to no resource —
   which needs a contract statement either way.

## 10. Getting there

1. Freeze the boundary — §3 and the four decisions in §9.
2. Introduce the declaration surface of §5a with core items *absent by construction*. **Done**:
   `query-api.ecore` split by role into the new `org.eclipse.fennec.persistence.capabilities`
   bundle, `PersistenceCapabilities` and `StoreCapabilities` added, `TRANSACTION_BRACKET` moved
   to `StoreFeature`, and `PersistenceResource.capabilities()` made the carrier while
   `CommandResource.capabilities()` was removed. Package renames were cheap while `base-version`
   is `0.1.0` and every consumer is workspace-internal; after 1.0 they cost a major bump.
   **Left**: registering the declaration as a service per `backend` × `flavor`, and reducing
   `MongoFlavorCapabilities` onto it. That waits for step 7 rather than being guessed now — the
   Mongo flavor is configuration already, but JPA has no flavor concept yet, so `flavor=h2`
   today would be a declaration nobody derived from anything.
3. Replace the seven hooks with an `ExecutionCondition` and a capability annotation, so
   gating is declarative and a skip reason is a capability statement in the report. The
   condition reads the declaration service, which is what makes "skip means undeclared
   capability" mechanical rather than a convention. **Done** (#160): the seven `supports*()`
   hooks are gone. Every non-core case carries `@RequiresCapabilities` over
   `QueryFeature`/`CommandFeature`/`StoreFeature`, `CapabilityGate` evaluates it against the
   binding's `declaredCapabilities()` — connection-free, per §5a — and skips with the
   undeclared features as the reason. The refusal direction became one generic case
   (`undeclaredFeaturesAreRefusedWithADiagnostic`, a minimal probe query per feature), and
   `effectiveCapabilitiesNeverExceedTheDeclaration` pins declaration against the live
   resource in both directions. `supportsCompositeIds` did not become an annotation — the
   §3 decision made it core, so its refusal branch is simply gone. One deviation from the
   sentence above: the condition reads the binding's declaration, not a *service* — the
   service registration per backend × flavor is still step 2's open remainder.
4. Make the boot fail loudly: undeclared capability → skip, unreachable backend → error.
   Retires the XML-parsing guard in `flavor-matrix.yml` (#132).
5. Close the core gaps of §8 as mandatory tests — the two known defects (#130, #133) become
   blocking rather than `@Disabled`.
6. Generate the command × selector cross product from the declarations (§4).
7. Extend the matrix to `backend × flavor` (§6). **Done**: the container harness is factored out
   of `MongoTestSupport` into `ContainerHarness` + `ContainerSpec`, `JpaTestSupport` provides `h2`
   and `postgres`, both axes reach the test JVM, and the four defects the second flavor found are
   fixed (#154–#157) rather than pinned — so `flavor-matrix.yml` runs `jpa × postgres` alongside
   the three mongo flavors and is green because it passes, not because failures are ignored.
   `jpa × h2` stays in `build.yml`, where it needs no container. Next flavor: MariaDB (#158).
8. Index capability last, once the plan-inspection harness exists (§7).

Steps 1–4 are structure and change no test outcome. Step 5 is where conformance starts
biting.

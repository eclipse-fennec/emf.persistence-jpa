# Conformance and capabilities — the persistence contract

**Status:** target picture, approved 2026-08-13; third category and declaration surface added
2026-08-14 (§2C, §5a); the four remaining boundary decisions frozen 2026-08-19 (§9, #171).
Partly implemented since: §4a and §4b describe shipped behaviour
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
| Maps (`EMap`) | round trip of entries, keys and values, with map semantics (one key, one entry) for every key type that renders to a string and parses back. Honoured on both backends, mandatory per flavor. Query *access* into a map is a separate question — `MAP_VALUE`, §9.2 and #186 |
| Command verbs | Insert / Update / Delete exist (§4) |
| Refusal | anything not supported is refused with a diagnostic, never silently mis-answered (§5) |

### Capabilities

| Capability | Why store-dependent |
|---|---|
| Geospatial | needs real geo indexes and operators; one grain today (`GEO_WITHIN`, `GEO_DISTANCE`), no bounding box below it until a backend has box-only power (§9.3) |
| Transaction bracket | the PostgreSQL wire gateways provably cannot; MongoDB needs a replica set |
| Aggregation / pipeline | genuinely absent or crippled on some stores |
| Full-text / score | index-dependent |
| Streaming | `stream()` and `pushStream` themselves are a capability (§9.4); `SERVER_CURSORS` is the separate question of the result's lifetime (§5a) |
| Index (future) | see §7 |
| Query expression vocabulary | the `QueryFeature` set, granular (§5) — including the four that look like translator gaps (§9.1) |

### Out of contract

Neither core, nor capability, nor form divergence: the contract makes no statement about the
item at all, and no backend may pretend otherwise. Declared here so that "unmapped" is a
decision on record rather than a hole nobody named.

| Item | What holds instead |
|---|---|
| Feature maps | no mapping anywhere, and unlike `EMap` no codec support either; a feature-map-typed feature must be **refused with a diagnostic** at mapping time, never silently dropped or flattened (§9.2) |

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

### 4c. Deleting something that is still referenced is refused

Decided 2026-08-21 (issue #195), measured on both backends before deciding.

Containment is ownership and cascades — settled, and not this question. This is the other
direction: a plain, non-containment reference whose target is deleted leaves a proxy that
resolves to nothing. **No backend may produce that silently.** Deleting an object something
still points at is refused, with a diagnostic naming the referrer.

The backends arrived here from opposite ends. JPA already refused, because the mapping
declares the reference as a foreign key and the database enforces it — nothing had to be
built. Mongo deleted happily and left the reference dangling, so it now looks before it
deletes: one query per reference in the model that could point at this type, all of them
before the first removal, because a half-done delete is worse than a refused one.

Two limits, stated because they are limits rather than oversights:

- The mongo search covers the EPackage of the deleted object's type. A reference from
  another model is not found — finding it would mean scanning every collection on every
  delete.
- The refusal is uniform, while the eorm mapping can in principle say something finer per
  reference (`nullable`, cascade). Making the behaviour follow that declaration would be a
  refinement; it is noted on issue #29 rather than guessed at here. Uniform refusal is the
  conservative reading, and it is the one portable across a backend that has no mapping
  model at all.

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

**Who answers a declaration** (issue #172, 2026-08-21). The vocabulary above says what can be
declared; `CapabilityDeclaration` says *who* declares it, and the answer is not per backend
but per **backend × flavor** — the same axis the TCK matrix runs on. It names both, answers
with the full `PersistenceCapabilities`, and is plain Java so the TCK can read it outside any
framework; an implementation may additionally be registered as a service carrying
`persistence.backend` and `persistence.flavor`.

Both backends derive their per-flavor sets from one baseline by exclusion
(`MongoFlavorCapabilities`, `JpaFlavorCapabilities`): a newly supported feature is available
everywhere by default, so a genuine gap has to be discovered and declared deliberately rather
than a new feature having to be added in three places.

The relational flavor is **probed**, not configured: the persistence unit asks the driver once
at activation (`JpaFlavor.detect`), while the EclipseLink factory is still unbuilt. A
configuration key would add a second truth that can disagree with the database — exactly the
failure mode a declaration must not have. A database nobody measured is `unknown` and declares
the portable baseline.

Consequences already visible: the mongo transaction bracket (FerretDB cannot, MongoDB and the
DocumentDB gateway can) is a production declaration instead of a line in the TCK binding, and
the TCK bindings read what the backend declares rather than assembling half of it themselves.
`StoreLimits` remains the open remainder — it rides this axis and gets built when something
needs the scalars.

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

**Query capabilities are backend-wide by definition — no narrowing surface** (#161, decided
2026-08-18). `CommandCapabilities` answers per `EClass` because write routing is genuinely a
per-target-type question. The query-side cases that looked like the same shape are not:
`EXISTS` only over `NESTED`-mapped containment, equality only on keyword projections — they
narrow per *feature and its mapping*, a third and finer axis. A declarable form of that would
replicate the validator: whoever answers it precisely must consult the same mapping knowledge
`validate()` already has, and two code paths for one truth drift apart. So the doctrine is:
a declared `QueryFeature` means the backend serves the feature's family natively *somewhere*;
whether a concrete query over concrete features is served is `validate()`'s answer, and a
mapping-dependent refusal carries a Diagnostic naming the feature and the way out (a keyword
sub-field, a `NESTED` mapping). If a real pre-validation router ever appears, a
`supports(feature, EStructuralFeature)` overload is a purely additive extension — until then
it would be an API answering a question nobody asks.

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
- **Untested entirely:** containment order preservation, object identity across
  repeated loads, default values, `resolveProxies=false` (which must *forbid* cross-resource
  containment), abstract/interface types, `EcoreUtil.delete` inverse cleanup.

`EMap` and feature maps were on that last line until §9.2 split them: the map round trip is core,
green on both backends, and mandatory for every flavor — `mapRoundTrip` and `mapKeysAreUnique`
are ordinary TCK cases now, and the TCK model carries an `EMap<EString,EString>` and an
`EMap<EInt,EString>` on `Person`. A feature map owes the suite a refusal rather than a round
trip: a model carrying one must fail mapping with a diagnostic naming it.

## 9. The boundary, frozen

Six decisions stood open on 2026-08-13. Two were settled by the work itself — composite ids
are core (§3, the hook retired with #160) and capability narrowing is `validate()`'s job, not a
declarable axis (§5a, #161) — and two more were settled on 2026-08-14: the third category (§2C)
and the declaration surface with its naming, its two levels and Java-versus-XMI (§5a). The four
below were decided on **2026-08-19 (#171)**, and with them §3 is closed: an item is core, a
capability, form, or out of contract, and there is no fifth drawer.

### 9.1 The four query-vocabulary features stay capabilities

`TYPE_CHECK`/`TYPE_CAST`, `SORT_EXPRESSION`, `EXPAND` and `COLLECTION_COUNT_FILTERED` remain
declarable, as `@RequiresCapabilities` already has them since #160. No code changes.

The counter-argument was serious: these read as translator gaps rather than store limits, and a
translator gap is a defect that a capability flag lets a backend keep. What settles it is the §2
test — *could a mature, well-implemented backend on a capable store reasonably say no?* — and
since #160 there is a live example that answers yes. The Lucene backend refuses `EXPAND` and
`SORT_EXPRESSION` **by design**: an inverted index has no join to expand across and no evaluator
to sort by a computed expression. Declaring these core would not put pressure on a lagging
translator, it would declare an honest search backend non-conformant for being what it is.

That the same literal is a deliberate refusal on one backend and an unfinished translator on
another is not the contract's problem to solve. The contract's job is that the answer is
declared, and refused with a diagnostic when it is not — which is the same obligation in both
cases. Whether *our* two backends should close their gaps is a roadmap question, tracked as
ordinary issues, not a conformance question.

### 9.2 `EMap` is core; feature maps are out of contract

This one was decided twice on the same day. The first decision put both out of contract on the
argument that neither backend maps them — and the argument was wrong for `EMap`, which is why
this section carries the measurement rather than the reasoning that replaced it.

**What the measurement says** (`MongoEMapRoundTripTest`, `JpaEMapRoundTripTest`, both over the
same `EMapTestModel`: one `Catalog` with `EMap<EString,EString>`, `EMap<EInt,EString>` and
`EMap<EString,Part>`):

| case | mongo | jpa |
|---|---|---|
| string-keyed round trip | pass | pass |
| `EObject` value, containment | pass | pass |
| same key twice replaces the value | pass | pass |
| one key per owner enforced by the schema | n/a — the sub-document shape gives it | pass |
| stored shape is a sub-document `{key: value}` | pass | n/a — rows in an entry table |
| int-keyed round trip | pass, since emf.codec#154 | pass |

Mongo maps `EMap` through the codec (`EMapHelper` plus the `serializeEMap`/`deserializeEMap`
pair). JPA does since #185: four defects stood between the model and a table, and each one is
worth knowing because none was about maps as such.

1. **The entity class** (#183). `EClassDescriptor` took `eClass.getInstanceClass()` whenever it
   was set — and a map entry class carries `java.util.Map$Entry`, which it must, or EMF hands
   out a list instead of an `EMap`. That is an interface, and it is the *same* class for every
   entry type in the unit. Now an instance class is used only when it can actually be an entity
   class (not an interface, not abstract, has a no-arg constructor); otherwise a dynamic class
   is generated, as for any other dynamic EClass. `EntityProcessor` needed the same correction —
   it named the entity from the instance class name too.
2. **The generated class's superclass.** An `EcoreEMap` stores entries in an array of
   `BasicEMap.Entry`, so anything else in it is an `ArrayStoreException` during copying, far from
   its cause. EMF's own entry class is `final` and shared, so a subclassable equivalent
   (`EMapEntryEObject`) is what the generated per-entry class extends.
3. **The synthetic key** (#184). A map entry has no id attribute and never will — EMF puts `key`
   and `value` in it and nothing else — so this had to be fixed generally rather than for maps:
   the synthetic `pk_<name>` now has a writable mapping whose value rides on an adapter
   (`ESyntheticKeyAccessor`), and it generates a UUID rather than drawing from a sequence table
   that no flavor creates. Reproduced without any map by `JpaSyntheticIdRoundTripTest`.
4. **The column names.** `key` and `value` are SQL reserved words, and the usual answer —
   `checkReservedName` warns, the model author renames — cannot apply when EMF fixes the names.
   The mapping names them `MAP_KEY`/`MAP_VALUE` once, for every dialect. Worth noting how this
   surfaced: `create-or-extend-tables` swallows a failing `CREATE TABLE`, so the miss appeared as
   a missing table at the first insert, not as a DDL error.

Map semantics is a schema constraint on that side, not just an in-memory promise: the entry
table carries a unique constraint over `(owner_fk, MAP_KEY)`. That also made the eorm's
`Table.uniqueConstraint` reach the descriptor for the first time — it was modelled and read by
nobody.

**The decision: `EMap` is core.** Both backends honour it now, and a construct EMF puts in every
user's reach cannot be declined because one translator is behind — that is precisely the "translator gap is a defect" test of §9.1, and here
it comes out the other way, because no store has a reason to refuse a map. What core covers:
entries survive, keys keep their type, values keep their type, an `EObject` value comes back
resolved, and map semantics hold — one key, one entry, a second put replaces.

**The key-type limit is part of the contract, not a bug to grow out of.** A key is a *name* in
every store worth having: a BSON field, a Lucene field, a column value. So core covers key types
that render to a string and parse back through their `EDataType` factory — `EString`, `EInt`,
enums, and anything a custom factory handles. A key type that cannot make that round trip is
refused with a diagnostic. Mongo additionally has to state what it does with a key containing
`.` or `$`: those are field-name syntax there, so they are refused rather than stored (a keyed
sub-document cannot hold them without an escaping scheme nobody has designed).

**The form divergence is expected and is not a defect** (§2C): mongo stores a map as a
sub-document keyed by the map key, JPA as rows in an entry table with a unique constraint on
`(owner, key)`. Same semantics, different shape — the residency precedent of §4b applies
unchanged.

**Feature maps stay out of contract.** No mapping on either backend and, unlike `EMap`, no codec
support either, so there is nothing to make true. The obligation is §5's: refuse with a
diagnostic at mapping time, never silently drop or flatten.

**Where a search backend lands, since it is the third implementation now.** The Lucene backend
(emf.search) maps a reference by one of three strategies — `ID_ONLY`, `EMBED`, `NESTED`
(`ReferenceStrategy`, `IndexSchema`) — and a map is a containment-many reference, so it falls
into that machinery without anyone having decided anything:

- **`EMBED` loses the map.** Embedding contributes a name prefix and nothing else, so two entries
  produce multi-valued `attributes.key` and `attributes.value` fields and the pairing between
  them is gone. A map must therefore never be embedded — that is a silent wrong answer, not a
  limitation.
- **`NESTED` preserves it.** One child document per entry keeps key and value paired, and the
  block join is exactly the `Exists`-over-entries shape. This is the honest default there.
- **The keyed form** — one dynamic field `attributes.color` per key, which is what makes
  `MapValue` cheap — is a *fourth* strategy that does not exist in that backend yet, and it
  carries an index cost the other two do not: unbounded key variety means unbounded fields.

So the expected declaration for a search backend is: the map round trip yes (via `NESTED`),
`EMBED` of a map refused, and key enumeration never a query capability. This is the same
pattern as §9.1 — the third backend is what turns an assumption into a decision.

**Querying into a map is a capability; its two rules are not** (#186, built 2026-08-19). The IR
construct is `MapValue(map, key)` and the capability is `QueryFeature.MAP_VALUE` — one literal,
backend-wide, declared today by JPA, mongo and the reference engine. Two rules ride along as
*contract* rather than declaration, because no backend may declare its way out of either: the
path must end in a map, and the key must be a literal or a bound parameter. The second one is
the interesting half — Mongo and Lucene turn the key into a field name, so a computed key would
be a construct only the relational backend could serve, and the IR does not carry those.

One consequence for consumers, because it is a refusal they can meet: a **quantifier** over a map
(`Exists`/`ForAll` ranging over the entries) is refused on mongo with a diagnostic pointing at
`MapValue` (#188). The entries are a sub-document there, not an array, so `$elemMatch` would match
nothing and return an empty result — the plausible wrong answer §5 forbids. JPA serves the same
quantifier correctly over the entry table; `MapValue` is the form that works on both.

The Lucene question this raised — a search backend can only serve `MAP_VALUE` for a map its
mapping stores in keyed form — is **not** a counter-example to "query capabilities are
backend-wide" (§5a, #161). It is the same feature-and-mapping axis #161 already decided about
`EXISTS` over `NESTED` and equality on keyword projections: the declaration says the backend
serves map access natively somewhere, and whether a *concrete* map feature is served follows
from that feature's mapping and is `validate()`'s answer with a Diagnostic naming the way out.
One truth, one code path.

The non-string key was the last gap and closed with emf.codec#154: the codec parses a stored
field name back through the key feature's data type instead of assigning it raw. On JPA the same
key is a typed column and never needed help — a good illustration of why the key-type limit above
is phrased as "renders to a string and parses back" rather than "is a string".

The round trip now lives in `AbstractPersistenceTCK` (`mapRoundTrip`, `mapKeysAreUnique`), so
every `backend × flavor` runs it and the model carries the two maps. What stays backend-specific
is the *shape* evidence, per §2C: the stored sub-document and the `MapValue` field path on mongo,
the entry table's unique constraint — proven by a native insert the schema rejects — on JPA.

### 9.3 Geo keeps one grain until a backend forces a second

`GEO_WITHIN` and `GEO_DISTANCE` stay as they are; a bounding box does **not** become a separate
capability below 2dsphere. Every declaring flavor today has both or neither, so a finer grain
would be a distinction no declaration can express differently from its neighbour — untestable in
the "not declared → refused" direction, which §2B requires of every capability.

The general rule this instance follows: **a capability is split when a backend exists that can
answer the halves differently**, not in anticipation of one. Splitting early produces flags whose
two states are never both observed, and those are the flags that quietly go wrong. When a
box-only backend appears, the split is additive — a new literal, the existing declarers gaining
it, no consumer breakage — which is precisely why it costs nothing to wait.

### 9.4 Streaming is a capability, with a contract statement about what it hands out

`stream()` and `pushStream` become a declarable capability (`StoreFeature.STREAMING`), not core.
Server-side cursor support genuinely varies, `StreamingResource` is already written as an
optional role a resource may or may not implement, and a backend that must materialise a full
result set to iterate it should say so rather than pretend to stream. Both of our backends
declare it — the capability is not an escape hatch here, it is an honest name for a role that
was optional in the type system and mandatory in the suite.

`SERVER_CURSORS` (§5a) is a different question and stays separate: `STREAMING` says the role
exists, `SERVER_CURSORS` says what the result's lifetime is.

**What a streamed object is, stated once and binding on every backend that declares it:**

- Streamed objects are handed out **detached**. They are not added to
  `Resource.getContents()`, and a consumer must not ask a streamed object which resource it
  belongs to — that answer is not part of the contract.
- Their references follow the ordinary contract: non-containment references are EMF proxies that
  resolve through the `ResourceSet`, containment children come with the object.
- The stream owns backend resources and **must be closed** by the caller; a backend may hold a
  cursor, a lease or a connection open for its lifetime.
- Mutating a streamed object writes nothing. Persisting it means adding it to a resource and
  saving that resource, like any other object.

The implementation work — the `STREAMING` literal, gating the two TCK cases, both declarations,
and the refusal direction for a backend that does not implement `StreamingResource` — is
follow-up, not part of this decision (§10.1).

## 10. Getting there

1. Freeze the boundary — §3 and the four decisions in §9. **Done** (#171, 2026-08-19): query
   vocabulary stays capability, `EMap`/feature maps are out of contract, geo keeps one grain,
   streaming becomes a capability. §9.2 was decided twice: the first cut put maps out of contract,
   measurement showed mongo maps them today, and the decision became core — work in #185 (the two
   JPA defects #183/#184, emf.codec#154, the entry-table unique constraint) plus #186 for query
   access. §9.4 produces the `STREAMING` literal, the gating of the two streaming TCK cases and
   both declarations. §9.1 and §9.3 confirm what the code already does and cost nothing. All of it
   is follow-up, not part of this step — the point of step 1 is that §3 no longer has an open
   drawer, not that everything it implies is built.
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
   §3 decision made it core, so its refusal branch is simply gone. The deviation recorded
   here — the condition reading the binding's own declaration rather than a service — is
   closed with #172: both bindings now answer `declaredCapabilities()` from what the backend
   declares for their flavor (`JpaFlavorCapabilities` / `MongoFlavorCapabilities`), so the
   gate reads one mechanism instead of a hand-assembled copy.
4. Make the boot fail loudly: undeclared capability → skip, unreachable backend → error.
   **Done** (#173): both support classes raise an error when no server can be provided, and a
   skip is available only by explicit opt-in (`-D<backend>.test.optional=true`) for the
   developer without a container runtime — never by accident, and never in CI, which sets
   nothing. That retired the XML-parsing guard in `flavor-matrix.yml` (#132): a suite that
   cannot reach its backend now fails by itself instead of being caught by counting executed
   tests afterwards.
5. Close the core gaps of §8 as mandatory tests — the two known defects (#130, #133) become
   blocking rather than `@Disabled`. **Mostly done** (#174): eight ungated core cases were
   added — multi-valued attribute round trip (and the empty case), single-valued containment,
   polymorphic round trip, `eUnset` on the write path, declared defaults, containment order,
   object identity across repeated loads — plus the `Place`/`GeoPoint` and new `Profile` types
   in the JPA bootstrap they needed. The polymorphic case found a real one: a subtype read
   through its supertype came back as the supertype on mongo. The cause turned out to be the
   fixture, not the codec — the model resource carried its file path, so every document stored
   a `file:/…#//Car` as its type, and the package lived only in the backend's
   `MetadataService`, which type resolution does not consult. Both are fixed here and the case
   passes on all four bindings; the diagnosis is recorded on eclipse-fennec/emf.codec#160.
   The fixture root is abstract since, so the same case
   covers §8's abstract-type item. Two §8 items remain, each a decision rather than coverage:
   #195.
6. Generate the command × selector cross product from the declarations (§4). **Done** (#175):
   a `@TestFactory` pairs every declared selector-shaped verb with every plain-filter probe
   from the same corpus the refusal test uses, so the case count follows the declaration
   rather than the suite author's imagination (54 cells per backend today). A declared cell
   must execute with the same reach its selector has as a query, or refuse with a
   `Diagnostic` — the feature-and-mapping axis of §5a, which is how mongo declares nested
   paths and still refuses one that crosses a document. To keep that from degenerating into
   "refuse everything and stay green", each verb must also have executed at least one cell.
   `PARAMETERS` is outside the corpus: `CommandResource.execute(Command)` takes no bindings,
   so a parameterized selector has nowhere to get its values from on any backend.
7. Extend the matrix to `backend × flavor` (§6). **Done**: the container harness is factored out
   of `MongoTestSupport` into `ContainerHarness` + `ContainerSpec`, `JpaTestSupport` provides `h2`
   and `postgres`, both axes reach the test JVM, and the four defects the second flavor found are
   fixed (#154–#157) rather than pinned — so `flavor-matrix.yml` runs `jpa × postgres` alongside
   the three mongo flavors and is green because it passes, not because failures are ignored.
   `jpa × h2` stays in `build.yml`, where it needs no container. MariaDB joined as the third
   jpa flavor (#158) and found three real divergences on its first runs: EclipseLink's
   filtered table creator reads table metadata across databases on Connector/J 3.x defaults
   (harness fix: `nullDatabaseMeansCurrent=true`), the case-insensitive default collation
   leaked into EMF string equality (mapping fix: binary-collated string columns on the MySQL
   family, pinned by the TCK's new case-sensitivity probe — case-insensitivity stays the
   per-predicate `STRING_MATCH_CASE_INSENSITIVE` opt-in), and a runtime zero divisor
   evaluates to NULL instead of raising — now a stated contract: error or 3VL exclusion,
   never a match.
8. Index capability last, once the plan-inspection harness exists (§7).

Steps 1–4 are structure and change no test outcome. Step 5 is where conformance starts
biting.

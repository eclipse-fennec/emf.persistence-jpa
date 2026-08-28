# Expand query options (#238)

**Status:** design, 2026-08-28. Last of the OData sweep #237–#242 (companions: `$root` #241,
`date()`/`time()` #240, multi-level grouping #239, `$delta` #242 — all closed). Companion to
`conformance-and-capabilities.md` §9 and `query-ir-redesign.md`.

OData lets `$expand` carry per-expansion query options:

```
GET /Customers?$expand=Orders($filter=Amount gt 100;$orderby=Date desc;$top=5;$count=true)
```

`Query.expand` is `PropertyPath[0..*]` — a path and nothing else — so `emf.odata` answers 501 for
every one of them. This is the largest remaining block of OData conformance that no amount of work
on the consumer side can reach.

---

## 1. What expand already is

The issue frames expand as a *fetch hint* and the options as *result shaping*. Reading the code,
that framing is wrong in a way that changes the whole design.

**Both backends deliver non-containment references as EMF proxies — single- and multi-valued.**

| | mechanism |
|---|---|
| JPA, single-valued | `EBasicIndirectionPolicy.buildTargetProxy()` — id attribute + `eProxyURI` |
| JPA, multi-valued | `ETransparentIndirectionPolicy` substitutes the mapping's **id-only** `DirectReadQuery` and fills the list with lightweight proxies (`jpa://puName/TargetEClass#//refName/idAttr/idValue`). *"Target objects are never materialised while the list is filled."* |
| Mongo | `MongoResourceImpl.createProxyFor()` + `toProxyUri()`; multi-valued through `UnresolvedReference.isMultiValued()` into the list slot |

So a reference feature is **always already populated** — with proxies. Resolution happens on first
access through the standard EMF machinery (`ResourceSet.getEObject` → the resource's `getEObject`).

And `expand` today is exactly **batched proxy resolution**: a `LEFT JOIN FETCH` chain for the
single-valued prefix, `eclipselink.batch` with `BATCH_TYPE = IN` from the first to-many segment on.
The eorm `batch=true` flag of #17 does the same for a collection. What expand adds over lazy
resolution is **batching, not capability** — it prevents N+1, it does not change what the object
contains.

## 2. The semantics that follows

> **D1 — Expand options select *which proxies get resolved*, never which entries exist.**

After `$expand=Orders($filter=Amount gt 100;$top=5)`, `customer.getOrders()` still holds every
order the store has. Five of them are materialised; the rest stay proxies.

This is what makes the construct safe:

- **The object never misreports the store.** A filtered collection materialised into the feature
  would — and writing that instance back would delete the filtered-out elements, the same class of
  defect as the containment-lifecycle round (#142–#144).
- **No new result surface.** `QueryShape.OBJECTS` exposes a `Stream<EObject>`; nothing has to be
  added beside it. The discriminator is `eIsProxy()`, which every consumer already has — see D1b
  for what that means for the delivered result.
- **The translation from the issue is unchanged and still right**: one batched second query per
  expanded reference, `WHERE parentKey IN (:keys)` plus the filter, and
  `ROW_NUMBER() OVER (PARTITION BY parentKey ORDER BY …)` for the per-parent `top` — the window
  mechanic #214 introduced for group representatives.

> **D1b — What is delivered is exactly the selected set.**

The two statements are about different layers and must not be conflated:

- **Delivered result.** The expansion handed to the consumer — and serialised by `emf.odata` —
  contains exactly the children the options selected. Five requested, five returned. `eIsProxy()`
  is the discriminator: the resolved ones are the expansion, the untouched proxies are not part of
  it. A response never carries more than was asked for, and `$top` is not a lie.
- **Live object graph, in process.** The feature itself still holds every child the store has, as
  proxies. A consumer that goes on navigating `customer.getOrders()` and touches one resolves it
  through the normal EMF path.

That second point is EMF's contract, not a leak in expand: a non-containment reference is always
navigable. Expand promises *these were resolved and returned to you in one query* — it does not,
and could not, revoke the ability to navigate the rest later.

## 3. What is refused, and why

> **D2 — `$count` inside `$expand` is not served.** (Decision, 2026-08-28.)

The count of *matching* children is neither the collection size nor derivable from the resolved
subset, and under D1 there is no slot on an EObject that could carry it. Refused in `validate()`
with `CODE_UNSUPPORTED_FEATURE` and **no capability literal** — per #207, no stock of unserved
literals.

> **D3 — `orderBy` is a selector input, not a delivered order.**

Under D1 the list order belongs to the store; expand cannot reorder a feature it does not own. But
ordering is exactly what makes `top` meaningful — *"the five newest"* is `orderBy` + `top`.
Therefore `orderBy` is served **only in combination with `top` or `skip`**, and refused standing
alone, where it could only promise an order we do not deliver.

This keeps the project's line from #239 and #233: what cannot be honoured is refused uniformly in
`validate()`, not approximated.

> **D4 — A filter needs proxies, so it needs a non-containment reference.**

Containment children are not proxies: they are part of the object and arrive with it. There is
nothing for a filter to select, and narrowing them would have to *remove entries from the
feature* — exactly what D1 says an expansion never does. A filtered expansion whose last segment
is a containment reference is therefore refused with `CODE_INVALID_EXPAND`, structurally, like
the standing-alone `orderBy` of D3. No capability lets a backend out of it.

Found by the TCK rather than by thinking: the first end-to-end test filtered `Person.addresses`,
which is containment, and both children came back resolved because they had never been proxies.

> **D1b is a contract on the feature, not only on the response.**

An expansion must leave the reference holding the resolved object. `EcoreUtil.resolve` returns the
target and leaves the list holding the proxy — `EcoreEList.Dynamic` does not resolve on access — so
resolving without replacing the entry means the expansion read its targets and threw them away, and
`eIsProxy()` stops being the discriminator D1b names. Both backends had this defect.

It also decides something about *plain* expansions on JPA: riding on the fetch joins and
batch-fetch hints of #95 is not enough, because those read the rows but leave the EMF feature
pointing at a proxy. A plain expansion therefore runs through the same resolution, selecting
nothing.

## 4. Shape

`Expand` becomes a class; `count` from the issue's sketch drops out under D2.

```
Expand
  path     : PropertyPath  [1]
  filter   : Expression    [0..1]
  orderBy  : OrderBy       [0..*]   -- only together with top/skip (D3)
  top      : int           [0..1]
  skip     : int           [0..1]
  expand   : Expand        [0..*]   -- nesting
```

Additive: an `Expand` carrying only a path is today's fetch hint, so nothing that works stops
working.

**Capabilities** split by cost, not by one flag — a backend that filters but cannot page inside an
expansion is a reasonable state:

- `EXPAND_FILTER` — filter the resolution set
- `EXPAND_PAGE` — per-parent `top`/`skip`, and with it `orderBy` as selector

## 5. Backend state, before any work

**JPA** declares `EXPAND` (`JpaFlavorCapabilities:85`) and implements it as described above.

**Mongo declares no `EXPAND` at all.** There is no expansion there to extend — the issue's
"`$lookup` takes a pipeline" argument describes a construct that does not exist yet. Teaching Mongo
plain expand is a separate piece of work, tracked as #254 and not part of this one.

## 6. Slicing

1. **Done** — `Expand` class in `query.ecore` + builder, plain-path behaviour unchanged;
   `validate()` refuses filter/order/page against the two new capabilities.
2. **Done** — `EXPAND_FILTER` on JPA: a keyed second query per chunk of roots,
   `SELECT e FROM Root p JOIN p.<ref> e WHERE p.<id> IN :expandKeys AND (<filter>)`. The target
   carries the `e` alias so the filter — which addresses the expanded type — translates through
   the ordinary path with nothing changed in the translator.
3. `EXPAND_PAGE` on JPA — window function per parent, `orderBy` as selector.
4. **Done** — Mongo plain `EXPAND` (#254), by batched `$in` reads rather than `$lookup`.

Nesting (`Expand.expand`) rides on the multi-segment hints of #95 and is decided per slice, not up
front.

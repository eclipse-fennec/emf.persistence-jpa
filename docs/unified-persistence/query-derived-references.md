# Query-backed derived references — OCL annotations translated to backend queries

**Status:** approved concept (2026-07-28) — P1–P8 as proposed, decisions D1–D5 closed
(§8); implementation pending (§9). Companions: `query-ir-redesign.md` (Expression IR,
OCL bridge, decision record R1–R10), `query-usage.md` (execution API, capability
matrix), `concept.md` §3.1/§14. Cross-reference: DataInMotion/xdp#87 — this is the
original "OCL to any persistence provider" use case.

---

## 1. The idea

A model author annotates a **derived reference** with an OCL expression:

```
School
  students        : Student[*]          (persisted reference)
  femaleStudents  : Student[*]          derived, volatile, transient
                    { derivation = "self.students->select(s | s.gender = Gender::FEMALE)" }
```

Reading `school.getFemaleStudents()` must not iterate loaded objects in memory when the
data lives in a database — it should execute a **native backend query** instead:

- JPA: `SELECT s FROM Student s WHERE s.sid IN :ownerIds AND s.gender = :p0`
- Mongo: `{_id: {$in: [...]}, gender: "FEMALE"}`

One annotation, evaluated in memory where the data is local, pushed down where it is not.

## 2. What already exists

Nothing in this concept invents new machinery — it composes three proven pieces:

1. **The annotation vocabulary and delegate mechanism** ship with m2x:
   `m2x.ocl.engine` registers `EStructuralFeature.Internal.SettingDelegate.Factory` (and
   `EOperation…InvocationDelegate.Factory`, `EValidator.ValidationDelegate`) as DS
   services with emf.osgi whiteboard properties under the delegate URI
   `http://www.eclipse.org/fennec/m2x/ocl/1.0`. The OCL body lives in the feature's
   EAnnotation under the detail key **`derivation`** (fallback `initial`); the EPackage
   declares the delegate URI via the standard Ecore `settingDelegates` annotation.
   Evaluation is **in-memory** over the object graph (the `OclEngine`).
2. **The translation chain** is this repo's query stack: m2x OCL AST → `OclToExpr`
   (partial, blessed subset, diagnostics) → Expression IR → `QueryProcessor` (JPQL /
   Mongo find / memory).
3. **The execution seam**: every persistence resource is a `QueryableResource`; a
   delegate reaches it via `owner.eResource()` — no extra wiring.

**The gap** is exactly one component: a *query-backed* setting delegate that recognizes
pushdown-eligible OCL derivations and routes them through the query stack instead of the
in-memory engine.

## 3. Proposed decisions (to discuss)

| # | Decision | Rationale |
|---|---|---|
| P1 | **Reuse the m2x annotation vocabulary unchanged** — same delegate URI, same `derivation` key. Our factory registers on the same whiteboard with **higher service ranking** and **decorates** the m2x factory: it holds a DS reference to the lower-ranked factory and wraps the delegate it creates. Every non-pushdown case forwards literally to the wrapped m2x delegate — identical semantics by composition, not by reimplementation. In runtimes without the persistence bundles, the plain m2x factory serves the model untouched | One annotation, two execution strategies; models stay portable between plain-EMF and persistence runtimes; no second dialect to document; forwarding rules out semantic drift on the in-memory path |
| P2 | **Pushdown is an optimization, not a semantic switch.** The defined semantics of a derivation is its OCL meaning; the delegate pushes down when it can and evaluates in memory when it cannot. This is *not* the "no silent in-memory post-filtering" case of explicit queries — for a derived feature, in-memory evaluation is the contract, pushdown the accelerator | Explicit `query(...)` calls stay refusal-based; derived features stay total. The differential rule (P7) keeps both paths honest |
| P3 | **v1 recognized shape:** `self.<manyReference>->select(v | <predicate>)` where `<predicate>` maps into the blessed Expression subset via `OclToExpr`. Everything else → in-memory path | Covers the canonical use case (filtered view on an existing reference) with zero IR changes; the recognizer is a thin, testable function over the OCL AST |
| P4 | **Owner correlation via id-membership:** the delegate reads the target ids from the owner's stored reference value (EMF proxies / Mongo id arrays / JPA FK rows carry them without materialising the objects) and builds `from Student where <idAttribute> IN :ownerIds AND <predicate>` | Backend-neutral, needs no new IR construct, works today on both backends. Join-based correlation (JPA `MEMBER OF`, mappedBy) is a backend-internal optimization for later |
| P5 | **Execution routing is object-specific, decided per access.** EMF's delegate mechanism is static per feature (one delegate instance serves *all* instances of the EClass), so the dispatch happens inside the delegate via `owner.eResource()`: attached to a `QueryableResource` **and** reference not containment-loaded → pushdown; containment or already-resolved reference → in-memory over the local objects; not attached to a `QueryableResource` (e.g. the same model loaded from **XMI**) → forward to the wrapped m2x delegate, i.e. plain standard-OCL behaviour. The same object model works in both worlds, even side by side in one process | Containment data is already in memory — a query would be slower and hits the Mongo embedded-only path anyway; XMI-loaded graphs get exactly the untouched m2x semantics |
| P6 | **Derived features are read-only, `volatile transient derived`,** and the eorm pipeline must skip them (no column/collection mapping). Result is an unmodifiable `EList` | Standard EMF semantics for setting delegates; writes through a filtered view are a semantic minefield (reserved) |
| P7 | **Differential conformance:** for every TCK fixture, the pushdown result must equal the m2x in-memory evaluation of the same annotation | The OclEngine is the normative semantics; this is the same oracle pattern the memory backend plays for explicit queries |
| P8 | **Failures carry Diagnostics.** Non-parsing OCL or an unrecognizable-but-required shape is reported at delegate creation (fail fast, per #19); backend refusal or execution failure at access time wraps the Diagnostic in a `WrappedException` (EMF getter contract — getters cannot throw checked exceptions) | Consistent with the query stack's diagnostics contract |

## 4. Execution decision tree

```
getFemaleStudents() on owner o, feature f (annotated derivation)
│
├─ compiled plan for f?  (once per feature, cached in the delegate)
│    parse OCL (m2x parser) → recognize shape (P3)
│    ├─ shape matches → bridge predicate via OclToExpr → PUSHDOWN-CAPABLE(baseRef, expr)
│    └─ else          → MEMORY-ONLY (keep parsed OCL for the engine)
│
├─ MEMORY-ONLY, or o not attached to a QueryableResource (e.g. XMI-loaded),
│  or baseRef containment / already resolved
│    → forward to the wrapped m2x delegate (OclEngine)    (in-memory path)
│
└─ PUSHDOWN-CAPABLE and baseRef holds unresolved proxies
     ids  := target ids from o.eGet(baseRef) (no resolution)
     query = from(targetEClass)
               .where(and(path(idAttr).in(param("__ownerIds")), <expr>))
     → ((QueryableResource) o.eResource()).query(query, {__ownerIds: ids}, options)
     → unmodifiable EList of the results                   (pushdown path)
```

The in-memory path over unresolved non-containment proxies **loads** them (EMF proxy
resolution) — that is today's behaviour for any m2x-delegated model and stays available;
the point of this concept is that the common filtered-reference case never needs it.

## 5. The compiler (annotation → prepared plan)

A small pure component, `DerivedReferenceCompiler`:

1. **Parse** the `derivation` text once (m2x `OclExpressionParser`; the engine's
   expression cache applies).
2. **Recognize** the P3 shape on the AST: a `select` iterator whose source is a
   navigation of exactly one many-valued reference from `self`, and whose body is
   bridgeable. The iterator variable becomes the query root — no `Select` construct is
   added to the IR; *the derived reference itself is the select*, its result-set
   semantics come from the query envelope.
3. **Bridge** the body via `OclToExpr` (existing, partial, diagnostics on refusal).
4. Emit either `PushdownPlan(baseReference, predicate)` or `MemoryPlan(parsedOcl)`.

Compilation happens at delegate creation (model registration time), so a model with a
broken annotation fails loudly and early, not on first access.

## 6. Reserved for v2 (out of scope now, kept unblocked)

- **Type-rooted derivations with self-correlation:** `Student.allInstances()->select(s |
  s.school = self and …)`. Needs a `self` value binding (a reserved parameter is enough)
  **and reference-equality predicates** in the IR/processors (`path(school).eq(<owner>)`)
  — a deliberate Expression-model extension with its own capability term.
- **Derived EOperations** (`getFemaleStudents(minAge : int)`): identical mechanics via
  the m2x `InvocationDelegate` whiteboard; operation parameters map to query parameters.
  Additive once the setting-delegate path stands.
- **Join-based correlation** on JPA (replace the id-IN with `MEMBER OF`/mappedBy joins)
  — an internal `JpaQueryProcessor` optimization, invisible to the model.
- **Caching/memoization** beyond volatile-per-access (see D3).
- **Writable filtered views** — explicitly not planned.

## 7. Module & wiring

New bundle **`org.eclipse.fennec.persistence.query.derived`**:

- `QueryBackedSettingDelegateFactory` — DS component, same whiteboard properties as the
  m2x factory (`emf.configuratorType=SETTING_DELEGATE_FACTORY`,
  `emf.configuratorName=http://www.eclipse.org/fennec/m2x/ocl/1.0`), **higher
  `service.ranking`** (P1); holds the compiler and a **DS reference to the m2x factory**
  (target-filtered on the same configurator name, lower ranking) whose delegates it
  wraps for the in-memory path.
- Depends on: `persistence.query` (SPI), `expression.ocl` (bridge), `m2x.ocl.parser`
  (parse), `m2x.ocl.engine` (the wrapped factory; **optional greedy** — without it,
  MEMORY-ONLY plans and non-attached owners are refused with a diagnostic instead of
  silently returning nothing).
- No changes to the backends: execution goes through the public `QueryableResource`.

Note for **generated** models: EMF codegen emits delegating accessors only for features
marked `volatile transient derived` with the delegate annotation — dynamic models (our
usual case) delegate automatically.

## 8. Open decisions for the discussion

- **D1 — Same delegate URI + ranking vs own URI** *(decided 2026-07-28: P1 as written)*:
  same m2x delegate URI, decorator factory, object-specific routing per access. An own
  URI would force model authors to choose a world at annotation time — exactly what the
  XMI-vs-persistence requirement rules out.
- **D2 — Full-load guard** *(decided 2026-07-28: allow + WARN diagnostic)*: in-memory
  evaluation over a *huge* unresolved non-containment reference (MEMORY-ONLY plan on
  persisted data) can load the world — but that is no worse than resolving the base
  reference directly (`getStudents()` without paging), which the API allows anyway.
  v1 allows it and emits a WARN diagnostic; a configurable refusal threshold stays a
  possible later hardening.
- **D3 — Result caching** *(decided 2026-07-28: volatile — query on every access)*:
  matches the EMF semantics of `volatile derived` (the value *is* the query, there is no
  stale state), and any memoization would pose an invalidation question that has no
  honest answer today. Callers in hot loops snapshot the EList once. Outlook: once the
  change-stream work (concept §18.1 items 2–3) exists, its capture/changelog provides a
  natural invalidation signal — memoized derivations then become a *projection over the
  stream* instead of guesswork, and caching can be revisited at zero conceptual cost.
- **D4 — id-IN scale limit** *(decided 2026-07-28: documented limit + refusal)*: the
  correlation id list is bounded by the owner's stored reference (Mongo: already inside
  the 16 MB document; JPA: driver parameter limits, tens of thousands). v1 documents a
  configurable limit and refuses beyond it with a diagnostic — a reference of that size
  is a modelling smell anyway. Chunking or join-based correlation are later, purely
  backend-internal optimizations.
- **D5 — Where does the annotation live** *(decided 2026-07-28: EAnnotations)*: the
  EMF delegate mechanism requires the EAnnotation wiring regardless, and the metadata
  model is currently being refactored upstream — its aspect concept is likely to
  disappear entirely. EAnnotations are therefore the plan of record, not just a v1
  stopgap.

## 9. Phasing sketch (issues, once agreed)

1. Epic: query-backed derived references (this document).
2. `DerivedReferenceCompiler`: shape recognition + bridge + diagnostics (pure, unit-tested).
3. Delegate factory bundle + whiteboard wiring (P1 ranking, optional engine).
4. Pushdown execution path incl. id-IN correlation; TCK fixtures (School/Student added
   to the TCK model) for both backends × both id types.
5. Differential suite: pushdown vs OclEngine in-memory (P7).
6. Docs: user-guide section + capability notes.

# Composite identity — the canonical declaration

Settled 2026-08-07 (issue #115). Supersedes the multi-`isID` convention that #109/#110
were built on.

## 1. The declaration

Composite identity is declared **explicitly** on the EClass:

```xml
<eClassifiers xsi:type="ecore:EClass" name="OrderLine">
  <eAnnotations source="http://eclipse.org/fennec/persistence/1.0">
    <details key="idFeatures" value="orderId,lineNo"/>
  </eAnnotations>
  ...
</eClassifiers>
```

- **Source**: `http://eclipse.org/fennec/persistence/1.0`
- **Detail** `idFeatures`: comma-separated EAttribute names of the declaring EClass, in
  canonical (key) order. This order is the fragment-contract order (#109) and the eorm
  primary-key column order.
- Without the annotation, identity is EMF's: the **single `eID` attribute**.
- **Several `isID` attributes are refused** with a precise message. Ecore itself allows
  at most one (`EcoreValidator`, `validateEClass_AtMostOneID`) — that shape is an
  invalid model, not a composite declaration (the same conclusion emf.codec#99 reached
  for the codec's fallback).

`CompositeIds` (`org.eclipse.fennec.persistence.helper`) is the single reader of this
contract; every backend derives its answer from it.

## 2. Why an own vocabulary (and not the codec's, and not ExtendedMetadata)

The vocabulary deliberately **mirrors** the emf.codec `idFeatures` annotation
(source `http://eclipse.org/fennec/codec`, spec `16-annotation-reference.md`) — same
key, same value shape — but lives under its own source: how an EObject serializes to
JSON and how it maps to a database are separate concerns, and the two configurations
need not be congruent. This is the same reasoning that kept ExtendedMetadata out —
annotations serve a declared purpose, and borrowing another vocabulary's namespace
couples two planes that must be able to diverge.

## 3. Backend behaviour (deliberately not unified)

A cross-layer "one mechanism" was considered and rejected — backends differ by nature.
The annotation declares *what* the identity is; each backend decides *how* it stores it:

| Backend | Behaviour |
|---------|-----------|
| **JPA/eorm** | The eorm derivation (`CompositeIdAnalyzer`) reads the same contract: composite classes get EmbeddedId primary keys from the `idFeatures` order. eorm remains the mapping authority for column-level detail. |
| **Mongo** | The resource's static policy (`_id` as compound structured sub-document, `idFormat=STRUCTURED`, `idKeyMode=BOTH`, issue #110) **overrides** model annotations by default — mongo documents are usually written by this backend, and model annotations may serve JSON de/serialization instead. `OPTION_ID_CONFIG_FROM_MODEL` flips the serialization plane to the model's codec annotations; the `_id` contract itself is backend identity and stays. |
| **memory** | Uses `CompositeIds` directly (fragment contract, keyed access). |
| **Lucene (emf.search)** | Reads the same declaration once it lands; storage form is its own concern. |

## 4. Relation to the fragment contract (#109)

Unchanged: keyed access uses `k1=v1,k2=v2` in canonical order, `%`/`=`/`,`
percent-encoded, single-id classes keep the bare-value fragment. Only the *source* of
the id-attribute list moved from multi-`isID` to the annotation.

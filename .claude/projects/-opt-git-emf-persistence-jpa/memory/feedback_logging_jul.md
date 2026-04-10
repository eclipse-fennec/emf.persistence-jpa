---
name: Logging via java.util.logging
description: Use java.util.logging (JUL) for all logging, not SLF4J or OSGi LogService
type: feedback
---

Logging soll über `java.util.logging` (JUL) umgesetzt werden — keine externe Logging-Dependency.

**Why:** Keine zusätzliche Abhängigkeit nötig, JUL ist im JDK enthalten und in OSGi über Log-Bridge konfigurierbar.

**How to apply:** Bei Logging-Refactorings und neuem Code immer `java.util.logging.Logger` verwenden. Pattern: `private static final Logger LOG = Logger.getLogger(MyClass.class.getName());`

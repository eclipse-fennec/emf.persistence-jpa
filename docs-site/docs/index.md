---
layout: home

hero:
  name: Fennec Persistence JPA
  text: EMF models, real databases
  tagline: Persist EObjects to relational databases via EclipseLink/JPA or to MongoDB — no entity classes, no bytecode weaving, OSGi-native.
  image:
    src: /fennec-logo.png
    alt: Eclipse Fennec logo
  actions:
    - theme: brand
      text: Get Started
      link: /guides/getting-started
    - theme: alt
      text: User Manual
      link: /guides/overview
    - theme: alt
      text: View on GitHub
      link: https://github.com/eclipse-fennec/emf.persistence-jpa

features:
  - icon: 🗄️
    title: Ecore → JPA, automatically
    details: EClasses, EAttributes and EReferences become EclipseLink dynamic entities at runtime — all relationship types, inheritance, lazy/eager fetching. Custom mappings via EORM metadata for existing schemas.
    link: /guides/jpa
    linkText: JPA User Guide
  - icon: 🍃
    title: MongoDB backend
    details: The same EMF resource contract against MongoDB — EObjects (de)serialized to BSON through the Fennec codec framework, mixable with JPA resources in one ResourceSet.
    link: /guides/mongo
    linkText: MongoDB User Guide
  - icon: 🧩
    title: Persistence as a Resource
    details: Load, save and delete through jpa:// and mongodb:// URIs. One whiteboard Resource.Factory dispatches to lazily managed persistence units with idle-close lifecycle.
    link: /architecture/jpa-osgi
    linkText: How it works
  - icon: 🚦
    title: Liveness-gated services
    details: A connection service is only registered while the database is actually reachable — presence indicates functionality. Conditions and a runtime DTO service make health observable.
    link: /concepts/connection-liveness
    linkText: Read the concept
---

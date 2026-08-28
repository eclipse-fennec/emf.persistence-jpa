# Security Policy

## Reporting a Vulnerability

Eclipse Fennec Persistence JPA is part of the [Eclipse Fennec](https://projects.eclipse.org/projects/modeling.fennec) project.

If you discover a security vulnerability in this project, please report it responsibly:

1. **Do NOT open a public GitHub issue** for security vulnerabilities.
2. Preferably, report it privately via [GitHub Security Advisories for this repository](https://github.com/eclipse-fennec/emf.persistence-jpa/security/advisories).
3. Alternatively, report it via the [Eclipse Foundation Security Team](https://www.eclipse.org/security/) or email security@eclipse.org.
4. Include a description of the vulnerability, steps to reproduce, and potential impact.

The Eclipse Foundation security team will acknowledge your report within 5 business days and work with the project maintainers to address the issue.

## Supported Versions

| Version | Supported |
|---------|-----------|
| Latest development branch | Yes |

## Security Considerations

This framework bridges EMF (Eclipse Modeling Framework) with Jakarta Persistence (JPA) via EclipseLink. Key security-relevant areas:

- **Database Connections:** Managed via OSGi DataSource services. Credentials should be configured via OSGi Configuration Admin, not hardcoded.
- **JPQL Queries:** Entity names are validated against known EclipseLink descriptors before use in queries to prevent injection.
- **Logging:** Production deployments should set EclipseLink logging to `WARNING` or higher to avoid exposing SQL statements or parameter values in logs.
- **OSGi Service Isolation:** Each persistence unit runs in its own service scope with dedicated EntityManagerFactory.

## Dependencies

This project uses the following key dependencies:
- EclipseLink 4.x (Jakarta Persistence provider)
- Eclipse EMF (Modeling framework)
- H2 Database (test only)
- Mongo Driver
- MariaDB (test only)
- PostgreSQL (test only)
- FerretdB (test only)
- DocumentDB (test only)

Dependency vulnerability scanning is recommended via CycloneDX SBOM (see `build.gradle`).

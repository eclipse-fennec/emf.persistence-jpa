// The published, user-facing pages (allowlist). Shared by the sync script and the
// VitePress config so the set and its order are defined exactly once.
//   file  — source markdown in ../docs (the single source of truth)
//   slug  — route name under the section
//   title — sidebar / nav label
//
// GUIDES       -> /guides/       (the user manual)
// ARCHITECTURE -> /architecture/ (how it works inside)
// CONCEPTS     -> /concepts/     (design concepts)
//
// Internal working documents (development-guide, REVIEW, issue notes, discussion and
// unified-persistence drafts, ci.md) are deliberately NOT published — links to them
// from published pages fall back to GitHub blob URLs via the sync script.
export const GUIDES = [
  { file: 'overview.md', slug: 'overview', title: 'Overview' },
  { file: 'getting-started.md', slug: 'getting-started', title: 'Getting Started' },
  { file: 'jpa-user-guide.md', slug: 'jpa', title: 'JPA User Guide' },
  { file: 'mongo-user-guide.md', slug: 'mongo', title: 'MongoDB User Guide' },
  { file: 'repository-user-guide.md', slug: 'repository', title: 'Repository User Guide' },
  { file: 'query-user-guide.md', slug: 'query', title: 'Query User Guide' },
  { file: 'configuration-reference.md', slug: 'configuration', title: 'Configuration Reference' },
];

export const ARCHITECTURE = [
  { file: 'osgi-architecture.md', slug: 'jpa-osgi', title: 'JPA & OSGi Architecture' },
  { file: 'mongo-architecture.md', slug: 'mongo', title: 'MongoDB Backend' },
];

export const CONCEPTS = [
  { file: 'concept-connection-liveness.md', slug: 'connection-liveness', title: 'Connection Liveness' },
  { file: 'diagnostics.md', slug: 'diagnostics', title: 'Diagnostics' },
];

import { defineConfig } from 'vitepress'
import { GUIDES, ARCHITECTURE, CONCEPTS } from '../../guides.mjs'

// Per-project docs are served under a versioned sub-path, matching the org
// convention (https://eclipse-fennec.github.io/<repo>/<version>/). The snapshot
// branch publishes to /emf.persistence-jpa/snapshot/; tagged releases / `latest`
// get added once the first release lands.
const version = process.env.DOCS_BRANCH || 'snapshot'
const base = `/emf.persistence-jpa/${version}/`

// Canonical published origin. Links that point OUTSIDE the current docs base
// (other doc versions) must be full URLs — VitePress auto-prepends `base` to any
// root-absolute (`/…`) link, which would otherwise double the path.
const SITE = 'https://eclipse-fennec.github.io/emf.persistence-jpa'

// Version selector. Only `snapshot` is deployed today; keep as data so adding
// `latest` and tagged versions later is a one-liner.
const versions = [{ text: 'snapshot', link: `${SITE}/snapshot/` }]

const guideItems = GUIDES.map((g) => ({ text: g.title, link: `/guides/${g.slug}` }))
const architectureItems = ARCHITECTURE.map((g) => ({ text: g.title, link: `/architecture/${g.slug}` }))
const conceptItems = CONCEPTS.map((g) => ({ text: g.title, link: `/concepts/${g.slug}` }))

export default defineConfig({
  title: 'Fennec Persistence JPA',
  description:
    'EMF persistence for OSGi — map Ecore models to relational databases via EclipseLink or to MongoDB, without writing entity classes.',
  lang: 'en-US',
  base,
  cleanUrls: true,
  lastUpdated: true,
  ignoreDeadLinks: true,

  markdown: {
    // Shiki has no dedicated 'gradle' grammar; Gradle build files are Groovy.
    languageAlias: { gradle: 'groovy' },
  },

  head: [
    ['link', { rel: 'icon', type: 'image/png', href: `${base}fennec-logo.png` }],
    ['meta', { name: 'theme-color', content: '#c0631c' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:title', content: 'Fennec Persistence JPA' }],
    [
      'meta',
      {
        property: 'og:description',
        content:
          'EMF persistence for OSGi — relational via EclipseLink/JPA and document-oriented via MongoDB.',
      },
    ],
  ],

  themeConfig: {
    logo: '/fennec-logo.png',
    siteTitle: 'Fennec Persistence JPA',

    nav: [
      { text: 'Home', link: '/' },
      { text: 'User Manual', items: guideItems },
      { text: 'Architecture', items: architectureItems },
      { text: 'Concepts', items: conceptItems },
      { text: `version: ${version}`, items: versions },
    ],

    sidebar: {
      '/guides/': [{ text: 'User Manual', items: guideItems }],
      '/architecture/': [{ text: 'Architecture', items: architectureItems }],
      '/concepts/': [{ text: 'Concepts', items: conceptItems }],
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/eclipse-fennec/emf.persistence-jpa' },
    ],

    search: { provider: 'local' },

    editLink: {
      pattern:
        'https://github.com/eclipse-fennec/emf.persistence-jpa/edit/snapshot/docs/:path',
      text: 'Edit this page on GitHub',
    },

    footer: {
      message:
        'Released under the EPL-2.0 License. Eclipse Fennec is part of the Eclipse Foundation.',
      copyright: 'Copyright © Eclipse Foundation and contributors',
    },
  },
})

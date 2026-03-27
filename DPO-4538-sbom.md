# DPO-4538 FINAP SBOM

A Software Bill of Materials (SBOM) is a comprehensive inventory of all the components, libraries, and dependencies 
that are included in a software application. It provides detailed information about the software's composition, 
including the versions of each component, their sources, and any known vulnerabilities associated with them.

A SBOM is needed for the application defined in [project.clj](./ote/project.clj)

## Scope

### In scope

The primary target is `ote/project.clj` — a Leiningen-based Clojure/ClojureScript project with ~60+ direct dependencies (plus transitive dependencies resolved via Maven Central and Clojars).

### Deployed artifacts from this repository

Analysis of the CI/CD pipeline (`aws/ansible/jenkins/jobs/`) shows the following deployed artifacts:

1. **OTE uberjar** (`ote/project.clj`) — The main application. Built via `lein production`, producing `ote-0.1-SNAPSHOT-standalone.jar`, deployed to staging/production via Ansible. **Primary SBOM target.**

2. **Database migrations** (`database/pom.xml`) — Flyway migrations run via Maven during every deployment against the production database with privileged credentials. Dependencies: `flyway-maven-plugin 10.10.0`, `flyway-database-postgresql 10.10.0`, `postgresql 42.7.3`. Although short-lived, these execute on the server and are security-relevant. **Secondary SBOM target.** This is a Maven project, so a Maven-compatible SBOM tool is needed (separate from the Leiningen approach for OTE).

### Out of scope

- **`tools/lambda-kalkati2gtfs/`** — Python AWS Lambda function. Although deployed to AWS, it has no automated deployment pipeline in this repo (manual `build.sh` → `deploy.zip` upload). Uses Python 2 syntax, appears to be legacy. Out of scope for this ticket.
- **`tools/load-test/`** — Development tool only, not deployed.
- **`tools/dashboard/`** — No deployment pipeline found in Jenkins jobs. Internal tool, not part of the release process.
- **`tools/changelog/`** — Runs during CI to generate `changelog.html`, which is copied into OTE's `resources/public/`. The changelog tool's own Clojure dependencies do NOT ship in the uberjar — only the static HTML output does.
- **`tools/feedvalidator/`** — Standalone Docker tool (Google transitfeed). No reference in deployment pipeline.
- **`tools/shaclvalidator/`** — Standalone Docker tool (Apache Jena). No reference in deployment pipeline.
- **Docker images** (database, nginx, cypress) — Infrastructure/testing containers.

### Scope decisions

- **Production dependencies only** — The SBOM covers only production dependencies that ship in the uberjar. The `:dev` profile deps (`test.check`, `json-schema`) are excluded. Dev dependency coverage may be added in a future iteration.

- **ClojureScript/CLJSJS packages** — These are Maven artifacts containing pre-bundled standalone JS builds (e.g., `react.min.js`). The internal npm transitive dependencies of each JS library are baked into the bundle and will NOT appear individually in the SBOM. Only peer dependencies modeled as Maven deps (e.g., `react-dom` → `react`) are visible. **Decision: Maven-level tracking is sufficient for now.** This is a known limitation — the SBOM will underrepresent the true JS dependency tree. Deeper JS-level SBOM coverage can be explored in a future iteration.

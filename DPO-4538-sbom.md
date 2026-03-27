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

## Output format

**CycloneDX JSON 1.6** — This is the required output format for both SBOM targets. Any tooling chosen must support generating CycloneDX spec version 1.6 in JSON format.

## Approach

### OTE uberjar — Artifact scan with dependency tree cross-check

**Step 1: Generate dependency tree reference**
Run `lein deps :tree` in the `ote/` directory to produce the resolved dependency tree (including transitive deps with versions after conflict resolution). This serves as the **expected** dependency list.

**Step 2: Build the uberjar**
Run the existing `lein production` build to produce `ote-0.1-SNAPSHOT-standalone.jar`. This is the artifact that actually ships.

**Step 3: Scan the artifact to produce the SBOM**
Use an artifact-level scanner (e.g., `syft`, `trivy`, or `cdxgen`) against the built uberjar. The scanner identifies components via `META-INF/maven/` POM files preserved inside the JAR and outputs a CycloneDX JSON 1.6 SBOM.

**Step 4: Cross-check (one-time manual validation)**
Compare the dependency tree from Step 1 against the components in the SBOM from Step 3. This is a manual step performed during initial setup to identify gaps in the artifact scanner's coverage (e.g., deps lacking `META-INF/maven/` metadata). Since dependencies change rarely in this project, once the initial cross-check passes, simple version bumps can be expected to reflect correctly in subsequent SBOM generations without re-validation.

### Database migrations — Source-level approach

The Flyway migrations (`database/pom.xml`) don't produce a fat JAR artifact, so artifact scanning doesn't apply. For this target, a source-level tool is needed — either the official `cyclonedx-maven-plugin` or `cdxgen` reading the `pom.xml` directly.

<!-- REVIEW: The database pom.xml has no runtime `<dependencies>` — the Flyway and PostgreSQL JDBC deps are declared as plugin dependencies inside the `<build><plugins>` section. Verify that whichever tool is chosen can capture Maven plugin dependencies, not just project-level dependencies. This is a non-standard setup that many SBOM tools may not handle correctly. -->

## Cross-check results

A three-way cross-check was performed between `lein deps :tree`, a Syft 1.42.3 artifact scan of the built uberjar, and a direct listing of the jar contents (`jar -tf`).

### Summary

| Source | Count |
|---|---|
| `lein deps :tree` compile-scope deps | 186 |
| `lein deps :tree` test-scope deps | 2 |
| Syft-detected components | 161 |
| `META-INF/maven/` entries in jar | 161 |
| Exact matches (lein ∩ Syft, same version) | 159 |
| Version mismatches | 1 |
| In lein but missing from Syft | 26 |

### Key finding: `lein deps :tree` is authoritative

The jar contents analysis confirmed that **`lein deps :tree` accounts for every component in the uberjar**. The only entry in the jar not in the lein tree is `ote/ote` itself (the project's own artifact). Syft adds no components beyond what lein already knows — it provides the CycloneDX format but not additional discovery.

### Syft detection is accurate but incomplete

Syft detected exactly the 161 components that have `META-INF/maven/` metadata in the jar — no false positives, no missed metadata. The 85.5% coverage gap is entirely due to 23 dependencies whose jars don't embed Maven metadata (plus 3 deps absent from the jar entirely).

### Version mismatch

`cheshire/cheshire`: lein declares 5.8.0, but the embedded `META-INF/maven/.../pom.properties` inside the jar reports 5.7.2-SNAPSHOT. This is a packaging bug in the upstream cheshire release — the actual classes are 5.8.0, but the baked-in metadata was not updated. Syft reports what the jar metadata says.

### Deps absent from the jar (3)

These appear in `lein deps :tree` but have no classes or metadata in the uberjar — they are filtered out during packaging:

| Dependency | Reason absent |
|---|---|
| `nrepl/nrepl 1.0.0` | Excluded during uberjar build (dev profile filtering) |
| `org.nrepl/incomplete 0.1.0` | nREPL companion, same exclusion |
| `com.google.jsinterop/jsinterop-annotations 1.0.0` | Compile-time annotations only, no runtime classes |

### Deps in jar but invisible to Syft (23)

These have classes in the uberjar but no `META-INF/maven/` metadata, making them invisible to artifact scanners. Categorised by reason:

| Category | Dependencies | Reason |
|---|---|---|
| Pre-Maven-era / old libs | `javax.activation/activation`, `javax.media/jai_core`, `jgridshift/jgridshift`, `postgresql/postgresql 9.3`, `org.mozilla/rhino` | Released before Maven metadata embedding was standard |
| Signed / special packaging | `org.bouncycastle/bcpkix-jdk15on`, `org.bouncycastle/bcprov-jdk15on` | BouncyCastle jars are signed; uberjar merge may strip metadata |
| Compression internals | `net.jpountz.lz4/lz4`, `org.tukaani/xz` | Small native-wrapper jars used by nippy; lz4 also ships native .so/.dylib |
| GeoTools transitive | `org.ejml/ejml-core`, `org.ejml/ejml-ddense`, `org.roaringbitmap/RoaringBitmap`, `com.github.andrewoma.dexx/collection`, `com.github.ben-manes.caffeine/caffeine` | Niche transitive deps with non-standard packaging |
| Annotation / compile-time | `org.checkerframework/checker-qual`, `org.ow2.asm/asm-all` | Annotation/bytecode tools that do ship classes but lack metadata |
| Jena / Thrift transitive | `com.google.protobuf/protobuf-java`, `org.apache.thrift/libthrift` | Large transitive deps (720 + 258 classes) without embedded Maven metadata |
| Build tool in jar | `com.yahoo.platform.yui/yuicompressor` | CSS minifier used by garden at compile time |
| Other | `net.java.dev.jna/jna`, `org.jdom/jdom2`, `com.amazonaws/dynamodb-streams-kinesis-adapter`, `org.postgresql/postgresql 42.5.4` | Various missing metadata |

**Security-relevant gaps:** BouncyCastle (crypto) and PostgreSQL JDBC (database driver) are the most important missing entries from a vulnerability tracking perspective.

### Implications for SBOM approach

Since `lein deps :tree` is the authoritative and complete source, and Syft only covers 85.5% of it, the recommended approach is:

1. **Use `lein deps :tree` as the primary dependency source** — it captures all 186 compile-scope deps including the 23 that Syft misses.
2. **Convert the lein dependency list to CycloneDX JSON 1.6** — either via a conversion script or by using a tool that reads Leiningen project files directly (e.g., `lein pom` → `cyclonedx-maven-plugin`, or `cdxgen`).
3. **Syft scan as optional secondary validation** — can still be used to cross-check the artifact, but is not needed as the primary SBOM source.
4. **Filter out the 3 absent deps** — `nrepl/nrepl`, `org.nrepl/incomplete`, and `com.google.jsinterop/jsinterop-annotations` should be excluded from the SBOM since they don't ship in the artifact. The 2 test-scope deps (`test.check`, `json-schema`) are also excluded per scope decisions.

### Database migrations — Source-level approach

The Flyway migrations (`database/pom.xml`) don't produce a fat JAR artifact, so artifact scanning doesn't apply. For this target, a source-level tool is needed — either the official `cyclonedx-maven-plugin` or `cdxgen` reading the `pom.xml` directly.

<!-- REVIEW: The database pom.xml has no runtime `<dependencies>` — the Flyway and PostgreSQL JDBC deps are declared as plugin dependencies inside the `<build><plugins>` section. Verify that whichever tool is chosen can capture Maven plugin dependencies, not just project-level dependencies. This is a non-standard setup that many SBOM tools may not handle correctly. -->

## Future work

- **nREPL as a dependency** — The lein dependency tree includes `nrepl/nrepl` and `org.nrepl/incomplete`, though they are correctly excluded from the uberjar. It may be worth investigating whether nREPL can be moved to a `:dev`-only profile to make the dependency tree cleaner.
- **Dev dependency SBOM coverage** — Extend SBOM scope to include `:dev` profile dependencies.
- **CLJSJS deep JS-level tracking** — Map bundled JS transitive deps within CLJSJS Maven artifacts.

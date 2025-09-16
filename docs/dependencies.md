# Finap dependencies

Purpose: High-level map of where runtime/build/test dependencies are
declared in this repository and how to produce a dependency listing
for each part. This intentionally does not list every single library
version; instead it shows where they live and how to enumerate them
yourself.

---
## Overview of contents
This repository contains several pieces of software in different
languages and ecosystems.

- Application (Clojure / ClojureScript): `ote/`
- Support tools (mainly Clojure, but also others): `tools/`
  - Load tests: `tools/load-test/`
  - Dashboard: `tools/dashboard/`
  - Changelog generator: `tools/changelog/`
  - Feed validator (Python via Docker): `tools/feedvalidator/`
  - KALKATI → GTFS lambda helper (Python): `tools/lambda-kalkati2gtfs/`
- Database migrations (Docker + Flyway + PostgreSQL): `database/`
- CKAN portal customizations: `nap/` (not really in use as of 2025)
- E2E tests (Docker, Cypress): `cypress/`
- Infrastructure & provisioning (Ansible / Docker): `aws/ansible`, `nap/docker`, assorted `Dockerfile`s

---
## Clojure / ClojureScript Projects (Leiningen)
Projects with `project.clj`:
- `ote/project.clj` (main backend + frontend build)
- `tools/load-test/project.clj`
- `tools/dashboard/project.clj`
- `tools/changelog/project.clj`

To produce a full dependency tree for each (including transitive dependencies),
cd into the directory with the `project.clj` and run:

```
lein deps :tree
```

An useful thing to know about the dependencies is that even the front-end dependencies
are retrieved through Leiningen via the cljsjs deps.

## Python (Lambda helper)
Path: `tools/lambda-kalkati2gtfs/requirements.txt`
Install & list (inside a fresh virtual environment):
```
cd tools/lambda-kalkati2gtfs
python -m venv venv
./venv/bin/pip install -r requirements.txt
./venv/bin/pip freeze

or look into venv/lib/python3.11/site-packages/
```

## Python (Feed Validator via Docker)
Path: `tools/feedvalidator/Dockerfile` (clones Google transitfeed repo and installs via
pip inside image). Build & enumerate:
```
cd tools/feedvalidator
docker build -t finap/feedvalidator .
docker run --rm finap/feedvalidator python -m pip freeze > feedvalidator-freeze.txt
```
To inspect apt-level packages:
```
docker run --rm finap/feedvalidator bash -lc "apt list --installed 2>/dev/null" > feedvalidator-apt.txt
```
## Database Migrations (Maven + Flyway)
Path: `database/pom.xml`
The POM has no regular `<dependencies>`; dependencies are embedded inside
the Flyway Maven plugin configuration (Flyway core + PostgreSQL driver).

**Not yet clear**, how to check the full dependency tree for this

There is also a Dockerfile to pack this into an image that can be run to supply
the database for local use.

## CKAN Portal Customizations (`nap/`)
The `nap` directory used to include code originating from CKAN, but nowadays
there is mainly a docker-compose setup for running the dependent services locally.

## Cypress (E2E Tests)
The `cypress/` directory contains an e2e test setup that uses Cypress and Docker.
It is not currently in use and not covered by this document apart from this mention.

## Docker Images & Base OS Dependencies
Dockerfiles:
- `tools/feedvalidator/Dockerfile` (Debian base, Python + transitfeed)
- `database/Dockerfile` (Ubuntu base, PostgreSQL 16 + PostGIS + Maven)
- `cypress/Dockerfile` (OpenJDK 8 base + Chrome + Node.js 6 + Cypress runtime deps)
- `nap/docker/nginx/Dockerfile` (CentOS 7 base + nginx)

Extract installed package list for each (example for database image):
```
cd database
docker build -t finap/db .
docker run --rm finap/db bash -lc "apt list --installed 2>/dev/null" > db-apt.txt
```
For the CentOS-based image:
```
docker build -t finap/nginx nap/docker/nginx
docker run --rm finap/nginx rpm -qa > nginx-rpm.txt
```

## Ansible (Infrastructure)
Directory: `aws/ansible/`

Ansible tools are not defined in this project. The CI setup provides its own copy
of Ansible.

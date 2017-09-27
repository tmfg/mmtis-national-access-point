# Docker instances for local development of NAP

This folder contains Docker images required setting up the National Access Point
development environent locally.

The database is shared between CKAN and the OTE application and is in folder: `../../database`.

## Running the instances

The instances are pushed to dockerhub and can be run without building locally.

To run the instances individually:

* SOLR: `docker run -it -p8983:8983 solita/napote-solr`
* Redis: `docker run -it -p6379:6379 solita/napote-redis`
* PostgreSQL: run `../../database/devdb_restart.sh` script

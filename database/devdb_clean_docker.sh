#!/bin/sh

### HOX: This will be run inside napotedb container ###

set -e

cd /database


psql -h napotedb -U napote postgres -c "DROP DATABASE napote;"
psql -h napotedb -U napote postgres -c "CREATE DATABASE napote OWNER napote;"
psql -h napotedb -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;"
psql -h napotedb -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;"
psql -h napotedb -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;"
psql -h napotedb -U postgres napote -c "CREATE EXTENSION postgis"
psql -h napotedb -U postgres napote -c "CREATE EXTENSION postgis_topology"

psql -h napotedb -U napote napote -f /nap/ckan-initial-db.sql

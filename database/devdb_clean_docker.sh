#!/bin/sh

### HOX: This will be run inside napotedb163 container ###

set -e

cd /database

psql -h napotedb163 -U napote postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='napote' AND pid <> pg_backend_pid();"
psql -h napotedb163 -U napote postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='napotetest' AND pid <> pg_backend_pid();"
psql -h napotedb163 -U napote postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='napotetest_template' AND pid <> pg_backend_pid();"

psql -h napotedb163 -U napote postgres -c "DROP DATABASE IF EXISTS napote;"
psql -h napotedb163 -U napote postgres -c "CREATE DATABASE napote OWNER napote;"
psql -h napotedb163 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;"
psql -h napotedb163 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;"
psql -h napotedb163 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;"
psql -h napotedb163 -U postgres napote -c "CREATE EXTENSION postgis"
psql -h napotedb163 -U postgres napote -c "CREATE EXTENSION postgis_topology"

psql -h napotedb163 -U napote napote -f /nap/ckan-initial-db.sql

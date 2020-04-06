#!/bin/sh

### HOX: This will be run inside napotedbpsql11 container ###

set -e

cd /database

echo "jee"

psql --version
psql -h napotedb11 -U napote postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='napote' AND pid <> pg_backend_pid();"
psql --version

echo "jee jee"

psql -h napotedb11 -U napote postgres -c "CREATE USER ote;" || true
psql -h napotedb11 -U napote postgres -c "CREATE USER flyway;" || true
psql -h napotedb11 -U napote postgres -c "DROP DATABASE IF EXISTS napote;"
psql -h napotedb11 -U napote postgres -c "CREATE DATABASE napote OWNER napote;"
psql -h napotedb11 -U postgres napote -c "CREATE EXTENSION postgis"
psql -h napotedb11 -U postgres napote -c "CREATE EXTENSION postgis_topology"

echo "Extracting restore SQL"
pg_restore -h napotedbpsql11 -U napote -c -C -f /database/restore.sql /database/finap.dump

# I really tried to make a collation with locale-gen and CREATE COLLATION
# but couldn't get it to work in the same way as AWS RDS postgres.
# Ignore the collation for now.
echo "Processing SQL file"
sed -i "s/COLLATE pg_catalog.\"fi_FI.utf8\"//" /database/restore.sql

echo "Running SQL file"
psql -h napotedb11 -U napote -f /database/restore.sql napote

rm /database/restore.sql

psql -h napotedb11 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;"
psql -h napotedb11 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;"
psql -h napotedb11 -U postgres napote -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;"

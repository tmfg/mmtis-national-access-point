#!/bin/sh

### HOX: This will be run inside napotedb11 container ###

set -e

cd /database

echo "Running: devdb_testdata.sh script"
echo "Insert test data"

psql -h napotedb11 -p 5432 -U ckan napote -X -q -a -v ON_ERROR_STOP=1 --pset pager=off -f testdata-ckan.sql > /dev/null || true

echo "Insert OTE test data"
psql -h napotedb11 -U napote napote -X -q -a -v ON_ERROR_STOP=1 --pset pager=off -f testdata-ote.sql > /dev/null || true


echo "Insert Finnish municipalities"
psql -h napotedb11 -U napote napote -c "TRUNCATE finnish_municipalities;" || true
psql -h napotedb11 -U napote napote -c "\COPY finnish_municipalities FROM /static-data/finnish_municipalities.csv CSV HEADER;" || true

echo "Insert Finnish postal codes"
psql -h napotedb11 -U napote napote -c "TRUNCATE finnish_postal_codes;" || true
psql -h napotedb11 -U napote napote -c "\COPY finnish_postal_codes FROM /static-data/finnish_postal_codes.csv CSV HEADER;" || true

echo "Insert Finnish regions"
psql -h napotedb11 -U napote napote -c "TRUNCATE finnish_regions;" || true
psql -h napotedb11 -U napote napote -c "\COPY finnish_regions FROM /static-data/maakunnat.csv CSV HEADER;" || true

echo "Insert spatial search table"
psql -h napotedb11 -U napote napote -c "TRUNCATE \"spatial-relations-places\";" ||  true
psql -h napotedb11 -U napote napote -c "\COPY \"spatial-relations-places\" FROM /static-data/spatial_relations_places.csv CSV HEADER;" || true

echo "Clean up and free connections"
psql -h napotedb11 -U napote napote -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'napote' AND pid <> pg_backend_pid();"

echo "Drop napotetest databases if they exists"
psql -h napotedb11 -U napote napote -c "DROP DATABASE IF EXISTS napotetest_template;"
psql -h napotedb11 -U napote napote -c "DROP DATABASE IF EXISTS napotetest;"

echo "Create napotetest_template database using napote database"
psql -h napotedb11 -U napote napote -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"

echo "Create napotetest database using napotetest_template database"
psql -h napotedb11 -U napote napote -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

echo "Done."

#!/bin/sh

### THIS IS MEANT TO BE RUN INSIDE CircleCI CONTAINERS ONLY!

sudo apt-get update
sudo apt-get install -y postgresql-client maven

P="psql -h localhost -p 5432 -U postgres"
$P -f ../nap/ckan-initial-db.sql napote

mvn flyway:migrate

$P -f testdata-ckan.sql napote
$P -f testdata-ote.sql napote

echo "Insert Finnish municipalities"
$P -c "TRUNCATE finnish_municipalities;" || true
$P -c "\COPY finnish_municipalities FROM /static-data/finnish_municipalities.csv CSV HEADER;" || true

echo "Insert Finnish postal codes"
$P -c "TRUNCATE finnish_postal_codes;" || true
$P -c "\COPY finnish_postal_codes FROM /static-data/finnish_postal_codes.csv CSV HEADER;" || true

echo "Insert Finnish regions"
$P -c "TRUNCATE finnish_regions;" || true
$P -c "\COPY finnish_regions FROM /static-data/maakunnat.csv CSV HEADER;" || true

echo "Insert countries"
$P -c "TRUNCATE country;" || true
$P -c "\COPY country FROM /static-data/countries.csv CSV HEADER;" || true


echo "Clean up and free connections"
$P -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'napote' AND pid <> pg_backend_pid();"

echo "Drop napotetest databases if they exists"
$P -c "DROP DATABASE IF EXISTS napotetest_template;"
$P -c "DROP DATABASE IF EXISTS napotetest;"

$P -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"
$P -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

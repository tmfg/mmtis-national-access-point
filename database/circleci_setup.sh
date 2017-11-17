#!/bin/sh

### THIS IS MEANT TO BE RUN INSIDE CircleCI CONTAINERS ONLY!

sudo apt-get install postgresql-client maven

P="psql -h localhost -p 5432 -U postgres -c"

$P "CREATE USER napotetest WITH CREATEDB;"
$P "ALTER USER napotetest WITH SUPERUSER;"
$P "CREATE USER napote;"
$P "ALTER USER napote WITH SUPERUSER;"
$P "CREATE USER ckan WITH CREATEDB;"
$P "ALTER USER ckan WITH SUPERUSER;"
$P "CREATE DATABASE napote OWNER napote;"
$P "CREATE DATABASE temp OWNER napotetest;"
$P "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;"
$P "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;"
$P "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;"
$P "CREATE EXTENSION postgis" napote
$P "CREATE EXTENSION postgis_topology" napote

mvn flyway:migrate

$P "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"
$P "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

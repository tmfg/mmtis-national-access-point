#!/bin/sh

### THIS IS MEANT TO BE RUN INSIDE CircleCI CONTAINERS ONLY!

sudo apt-get install postgresql-client maven

P="psql -h localhost -p 5432 -U postgres"
$P -f ../nap/ckan-initial-db.sql

mvn flyway:migrate

$P -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"
$P -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

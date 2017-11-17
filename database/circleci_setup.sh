#!/bin/sh

### THIS IS MEANT TO BE RUN INSIDE CircleCI CONTAINERS ONLY!

sudo apt-get install postgresql-client maven

mvn flyway:migrate

P="psql -h localhost -p 5432 -U postgres -c"

$P "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"
$P "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

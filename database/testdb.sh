#!bin/bash

echo "Creating local PostgreSQL database for CI build"

P="psql -h 127.0.0.1 -U napote "


$P -c "DROP DATABASE IF EXISTS napotetest_template;"
$P -c "CREATE DATABASE napotetest_template;"

$P -f nap/ckan-initial-db.sql napotetest_template

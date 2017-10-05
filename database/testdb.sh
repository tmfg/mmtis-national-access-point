#!bin/bash

echo "Creating local PostgreSQL database for CI build"

P="psql -h localhost -U napote -c "

$P "DROP DATABASE IF EXISTS napotetest_template;"
$P "CREATE DATABASE napotetest_template;"

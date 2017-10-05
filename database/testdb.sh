#!bin/sh

echo "Creating local PostgreSQL database for CI build"

p = "psql -h localhost -U napote -c "

$p "DROP DATABASE IF EXISTS napotetest_template;"
$p "CREATE DATABASE napotetest_template;"

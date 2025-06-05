#!/bin/sh

### HOX: This will be run inside napotedb163 container ###

set -e

cd /database

echo "Drop napotetest_template database if it exist"
psql -h napotedb163 -U napote napote -c "DROP DATABASE IF EXISTS napotetest_template;"

echo "Drop napotetest database if it exist"
psql -h napotedb163 -U napote napote -c "DROP DATABASE IF EXISTS napotetest;"

echo "Clean up and free connections"
psql -h napotedb163 -U napote napote -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'napote' AND pid <> pg_backend_pid();"

echo "Create napotetest_template database using napote database"
psql -h napotedb163 -U napote napote -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"

echo "Create napotetest database using napotetest_template database"
psql -h napotedb163 -U napote napote -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

echo "Done."

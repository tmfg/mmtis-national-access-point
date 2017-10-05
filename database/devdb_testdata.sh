#!/bin/sh

### HOX: This will be run inside napotedb container ###

set -e

cd /database

echo "Run test data to napote database"
psql -h napotedb -U napote napote -X -q -a -v ON_ERROR_STOP=1 --pset pager=off -f testdata-ote.sql > /dev/null

echo "Clean up and free connections"
psql -h napotedb -U napote napote -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'napote' AND pid <> pg_backend_pid();"

echo "Create napotetest_template database using napote database"
psql -h napotedb -U napote napote -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"

echo "Create napotetest database using napotetest_template database"
psql -h napotedb -U napote napote -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"


echo "Run test data to ckan database"
psql -h napotedb -U ckan ckan -X -q -a -v ON_ERROR_STOP=1 --pset pager=off -f testdata-ckan.sql > /dev/null

echo "Clean up and free connections"
psql -h napotedb -U ckan ckan -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'ckan' AND pid <> pg_backend_pid();"

echo "Create ckantest_template database using ckan database"
psql -h napotedb -U ckan ckan -c "CREATE DATABASE ckantest_template WITH TEMPLATE ckan OWNER napotetest;"

echo "Create ckantest database using ckantest_template database"
psql -h napotedb -U ckan ckan -c "CREATE DATABASE ckantest WITH TEMPLATE ckantest_template OWNER napotetest;"

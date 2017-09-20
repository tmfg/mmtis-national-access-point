#!/bin/sh

### HUOM: tämä ajetaan napotedb-kontin sisällä ###

set -e

cd /tietokanta

echo "Ajetaan testidata napote-kantaan"
psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U napote napote -X -q -a -v ON_ERROR_STOP=1 --pset pager=off -f testidata.sql > /dev/null

echo "Tapa porsaat ja vapauta olemassaolevat yhteydet"
psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U napote napote -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'napote' AND pid <> pg_backend_pid();"

echo "Luodaan napotetest_template kanta napote-kannan pohjalta"
psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U napote napote -c "CREATE DATABASE napotetest_template WITH TEMPLATE napote OWNER napotetest;"

echo "Luodaan napotetest kanta napotetest_template kannan pohjalta"
psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U napote napote -c "CREATE DATABASE napotetest WITH TEMPLATE napotetest_template OWNER napotetest;"

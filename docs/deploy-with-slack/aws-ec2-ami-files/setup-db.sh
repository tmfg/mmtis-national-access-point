#!/bin/bash

set -e

psql -c "CREATE USER napotetest WITH CREATEDB;" -U postgres && \
psql -c "ALTER USER napotetest WITH SUPERUSER;" -U postgres && \
psql -c "CREATE USER napote;" -U postgres && \
psql -c "ALTER USER napote WITH SUPERUSER;" -U postgres && \
psql -c "CREATE USER ckan WITH CREATEDB;" -U postgres && \
psql -c "ALTER USER ckan WITH SUPERUSER;" -U postgres && \
psql -c "CREATE DATABASE napote OWNER napote;" -U postgres && \
psql -c "CREATE DATABASE napotetest OWNER napotetest;" -U postgres && \
psql -c "CREATE DATABASE temp OWNER napotetest;" -U postgres && \
psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;" -U postgres && \
psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;" -U postgres  && \
psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;" -U postgres && \
psql -c "CREATE EXTENSION postgis" -U postgres napotetest && \
psql -c "CREATE EXTENSION postgis_topology" -U postgres napotetest
#!/bin/bash

db_name="$1"

set -e

if [ -f finnish_municipalities.csv ]; then
    echo "Insert Finnish municipalities"
    psql -U napote "$db_name" -c "TRUNCATE finnish_municipalities;" || true
    psql -U napote "$db_name" -c "\COPY finnish_municipalities FROM finnish_municipalities.csv CSV HEADER;" || true
fi

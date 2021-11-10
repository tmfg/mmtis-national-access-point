#!/usr/bin/env bash

set -e
set -x
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
echo "Run migrations to napote database"

until mvn flyway:info &>/dev/null; do
    echo "Waiting that Flyway gets database connection..."
    sleep 0.5
done

echo "Connection established!"
mvn flyway:migrate
cd -
docker run -v $DIR:/database -v $DIR/../../fintraffic-napote-config/static-data:/static-data -it --network docker_napote --link napotedb11:postgres --rm postgres sh /database/devdb_testdata.sh

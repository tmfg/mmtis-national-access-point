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
docker run -v $DIR:/database -v -it --network docker_napote --link napotedb163:postgres --rm postgres sh /database/devdb_rewrite_db_template.sh

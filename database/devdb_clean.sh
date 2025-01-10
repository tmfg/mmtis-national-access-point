#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Cleaning devdb (migrations from scratch)"

docker run -v $DIR/../nap:/nap -v $DIR:/database -v $DIR/../../fintraffic-napote-config/static-data:/static-data -it --network docker_napote --link napotedb163:postgres --rm postgres sh /database/devdb_clean_docker.sh

bash $DIR/devdb_migrate.sh

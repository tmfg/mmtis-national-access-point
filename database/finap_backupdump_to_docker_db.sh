#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker run -v $DIR:/database -it \
       --network docker_napote \
       --link napotedb:postgres --rm postgres:10 \
       sh /database/finap_backup_docker.sh

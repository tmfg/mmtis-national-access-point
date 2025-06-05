#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE=${1:-solita/napotedb163}

if ! docker images | grep $IMAGE >> /dev/null; then
    echo "Image" $IMAGE "cannot be found. Trying to pull it again."
    if ! docker pull $IMAGE; then
        echo $IMAGE "is not in the docker hub. Building new one."
        docker build -t $IMAGE . ;
    fi
    echo ""
fi

docker run -p 5432:5432 --name napotedb163 -dit $IMAGE 1> /dev/null

echo "Starting Docker image" $IMAGE
echo ""
docker images | head -n1
docker images | grep $IMAGE

echo ""
echo "Waiting that PostgreSQL is running and it answers at port 5432"
while ! nc -z localhost 5432; do
    echo "Waiting that PostgreSQL is running and it answers at port 5432"
    sleep 0.5;
done;
echo "Connection established!"

## Wait for few seconds to make sure that PostgreSQL is ready to accept connections
sleep 2;
echo "Initializing database with ckan-initial-db.sql"
psql -h localhost -U napote -d napote -f ../nap/ckan-initial-db.sql

echo "Migrate database:"
bash $DIR/devdb_migrate.sh

echo ""
echo "Napote db version 16.3 is up and running! Image data:"
echo ""

docker images | head -n1
docker images | grep $IMAGE

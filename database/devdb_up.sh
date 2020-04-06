#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE=${1:-solita/napotedb11}

if ! docker images | grep $IMAGE >> /dev/null; then
    echo "Image" $IMAGE "cannot be found. Trying to pull it again."
    if ! docker pull $IMAGE; then
        echo $IMAGE "is not in the docker hub. Building new one."
        docker build -t $IMAGE . ;
    fi
    echo ""
fi

docker run -p 5432:5432 --name napotedb11 -dit $IMAGE 1> /dev/null

echo "Starting Docker image" $IMAGE
echo ""
docker images | head -n1
docker images | grep $IMAGE

echo ""
echo "Waiting that PostgreSQL is running and it answers at port 5432"
while ! nc -z localhost 5432; do
    sleep 0.5;
done;

bash $DIR/devdb_migrate.sh

echo ""
echo "Napote db is up and running! Image data:"
echo ""

docker images | head -n1
docker images | grep $IMAGE

#!/bin/sh

echo "Remove napotedb Docker container"
docker rm -f napotedb 1> /dev/null

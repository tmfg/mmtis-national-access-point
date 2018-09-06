#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

host="$1"
shift
cmd="$@"

until psql -h "$host" -U "napote" -c '\l'; do
  >&2 echo "Postgres is unavailable - sleeping..."
  sleep 2
done

>&2 echo "Postgres is up - executing command"
exec $cmd
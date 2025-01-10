#!/usr/bin/env bash

# This script is used to enable query logging in the database container. In localhost environment

docker exec napotedb163 bash -c 'echo "log_statement = all" >>  /etc/postgresql/16.3/main/postgresql.conf; kill -HUP `cat /run/postgresql/16.3-main.pid`; tail -f /var/log/postgresql/postgresql-16.3-main.log'

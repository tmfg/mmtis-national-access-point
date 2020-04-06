#!/usr/bin/env bash

docker exec napotedb11 bash -c 'echo "log_statement = all" >>  /etc/postgresql/9.6/main/postgresql.conf; kill -HUP `cat /run/postgresql/9.6-main.pid`; tail -f /var/log/postgresql/postgresql-9.6-main.log'

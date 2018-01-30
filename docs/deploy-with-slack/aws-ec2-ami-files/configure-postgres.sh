#!/bin/bash

# Configure PG to listen network

sed -i "s/#listen_addresses.*/listen_addresses = '*'/g" /var/lib/pgsql/9.6/data/postgresql.conf
sed -i "s/port = 5433/port = 5432/g" /var/lib/pgsql/9.6/data/postgresql.conf
echo "local all all trust" > /var/lib/pgsql/9.6/data/pg_hba.conf
echo "host    all             all             0.0.0.0/0            trust" >> /var/lib/pgsql/9.6/data/pg_hba.conf

# Configure fsync and full page writes off
sed -i "s/#fsync.*/fsync = off/g" /var/lib/pgsql/9.6/data/postgresql.conf
sed -i "s/#full_page_writes.*/full_page_writes = off/g" /var/lib/pgsql/9.6/data/postgresql.conf
FROM ubuntu:bionic

# Install dependencies
RUN apt-get update
RUN apt update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends --no-install-recommends tzdata postgresql-10 postgresql-10-postgis-2.4 postgresql-10-postgis-2.4-scripts postgresql-contrib-10 maven

# Configure pg to listen network
RUN sed -i "s/#listen_addresses.*/listen_addresses = '*'/g" /etc/postgresql/10/main/postgresql.conf
RUN sed -i "s/port = 5433/port = 5432/g" /etc/postgresql/10/main/postgresql.conf
RUN echo "local all all trust" > /etc/postgresql/10/main/pg_hba.conf
RUN echo "host    all             all             0.0.0.0/0            trust" >> /etc/postgresql/10/main/pg_hba.conf

# Configure fsync and full page writes off
RUN sed -i "s/#fsync.*/fsync = off/g" /etc/postgresql/10/main/postgresql.conf
RUN sed -i "s/#full_page_writes.*/full_page_writes = off/g" /etc/postgresql/10/main/postgresql.conf

# Create database
RUN service postgresql restart && \
    sleep 5 && \
    psql -c "CREATE USER napotetest WITH CREATEDB;" -U postgres && \
    psql -c "ALTER USER napotetest WITH SUPERUSER;" -U postgres && \
    psql -c "CREATE USER napote;" -U postgres && \
    psql -c "ALTER USER napote WITH SUPERUSER;" -U postgres && \
    psql -c "CREATE USER ckan WITH CREATEDB;" -U postgres && \
    psql -c "ALTER USER ckan WITH SUPERUSER;" -U postgres && \
    psql -c "CREATE DATABASE napote OWNER napote;" -U postgres && \
    psql -c "CREATE DATABASE temp OWNER napotetest;" -U postgres && \
    psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napotetest;" -U postgres && \
    psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO napote;" -U postgres  && \
    psql -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ckan;" -U postgres && \
    psql -c "CREATE EXTENSION postgis" -U postgres napote && \
    psql -c "CREATE EXTENSION postgis_topology" -U postgres napote


# Run migraations
COPY pom.xml /tmp
COPY src /tmp/src
#RUN service postgresql start && cd /tmp && sleep 20 && mvn flyway:migrate && service postgresql stop

# Clean up and minify image
RUN apt-get -y remove maven;
RUN apt-get -y autoremove;
RUN rm /tmp/pom.xml
RUN rm -R /tmp/src


# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]


## SetUp EntryPoint ##
# COPY ./entrypoint.sh /

# CHMOD
# RUN chmod +x /entrypoint.sh


# ENTRYPOINT ["./entrypoint.sh"]


EXPOSE 5432

USER postgres

CMD service postgresql start && tail -F /var/log/postgresql/postgresql-10-main.log
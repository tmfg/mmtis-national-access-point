# Contains database schema for NAPOTE project

Use Flyway migration script to setup and update database

# Development environment

Dockerfile contains setup configurations for local development environment

# Update Docker database to it's newest migration

When Docker image needs to be updated to latest migration use following commands:

> docker build -t solita/napotedb:latest .
> docker push solita/napotedb:latest


Inform others to take into use this latest image

> docker pull solita/napotedb:latest

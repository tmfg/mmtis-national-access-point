# Docker instances for local development of NAP

This folder contains Docker images required setting up the National Access Point
development environent locally.

The database is shared between CKAN and the OTE application and is in folder: `../../database`.

## Running the instances

The instances are pushed to dockerhub and can be run without building locally.

To start all the ckan nap containers, run:
`docker-compose up -d`

Note, that you might have to wait a while before CKAN is available at: http://localhost:5555,

Give the db service time to initialize the db cluster on first run:  
`docker-compose restart napote-ckan`

If you encounter weird errors with docker containers or the images are taking too much space, run the following:
`docker-compose down`  
`docker rmi -f docker_napote-ckan docker_napote-solr docker_napotedb docker_napote-redis`  
`docker rmi $(docker images -f dangling=true -q)`  
`docker-compose build`  
`docker-compose up -d`  

If you want to take a look inside a docker container run e.g.
`docker exec -it napote-ckan /bin/bash`

## ENV variables

Some env variables, such as ckan site url, can be easily configured through .env file.

# Setup environment
Open console and go to `mmtis-national-access-point/nap/docker`

First time build everything
`docker-compose build`

Start all dockers
`docker-compose up`

If napotedb gives error remove 'old' version
`docker rm napotedb`

If db migrate is needed stop napotedb (not sure if this is really needed?)
`docker-compose rm -s napotedb`

Restart napotedb
`docker-compose up`

Migrate latest db changes in mmtis-national-access-point/database
`sh devdb_migrate.sh`

Go to mmtis-national-access-point/ote
Start leiningen
`lein run`
In another console start figwheel
`lein figwheel`

In browser go to ckan http://localhost:3000
Sign in using `admin/admin`
After signing in you can go to OTE http://localhost:3000/index.html
   

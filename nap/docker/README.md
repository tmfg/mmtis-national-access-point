# Docker instances for local development of NAP

This folder contains Docker images required setting up the National Access Point
development environent locally.

The database is shared between CKAN and the OTE application and is in folder: `../../database`.

## Running the instances

The instances are pushed to dockerhub and can be run without building locally.

To start all the nap containers, run:
`docker-compose up -d`

If you encounter weird errors with docker containers or the images are taking too much space, run the following:
`docker-compose down`  
`docker rmi -f docker_napotedb`  
`docker rmi $(docker images -f dangling=true -q)`  
`docker-compose build`  
`docker-compose up -d`  

If you have problems with ckan related tables. Run initial db scripts.
Go to /nap folder and run
> psql -h localhost -p 5432 -U napote -f ckan-initial-db.sql napote
Then you can run migrate scripts from /database folder
> sh devdb_migrate.sh 
And all are set.

If you want to take a look inside a docker container run e.g.
`docker exec -it napote-db /bin/bash`

## ENV variables

Some env variables, can be easily configured through .env file.

# Setup environment
Open console and go to `mmtis-national-access-point/nap/docker`

First time build everything  
`docker-compose build`

Start all dockers  
`docker-compose up`

If napotedb11 gives error remove 'old' version  
`docker rm napotedb11`

If db migrate is needed stop napotedb11  
`docker-compose rm -s napotedb11`

Restart napotedb11  
`docker-compose up`

Migrate latest db changes in `mmtis-national-access-point/database`  
`sh devdb_migrate.sh`

Start backend to REPL in IDE

Go to `mmtis-national-access-point/ote`
Start leiningen  
`lein run`
In another console start figwheel  
`lein figwheel`

In browser go to ckan http://localhost:3000
Sign in using `admin/admin`
After signing in you can go to OTE http://localhost:3000/index.html
   

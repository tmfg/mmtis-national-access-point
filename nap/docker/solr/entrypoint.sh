#!/bin/bash

solr_url=http://localhost:8983

/opt/solr/bin/solr start -f

echo "Solr is up and running successfully!"

echo "Creating a core for ckan..."

#/opt/solr/bin/solr create -p 8983 -c ckan

#!/bin/sh

# Download MobilityDCAT-AP SHACL shapes if not present
if [ ! -f shapes.ttl ]; then
    echo "shapes.ttl not found, downloading MobilityDCAT-AP SHACL shapes..."
    wget -q -O shapes.ttl https://raw.githubusercontent.com/mobilityDCAT-AP/mobilityDCAT-AP/refs/heads/gh-pages/releases/1.1.0/validationFiles/mobilitydcat-ap_shacl_shapes.ttl
    if [ $? -eq 0 ]; then
        echo "Downloaded shapes.ttl successfully"
    else
        echo "Failed to download shapes.ttl"
        exit 1
    fi
fi

# Download RDF data if not present
if [ ! -f data.ttl ]; then
    echo "data.ttl not found, downloading from http://localhost:3000/rdf..."
    curl -s -o data.ttl http://localhost:3000/rdf
    if [ $? -eq 0 ]; then
        echo "Downloaded data.ttl successfully"
    else
        echo "Failed to download data.ttl"
        exit 1
    fi
fi

docker run -v `pwd`:/data solita/shacl-validator validate --shapes /data/shapes.ttl --data /data/data.ttl

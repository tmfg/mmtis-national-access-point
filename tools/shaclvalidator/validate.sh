#!/bin/sh

# Download MobilityDCAT-AP SHACL shapes if not present
if [ ! -f shapes.ttl ]; then
    echo "shapes.ttl not found, downloading MobilityDCAT-AP SHACL shapes..."
    curl -s -o shapes.ttl https://raw.githubusercontent.com/mobilityDCAT-AP/mobilityDCAT-AP/refs/heads/gh-pages/releases/1.1.0/validationFiles/mobilitydcat-ap_shacl_shapes.ttl
    if [ $? -eq 0 ]; then
        echo "Downloaded shapes.ttl successfully"
        # Fix GitHub issue #131: Update access-right URL to licence URL
        # https://github.com/mobilityDCAT-AP/mobilityDCAT-AP/issues/131
        echo "Applying fix for GitHub issue #131..."
        sed -i 's|http://publications.europa.eu/resource/authority/access-right|http://publications.europa.eu/resource/authority/licence|g' shapes.ttl
        
        # Add shacl prefix at the beginning of the file
        echo "Applying fix for DCAT-AP issue #403..."
        echo "https://github.com/SEMICeu/DCAT-AP/issues/403"
        sed -i '1i@prefix shacl:   <http://www.w3.org/ns/shacl#> .' shapes.ttl
        # Remove all owl:imports lines
        echo "Removing owl:imports..."
        sed -i '/owl:imports/d' shapes.ttl        

        echo "Fixed shapes.ttl"
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

# Container name for reuse
CONTAINER_NAME="shacl-validator"

# Check if container exists and is running
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    # Container exists, reuse it
    docker start -a $CONTAINER_NAME
else
    # Create new container with name for future reuse
    docker run --name $CONTAINER_NAME -v `pwd`:/data solita/shacl-validator validate --shapes /data/shapes.ttl --data /data/data.ttl
fi

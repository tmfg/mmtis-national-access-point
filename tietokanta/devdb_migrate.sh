#!/usr/bin/env bash

set -e
set -x
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
echo "Ajetaan migraatiot napote-kantaan"

until mvn flyway:info &>/dev/null; do
    echo "Odotetaan että flyway saa yhteyden kantaan..."
    sleep 0.5
done

echo "Yhteys saatu!"
mvn flyway:migrate
cd -

#ei ole vielä mitään testidataa
#docker run -v $DIR:/tietokanta -it --link napotedb:postgres --rm postgres sh /tietokanta/devdb_testidata.sh

#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export AWS_PROFILE=napote-dev

LATEST_DUMP=`aws s3 ls finap-backup | grep staging | tail -n1 | awk '{ print $4; }'`

echo "Copying latest finap dump: $LATEST_DUMP"

aws s3 cp s3://finap-backup/$LATEST_DUMP $LATEST_DUMP

mv $LATEST_DUMP finap.dump

docker run -v $DIR:/database -it \
       --network docker_napote \
       --link napotedb11:postgres --rm postgres:9.6.8 \
       sh /database/finap_backup_docker.sh

rm finap.dump

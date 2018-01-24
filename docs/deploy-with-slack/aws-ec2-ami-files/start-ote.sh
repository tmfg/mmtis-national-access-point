#!/bin/bash

branch_ref="$1"
response_url="$2"
public_url="`wget -qO- http://instance-data/latest/meta-data/public-hostname`"
debug_help="ssh -i "napote-test-deploy.pem" centos@${public_url}"

# This is required to allow nginx to connect to localhost:3000
sudo setsebool httpd_can_network_connect on -P

echo "Starting OTE..."
java -jar "ote-${branch_ref}.jar" &


counter=10
until [ "`curl --silent --show-error --connect-timeout 1 -I http://127.0.0.1 | grep '200 OK'`" != "" ];
do
  if [ "$counter" -eq 0 ]; then
    echo "OTE start failed!"
    curl -X POST -H 'Content-type: application/json' \
    --data '{"text":"OTE start for branch: '"$branch_ref"' failed!\nDebug running instance: '"$debug_help"'", "response_type":"ephemeral"}' \
    $response_url

    break
  fi

  echo "Sleeping for 10 secs..."
  sleep 10

  let "counter-=1"
done

if [ "$counter" -gt 0 ]; then
  echo "OTE branch ${branch_ref} started successfully: ${public_url}"

  curl -X POST -H 'Content-type: application/json' \
  --data '{"text":"OTE branch '"$branch_ref"' started successfully: '"$public_url"'", "response_type":"ephemeral"}' \
  $response_url
fi

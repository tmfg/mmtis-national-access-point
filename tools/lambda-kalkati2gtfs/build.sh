#!/usr/bin/env bash

set -e

[ -e deploy.zip ] && rm deploy.zip
mkdir -p deploy

pip install -r requirements.txt -t ./deploy

cp *.py deploy
cd deploy && zip -9 -r ../deploy.zip * && cd ..

rm -rf deploy
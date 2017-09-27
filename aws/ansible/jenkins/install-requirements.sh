#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR

mkdir -p roles

ansible-galaxy install -p roles -r requirements.yml

cd -

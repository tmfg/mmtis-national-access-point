#!/usr/bin/env bash

docker build -t solita/napote-circleci .
docker tag solita/napote-circleci solita/napote-circleci:latest
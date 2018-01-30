#!/bin/bash

sudo yum update && \
sudo yum -y --setopt=tsflags=nodocs install java-1.8.0-openjdk && \
sudo yum install epel-release && \
sudo rpm -Uvh http://yum.postgresql.org/9.6/redhat/rhel-7-x86_64/pgdg-centos96-9.6-3.noarch.rpm && \
sudo yum update && \
sudo yum -y --setopt=tsflags=nodocs install postgresql96 postgresql96-selsrver postgresql96-libs postgresql96-contrib postgis2_96 postgis2_96-client && \
sudo yum -y --setopt=tsflags=nodocs nginx && \
yum clean all
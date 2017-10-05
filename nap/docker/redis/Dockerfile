# centos 7 image for Redis database
# Is used from ckan image.

FROM centos:7

RUN yum -y --setopt=tsflags=nodocs update && \
    yum -y --setopt=tsflags=nodocs install epel-release && \
    yum -y --setopt=tsflags=nodocs install redis && \
    yum clean all

# Explose redis default port
EXPOSE 6379

#Start redis server
CMD redis-server --protected-mode no

FROM centos:7

RUN yum -y --setopt=tsflags=nodocs update && \
    yum -y --setopt=tsflags=nodocs install epel-release && \
    yum -y --setopt=tsflags=nodocs install nginx && \
    yum clean all

COPY ./proxy.conf /etc/nginx/nginx.conf


EXPOSE 8080

# Start nginx and tail its log
CMD nginx && echo "Nginx started" && tail -F /var/log/nginx/access.log

# docker build -t solita/napote-solr:latest .
# docker run -it -p8983:8983 solita/napote-solr

FROM centos:7

# Enviroment
ENV SOLR_CORE ckan

ENV SOLR_VERSION 6.2.0
ENV SOLR_HOME /var/solr
ENV SOLR_USER solr
ENV SOLR_UID 8983
ENV SOLR_GROUP solr
ENV SOLR_GID 8983
ENV PATH /opt/solr/bin:/opt/docker-solr/scripts:$PATH

# User
USER root

RUN yum -y update && yum -y upgrade && yum -y install \
    java-1.8.0-openjdk.x86_64 \
    git \
    curl \
    lsof \
    wget \
  && yum clean all

RUN groupadd -r -g $SOLR_GID $SOLR_GROUP && \
    useradd -r -m -u $SOLR_UID -g $SOLR_GROUP $SOLR_USER

# Clone ckan to access required ckan shemas.
RUN git clone -b 'ckan-2.7.0' --single-branch --depth 1 "https://github.com/ckan/ckan.git" /tmp/ckan

# Download and install SORL
RUN curl -o /tmp/solr-$SOLR_VERSION.tgz http://archive.apache.org/dist/lucene/solr/$SOLR_VERSION/solr-$SOLR_VERSION.tgz
RUN cd /tmp && tar zxf solr-$SOLR_VERSION.tgz

RUN mkdir -p /opt/solr/
RUN mkdir -p /var/solr/
RUN cp -R /tmp/solr-$SOLR_VERSION/* /opt/solr/

# Create Core directories
RUN mkdir -p /opt/solr/server/solr/$SOLR_CORE/conf
RUN mkdir -p /opt/solr/server/solr/$SOLR_CORE/data

## Copy config files
COPY ./solrconfig.xml /
COPY ./schema.xml /

RUN cp /solrconfig.xml \
/schema.xml \
/tmp/solr-$SOLR_VERSION/server/solr/configsets/basic_configs/conf/currency.xml \
/tmp/solr-$SOLR_VERSION/server/solr/configsets/basic_configs/conf/synonyms.txt \
/tmp/solr-$SOLR_VERSION/server/solr/configsets/basic_configs/conf/stopwords.txt \
/tmp/solr-$SOLR_VERSION/server/solr/configsets/basic_configs/conf/protwords.txt \
/tmp/solr-$SOLR_VERSION/server/solr/configsets/data_driven_schema_configs/conf/elevate.xml \
/opt/solr/server/solr/$SOLR_CORE/conf/


# Create Core.properties
RUN echo name=$SOLR_CORE > /opt/solr/server/solr/$SOLR_CORE/core.properties

# Setup solr home directory
RUN cp -R /opt/solr/server/solr/* $SOLR_HOME

# Giving ownership to Solr
RUN chown -R $SOLR_USER:$SOLR_GROUP /opt/solr/
RUN chown -R $SOLR_USER:$SOLR_GROUP /var/solr/


USER solr

EXPOSE 8983

VOLUME ["/opt/solr/"]
VOLUME ["/var/solr/"]

CMD /opt/solr/bin/solr start -f && tail -F /var/solr/logs/solr.log
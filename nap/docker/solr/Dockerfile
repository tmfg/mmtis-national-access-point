# docker build -t solita/napote-solr:latest .
# docker run -it -p8983:8983 solita/napote-solr

FROM ubuntu:artful

RUN apt-get -q -y update && apt-get -q -y install openjdk-8-jdk git curl lsof

RUN curl -o /tmp/solr-7.0.0.tgz http://apache.org/dist/lucene/solr/7.0.0/solr-7.0.0.tgz
RUN cd /tmp && tar zxf solr-7.0.0.tgz

RUN bash /tmp/solr-7.0.0/bin/install_solr_service.sh /tmp/solr-7.0.0.tgz

RUN git clone "https://github.com/ckan/ckan.git" && cd ckan && git checkout tags/ckan-2.7.0 && cd ..

RUN cp ckan/ckan/config/solr/schema.xml /opt/solr/server/solr/configsets/_default/conf/managed-schema

EXPOSE 8983

CMD service solr start && tail -F /var/solr/logs/solr.log
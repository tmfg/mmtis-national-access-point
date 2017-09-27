# docker build . -t ckan && docker run -d -p 80:5000 --link db:db --link redis:redis --link solr:solr ckan

FROM centos:7
MAINTAINER Open Knowledge

ENV CKAN_HOME /usr/lib/ckan/default
ENV CKAN_CONFIG /etc/ckan/default
ENV CKAN_STORAGE_PATH /var/lib/ckan
ENV CKAN_SITE_URL http://localhost:5000

## Install required packages ##
RUN yum -y update && yum -y upgrade && yum -y install \
		python-devel \
		postgresql-devel \
        python-virtualenv \
        libpq-dev \
        git-core \
        build-essential \
        libssl-dev \
        libffi-dev \
        gcc \
	&& yum clean all

## SetUp Virtual Environment CKAN ##
RUN python --version
RUN mkdir -p $CKAN_HOME $CKAN_CONFIG $CKAN_STORAGE_PATH
RUN virtualenv $CKAN_HOME
RUN ln -s $CKAN_HOME/bin/pip /usr/local/bin/ckan-pip
RUN ln -s $CKAN_HOME/bin/paster /usr/local/bin/ckan-paster


## Install recommended version of setup tools ##
# NOTE: setuptools version defined in ckan/dev-requirements.txt
RUN ckan-pip install setuptools==20.4


## Install latest stable CKAN in $CKAN_HOME/src/ckan directory ##
RUN ckan-pip install -e 'git+https://github.com/ckan/ckan.git@ckan-2.7.0#egg=ckan'
RUN ln -s $CKAN_HOME/src/ckan/ckan/config/who.ini $CKAN_CONFIG/who.ini

## Install pytz. ##
# NOTE: It seems that some of the requirements have pytz dependency, so we'll have to install pytz before installing
#       requirements.txt

# NOTE: pytz version defined in ckan/requirements.txt
RUN ckan-pip install pytz==2016.7


## Install CKAN dependencies ##
RUN ckan-pip install -r $CKAN_HOME/src/ckan/requirements.txt


## SetUp EntryPoint ##
COPY ./ckan-entrypoint.sh /
RUN chmod +x /ckan-entrypoint.sh
ENTRYPOINT ["/ckan-entrypoint.sh"]


## Volumes ##
VOLUME ["/etc/ckan/default"] # Config
VOLUME ["/var/lib/ckan"] # Storage
EXPOSE 5000
CMD ["ckan-paster","serve","/etc/ckan/default/ckan.ini"]

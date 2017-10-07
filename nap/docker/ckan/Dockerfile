# BUILD: docker build . -t solita/napote-ckan
# docker build . -t ckan && docker run -d -p 80:5000 --link db:db --link redis:redis --link solr:solr ckan

FROM centos:7

ENV CKAN_HOME /usr/lib/ckan
ENV CKAN_VIRTUAL_ENV $CKAN_HOME/virtualenv
ENV CKAN_CONFIG /etc/ckan
ENV CKAN_STORAGE_PATH /var/lib/ckan
ENV CKAN_CUSTOM_PLUGINS_PATH /ckan-plugins

# These build-time vars are specified by docker-compose.yml
ARG CKAN_PORT=5555
ARG CKAN_SITE_URL=http://localhost:8080

## Install required packages ##
# NOTE: lipgq-dev (debian) = postgresql-libs (centos)
# NOTE: build-essential (debian) = gcc gcc-c++ make openssl-devel (centos)

# Perform updates and install EPEL for inotify-tools
RUN yum -y update && yum -y upgrade && yum -y install epel-release

# Install packages
RUN yum -y update && yum --enablerepo=epel -y install \
		python-devel \
		postgresql-devel \
		postgresql-libs \
        python-virtualenv \
        git-core \
        gcc \
        gcc-c++ \
        make \
        openssl-devel \
        libffi-devel \
        gcc \
        inotify-tools \
	&& yum clean all

# Create ckan user
RUN useradd -r -u 900 -m -c "ckan account" -d $CKAN_HOME -s /bin/false ckan

## SetUp Virtual Environment for CKAN ##
RUN mkdir -p $CKAN_VIRTUAL_ENV $CKAN_CONFIG $CKAN_STORAGE_PATH $CKAN_CUSTOM_PLUGINS_PATH

RUN virtualenv $CKAN_VIRTUAL_ENV
RUN ln -s $CKAN_VIRTUAL_ENV/bin/pip /usr/local/bin/ckan-pip
RUN ln -s $CKAN_VIRTUAL_ENV/bin/paster /usr/local/bin/ckan-paster


## Install recommended version of setup tools ##
# NOTE: setuptools version defined in ckan/dev-requirements.txt
RUN ckan-pip install setuptools==20.4

## Install latest stable CKAN in $CKAN_HOME/src/ckan directory ##
RUN ckan-pip install -e 'git+https://github.com/ckan/ckan.git@ckan-2.7.0#egg=ckan'

# Install ckan plugins
ADD ./ckan-plugins $CKAN_HOME/src/
RUN ckan-pip install -e $CKAN_HOME/src/ckanext-napote_theme

# Copy config files
COPY ./who.ini /
RUN ln -s /who.ini $CKAN_CONFIG/who.ini

## Install pytz. ##
# NOTE: It seems that some of the requirements have pytz dependency, so we'll have to install pytz before installing
#       requirements.txt

# NOTE: pytz version defined in ckan/requirements.txt
RUN ckan-pip install pytz==2016.7

## Install CKAN dependencies ##
RUN ckan-pip install --upgrade -r $CKAN_VIRTUAL_ENV/src/ckan/requirements.txt


## SetUp EntryPoint ##
COPY ./ckan-entrypoint.sh /


# CHMOD
RUN chmod +x /ckan-entrypoint.sh
RUN chown -R ckan:ckan $CKAN_HOME $CKAN_VIRTUAL_ENV $CKAN_CONFIG $CKAN_STORAGE_PATH


ENTRYPOINT ["/ckan-entrypoint.sh"]


## Volumes ##

# Config
VOLUME ["/etc/ckan"]

# Home
VOLUME ["/usr/lib/ckan"]

# Storage
VOLUME ["/var/lib/ckan"]

# Volume for custom ckan plugins
VOLUME ["/ckan-plugins"]

USER ckan
EXPOSE $CKAN_PORT


CMD ["ckan-paster","serve","--reload","/etc/ckan/ckan.ini"]

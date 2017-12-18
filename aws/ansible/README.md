# Ansible scripts for NAPOTE cloud environment


## CKAN

ckan.yml installs CKAN 2.7 version which refers to the napote_theme plugin (which is installed by Jenkins job).

After first Jenkins deploy, the following commands must be run:

> . /usr/lib/ckan/default/bin/activate
> cd /usr/lib/ckan/default/src/ckanext/ckanext-napote_theme
> python setup.py develop
> cd /usr/lib/ckan/default/src/ckan/ckan/public/base/i18n
> chown napote .
> chgrp napote .
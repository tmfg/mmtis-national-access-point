# Ansible scripts for NAPOTE cloud environment

First ensure that Redis is running in AWS.
Then run
- ansible-playbook --vault-pass <path/to/pass.txt> -i enviroments/<production/staging>/inventory solr.yml

## OTE
ote.yml should be deployed via jenkins. Do not use ansible-playbook command from your own machine.

## CKAN

Run ansible-playbook to deploy ckan.
It should be run from your machine.
- ansible-playbook --vault-pass <path/to/pass.txt> -i enviroments/<production/staging>/inventory ckan.yml
  
ckan.yml installs CKAN 2.7 version which refers to the napote_theme plugin (which is installed by Jenkins job).

After first Jenkins deploy, the following commands must be run in ckan1 and ckan2 machines:

> . /usr/lib/ckan/default/bin/activate
> cd /usr/lib/ckan/default/src/ckanext/ckanext-napote_theme
> python setup.py develop
> cd /usr/lib/ckan/default/src/ckan/ckan/public/base/i18n
> chown napote .
> chgrp napote .
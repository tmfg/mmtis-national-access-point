#!/bin/sh

ansible-playbook -i inventory jenkins.yml --tags solita_jenkins_jobs

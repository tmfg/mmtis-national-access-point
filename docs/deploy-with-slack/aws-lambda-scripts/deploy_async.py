# coding=utf-8
import os
import boto3
import urllib2
import time
import ssl
import json
import string
import re


## Cloud-init config. This will be sent through boto3 as UserData to our EC2 Centos instance
initscript = '''#cloud-config
runcmd:
  - cd /home/centos/
  - echo "Start PostgreSQL 9.6";
  - sudo systemctl start postgresql-9.6;
  - sudo systemctl start nginx
  - sudo wget https://s3.eu-central-1.amazonaws.com/napote-circleci/build-artifacts/ote-$BRANCH-pgdump.gz;
  - sudo wget https://s3.eu-central-1.amazonaws.com/napote-circleci/build-artifacts/ote-$BRANCH.jar;
  - sudo wget https://s3.eu-central-1.amazonaws.com/napote-circleci/build-artifacts/ote-$BRANCH-config.edn;
  - mv ote-$BRANCH-config.edn config.edn
  - sed -r -e "s/(:dev-mode?)([^}]*)/\\1 false/g" -e "/.*:log.*/d" -e "/.*:ga.*/d" config.edn > config.edn.tmp && mv config.edn.tmp config.edn
  - ./setup-db.sh
  - sudo zcat "ote-$BRANCH-pgdump.gz" | sudo -u postgres psql napote
  - ./add-gisdata-into-db.sh napote
  - ./start-ote.sh $BRANCH $RESPONSE_URL
'''


def send_slack_msg(msg, response_url):
    try:
        payload = '{"text": "' + msg + '", "response_type": "ephemeral"}'
        urllib2.urlopen(response_url, payload)
    except Exception, e:
        print 'error sending Slack response: ' + str(e)


def find_pr(branch_name):
    req = urllib2.Request(
        'https://api.github.com/search/issues?q=repo:finnishtransportagency/mmtis-national-access-point+type:pr+head:' + branch_name)

    try:
        res = urllib2.urlopen(req)

        if res.getcode() == 200:
            data = json.load(res)

            return data["items"]
    except:
        pass

    return []


def get_branch_ref(branch):
    return branch["head"]["ref"]


def get_branch_data(pull_request):
    req = urllib2.Request(pull_request["pull_request"]["url"])

    try:
        res = urllib2.urlopen(req)

        if res.getcode() == 200:
            data = json.load(res)

            return data
    except:
        pass

    return False


def check_build_artifacts(branch_ref):
    req = urllib2.Request('https://<your_s3_bucket_path>/ote-' + branch_ref + '.jar')
    req.get_method = lambda: 'HEAD'

    try:
        res = urllib2.urlopen(req)
        if res.getcode() == 200:
            return True
    except:
        pass
    return False


def deploy(branch_ref, response_url):
    script = initscript.replace('$BRANCH', branch_ref)
    script = script.replace('$RESPONSE_URL', response_url)

    ec2 = boto3.client('ec2', region_name='eu-central-1')

    try:
        res = ec2.run_instances(ImageId='<your-ami-image-id>',
                                InstanceType='t2.medium',
                                UserData=script,
                                KeyName='<your-ec2-keypair-name>',
                                MinCount=1,
                                MaxCount=1)
    except Exception, e:
        msg = e.response['Error']['Message'] or str(e)

        print msg

        raise Exception(msg)

    id = res['Instances'][0]['InstanceId']
    host = None

    while host is None:
        time.sleep(2)
        instances = ec2.describe_instances(InstanceIds=[id])

        for r in instances['Reservations']:
            for i in r['Instances']:
                dns = i['PublicDnsName']
                if dns != '':
                    host = dns
    return host


def start_instance(branch_ref, response_url):
    host = deploy(branch_ref, response_url)
    url = 'http://' + host + '/'

    return url

#### Lambda handler ####
def lambda_handler(event, context):
    query = event['branch']

    # Find branch by ticket id or name. If ticked id is used, remove hash symbol
    pull_reqs = find_pr(re.sub(r'^#', '', query))

    pr_titles = [x["title"] for x in pull_reqs]

    if len(pull_reqs) == 1:
        branch = get_branch_data(pull_reqs[0])
        branch_ref = get_branch_ref(branch)

        if check_build_artifacts(branch_ref):
            send_slack_msg('Starting an EC2 instance for branch: ' + branch_ref + '... This might take a while.',
                           event['response_url'])

            try:
                # Note: Instance will send a Slack message when the app server is up and running
                url = start_instance(branch_ref, event['response_url'])
                txt = 'Started instance for branch: ' + branch_ref + ': ' + url + '\nInitializing... Please, wait.'
            except Exception, e:
                txt = 'Failed to start instance: ' + str(e)
        else:
            txt = 'No CircleCI build data found from S3 for branch: ' + branch_ref + '. Check that build has been successful!'

    elif len(pull_reqs) > 1:
        txt = 'Found multiple PRs from repo, pick one:\n' + string.join(pr_titles, '\n')

    else:
        txt = 'No PRs found with: ' + query

    send_slack_msg(txt, event['response_url'])

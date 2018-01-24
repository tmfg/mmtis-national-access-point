# coding=utf-8
import os
import boto3

"""
Required Lambda Environment Variables:
- slacktoken: Your private Slack App Verification Token
- allowed_users: List of Slack user_ids id1,id2,...
"""


def deploy(branch, response_url):
    lam = boto3.client('lambda', region_name='eu-central-1')
    lam.invoke(FunctionName='deploy_async',
               InvocationType='Event',
               Payload=b'{"branch": "' + branch + '", "response_url": "' + response_url + '"}')


def terminate_instances(response_url):
    lam = boto3.client('lambda', region_name='eu-central-1')
    lam.invoke(FunctionName='terminate_instances',
               InvocationType='Event')


def lambda_handler(event, context):
    token = os.environ['slacktoken']
    # The user_name field is being phased out from Slack. Always identify users by the combination of their user_id and team_id.
    user_id = event.get('user_id', None)
    allowed = os.environ['allowed_users'].split(',')

    if not user_id in allowed:
        return {'text': 'You are not on the allowed user list.', 'response_type': 'ephemeral'}
    else:
        try:
            if event.get('token') == token:
                if event['command'] == '/napotedeploy':
                    deploy(event.get('text', ''), event['response_url'])

                    return {'text': 'Searching branch with query: ' + event.get('text', '') + '. Please wait...',
                            'response_type': 'ephemeral'}
                elif event['command'] == '/napoteterminate':
                    terminate_instances(event['response_url'])

                    return {'text': 'Terminating running EC2 instances. Please wait...', 'response_type': 'ephemeral'}
                else:
                    return {'text': 'Error: Unknown command', 'response_type': 'ephemeral'}
        except Exception, e:
            return "error: " + str(e)

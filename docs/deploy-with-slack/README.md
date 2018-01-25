## Deploy branches with Slack slash commands [WIP]

For warm up, read the related blog post here: http://dev.solita.fi/2017/04/12/easy-test-deployments.html

We are basing our implementation on this blog post, using a slightly different environment and adding some enhancements not mentioned there.
This guide will contain all the steps, code and security policy settings required to build a similar level of automation as introduced in the related blog post.

## Requirements
The whole architecture of this setup consists of lot of little things that need to be setup properly and security
should be always in the back of your mind.

The list of requirements may be baffling, but don't worry, you don't have to have very deep understanding of any of these areas.
Amazon also has a very comprehensive documentation of every service they provide.

1. Preferably a separate AWS environment or user to not mess up your main AWS environment.
1. CircleCI, Jenkins or other automatic build environment.
1. Some knowledge of AWS S3, AWS Lambda, AWS EC2, AWS AMIs, AWS API Gateway, AWS Cloud Watch, 
managing AWS user permissions with security groups, roles and custom policies, Cloud Init basics.
1. Python 2.7 basics.
1. GitHub API basics.



## Setup automatic builds per branch (for CircleCI) (TODO)
Refer to: [.circleci](../../.circleci)

1. **Create an S3 bucket for your build artifacts**  
1. **Create a circle-ci aws user and config user security**  
1. **Config a build and S3 deploy task**  

## Create a custom Slack App
https://api.slack.com/apps

1. Create New App
1. Name app and define workspace
1. Active Incoming Webhooks
1. Add Slash Commands
    1. /napotedeploy
    1. /napoteterminate  
    You can leave "Request Url" blank for now. We'll come back after our API Gateway has been configured.
1. Copy Verification Token from Basic Information page. **Warning: Do not publish this key anywhere!**

Verification token is needed to verify that messages received by our Lambda functions are coming from our Slack App.  


## Setup your AWS environment

### Lambda

We are using AWS Lambda for handling our Slack Slash command queries and triggering actions on our EC2 instances.
Go to your Lambda Dashboard. We are going to create three Lambda functions that will handle queries from Slack, trigger
deployment and terminate running EC2 instances periodically.


#### Lambda Script 1: Slack slash command handler
Code: [slack_deployhook](aws-lambda-scripts/slack_deployhook.py)

First, go to the Functions view and click "Create function" and Author from scratch.
Name your function and select Python 2.7 Runtime.
In the Role section, click "Create a custom role". We will use this same role for each of our Lambda function for convenience.

**Setup Lambda user role and security**
Now you should be in the AWS role creation page.
For "IAM Role", select "Create a new IAM Role". Lets name the role, for example "deploy_branch""
Now, open View Policy Document and click Edit. Copy-paste the following policy snippet in the editor box.

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
          "Effect": "Allow",
          "Action": [
            "logs:CreateLogGroup",
            "logs:CreateLogStream",
            "logs:PutLogEvents"
          ],
          "Resource": "arn:aws:logs:*:*:*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "lambda:InvokeFunction"
            ],
            "Resource": [
                "*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "ec2:Start*",
                "ec2:Stop*",
                "ec2:RunInstances",
                "ec2:DescribeInstances",
                "ec2:TerminateInstances"
            ],
            "Resource": "*"
        }
    ]
}
```

The policy settings allow our Lambda functions to log events, invoke other Lambda functions, and control our EC2
instances. These policies are active for all resources. If you like, you can limit the affected resources by tuning
the Resource-option.

Finally, click "Allow". 

We can reuse this role with the other Lambda functions we create later.

In the create function page, you can choose Existing role we just created.
Click, "Create function".


**Let's add some code**

Copy the code from file: [slack_deployhook](aws-lambda-scripts/slack_deployhook.py), into the Function code editor and
click "Save".

Notice, that in the code we need to define slacktoken and allowed_users environment variables. 

```python
def lambda_handler(event, context):
    token = os.environ['slacktoken']
    
    ...
    
    allowed = os.environ['allowed_users'].split(',')
    
    ...
```
You can add them in the
"Environment Variables" section. 
Use the Slack "Verification Token" value for slacktoken env variable.
For the allowed_users variable, get your slack user id and paste it there.


We trigger our deploy Lambda function using boto3 client:

```python
...

def deploy(branch, response_url):
    lam = boto3.client('lambda', region_name='eu-central-1')
    lam.invoke(FunctionName='deploy_async',
               InvocationType='Event',
               Payload=b'{"branch": "' + branch + '", "response_url": "' + response_url + '"}')
               
    ...
```

**Settings**  
You might want to increase the function timeout value to one minute or so.


#### Lambda Script 2: Async deploy
Code: [deploy_async](aws-lambda-scripts/deploy_async.py)

Create a new function as described above. You can now reuse the previously created role in the "Existing role"-selection.

**Let's add some code**  
Copy the code from file: [slack_deployhook](aws-lambda-scripts/slack_deployhook.py), into the Function code editor and
click "Save".

**A word about Cloud Init**
Our EC2 instance supports [cloud-init](https://cloud-init.io/). The cloud-init configuration script is in yml-format.
We can pass the configuration as "UserData" string using the boto3-client to our EC2 instance. Cloud-init allows
us to control our EC2 instance as one would control e.g. CircleCI build process. This level of control also facilitates
the update process of the instance; we do not have to create a new AMI version each time we want to add some small update
to the deployment steps.

**A brief introduction to GitHub API**

We are using the open GitHub REST API for searching branches related to pull requests in our GitHub repository.
This way, slack users do not have to remember the exact branch name to trigger the deployment process. Partial name or
ticket ID is enough. If no unique result is found, the slack user receives a list of matching branches. Unique branch
query is required for the deployment to proceed.

Currently, we are utilizing the anonymous version of the GitHub API. One can send 60 query per hour anonymously.
This is more than enough for our development team. In case you need to increase your query limit, you can register for 
an API key, and send up to 5000 queries per hour.

**Settings**  
Add a longer timeout for this function, about 5 minutes would be good.
It might take some time for EC2 to boot up our instance (1-10 min), and we do not want to allow our Lambda function to
timeout and restart.

#### Lambda Script 3: Periodic EC2 instance terminator
Code: [terminate_instances](aws-lambda-scripts/terminate_instances.py)

Create a new function as described above. You can now reuse the previously created role in the "Existing role"-selection.

**Let's add some code**  
Copy the code from file: [terminate_instances](aws-lambda-scripts/terminate_instances.py), into the Function code editor and
click "Save".

As you can see, this is our least complex function. We simply get the IDs of our EC2 instances and terminate any running
or stopped instance. Ideally, we would like this function to run every evening, so we do not have to worry about shutting down
the instances after use. You can also create another Slack Slash command, as is done in the slack_deployhook function, that
terminates all the instances by command.

**CloudWatch cron trigger*

We want to terminate all the instances at 19:00 GMT+2 every day after work hours. This is very simple process.  
In the function editor, at the "Add triggers"-menu, click "CloudWatch Events". Then, select "Create a new rule" and name it.  
For "Rule type", select Schedule expression and add ```cron(0 17 * * ? *)```.
This defines an event that triggers each day at 17:00 UTC.  
Finally, click "Add".


### API Gateway (TODO)

We will create a custom API using API Gateway. This API will be our endpoint for Slack to send queries. It will also
trigger our slack_deployhook Lambda function.

1. **Create API**
1. **Add a new API resource**  
1. **Add POST - Integration Request Body Mapping Template**

Template for converting Slack request body from x-www-form-urlencoded to json:
```text
{
  "body" : "$input.body"
}
```

Our Slack lambda function will receive an event with urlencoded data in "body"-attribute. This can be easily parsed
with python code and converted into dict.

More info: [AWS forum thread](https://forums.aws.amazon.com/thread.jspa?messageID=673012&tstart=0#673012)
           [Amazon API Gateway - Mapping Template Reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)



It is better to get an isolated AWS environment or a limited aws user for this setup, so you do not mess up your
main deployment enviroment accidentally. This is going to be a separated sandbox for your investigative testing needs.

### EC2 (TODO)
#### 1. Create a Key pair
#### 2. Launch a Centos 7 Instance
#### 3. Setup and configure your instance
First, take a look in this directory: [aws-ec2-ami-files](aws-ec2-ami-files/)

TODO:
#### 4. Create a new Amazon Machine Image (AMI) based on the instance

TODO:

#### 5. Setup network security
TODO:
*











## Deploy branches with Slack slash commands

As a warm up, read the related blog post here: http://dev.solita.fi/2017/04/12/easy-test-deployments.html

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



## Setup automatic builds per branch (for CircleCI)
Refer to: [.circleci](../../.circleci)

We are using CircleCI for building and testing our feature branches, and we are running our staging and production builds separately in Jenkins.  
The main idea here is to create a new build for each commit pushed in a feature branch and send the needed build artifacts
to somewhere easily accessible storage, such as AWS S3.

In short you could:

1. Create an own S3 bucket for your build artifacts.
1. Create a separate circle-ci aws user and config user security
    1. Allow circle-ci user only to push new artifacts into a predefined bucket. You can use a security policy, such as:
    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "s3:PutObject"
                ],
                "Resource": [
                    "arn:aws:s3:::napote-circleci/build-artifacts/*"
                ]
            }
        ]
    }
    ```
    This allows CircleCI AWS user to puy new artifacts only into build-artifacts directory in napote-circleci bucket.
    
1. Config a build and S3 deploy task

    If using S3, this is quite straightforward. We are using the AWS client for simplicity.
    
    The following snippet is from: [config.yml](../../.circleci/config.yml)
    ```yml
    - run:
      name: Deploy build artifacts
      command: |
        mkdir deploy
        ln ote/config.edn deploy/ote-${CIRCLE_BRANCH}-config.edn
        ln /tmp/ote/ote.jar deploy/ote-${CIRCLE_BRANCH}.jar
        pg_dump -h localhost -p 5432 -U postgres -Z 1 napote > deploy/ote-${CIRCLE_BRANCH}-pgdump.gz
        aws s3 cp deploy s3://napote-circleci/build-artifacts --recursive
    ```
    
    We have created a custom CircleCI docker image that included all the dependencies needed for our build tasks.  
    You can check it out at [Docker Hub](https://hub.docker.com/r/solita/napote-circleci/) or in this [repo](../../.circleci/Dockerfile).
    
    
**Final touches**
You might want to add a Lifecycle rule for your build-artifacts directory to remove old build artifacts from your bucket.
This can be easily done by navigating into your S3 bucket in the Amazon S3 dashboard, and then choosing the 
"Management"-tab. Here you can add a Lifecycle-rule. The Expiration-setting manages when objects are deleted from your S3-bucket.

## Create a custom Slack App
https://api.slack.com/apps

1. Create New App
1. Name app and define workspace
1. Active Incoming Webhooks
    1. Copy the Webhook URL **Warning: Do not publish this url anywhere!**
1. Add Slash Commands
    1. /napotedeploy
    1. /napoteterminate  
    You can leave "Request Url" blank for now. We'll come back after our API Gateway has been configured.
1. Copy Verification Token from Basic Information page. **Warning: Do not publish this key anywhere!**

Verification token is needed to verify that messages received by our Lambda functions are coming from our Slack App.  


## Setup your AWS environment

It is better to get an isolated AWS environment or a limited aws user for this setup, so you do not mess up your
main deployment environment accidentally. This is going to be a separated sandbox for your investigative testing needs.

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

Notice, that in the code we need to define ```slacktoken``` and ```allowed_users``` environment variables. 

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

Define ```slack_webhook``` environment variable. This is the Webhook URL of your Slack App.


**CloudWatch cron trigger**

We want to terminate all the instances at 19:00 GMT+2 every day after work hours. This is very simple process.  
In the function editor, at the "Add triggers"-menu, click "CloudWatch Events". Then, select "Create a new rule" and name it.  
For "Rule type", select Schedule expression and add ```cron(0 17 * * ? *)```.
This defines an event that triggers each day at 17:00 UTC.  
Finally, click "Add".

**Settings**
Add a longer timeout for this function, about 1 minutes should be fine.



### API Gateway

We will create a custom API using API Gateway. This API will be our endpoint for Slack to send queries. It will also
trigger our ```slack_deployhook``` Lambda function. To start, go to the API Gateway dashboard. Now, let's create a freshly
baked API!

1. In the menu click APIs and then "Create API".
1. Name your API and click "Create API" in the bottom corner. Don't worry about Endpoint Type."
1. Click Resources -> Actions and choose "Create Resource"
1. Name your resource to e.g. "deploy" and click Create Resource in the bottom corner.
1. Now, click your newly created /deploy resource and choose Actions -> "Create Method". Finally, select POST and click the tick-button.

We have created an API endpoint that we will used by our Slack slash commands. Let's trigger our ```slack_deployhook```
function with this endpoint.

1. In /deploy - POST - Setup, select "Lambda Function" as Integration type".
1. For Lambda Region, select whatever region you used when you created your Lambda functions.
1. Write your function name, ```slack_deployhook```, in the Lambda Function textfield and click "Save".

Slack sends all POST request in x-www-form-urlencoded form. We have to convert those into JSON, so we can pass them to our
Lambda function. For this, we'll have to create a Body Mapping Template.

1. Click /deploy -> Post.
1. In /deploy - POST - Method Execution view, click "Integration Request".
1. In "Body Mapping Templates", click "Never", for "Request body passthrough".
1. Then, click "+ Add mapping template".
1. Write "application/x-www-form-urlencoded" in the Content-Type section and click the tick-button.
1. Now, click the "application/x-www-form-urlencoded"-link and copy the snippet below into the opening text area and save.

Template for converting Slack request body from x-www-form-urlencoded to json:
```text
{
  "body" : "$input.body"
}
```

Our Slack lambda function will receive an event with urlencoded data in "body"-attribute. This can be easily parsed
with python code and converted into a dict.

The final phase of API creation is to deploy it. After deploying, we get a public URL for our API.

1. Click Actions -> Deploy API.
1. In Deploy API dialog, select "prod" and click Deploy.
1. Now you should be redirected in the Stages-view. Copy the "Invoke URL". This is the base URL of our API and it is 
something like: https://<random-code>.execute-api.<region>.amazonaws.com/prod/<resource_path>
1. In Stages-view, click "prod" -> / -> /deploy -> POST and copy the "Invoke URL". We'll need this for our Slack App.


**Final touches**  
Go to your Slack APP and edit your Slash Commands. Copy the URL above into the "Request URL"-textfields.


More info: [AWS forum thread](https://forums.aws.amazon.com/thread.jspa?messageID=673012&tstart=0#673012)
           [Amazon API Gateway - Mapping Template Reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)


### EC2

Phew, we have come a long way. We have setup CircleCI builds, created a custom Slack App, confgured an API Gateway and created
A bunch of Lambda functions. Only one step left anymore, so bear with me for a moment.  
We are using Amazon Elastic Compute Cloud, because it is very simple to launch and control new instances. Each time
a deploy hook function is triggered we want to boot up a fresh Centos 7 instance with all dependencies installed.
To begin, go to your EC2 Dashbord page.


#### 1. Create a Key pair
First, we'll have to create an SSH key pair. It will be used to connect to our launched instance in a secure manner.  

1. In Network & Security meny, click Key Pairs.
1. Click "Create Key Pair" and name it. Click Create, and wait for download dialog.
1. Notice, that this is the only moment that you can download the .pem-file required for ssh-connection. Download the file
and store it in a secure place. Never place it in a public repository! If you failed to download the file, delete the key pair
and create a new one.

#### 2. Launch a Centos 7 Instance
Let's launch a bare-bones Centos 7 instance that we are going to use as a base for our AMI.

1. In Instances-menu, click "Instances". Click, Launch Instance.
1. You should now be in Instance Wizard page.
1. In Step 1, click AWS Marketplace and search for CentOS 7. Find CentOS 7 (x86_64) - with Updates HVM and click Select-button.
1. In the opening dialog click Continue.
1. Leave all the settings in default values and click Review and Launch.
1. In the opening dialog, select the key pair we created above, and click Launch Instances.

#### 3. Setup and configure your instance
First, take a look in this directory: [aws-ec2-ami-files](aws-ec2-ami-files/)

You can use the provided bash-scripts to install dependencies in the instance and to configure postgres and nginx properly
for our project. However, ```start-ote.sh```, ```setup-db.sh``` and```config.end``` are required. These files are 
run by the cloud-init, as defined in the ```deploy_async``` Lambda function.

You can connect to the instance by selecting the running instance in the Instances-view and by clicking Connect-button.
Read the instructions in the opening dialog.

#### 4. Create a new Amazon Machine Image (AMI) based on the instance

Now that you have installed all the stuff needed in the instance, it is time to freeze it and create a new AMI.

1. In the Instances-view, click your running instance and then click Actions -> Image -> Create Image.
1. Name and describe your image, and click "Create Image"-button.
1. The image creation process will start and it will take a while, so you might want to grab a cup coffee.
1. You can track the image status in Images-> AMIs view.
1. Copy the AMI ID.


**Setup network security**

By default, we are launching each new instance in the "default" network security group.
For convenience, we will edit the default group instead of creating a new one.

1. Click Network & Security -> Security Groups.
1. Find and select the "default"-group.
1. Open Inboud-tab, and add the following rules, and click Save:
  1. Type: HTTP, Source: Custom 0.0.0.0/0
  1. Type: SSH, Source: Custom 0.0.0.0/0

These rules allows us to connect to the running instance via SSH if debugging is needed. Also, we'll have to allow
access to port 80 to serve the deployed app.

**Final touches**

Edit your ```deploy_async``` Lambda function and paste your AMI ID and Key pair name in the proper places here:
```python
...

res = ec2.run_instances(ImageId='<your-ami-image-id>',
                                InstanceType='t2.medium',
                                UserData=script,
                                KeyName='<your-ec2-keypair-name>',
                                MinCount=1,
                                MaxCount=1)                      
...

```










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
1. Knowledge on how to create a custom Slack App for your organization.
1. Python 2.7 basics.
1. GitHub API basics.



## Setup automatic builds per branch (for CircleCI)
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
1. Copy Verification Token from Basic Information page. **Warning: Do not publish this key anywhere!**

## Setup your AWS environment

It is better to get an isolated AWS environment or a limited aws user for this setup, so you do not mess up your
main deployment enviroment accidentally. This is going to be a separated sandbox for your investigative testing needs.

### EC2
#### 1. Create a Key pair
#### 2. Launch a Centos 7 Instance
#### 3. Setup and configure your instance
First, take a look in this directory: [aws-ec2-ami-files](aws-ec2-ami-files/)

TODO:
#### 4. Create a new Amazon Machine Image (AMI) based on the instance

TODO:

#### 5. Setup network security
TODO:

### API Gateway
1. **Create API**  
1. **Add a new API resource**  
1. **Add POST - Integration Request Body Mapping Template**

Template for converting request body from x-www-form-urlencoded to json:
```ftl
## convert HTML POST data or HTTP GET query string to JSON
 
## get the raw post data from the AWS built-in variable and give it a nicer name
#if ($context.httpMethod == "POST")
 #set($rawAPIData = $input.path('$'))
#elseif ($context.httpMethod == "GET")
 #set($rawAPIData = $input.params().querystring)
 #set($rawAPIData = $rawAPIData.toString())
 #set($rawAPIDataLength = $rawAPIData.length() - 1)
 #set($rawAPIData = $rawAPIData.substring(1, $rawAPIDataLength))
 #set($rawAPIData = $rawAPIData.replace(", ", "&"))
#else
 #set($rawAPIData = "")
#end
 
## first we get the number of "&" in the string, this tells us if there is more than one key value pair
#set($countAmpersands = $rawAPIData.length() - $rawAPIData.replace("&", "").length())
 
## if there are no "&" at all then we have only one key value pair.
## we append an ampersand to the string so that we can tokenise it the same way as multiple kv pairs.
## the "empty" kv pair to the right of the ampersand will be ignored anyway.
#if ($countAmpersands == 0)
 #set($rawPostData = $rawAPIData + "&")
#end
 
## now we tokenise using the ampersand(s)
#set($tokenisedAmpersand = $rawAPIData.split("&"))
 
## we set up a variable to hold the valid key value pairs
#set($tokenisedEquals = [])
 
## now we set up a loop to find the valid key value pairs, which must contain only one "="
#foreach( $kvPair in $tokenisedAmpersand )
 #set($countEquals = $kvPair.length() - $kvPair.replace("=", "").length())
 #if ($countEquals == 1)
  #set($kvTokenised = $kvPair.split("="))
  #if ($kvTokenised[0].length() > 0)
   ## we found a valid key value pair. add it to the list.
   #set($devNull = $tokenisedEquals.add($kvPair))
  #end
 #end
#end
 
## next we set up our loop inside the output structure "{" and "}"
{
#foreach( $kvPair in $tokenisedEquals )
  ## finally we output the JSON for this pair and append a comma if this isn't the last pair
  #set($kvTokenised = $kvPair.split("="))
 "$util.urlDecode($kvTokenised[0])" : #if($kvTokenised[1].length() > 0)"$util.urlDecode($kvTokenised[1])"#{else}""#end#if( $foreach.hasNext ),#end
#end
}
```

Template snatched from: [here](https://gist.githubusercontent.com/ryanray/668022ad2432e38493df/raw/a3b8c765791ac6cfc15811a5dcb2d97056adc107/aws-api-gateway-form-to-json.ftl)  
More info: [AWS forum thread](https://forums.aws.amazon.com/thread.jspa?messageID=673012&tstart=0#673012)
           [Amazon API Gateway - Mapping Template Reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)

### Lambda

**Setup Lambda user role and security**


#### Lambda Script 1: Slack slash command handler
Code: [slack_deployhook](aws-lambda-scripts/slack_deployhook.py)

**API Gateway trigger**  
**A word about Cloud Init**


#### Lambda Script 2: Async deploy
Code: [deploy_async](aws-lambda-scripts/deploy_async.py)

**Triggering deploy from slack_deployhook**
**A brief introduction to GitHub API**

#### Lambda Script 3: Periodic EC2 instance terminator
Code: [terminate_instances](aws-lambda-scripts/terminate_instances.py)

**CloudWatch cron trigger**











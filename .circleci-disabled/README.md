## AWS

### IAM
* Create a separate circleci user in IAM
* Create a custom access group and add a new custom security policy in this group:
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

Now, the circleci aws user has only right to put new objects into speficic s3 bucket.


### S3

Create a new bucket: ``ǹapote-circleci`` and add a new directory in it: ``build-artifacts``


## CircleCI settings

Remember to define the following env-variables for circleci

* CYPRESS_RECORD_KEY: [Get it from here](https://dashboard.cypress.io/#/projects/ucw436/settings)
* CircleCI settings / permissions / AWS Permissions:
  * Fill: Access Key ID
  * Fill: Secret Access Key
  * ^ Get these from AWS IAM Dashboard / Users / napote-circleci / security credentials.  
    If you forget the secret access key, make the old one inactive and create a new one.

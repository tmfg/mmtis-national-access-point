# coding=utf-8

import logging
import urllib2
import zipfile
import io
import os
import datetime
import json
import boto3
from botocore.client import Config

from kalkati2gtfs import convert

logger = logging.getLogger()
logger.setLevel(logging.INFO)

s3 = boto3.client('s3', config=Config(signature_version='s3v4'))


def zip_files(path, zipfile_path):
    zipf = zipfile.ZipFile(zipfile_path, mode='w')

    for root, dirs, files in os.walk(path):
        for file in files:
            zipf.write(os.path.join(root, file), arcname=file)
            os.remove(os.path.join(root, file))


### Lambda handler ###
## NOTE: We are using Lambda proxy integration in AWS API Gateway
def lambda_handler(event, context):
    try:
        try:
            file_url = json.loads(event['body'])
        except ValueError as e:
            raise ValueError('Invalid Kalkati URL: ' + str(e))

        try:
            logger.info('Trying to fetch a kalkati zip-file from URL: {}'.format(file_url))

            opener = urllib2.build_opener()
            response = opener.open(file_url)

            with io.BytesIO(response.read()) as tf:
                # rewind the file
                tf.seek(0)

                # Read the file as a zipfile and process the members
                with zipfile.ZipFile(tf, mode='r') as zipf:
                    for file in zipf.infolist():
                        path = zipf.extract(file, '/tmp/kalkati/')
                        convert(path, '/tmp/gtfs/')

            zip_files('/tmp/gtfs', '/tmp/gtfs.zip')

        except Exception as e:
            raise RuntimeError('Error while processing file {}: {}'.format(file_url, str(e)))

        file_key = 'gtfs/gtfs-%s.zip' % datetime.datetime.now().strftime('%Y%m%d-%H%M%S')

        try:
            s3.put_object(Bucket='kalkati2gtfs', Key=file_key, Body=open('/tmp/gtfs.zip'))
            # Create temporary url for downloading the generated zip file (expires in 10 minutes)
            signed_url = s3.generate_presigned_url(ClientMethod='get_object',
                                                   Params={'Bucket': 'kalkati2gtfs', 'Key': file_key},
                                                   ExpiresIn=10 * 60)

            return {
                'statusCode': 303,
                'headers': {'Location': signed_url}
            }

        except Exception as e:
            raise RuntimeError('Error while uploading {} to bucket {}: {}'.format(file_key, 'kalkati2gtfs', str(e)))

    except Exception as e:
        logger.error(str(e))

        return {
            'statusCode': 500,
            'body': json.dumps({
                'error': str(e)
            })
        }

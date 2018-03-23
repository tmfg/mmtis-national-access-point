# coding=utf-8

import logging
import urllib
import boto3
import zipfile
import io
import os
import datetime

from kalkati2gtfs import convert

logger = logging.getLogger()
logger.setLevel(logging.INFO)

s3 = boto3.client('s3')

def zip_files(path, zipfile_path):
    zipf = zipfile.ZipFile(zipfile_path, mode='w')

    for root, dirs, files in os.walk(path):
        for file in files:
            zipf.write(os.path.join(root, file), arcname=file)
            os.remove(os.path.join(root, file))


### Lambda handler ###
def lambda_handler(event, context):
    bucket_name = event['Records'][0]['s3']['bucket']['name']
    file_key = urllib.unquote_plus(event['Records'][0]['s3']['object']['key'].encode('utf8'))

    logger.info('Reading {} from {}'.format(file_key, bucket_name))

    try:
        obj = s3.get_object(Bucket=bucket_name, Key=file_key)

        with io.BytesIO(obj["Body"].read()) as tf:
            # rewind the file
            tf.seek(0)

            # Read the file as a zipfile and process the members
            with zipfile.ZipFile(tf, mode='r') as zipf:
                for file in zipf.infolist():
                    path = zipf.extract(file, '/tmp/kalkati/')
                    convert(path, '/tmp/gtfs/')

        zip_files('/tmp/gtfs', '/tmp/gtfs.zip')

    except Exception as e:
        logger.error('Error while loading {} from bucket {}: {}'.format(file_key, bucket_name, e))

        raise e

    try:
        file_key = 'gtfs/gtfs-%s.zip' % datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
        s3.put_object(Bucket=bucket_name, Key=file_key, Body=open('/tmp/gtfs.zip'))

    except Exception as e:
        logger.error('Error while uploading {} to bucket {}: {}'.format(file_key, bucket_name, e))

        raise e

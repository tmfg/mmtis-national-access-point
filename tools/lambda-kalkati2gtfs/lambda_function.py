# coding=utf-8

import logging
import urllib2
import io
import datetime
import json
import boto3

from zipfile import ZipFile, ZIP_DEFLATED
from botocore.client import Config
from kalkati2gtfs import convert_in_memory

logger = logging.getLogger()
logger.setLevel(logging.INFO)

s3 = boto3.client('s3', config=Config(signature_version='s3v4'))


def extract_zip(buf):
    # Rewind the buffer
    buf.seek(0)
    with ZipFile(buf, mode='r') as zipf:
        return [zipf.open(name) for name in zipf.namelist()]


def zip_files(files):
    buf = io.BytesIO()

    with ZipFile(buf, 'w', ZIP_DEFLATED, False) as zipf:
        for name, file in files.iteritems():
            zipf.writestr(name + '.txt', file.read())
            if name is "routes":
                print(file.read())

            # Free file buffer
            file.close()

    buf.seek(0)

    return buf


# NOTE: We assume that there is only one Kalkati LVM.xml file in the provided zip, as should be.
def kalkati_zip_to_gtfs_zip(zip_buf):
    kalkati_files = extract_zip(zip_buf)
    gtfs_files = convert_in_memory(kalkati_files[0])

    for file in kalkati_files:
        file.close()

    return zip_files(gtfs_files)


s3_bucket = 'napote-gtfs'
s3_folder = 'kalkati2gtfs'


### Lambda handler ###
## NOTE: We are using Lambda proxy integration in AWS API Gateway
def lambda_handler(event, context):

    headers = event.get('headers')

    try:
        try:
            file_url = json.loads(event['body'])
        except ValueError as e:
            raise ValueError('Invalid Kalkati URL: ' + str(e))

        try:
            logger.info('Trying to fetch a kalkati zip-file from URL: {}'.format(file_url))

            opener = urllib2.build_opener()

            if headers and type(headers) is dict:
                opener.addheaders = headers.items()

            response = opener.open(file_url)

            with io.BytesIO(response.read()) as tf:
                zipf_buf = kalkati_zip_to_gtfs_zip(tf)

        except urllib2.HTTPError, e:
            return {
                'statusCode': e.code,
                'headers':  dict(e.info()),
                'body': json.dumps({
                    'error': str(e)
                })
            }

        except Exception as e:
            raise RuntimeError('Error while processing file {}: {}'.format(file_url, str(e)))

        file_key = '%s/gtfs-%s.zip' % (s3_folder, datetime.datetime.now().strftime('%Y%m%d-%H%M%S'))

        try:
            s3.put_object(Bucket=s3_bucket, Key=file_key, Body=zipf_buf)
            zipf_buf.close()

            # Create temporary url for downloading the generated zip file (expires in 10 minutes)
            signed_url = s3.generate_presigned_url(ClientMethod='get_object',
                                                   Params={'Bucket': s3_bucket, 'Key': file_key},
                                                   ExpiresIn=10 * 60)

            return {
                'statusCode': 303,
                'headers': {'Location': signed_url}
            }

        except Exception as e:
            raise RuntimeError('Error while uploading {} to bucket {}: {}'.format(file_key, s3_bucket, str(e)))

    except Exception as e:
        logger.error(str(e))

        return {
            'statusCode': 500,
            'body': json.dumps({
                'error': str(e)
            })
        }

# -*- coding: utf-8 -*-

from django.conf import settings
import boto
from boto.s3.connection import Location

def bulid_connect_s3():
    s3 = boto.connect_s3(settings.AWS_ACCESS_KEY_ID, settings.AWS_SECRET_ACCESS_KEY)
    return s3

def get_or_create_bucket(bucket_name, location=Location.DEFAULT):
    s3 = bulid_connect_s3()
    bucket = s3.lookup(bucket_name)
    if not bucket:
        try:
            bucket = s3.create_bucket(bucket_name, location=location)
        except s3.provider.storage_create_error, e:
            print 'Bucket (%s) is owned by another user' % bucket_name
    return bucket

def store_private_data_from_filename(bucket_name, key_name, path_source_file, metadata=None):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    key = bucket.new_key(key_name)
    key.set_contents_from_filename(path_source_file)
    if metadata:
        key.metadata.update(metadata)
    return key

def store_private_data_from_stream(bucket_name, key_name, stream, metadata=None):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    key = bucket.new_key(key_name)
    key.set_contents_from_stream(stream)
    if metadata:
        key.metadata.update(metadata)
    return key

def store_private_data_from_string(bucket_name, key_name, need_store_string, metadata=None):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    key = bucket.new_key(key_name)
    key.set_contents_from_string(need_store_string)
    if metadata:
        key.metadata.update(metadata)
    return key

def modify_metadata(bucket_name, key_name, metadata):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    key = bucket.lookup(key_name)
    if key:
        key.copy(bucket.name, key.name, metadata, preserve_acl=True)
    return key

def enable_logging(bucket_name, log_bucket_name, log_prefix=None):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    log_bucket = s3.lookup(log_bucket_name)
    log_bucket.set_as_logging_target()
    bucket.enable_logging(log_bucket, target_prefix=log_prefix)

def disable_logging(bucket_name):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    bucket.disable_logging()

def bucket_du(bucket_name):
    bucket = get_or_create_bucket(bucket_name, location=Location.DEFAULT)
    total_bytes = 0
    if bucket:
        for key in bucket:
            total_bytes += key.size
    return total_bytes


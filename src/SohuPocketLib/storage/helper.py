# -*- coding: utf-8 -*-

from django.conf import settings
import boto
from boto.s3.connection import Location

def bulid_connect_s3():
    s3 = boto.connect_s3(settings.AWS_ACCESS_KEY_ID, settings.AWS_SECRET_ACCESS_KEY)
    return s3

def get_or_create_bucket(bucket_name, policy='public-read', location=Location.DEFAULT):
    s3 = bulid_connect_s3()
    bucket = s3.lookup(bucket_name)
    if not bucket:
        try:
            bucket = s3.create_bucket(bucket_name, policy=policy, location=location)
            bucket.set_canned_acl('public-read')
        except s3.provider.storage_create_error, e:
            print 'Bucket (%s) is owned by another user' % bucket_name
    return bucket

def get_data_to_string(bucket_name, key_name):
    #get_contents_to_filename,get_file
    bucket = get_or_create_bucket(bucket_name)
    key = bucket.lookup(key_name)
    if not key:
        return None
    return key.get_contents_as_string()

def get_data_to_filename(bucket_name, key_name, filename):
    #get_contents_to_filename,get_file
    bucket = get_or_create_bucket(bucket_name)
    key = bucket.lookup(key_name)
    if not key:
        return None
    return key.get_contents_to_filename(filename)

def store_data_from_filename(bucket_name, key_name, path_source_file, metadata=None, policy='public-read'):
    bucket = get_or_create_bucket(bucket_name, policy)
    key = bucket.new_key(key_name)
    key.set_contents_from_filename(path_source_file, policy=policy)
    if metadata:
        key.metadata.update(metadata)
    return key

def store_data_from_stream(bucket_name, key_name, stream, metadata=None, policy='public-read'):
    bucket = get_or_create_bucket(bucket_name, policy)
    key = bucket.new_key(key_name)
    key.set_contents_from_stream(stream, policy=policy)
    if metadata:
        key.metadata.update(metadata)
    return key

def store_data_from_string(bucket_name, key_name, need_store_string, metadata=None, policy='public-read'):
    bucket = get_or_create_bucket(bucket_name, policy)
    key = bucket.new_key(key_name)
    key.set_contents_from_string(need_store_string, policy=policy)
    if metadata:
        key.metadata.update(metadata)
    return key

def modify_metadata(bucket_name, key_name, metadata):
    bucket = get_or_create_bucket(bucket_name)
    key = bucket.lookup(key_name)
    if key:
        key.copy(bucket.name, key.name, metadata, preserve_acl=True)
    return key

def enable_logging(bucket_name, log_bucket_name, log_prefix=None):
    bucket = get_or_create_bucket(bucket_name)
    log_bucket = s3.lookup(log_bucket_name)
    log_bucket.set_as_logging_target()
    bucket.enable_logging(log_bucket, target_prefix=log_prefix)

def disable_logging(bucket_name):
    bucket = get_or_create_bucket(bucket_name)
    bucket.disable_logging()

def bucket_du(bucket_name):
    bucket = get_or_create_bucket(bucket_name)
    total_bytes = 0
    if bucket:
        for key in bucket:
            total_bytes += key.size
    return total_bytes

def get_expire_data_url(bucket_name, key_name, expires_seconds):#该URL地址有过期时间
    bucket = get_or_create_bucket(bucket_name)
    key = bucket.lookup(key_name)
    if not key:
        return None
    return key.generate_url(expires_seconds)

def get_data_url(bucket_name, key_name):
    domain = 's3.amazonaws.com'
    url = 'http://%s.s3.amazonaws.com/%s' % (bucket_name, key_name)
    return url

def set_bucket_acl(bucket_name, policy):
    '''
    POLICY: 'private', 'public-read','public-read-write', 
    'authenticated-read', 'bucket-owner-read', 'bucket-owner-full-control'
    '''
    bucket = get_or_create_bucket(bucket_name, policy)
    bucket.set_acl(policy)

def get_bucket_acl(bucket_name):
    bucket = get_or_create_bucket(bucket_name)
    return bucket.get_acl()
    

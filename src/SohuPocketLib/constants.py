# -*- coding: utf-8 -*-

LIMIT_USERS_ONE_DB = 1000000

DEFAULT_ARTICLE_LIST_LIMIT = 20

KEY_ARTICLE_INSTANCE = 'key-article-instance-%s-%s-v1' # require user_id, url_hash
KEY_IMAGE_TOBEDONE = 'key-image-tobedone-%s-v1' # require article_id 
KEY_IMAGE_INSTANCE = 'key-image-instance-%s-%s-v1' # require article_id, image_url_hash
KEY_FOLDER = 'key-folder-%s-%s-v1' # require user_id, folder_name

BUCKET_NAME_ARTICLE = 'bucket-sohukan-article-v1'
BUCKET_NAME_IMAGE = 'bucket-sohukan-image-v1'

TRUE_REPR = '1'
FALSE_REPR = '0'

PAGE_FETCH_MAX_RETRIES = 3
PAGE_FETCH_DEFAULT_RETRY_DELAY = 3 # second
PAGE_FETCH_TIME_LIMIT = 5 * 60 # second

UPLOAD_ARTICLE_MAX_RETRIES = 3
UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY = 3 # second 
UPLOAD_ARTICLE_TIME_LIMIT = 5 * 60 # second

DOWNLOAD_IMAGE_MAX_RETRIES = 3
DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY = 3 # second
DOWNLOAD_IMAGE_TIME_LIMIT = 5 * 60 # second

UPLOAD_IMAGE_MAX_RETRIES = 3
UPLOAD_IMAGE_DEFAULT_RETRY_DELAY = 3 # second
UPLOAD_IMAGE_TIME_LIMIT = 5 * 60 # second

CLEAN_UP_TIME_BEFORE_KILLED = 5 #second

# -*- coding: utf-8 -*-

LIMIT_USERS_ONE_DB = 1000000

DEFAULT_ARTICLE_LIST_LIMIT = 20

KEY_ARTICLE_INSTANCE = 'key_article_instance_%s_%s_v1' # require user_id, url_hash
KEY_IMAGE_TOBEDONE = 'key_image_tobedone_%s_v1' # require article_id 
KEY_IMAGE_INSTANCE = 'key_image_instance_%s_%s_v1' # require article_id, image_url_hash

BUCKET_NAME_ARTICLE = 'bucket_sohukan_article_v1'
BUCKET_NAME_IMAGE = 'bucket_sohukan_image_v1'

TRUE_REPR = '1'
FALSE_REPR = '0'

PAGE_FETCH_MAX_RETRIES = 5
PAGE_FETCH_DEFAULT_RETRY_DELAY = 1 # 1 min
PAGE_FETCH_TIME_LIMIT = 3 * 60 # 3 min

UPLOAD_ARTICLE_MAX_RETRIES = 10
UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY = 3 # 3 min 
UPLOAD_ARTICLE_TIME_LIMIT = 5 * 60 # 3 min

DOWNLOAD_IMAGE_MAX_RETRIES = 5
DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY = 1 # 1 min
DOWNLOAD_IMAGE_TIME_LIMIT = 5 * 60 # 5 min

UPLOAD_IMAGE_MAX_RETRIES = 10
UPLOAD_IMAGE_DEFAULT_RETRY_DELAY = 3 # 3 min
UPLOAD_IMAGE_TIME_LIMIT = 7 * 60 # 7 min

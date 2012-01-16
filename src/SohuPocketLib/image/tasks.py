# -*- coding: utf-8 -*-

from SohuPocketLib.article.models import MyArticleInstance
from celery.task import Task
from SohuPocketLib.constants import CACHE_KEY_IMAGE, IMAGE_BUCKET_NAME
from django.core.cache import cache
from SohuPocketLib.storage.helper import store_data_from_string
import hashlib
import urllib2


class DownloadAndSaveImageHandler(Task):
    """
    handler that download a image, save to s3, and then write record to local db
    """
    
    def run(self, image_url, user_id, article_id, image_left_count_key):
        image_data = urllib2.urlopen(image_url).read()
        
        hash_source = image_url 
        key = hashlib.new('sha1', hash_source).hexdigest()
        global_key = CACHE_KEY_IMAGE % (str(user_id), str(article_id), key)
        store_data_from_string(IMAGE_BUCKET_NAME, global_key, image_data)
        
        cache.decr(image_left_count_key, 1)
        if cache.get(image_left_count_key) == 0:
            try:
                myarticle_instance = MyArticleInstance.objects.get(id = article_id)
                myarticle_instance.is_ready = True
            except Exception:
                pass

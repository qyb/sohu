# -*- coding: utf-8 -*-

from SohuPocketLib.article.models import MyArticleInstance
from SohuPocketLib.constants import BUCKET_NAME_IMAGE
from SohuPocketLib.image.helper import generate_image_instance_key, \
    decrease_image_tobedone, get_image_tobedone, create_myimage_instance
from SohuPocketLib.storage.helper import store_data_from_string
from celery.task import Task
import urllib2


class DownloadImageHandler(Task):
    """
    download image
    """
    
    def run(self, image_url, image_tobedone_key, info):
        is_successful = True
        try:
            image_data = urllib2.urlopen(image_url).read()
        except Exception:
            is_successful = False
        else:
            StoreImageHandler.delay(image_url, image_data, image_tobedone_key, info)
            
        return is_successful    


class StoreImageHandler(Task):
    """
    store image to s3
    """
    
    def run(self, image_url, image_data, image_tobedone_key, info):
        is_successful = True
        image_instance_key = generate_image_instance_key(info['article_id'], image_url)
        try:
            store_data_from_string(BUCKET_NAME_IMAGE, image_instance_key, image_data)
            create_myimage_instance(image_instance_key, image_url, info['article_id'])
        except Exception:
            is_successful = False
        else:
            decrease_image_tobedone(image_tobedone_key)
            if get_image_tobedone(image_tobedone_key) == 0:
                try:
                    article_instance = MyArticleInstance.objects.get(id = info['article_id'])
                    article_instance.is_ready = True
                    article_instance.save()
                except MyArticleInstance.DoesNotExist:
                    is_successful = False
                    
        return is_successful

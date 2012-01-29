# -*- coding: utf-8 -*-

from article.helper import choose_a_db
from article.models import MyArticleInstance
from constants import BUCKET_NAME_IMAGE
from image.helper import generate_image_instance_key, \
    decrease_image_tobedone, get_image_tobedone, create_myimage_instance
from storage.helper import store_data_from_string
from celery.task import Task
import urllib2


class DownloadImageHandler(Task):
    """
    download image
    """
    
    def run(self, image_url, image_tobedone_key, info):
        is_successful = True
        image_info = dict()
        try:
            image_data = urllib2.urlopen(image_url).read()
        except Exception:
            is_successful = False
            raise
        else:
            image_info['image_url'] = image_url
            image_info['image_data'] = image_data
            image_info['image_tobedone_key'] = image_tobedone_key
#            call next step
            StoreImageInfoHandler.delay(image_info, info)
            
        return is_successful    


class StoreImageInfoHandler(Task):
    """
    store image info to local db
    """
    
    def run(self, image_info, info):
        is_successful = True
        image_instance_key = generate_image_instance_key(info['article_id'], image_info['image_url'])
        try:
            create_myimage_instance(image_instance_key, image_info['image_url'], info['article_id'])
        except Exception:
            is_successful = False
            raise
        else:
            image_info['image_instance_key'] = image_instance_key
#            call next step
            UploadImageHandler.delay(image_info, info)
            
        return is_successful
            

class UploadImageHandler(Task):
    """
    upload image to s3
    """
    
    def run(self, image_info, info):
        is_successful = True
        try:
            store_data_from_string(BUCKET_NAME_IMAGE, image_info['image_instance_key'], image_info['image_data'])
        except Exception:
            is_successful = False
            raise
        else:
#            call next step
            CheckImagetobedoneHandler.delay(image_info, info)
            
        return is_successful


class CheckImagetobedoneHandler(Task):
    """
    check whether to mark article as is_ready
    """
    
    def run(self, image_info, info):
        is_successful = True
        decrease_image_tobedone(image_info['image_tobedone_key'])
        if get_image_tobedone(image_info['image_tobedone_key']) == 0:
            try:
                chosen_db = choose_a_db(info['user_id'])
                article_instance = MyArticleInstance.objects.using(chosen_db).get(id = info['article_id'])
                article_instance.is_ready = True
                article_instance.save()
            except MyArticleInstance.DoesNotExist:
                is_successful = False
                raise
            
        return is_successful
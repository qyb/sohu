# -*- coding: utf-8 -*-

from article.helper import choose_a_db
from article.models import MyArticleInstance
from constants import BUCKET_NAME_IMAGE
from image.helper import generate_image_instance_key, \
    decrease_image_tobedone, get_image_tobedone, create_myimage_instance, \
    UpdateImageInfo
from storage.helper import store_data_from_string
from celery.task import Task
from celery.task.sets import subtask
import urllib2
import logging

class DownloadImageHandler(Task):
    """
    download image
    """
    
    def run(self, image_url, image_tobedone_key, update_article_info):
        is_successful = True
        update_image_info = UpdateImageInfo(image_url)
        try:
            resource = urllib2.urlopen(image_url)
            image_data = resource.read()
            try:
                mime = resource.info()['Content-Type']
            except:
                mime = None
        except Exception:
            is_successful = False
        else:
            update_image_info.image_url = image_url
            update_image_info.image_data = image_data
            update_image_info.mime = mime
            update_image_info.image_tobedone_key = image_tobedone_key
#            call next step
#            StoreImageInfoHandler.delay(update_image_info, update_article_info)
            StoreImageInfoHandler.delay(update_image_info,
                                        update_article_info,
                                        callback=subtask(UploadImageHandler,
                                        callback=subtask(CheckImagetobedoneHandler)))
            
        return is_successful    


class StoreImageInfoHandler(Task):
    """
    store image info to local db
    """
    
    def run(self, update_image_info, update_article_info, callback=None):
        is_successful = True
        image_instance_key = generate_image_instance_key(update_article_info.article_id, update_image_info.image_url)
        try:
            create_myimage_instance(update_article_info.user_id, image_instance_key, update_image_info.image_url, update_article_info.article_id)
        except Exception:
            is_successful = False
        else:
            update_image_info.image_instance_key = image_instance_key
#            call next step
#            UploadImageHandler.delay(update_image_info, update_article_info)
            subtask(callback).delay(update_image_info, update_article_info)
            
        return is_successful
            

class UploadImageHandler(Task):
    """
    upload image to s3
    """
    
    def run(self, update_image_info, update_article_info, callback=None):
        is_successful = True
        try:
            headers = dict()
            if update_article_info.mime:
                headers['Content-Type'] = update_image_info.mime
            store_data_from_string(BUCKET_NAME_IMAGE,
                                   update_image_info.image_instance_key,
                                   update_image_info.image_data,
                                   headers=headers)
        except Exception:
            is_successful = False
        else:
#            call next step
#            CheckImagetobedoneHandler.delay(update_image_info, update_article_info)
            subtask(callback).delay(update_image_info, update_article_info)
            
        return is_successful


class CheckImagetobedoneHandler(Task):
    """
    check whether to mark article as is_ready
    """
    
    def run(self, update_image_info, update_article_info):
        is_successful = True
        decrease_image_tobedone(update_image_info.image_tobedone_key)
        if get_image_tobedone(update_image_info.image_tobedone_key) == 0:
            try:
                chosen_db = choose_a_db(update_article_info.user_id)
                article_instance = MyArticleInstance.objects.using(chosen_db).get(id = update_article_info.article_id)
                article_instance.is_ready = True
                article_instance.save()
            except MyArticleInstance.DoesNotExist:
                is_successful = False
            
        return is_successful

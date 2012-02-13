# -*- coding: utf-8 -*-

from article.helper import choose_a_db, mark_article_as_done
from article.models import MyArticleInstance
from celery.task import Task
from celery.task.sets import subtask
from constants import BUCKET_NAME_IMAGE, DOWNLOAD_IMAGE_MAX_RETRIES, \
    DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY, UPLOAD_IMAGE_MAX_RETRIES, \
    UPLOAD_IMAGE_DEFAULT_RETRY_DELAY, DOWNLOAD_IMAGE_TIME_LIMIT, \
    UPLOAD_IMAGE_TIME_LIMIT
from image.helper import generate_image_instance_key, decrease_image_tobedone, \
    get_image_tobedone, create_myimage_instance, UpdateImageInfo, \
    delete_myimage_instance_in_db
from storage.helper import store_data_from_string
import celery
import logging
import urllib2


class DownloadImageHandler(Task):
    """
    download image
    """
    
    time_limit = DOWNLOAD_IMAGE_TIME_LIMIT
    max_retries = DOWNLOAD_IMAGE_MAX_RETRIES
    default_retry_delay = DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY
    ignore_result = True
    
    def run(self, image_url, image_tobedone_key, update_article_info):
        update_image_info = UpdateImageInfo(image_url)
        update_image_info.image_tobedone_key = image_tobedone_key
        try:
            resource = urllib2.urlopen(image_url)
            image_data = resource.read()
            try:
                mime = resource.info()['Content-Type']
            except:
                mime = None
        except Exception as exc:
            DownloadImageHandler.retry(exc=exc)
        else:
            update_image_info.image_url = image_url
            update_image_info.image_data = image_data
            update_image_info.mime = mime
#            call next step
            StoreImageInfoHandler.delay(update_image_info,
                                        update_article_info,
                                        callback=subtask(UploadImageHandler,
                                        callback=subtask(MarkImagetobedoneHandler)))
            
        return None
    
    def on_failure(self, exc, task_id, args, kwargs, einfo):
        image_url, image_tobedone_key, update_article_info = args
        update_image_info = UpdateImageInfo(image_url)
        update_image_info.image_tobedone_key = image_tobedone_key
        MarkImagetobedoneHandler.delay(update_image_info, update_article_info)
        
        return None
    

class StoreImageInfoHandler(Task):
    """
    store image info to local db
    """
    
    ignore_result = True
    
    def run(self, update_image_info, update_article_info, callback=None):
        image_instance_key = generate_image_instance_key(update_article_info.article_id,
                                                         update_image_info.image_url)
        try:
            create_myimage_instance(update_article_info.user_id,
                                    image_instance_key,
                                    update_image_info.image_url,
                                    update_article_info.article_id)
        except Exception:
            MarkImagetobedoneHandler.delay(update_image_info, update_article_info)
        else:
            update_image_info.image_instance_key = image_instance_key
#            call next step
            subtask(callback).delay(update_image_info, update_article_info)
            
        return None
    

class UploadImageHandler(Task):
    """
    upload image to s3
    """
    
    time_limit = UPLOAD_IMAGE_TIME_LIMIT
    max_retries = UPLOAD_IMAGE_MAX_RETRIES
    default_retry_delay = UPLOAD_IMAGE_DEFAULT_RETRY_DELAY
    ignore_result = True

#    attention: 'callback' here must be called as 'kwargs', not 'args'    
    def run(self, update_image_info, update_article_info, callback=None):
        try:
            headers = dict()
            if update_article_info.mime:
                headers['Content-Type'] = update_image_info.mime
            store_data_from_string(BUCKET_NAME_IMAGE,
                                   update_image_info.image_instance_key,
                                   update_image_info.image_data,
                                   headers=headers)
        except Exception as exc:
            UploadImageHandler.retry(exc=exc)
        else:
#            call next step
            subtask(callback).delay(update_image_info, update_article_info)
            
        return None
    
    def on_failure(self, exc, task_id, args, kwargs, einfo):
        update_image_info, update_article_info = args
        RollbackImageInDbHandler.delay(update_image_info, update_article_info)
        
        return None


class MarkImagetobedoneHandler(Task):
    """
    alter 'image_tobedone_key' and check whether to mark article as is_ready
    """
    
    ignore_result = True
    
    def run(self, update_image_info, update_article_info):
        decrease_image_tobedone(update_image_info.image_tobedone_key)
        if get_image_tobedone(update_image_info.image_tobedone_key) == 0:
            mark_article_as_done(update_article_info)
            
        return None


class RollbackImageInDbHandler(Task):
    """
    rollback image instance in db
    """
    
    ignore_result = True
    
    def run(self, update_image_info, update_article_info):
        delete_myimage_instance_in_db(update_article_info.user_id,
                                      update_article_info.article_instance_key)
        MarkImagetobedoneHandler.delay(update_image_info, update_article_info)
        
        return None
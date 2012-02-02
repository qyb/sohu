# -*- coding: utf-8 -*-

from article.helper import choose_a_db
from article.models import MyArticleInstance
from constants import BUCKET_NAME_IMAGE, \
    DOWNLOAD_IMAGE_MAX_RETRIES, DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY, \
    UPLOAD_IMAGE_MAX_RETRIES, UPLOAD_IMAGE_DEFAULT_RETRY_DELAY
from image.helper import generate_image_instance_key, \
    decrease_image_tobedone, get_image_tobedone, create_myimage_instance, \
    UpdateImageInfo
from storage.helper import store_data_from_string
from celery.task import Task
from celery.task.sets import subtask
import urllib2
import logging
import celery


class DownloadImageHandler(Task):
    """
    download image
    """
    
    max_retries = DOWNLOAD_IMAGE_MAX_RETRIES
    default_retry_delay = DOWNLOAD_IMAGE_DEFAULT_RETRY_DELAY
    
    def run(self, image_url, image_tobedone_key, update_article_info):
        is_successful = True
        fail_callback = subtask(CheckImagetobedoneHandler)
        update_image_info = UpdateImageInfo(image_url)
        update_image_info.image_tobedone_key = image_tobedone_key
        try:
            resource = urllib2.urlopen(image_url)
            image_data = resource.read()
            try:
                mime = resource.info()['Content-Type']
            except:
                mime = None
            raise
        except Exception as exc:
            is_successful = False
            try:
                DownloadImageHandler.retry(exc=exc)
#            when RetryTaskError happens
            except celery.exceptions.RetryTaskError:
                pass
#            when MaxRetriesExceeded or other exception happens
            except Exception:
                subtask(fail_callback).delay(update_image_info, update_article_info)
        else:
            update_image_info.image_url = image_url
            update_image_info.image_data = image_data
            update_image_info.mime = mime
#            call next step
            StoreImageInfoHandler.delay(update_image_info,
                                        update_article_info,
                                        callback=subtask(UploadImageHandler,
                                        callback=subtask(CheckImagetobedoneHandler),
                                        fail_callback=subtask(fail_callback)),
                                        fail_callback=subtask(fail_callback))
            
        return is_successful    


class StoreImageInfoHandler(Task):
    """
    store image info to local db
    """
    
    def run(self, update_image_info, update_article_info, callback=None, fail_callback=None):
        is_successful = True
        image_instance_key = generate_image_instance_key(update_article_info.article_id,
                                                         update_image_info.image_url)
        try:
            create_myimage_instance(update_article_info.user_id,
                                    image_instance_key,
                                    update_image_info.image_url,
                                    update_article_info.article_id)
        except Exception:
            is_successful = False
            subtask(fail_callback).delay(update_image_info, update_article_info)
        else:
            update_image_info.image_instance_key = image_instance_key
#            call next step
            subtask(callback).delay(update_image_info, update_article_info)
            
        return is_successful
            

class UploadImageHandler(Task):
    """
    upload image to s3
    """
    
    max_retries = UPLOAD_IMAGE_MAX_RETRIES
    default_retry_delay = UPLOAD_IMAGE_DEFAULT_RETRY_DELAY
    
    def run(self, update_image_info, update_article_info, callback=None, fail_callback=None):
        is_successful = True
        try:
            headers = dict()
            if update_article_info.mime:
                headers['Content-Type'] = update_image_info.mime
            store_data_from_string(BUCKET_NAME_IMAGE,
                                   update_image_info.image_instance_key,
                                   update_image_info.image_data,
                                   headers=headers)
        except Exception as exc:
            is_successful = False
            try:
                UploadImageHandler.retry(exc=exc)
#            when RetryTaskError happens
            except celery.exceptions.RetryTaskError:
                pass
#            when MaxRetriesExceeded or other exception happens
            except Exception:
                subtask(fail_callback).delay(update_image_info, update_article_info)
        else:
#            call next step
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

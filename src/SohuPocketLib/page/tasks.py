# -*- coding: utf-8 -*-

from article.helper import generate_article_instance_key, \
    create_myarticle_instance
from celery.task import Task
from celery.task.sets import subtask
from constants import BUCKET_NAME_ARTICLE, PAGE_FETCH_MAX_RETRIES, \
    PAGE_FETCH_DEFAULT_RETRY_DELAY, UPLOAD_ARTICLE_MAX_RETRIES, \
    UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY, PAGE_FETCH_TIME_LIMIT, \
    UPLOAD_ARTICLE_TIME_LIMIT
from image.helper import parse_and_replace_image_url_list, set_image_tobedone, \
    generate_image_tobedone_key
from image.tasks import DownloadImageHandler
from page.helper import delete_html_tag_attribute
from readability.readability import Document
from storage.helper import store_data_from_string
import logging
import urllib2


class PageFetchHandler(Task):
    '''
    fetch a single html page
    '''
    
    time_limit = PAGE_FETCH_TIME_LIMIT
    max_retries = PAGE_FETCH_MAX_RETRIES
    default_retry_delay = PAGE_FETCH_DEFAULT_RETRY_DELAY
    ignore_result = True
    
    def run(self, url, update_article_info):
        is_successful = True
        try:
            resource = urllib2.urlopen(url)
            raw_html = resource.read() 
            try:
                mime = resource.info()['Content-Type']
            except:
                mime = None
        except Exception as exc:
            is_successful = False
            PageFetchHandler.retry(exc=exc)
        else:
            update_article_info.url = url
            update_article_info.mime = mime
#            call next step
            ReadableArticleHandler.delay(raw_html,
                                         update_article_info,
                                         callback=subtask(StoreArticleInfoHandler,
                                         callback=subtask(ImageUrlListHandler,
                                         callback=subtask(UploadArticleHandler,
                                         callback=subtask(BulkImageDownloadHandler,
                                         callback=subtask(DownloadImageHandler))))))
            
        return is_successful


class ReadableArticleHandler(Task):
    '''
    make html readable
    '''
    
    ignore_result = True
    
    def get_title(self, doc):
        
        return doc.short_title()
    
    def get_content(self, doc):
        
        return doc.summary()
    
    def run(self, raw_html, update_article_info, callback=None):
        is_successful = True
        try:
            doc = Document(raw_html)
            article_title = self.get_title(doc)
            article_content = self.get_content(doc)
        except Exception:
            is_successful = False
        else:
            update_article_info.article_title = article_title
            update_article_info.article_content = article_content
#            call next step
            subtask(callback).delay(update_article_info)
            
        return is_successful


class StoreArticleInfoHandler(Task):
    """
    store article info to local db
    """
    
    ignore_result = True
    
    def run(self, update_article_info, callback=None):
        is_successful =  True
        article_instance_key = generate_article_instance_key(update_article_info.url, update_article_info.user_id)
        try:
            article_instance = create_myarticle_instance(update_article_info.user_id, article_instance_key, update_article_info.article_title, update_article_info.url)
            article_id = article_instance.id
        except Exception:
            is_successful = False
        else:
            update_article_info.article_id = article_id
            update_article_info.article_instance_key = article_instance_key
#            call next step
            subtask(callback).delay(update_article_info)
            
        return is_successful


class ImageUrlListHandler(Task):
    """
    parse image url list and replace them with identification in s3
    """
    
    ignore_result = True
    
    def run(self, update_article_info, callback=None):
        is_successful = True
        try:
            image_url_list, new_article_content = parse_and_replace_image_url_list(update_article_info.url, update_article_info.article_content, update_article_info)
        except Exception:
            is_successful = False
        else:
            update_article_info.image_url_list = image_url_list
            update_article_info.article_content = new_article_content
#            call next step
            subtask(callback).delay(update_article_info)
            
        return is_successful


class UploadArticleHandler(Task):
    """
    upload article html to s3
    """
    
    time_limit = UPLOAD_ARTICLE_TIME_LIMIT
    max_retries = UPLOAD_ARTICLE_MAX_RETRIES
    default_retry_delay = UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY
    ignore_result = True
    
    def run(self, update_article_info, callback=None):
        is_successful = True
        try:
            headers = dict()
            if update_article_info.mime:
                headers['Content-Type'] = update_article_info.mime
            store_data_from_string(BUCKET_NAME_ARTICLE,
                                   update_article_info.article_instance_key,
                                   update_article_info.article_content,
                                   headers=headers)
        except Exception as exc:
            is_successful = False
            UploadArticleHandler.retry(exc=exc)
        else:
#            call next step
            subtask(callback).delay(update_article_info)
            
        return is_successful
    
    
class BulkImageDownloadHandler(Task):
    """
    start bulk image download
    """
    
    ignore_result = True
    
    def run(self, update_article_info, callback=None):
        is_successful = True
        try:
            image_tobedone_key = generate_image_tobedone_key(update_article_info.article_id)
            set_image_tobedone(image_tobedone_key, len(update_article_info.image_url_list))
        except Exception:
            is_successful = False
        else:
#            call next step
            for image_url in update_article_info.image_url_list:
                subtask(callback).delay(image_url, image_tobedone_key, update_article_info)
        
        return is_successful

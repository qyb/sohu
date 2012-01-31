# -*- coding: utf-8 -*-

from article.helper import generate_article_instance_key, \
    create_myarticle_instance
from constants import BUCKET_NAME_ARTICLE
from image.helper import parse_and_replace_image_url_list, \
    set_image_tobedone, generate_image_tobedone_key
from image.tasks import DownloadImageHandler
from page.helper import delete_html_tag_attribute
from storage.helper import store_data_from_string
from celery.task import Task
from readability.readability import Document
import urllib2
import logging


class PageFetchHandler(Task):
    '''
    fetch a single html page
    '''
    
#    info is a dict, used for arguments simplicity
#    info here must include key 'user_id'
    def run(self, url, update_article_info):
        is_successful = True
        try:
            raw_html = urllib2.urlopen(url).read()
        except IOError:
            is_successful = False
            raise
        else:
            update_article_info.url = url
#            call next step
            ReadableArticleHandler.delay(raw_html, update_article_info)
            
        return is_successful


class ReadableArticleHandler(Task):
    '''
    make html readable
    '''
    
    def get_title(self, doc):
        
        return doc.short_title()
    
    def get_content(self, doc):
        
        return doc.summary()
    
    def run(self, raw_html, update_article_info):
        is_successful = True
        try:
            doc = Document(raw_html)
            article_title = self.get_title(doc)
            article_content = self.get_content(doc)
        except Exception:
            is_successful = False
            raise
        else:
            update_article_info.article_title = article_title
            update_article_info.article_content = article_content
#            call next step
            StoreArticleInfoHandler.delay(update_article_info)
            
        return is_successful


class StoreArticleInfoHandler(Task):
    """
    store article info to local db
    """
    
    def run(self, update_article_info):
        is_successful =  True
        article_instance_key = generate_article_instance_key(update_article_info.url, update_article_info.user_id)
        try:
            article_instance = create_myarticle_instance(update_article_info.user_id, article_instance_key, update_article_info.article_title, update_article_info.url)
            article_id = article_instance.id
        except Exception:
            is_successful = False
            raise
        else:
            update_article_info.article_id = article_id
            update_article_info.article_instance_key = article_instance_key
#            call next step
            ImageUrlListHandler.delay(update_article_info)
            
        return is_successful


class ImageUrlListHandler(Task):
    """
    parse image url list and replace them with identification in s3
    """
    
    def run(self, update_article_info):
        is_successful = True
        try:
            image_url_list, new_article_content = parse_and_replace_image_url_list(update_article_info.url, update_article_info.article_content, update_article_info)
        except Exception:
            is_successful = False
            raise
        else:
            update_article_info.image_url_list = image_url_list
            update_article_info.article_content = new_article_content
#            call next step
            UploadArticleHandler.delay(update_article_info)
            
        return is_successful


class UploadArticleHandler(Task):
    """
    upload article html to s3
    """
    
    def run(self, update_article_info):
        is_successful = True
        try:
            store_data_from_string(BUCKET_NAME_ARTICLE, update_article_info.article_instance_key, update_article_info.article_content)
        except Exception:
            is_successful = False
        else:
#            call next step
            BulkImageDownloadHandler.delay(update_article_info)
            
        return is_successful
    
    
class BulkImageDownloadHandler(Task):
    """
    start bulk image download
    """
    
    def run(self, update_article_info):
        is_successful = True
        try:
            image_tobedone_key = generate_image_tobedone_key(update_article_info.article_id)
            set_image_tobedone(image_tobedone_key, len(update_article_info.image_url_list))
        except Exception:
            is_successful = False
            raise
        else:
#            call next step
            for image_url in update_article_info.image_url_list:
                DownloadImageHandler.delay(image_url, image_tobedone_key, update_article_info)
        
        return is_successful

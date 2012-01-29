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
    def run(self, url, info):
        is_successful = True
        try:
            raw_html = urllib2.urlopen(url).read()
        except IOError:
            is_successful = False
            raise
        else:
            info['url'] = url
#            call next step
            ReadableArticleHandler.delay(raw_html, info)
            
        return is_successful


class ReadableArticleHandler(Task):
    '''
    make html readable
    '''
    
    def get_title(self, doc):
        
        return doc.short_title()
    
    def get_content(self, doc):
        
        return doc.summary()
    
    def run(self, raw_html, info):
        is_successful = True
        try:
            doc = Document(raw_html)
            article_title = self.get_title(doc)
            article_content = self.get_content(doc)
        except Exception:
            is_successful = False
            raise
        else:
            info['article_title'] = article_title
            info['article_content'] = article_content
#            call next step
            StoreArticleInfoHandler.delay(info)
            
        return is_successful


class StoreArticleInfoHandler(Task):
    """
    store article info to local db
    """
    
    def run(self, info):
        is_successful =  True
        article_instance_key = generate_article_instance_key(info['url'], info['user_id'])
        try:
            article_instance = create_myarticle_instance(info['user_id'], article_instance_key, info['article_title'], info['url'])
            article_id = article_instance.id
        except Exception:
            is_successful = False
            raise
        else:
            info['article_id'] = article_id
            info['article_instance_key'] = article_instance_key
#            call next step
            ImageUrlListHandler.delay(info)
            
        return is_successful



class ImageUrlListHandler(Task):
    """
    parse image url list and replace them with identification in s3
    """
    
    def run(self, info):
        is_successful = True
        try:
            image_url_list = parse_and_replace_image_url_list(info['url'], info['article_content'], info)
        except Exception:
            is_successful = False
            raise
        else:
            info['image_url_list'] = image_url_list
#            call next step
            UploadArticleHandler.delay(info)
            
        return is_successful


class UploadArticleHandler(Task):
    """
    upload article html to s3
    """
    
    def run(self, info):
        is_successful = True
        try:
            store_data_from_string(BUCKET_NAME_ARTICLE, info['article_instance_key'], info['article_content'])
        except Exception:
            is_successful = False
        else:
#            call next step
            BulkImageDownloadHandler.delay(info)
            
        return is_successful
    
    
class BulkImageDownloadHandler(Task):
    """
    start bulk image download
    """
    
    def run(self, info):
        is_successful = True
        try:
            image_tobedone_key = generate_image_tobedone_key(info['user_id'])
            set_image_tobedone(image_tobedone_key, len(info['image_url_list']))
        except Exception:
            is_successful = False
            raise
        else:
#            call next step
            for image_url in info['image_url_list']:
                DownloadImageHandler.delay(image_url, image_tobedone_key, info)
        
        return is_successful

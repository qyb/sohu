# -*- coding: utf-8 -*-
'''
Created on 2012-1-11

@author: diracfang
'''
from SohuPocketLib.article.helper import delete_html_tag_attribute, \
    create_myarticle_instance, parse_and_replace_image_url_list
from SohuPocketLib.constants import *
from SohuPocketLib.storage.helper import store_data_from_string
from celery.task import Task
from django.core.cache import cache
from readability.readability import Document
import hashlib
import urllib2
from SohuPocketLib.article.models import MyArticleInstance

class PageFetchHandler(Task):
    '''
    Page Fetch Handler
    '''
    def single_page(self, url):
        page = ''
        try:
            page = urllib2.urlopen(url).read()
        except IOError:
            page = None
            
        return page
    
    def run(self, url, user_id):
        is_successful = True
        try:
            raw_html = self.single_page(url)
#            call next process
            ReadableArticleHandler.delay(url, raw_html, user_id)
        except Exception:
            is_successful = False
            
        return is_successful 

class ReadableArticleHandler(Task):
    '''
    translate html into readable article
    '''
    def feed(self, raw_html):
        self.doc = Document(raw_html)
        
        return None
        
    def get_readable_title(self):
        
        return self.doc.short_title()
    
    def get_readable_article(self):
        
        return delete_html_tag_attribute(self.doc.summary())
    
    def run(self, url, raw_html, user_id):
        is_successful = True
        try:
            self.doc = raw_html
            title = self.get_readable_title()
            article = self.get_readable_article()
#            call next process
            hash_source = url
            key = hashlib.new('sha1', hash_source).hexdigest()
            global_key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
            
            store_data_from_string(ARTICLE_BUCKET_NAME, global_key, article)
            article_instance = create_myarticle_instance(user_id, global_key, title, url)
            article_id = article_instance.id
                        
            image_url_list = parse_and_replace_image_url_list(article, user_id)
            image_left_count_key = CAHCE_KEY_IMAGE_LEFT_COUNT % global_key 
            cache.set(image_left_count_key, len(image_url_list))
            for image_url in image_url_list:
                DownloadAndSaveImageHandler.delay(image_url, user_id, article_id, image_left_count_key)
        except Exception:
            is_successful = False
        
        return is_successful
    
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
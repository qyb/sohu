# -*- codiing: utf-8 -*-

from SohuPocketLib.article.helper import delete_html_tag_attribute, \
    create_myarticle_instance, parse_and_replace_image_url_list
from SohuPocketLib.constants import CACHE_KEY_USER_ARTICLE_INSTANCE, \
    ARTICLE_BUCKET_NAME, CAHCE_KEY_IMAGE_LEFT_COUNT
from SohuPocketLib.image.tasks import DownloadAndSaveImageHandler
from SohuPocketLib.storage.helper import store_data_from_string
from celery.task import Task
from django.core.cache import cache
from readability.readability import Document
import hashlib
import urllib2


class PageFetchHandler(Task):
    '''
    fetch a single html page
    '''
    
    def run(self, url, user_id):
        is_successful = True
        try:
            raw_html = urllib2.urlopen(url).read()
        except IOError:
            is_successful = False
#            call next process
        else:
            ReadableArticleHandler.delay(url, raw_html, user_id)
            
        return is_successful


class ReadableArticleHandler(Task):
    '''
    translate html into readable article
    '''
    
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
                DownloadAndSaveImageHandler.delay(image_url, user_id, article_id,
                                                  image_left_count_key)
                PageFetchHandler.delay()
        except Exception:
            is_successful = False
        
        return is_successful

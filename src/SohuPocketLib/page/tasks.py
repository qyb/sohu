# -*- coding: utf-8 -*-

from SohuPocketLib.article.helper import generate_article_instance_key, \
    create_myarticle_instance
from SohuPocketLib.constants import BUCKET_NAME_ARTICLE
from SohuPocketLib.image.helper import parse_and_replace_image_url_list, \
    set_image_tobedone, generate_image_tobedone_key
from SohuPocketLib.image.tasks import DownloadImageHandler
from SohuPocketLib.page.helper import delete_html_tag_attribute
from SohuPocketLib.storage.helper import store_data_from_string
from celery.task import Task
from readability.readability import Document
import urllib2


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
        else:
#            call next step
            info['url'] = url
            ReadableArticleHandler.delay(raw_html, info)
            
        return is_successful


class ReadableArticleHandler(Task):
    '''
    make html readable
    '''
    
    def get_title(self, doc):
        
        return doc.short_title()
    
    def get_content(self, doc):
        
        return delete_html_tag_attribute(doc.summary())
    
    def run(self, raw_html, info):
        is_successful = True
        try:
            doc = Document(raw_html)
            article_title = self.get_title(doc)
            article_content = self.get_article(doc)
        except Exception:
            is_successful = False
        else:
#            call next step
            info['article_title'] = article_title
            info['article_content'] = article_content
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
        else:
            info['image_url_list'] = image_url_list
#            call next step
            StoreArticleHandler.delay(info)
            
        return is_successful


class StoreArticleHandler(Task):
    """
    store article html to s3, write record to local db
    """
    
    def run(self, info):
        is_successful = True
        article_instance_key = generate_article_instance_key(info['url'], info['user_id'])
        try:
            store_data_from_string(BUCKET_NAME_ARTICLE, article_instance_key, info['article_content'])
            article_instance = create_myarticle_instance(info['user_id'], article_instance_key, info['article_title'], info['url'])
            article_id = article_instance.id
        except Exception:
            is_successful = False
        else:
            info['article_id'] = article_id
            info['article_instance_key'] = article_instance_key
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
        else:
#            call next step
            for image_url in info['image_url_list']:
                DownloadImageHandler.delay(image_url, image_tobedone_key, info)
        
        return is_successful

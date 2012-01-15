# -*- coding: utf-8 -*-
'''
Created on 2012-1-11

@author: diracfang
'''
import urllib2

from celery.task import Task
from readability.readability import Document

from article.models import RawHtml
from article import helper

class PageFetchHandler(Task):
    '''
    Page Fetch Handler
    '''
    
    def __init__(self):
        '''
        Constructor
        '''
        pass
    
    def single_page(self, url):
        '''
        fetch a single page, no cookies, etc.
        '''
        page = ''
        try:
            page = urllib2.urlopen(url).read()
        except IOError:
            page = None
            
        return page
    
    def run(self, url):
        is_successful = True
        try:
            raw_html = self.single_page(url)
#            call next process
            ReadableArticleHandler.delay(raw_html)
        except Exception:
            is_successful = False
            
        return is_successful 

class ReadableArticleHandler(Task):
    '''
    translate html into readable article
    usage:
        rah = ReadableArticleHandler()
        rah.feed('<html><title>test</title><body>test body</body></html>')
        print rah.get_readable_title()
        print rah.get_readable_article()
    '''

    def __init__(self):
        '''
        Constructor
        '''
        pass
        
    def feed(self, raw_html):
        '''
        feed raw_html into the parser
        '''
        self.doc = Document(raw_html)
        
        return None
        
    def get_readable_title(self):
        '''
        get readable title
        '''
        
        return self.doc.short_title()
    
    def get_readable_article(self):
        '''
        get readable article
        '''
        
        return helper.delete_html_tag_attribute(self.doc.summary())
    
    def run(self, raw_html):
        is_successful = True
        try:
            self.doc = raw_html
            title = self.get_readable_title()
            article = self.get_readable_article()
#            call next process
            article_id = create_article_instance(title, article)
            image_list = ImageListHandler.delay(article)
            for image in image_list:
                DownloadAndSaveImageHandler.delay(image, article_id)
        except Exception:
            is_successful = False
        
        return is_successful
    
class DownloadAndSaveImageHandler(Task):
    """
    handler that download a image, save to s3, write record to local db
    """
    def run(self, image, article_id):
        pass
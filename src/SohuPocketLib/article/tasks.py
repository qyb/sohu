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
            html = self.single_page(url)
            raw_html = RawHtml(content = html)
            raw_html.save()
            ReadableArticleHandler.delay(raw_html.id)
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
    
    def run(self, raw_html_id):
        is_successful = True
        try:
            self.doc = RawHtml.objects.get(id = raw_html_id)
            title = self.get_readable_title()
            article = self.get_readable_article()
            print title
        except Exception:
            is_successful = False
        
        return is_successful
# -*- coding: utf-8 -*-
'''
Created on 2012-1-11

@author: diracfang
'''
import urllib2
from celery.task import Task

class PageFetcher(Task):
    '''
    a simple Page Fetcher
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
            page = '404 not found'
        return page
    
    def run(self, url):
        result = self.single_page(url)
        return result

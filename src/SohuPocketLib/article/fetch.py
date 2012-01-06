'''
Created on 2012-1-6

@author: diracfang
'''
import urllib2

class PageFetcher(object):
    '''
    fetch page from page
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
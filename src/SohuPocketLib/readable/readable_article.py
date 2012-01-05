'''
Created on 2012-1-5

@author: diracfang
'''
from readability.readability import Document
from celery.task import task

class ReadableArticle(object):
    '''
    translate html into readable article
    usage:
        ra = ReadableArticle()
        ra.feed('<html><title>test</title><body>test body</body></html>')
        print ra.get_readable_title()
        print ra.get_readable_article()
    '''


    def __init__(self):
        '''
        Constructor
        '''
        pass
        
    @task
    def feed(self, raw_html):
        '''
        feed raw_html into the parser
        '''
        self.doc = Document(raw_html)
        
    def get_readable_title(self):
        '''
        get readable title
        '''
        return self.doc.short_title()
    
    def get_readable_article(self):
        '''
        get readable article
        '''
        return self.doc.summary()
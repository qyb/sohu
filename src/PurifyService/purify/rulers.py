'''
Created on 2012-2-29

@author: Leon

'''
#-*-encoding=gbk-*-

import logging

from crawler        import Crawler
from interfaces     import IFilter

from readability.readability import Document

class ReadabilityRuler(IFilter):
    '''
        according domain and uri, specially deal with some sites.
        main target is to retrieve important content of the article.
    '''
    def __init__(self):
        IFilter.__init__(self)
        self.logger = logging.getLogger("Readability")

    def onProcess(self, request):
        self.logger.debug("Ruler process: content: %s " % request.content)
        try:
            if not request.content or len(request.content) == 0: 
                #need crawler this content for purify.
                request.content = Crawler().download(request.domain)
                #self.logger.debug(request.content)
                
            # readability deal content
            request.result = Document(request.content).summary()
        except Exception, e:
            self.logger.error("Ruler process Error: %s" % e)
            request.error = e

    
    
    

'''
Created on 2012-2-29

@author: Leon

'''

import urllib

class Crawler:
    '''
        various crawlers maybe. later we could judge it by domain name
    '''
    def __init__(self):
        pass
    
    def download(self, uri):
        txt = urllib.urlopen(uri).read()
        return txt




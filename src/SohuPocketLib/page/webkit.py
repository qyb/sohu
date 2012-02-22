# -*- coding: utf-8 -*-
import sys
import urllib2
from PyQt4.QtGui import *
from PyQt4.QtCore import *
from PyQt4.QtWebKit import *
from PyQt4.QtNetwork import QNetworkProxy
from PyQt4.QtNetwork import QNetworkAccessManager

class PageBrower(QWebPage):  
    
    def __init__(self):  
        QWebPage.__init__(self)  

        self.networkAccessManager = QNetworkAccessManager()
        self.networkNoProxy = QNetworkProxy(QNetworkProxy.NoProxy)
        self.networkProxy = QNetworkProxy(QNetworkProxy.HttpProxy,"127.0.0.1",8087)

        self.setNetworkAccessManager(self.networkAccessManager)
        self.loadFinished.connect(self._loadFinished)  

    def installProxy(self):
        self.networkAccessManager.setProxy(self.networkProxy)

    def uninstallProxt(self):
        self.networkAccessManager.setProxy(self.networkNoProxy)

    def getErrorCode(self,url):
        ret = False
        req = urllib2.Request(url) 

        try:
            urllib2.urlopen(url)
        except IOError, e:
            if hasattr(e,'reason'):
                print "%s failed to be reached due to %s" % (url,e.reason)
                ret = True
            elif hasattr(e,'code'):
                print " %d :The server could not fulfill the request" % (e.code)
                ret = True
            else:
                print "unknow error type"
                ret = True

        return ret 

    def crawl(self,url):

        if self.getErrorCode(url) == False:
            self.uninstallProxt()
            print "No proxy"
        else:
            self.installProxy()
            print "With Proxy"

        self.mainFrame().load(QUrl(url))

    def _loadFinished(self, result):  
        print self.mainFrame().toHtml()

def main():
    app = QApplication(sys.argv)
    url1 = 'http://www.baidu.com'
    url2 = 'http://www.twitter.com'
    url3 = 'http://www.qidian.com/BookReader/2203096,36693998.aspx'  

    pageBrower = PageBrower()  
    pageBrower.crawl(url2)
    #pageBrower.crawl(url3)

    sys.exit(app.exec_())

if __name__ == "__main__":
    main()

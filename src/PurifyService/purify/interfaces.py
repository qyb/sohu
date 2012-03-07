'''
Created on 2012-2-29

@author: Leon

'''

class IFilter:
    def __init__(self):
        self.next   = None
        
    def handle(self, request):
        if request.getError():
            if self.next:
                self.next.process(request)

        try:
            self.logger.debug("\033[32m=============== begin ================\033[0m")
            self.onProcess(request)
            self.logger.debug("\033[32m=============== end ================\033[0m")
        finally:
            if self.next:
                print "<<< call next >>>"
                self.next.handle(request)

    def onProcess(self, request):
        pass
    
    def setNext(self, ifilter):
        self.next = ifilter
        
    def getNext(self):  return self.next
        




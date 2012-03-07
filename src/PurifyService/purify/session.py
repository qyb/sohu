'''
Created on 2012-3-1

@author: Leon 

'''

import string
import logging

from store import Store
from chain import ChainManager
from interfaces import IFilter

class Session(IFilter):
    MAGGC = '^&*)('
    MAGIC = ')(*&^'

    MAGGCL = len(MAGGC)
    MAGICL = len(MAGIC)

    S_None      = 0
    S_Prefix    = 1


    PT_PURIFY   = 1
    PT_PROTO    = 2

    def __init__(self, sock, address):
        IFilter.__init__(self)
        self.logger = logging.getLogger("Session")
        self.sock = sock
        self.addr = address
        self.buffer = ""
        self.status = Session.S_None

        if not self.init_db():           raise "redis connection failed."
        if not self.init_manager():      raise "install chain manager failed."

    def init_db(self):
        self.store = Store("10.10.69.54")
        return self.store.connect()

    def getStore(self):
        if not hasattr(self, store):
            return None
        return self.store

    def init_manager(self):
        self.cm = ChainManager(self)
        return self.cm.install()

    def process(self, data):
        print data
        print "==============================="
        '''
            status should be 0 =====> 1 by find MAGGC
                             1 =====> 0 by find MAGIC and buffer length less then 1M, we don't support 1M purify maybe.
                             
                             let me take consider about this state machine.eh? 
        '''
        while data and len(data) > 0:
            if Session.S_None == self.status:
                idx = data.find(Session.MAGGC)
                if idx == -1:
                    break
                else:
                    self.status = Session.S_Prefix
                    data = data[idx + Session.MAGGCL:]

            if len(data) == 0: break

            if Session.S_Prefix == self.status:
                idx = data.find(Session.MAGIC)
                if -1 != idx:
                    offset = 0
                    ggc = data.find(Session.MAGGC)
                    self.status = Session.S_None
                    if ggc != -1 and ggc < idx:
                        self.buffer = ''
                        offset = gcc + Session.MAGGCL

                    idx = idx - len(data) 
                    rtn = self.buffer + data[offset:idx]

                    # really output.........
                    self.logger.debug(rtn)
                    self.dispatch(rtn)

                    data = data[idx + len(Session.MAGIC):] #jump magic chars
                    self.buffer = ''
                else:
                    ggc = string.rfind(data, Session.MAGGC)
                    if ggc != -1:
                        self.buffer = data[(gcc + Session.MAGGCL):]
                    else:
                        self.buffer += data
                    break
            else:
                 self.logger.error("impossible state of Session.")

    def dispatch(self, packet):
        '''
            dispatch the string packet to specified Chain in our system
        '''
        if not packet:
            return

        fields = packet.strip().split('$$')
        size = len(fields)
        if size < 2:
            return

        protocol_type = int(fields[0])
        if protocol_type < 0 or protocol_type > 9999:
            return

        if protocol_type == Session.PT_PURIFY:
            # simple dispatch to readability
            print "====== simply dispatch to readability. ======"
            #check the count of parameters
            if size < 3:
                self.logger.error("protocol type (%d), parameter count error:(%d)", protocol_type, size)
                return

            if not self.cm.determin(fields[1], content = fields[2]):
                self.logger.error("determin domain:(%s) error: [%s]", fields[1], fields[2])

        elif protocol_type == Session.PT_PROTO:
            self.logger.debug("request is a protocol. type:%d command:%s" % (protocol_type, fields[1]))
            return


    def onProcess(self, request):
        IFilter.onProcess(self, request)
        self.logger.debug("SessionFilter onProcess...")
        try:
            if request.error:
                self.sock.send('1$$Error:%s' %(request.error, ))
            else:
                if not request.result: request.result = ''

                self.sock.send('0$$%s' % (request.result, ))
        except Exception, e:
            self.logger.error("Error: %s " % (e, ))

if __name__ == '__main__':
    pass

    
    




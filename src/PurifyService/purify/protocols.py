'''
Created on 2012-3-1

@author: Leon 

'''
#-*-encoding=gbk-*-
import os, sys
import logging
import select
from socket import *
from multiprocessing import Process
from multiprocessing import reduction

from session import Session

class IPC:
    PACKET_LENGTH = 65535  #it's just ip packet max length, but in real , we may penetreat routers, that may be limited by MTU, usually be 1460, or 1452 on PPPoE, by Leon

    def __init__(self, address, backlog = 5):
        self.processes = []
        self.logger = logging.getLogger("IPC")
        
        try:
            self.socket = socket(AF_INET, SOCK_STREAM)
            self.socket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
            
            #flag = fcntl.fcntl(self.socket.fileno(), fcntl.F_GETFL)
            #fcntl.fcntl(self.socket.fileno(), fcntl.F_SETFL, flag | os.O_NONBLOCK)
            
            self.socket.bind(address)
            self.socket.listen(backlog)
        except Exception, e:
            self.logger.error("processing.connection Listener construct failed: [%s]" % (e, ))
                
    def run(self, sock, addr):
        try:
            self.logger.debug('\033[31mcreating and running Process [%s]\033[0m' % str(addr))
            session = Session(sock, addr)
            while True:
                rtn = sock.recv(IPC.PACKET_LENGTH)		# should be block when recv
                if len(rtn) > 0:
                    session.process(rtn)
                else:
                    self.logger.debug('socket eof.')
                    sock.close()
                    break
        except Exception, e:
            self.logger.error("Worker Process error: [%s]" % e)
            sock.close
        self.logger.debug("worker[%s] end " % (addr, ))
            
    
    def loop(self):
        '''
            it seems multiprocessing does not support epoll or select.
        '''
        try:
            while True:
                infds, outfds, errfds = select.select([self.socket], [], [], 5)
                if len(infds) > 0:
                    clientsocket, clientaddr = self.socket.accept()
                    self.logger.debug("\033[31mAccept a brand new connection: %s\033[0m" % (str(clientaddr), ))
                    #
                    # pass informations to sub-process
                    #
                    #
                    if sys.platform == "linux2":
                        process = Process(target = self.run, args = (clientsocket, clientaddr,))
                        self.processes.append(process)
                        process.start()
                    else:
                        #self.logger.debug("Don\' support windows now!")
                        self.run(clientsocket, clientaddr)
                        break
                        
                elif len(infds) == 0:
                    #self.logger.debug("select result zero event occured.")
                    pass
                else:
                    self.logger.error("select error!")
                    break
                
                for i in xrange(len(self.processes)):
                    process = self.processes[i]
                    if not process.is_alive():
                        process.join()
                        del self.processes[i]

        except Exception, e:
            self.logger.error("IPC Loop error [%s]" % e)
            return False
        return True
    

if __name__ == "__main__":
    ipc = IPC(("localhost", 7000))
    ipc.loop()
    





#! /usr/local/bin/python
#-*-encoding=gbk-*-

from __future__ import with_statement 
import os, sys
import platform
import logging.config

from optparse import OptionParser

from protocols import IPC

class PS:
    
    def __init__(self, options):
        self.logfile    = options.logfile
        self.daemon     = options.daemon
        
    def init_logger(self, key = 'purify'):
        '''
            initialize logger when parent process started.
        '''
        logging.config.fileConfig(self.logfile)

        self.logger     = logging.getLogger(key)
        if not self.logger:
            print "Fatal: initiate logger system failed!"
            return False
            
        self.logger.debug("\033[31mLogger system start successfully!\033[0m")
        return True
   
    def init_protocol(self):
        self.ipc = IPC(('localhost', 6000))
        if not self.ipc:
            self.logger.error("IPC protocol module load failed.")
            return False
        
        if not self.ipc.loop():
            self.logger.error("IPC protocol module running exceptionally.")
            return False
        
        return True
    
    def start_server(self):
        if self.daemon and platform.system() == "Linux":
            try:
                # first fork child process, then kill parent
                pid = os.fork()
                if pid > 0:
                    sys.exit(0)
            except OSError, e: 
                print >> sys.stderr, "fork #1 failed: %d (%s)" % (e.errno, e.strerror)
                sys.exit(1)
                
            os.chdir('/')
            os.setsid()
            os.umask(0)
            
            try:
                # second fork to avoid some OS bug.
                pid = os.fork()
                if pid > 0:
                    open('/var/run/purifier.pid', 'w').write("%d" % pid).close()
                    sys.exit(0)
            except OSError, e: 
                print >> sys.stderr, "fork #2 failed: %d (%s)" % (e.errno, e.strerror)
                sys.exit(1)
                
        FILENAME = "logger.conf"
        if not self.logfile:
            self.logfile = FILENAME 
            
        try: 
            print 1
            if not self.init_logger():      return False
            if not self.init_protocol():    return False
        except Exception, e:
            self.logger.error("Exceptin: [%s]" % (e, ))
            return False
            

if __name__ == "__main__":
    '''
        testbed.py        
    '''
    parser = OptionParser(usage = "%prog: [options] [file]")
    parser.add_option('-v', '--verbose' , action = "store_true")
    parser.add_option('-l', '--logfile' , action = "store")
    parser.add_option('-t', '--target'  , action = "store")
    parser.add_option('-d', '--daemon'  , action = "store_true")
    
    (options, args) = parser.parse_args(); #print options
    
    ps = PS(options)
    ps.start_server()
    


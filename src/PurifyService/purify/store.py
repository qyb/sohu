'''
Created on 2012-2-29

@author: Leon
'''

import redis
import logging

class Store:
    gLogger = None
    
    def __init__(self, host, port = 6379, db = 0):
        self.logger = logging.getLogger("Store")
        self.handle = None
        self.host = host
        self.port = port
        self.db   = db
        
    def connect(self):
        self.redis = redis.Redis(host = self.host, port = self.port, db = self.db)
        if not self.redis:
            self.logger.error("redis connect faild. host:" + self.host + ":" + self.port)
            return False
        
        #print self.redis.keys("*")

        self.logger.debug("\033[31mRedis connection established!\033[0m")
        return True
            
        
        
    
    
    
    

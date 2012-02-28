# -*- coding: utf-8 -*-


class KanError(object):
    """
    define errors in sohukan
    """
    
    errors = {
              '1040': 'Rate-limit exceeded',
              
              }
    
    def __init__(self, code):
        self.code = code
        
    def get_message(self):
        return self.errors
        
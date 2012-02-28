# -*- coding: utf-8 -*-


class KanError(object):
    """
    define errors in sohukan
    """
    
    error_messages = {
              '1040': 'Rate-limit exceeded',
              '1041': 'Subscription account required',
              '1042': 'Application is suspended',
              '1220': 'Domain requires full content to be supplied',
              '1221': 'Domain has opted out of Instapaper compatibility',
              '1240': 'Invalid URL specified',
              '1241': 'Invalid or missing bookmark_id',
              '1242': 'Invalid or missing folder_id',
              '1243': 'Invalid or missing progress',
              '1244': 'Invalid or missing progress_timestamp',
              '1245': 'Private bookmarks require supplied content',
              '1246': 'Unexpected error when saving bookmark',
              '1250': 'Invalid or missing title',
              '1251': 'User already has a folder with this title',
              '1252': 'Cannot add bookmarks to this folder',
              '1500': 'Unexpected service error',
              '1550': 'Error generating text version of this URL',
              }
    
    def __init__(self, code):
        self.code = code
        
    def get_code(self):
        return self.code
        
    def get_message(self):
        return self.error_messages[self.code]

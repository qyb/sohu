# -*- coding: utf-8 -*-
'''
Created on 2012-1-8

@author: diracfang
'''
import hashlib
import time
from models import User, Access

class KanUser(object):
    """
    wrapper around User
    """
    def __init__(self, sohupassport_uuid, access_token_input):
        """
        constructor
        """
        self.sohupassport_uuid = sohupassport_uuid
        self.access_token_input = access_token_input
        self.access_token = ''
        self.user = None
        self.is_logged_in = False
        
    def login(self):
        """
        if successfully log in, returns true, self.is_logged_in self.access_token self.user will be updated
        if not, returns false
        """
        if self.sohupassport_uuid and not self.access_token_input:
            self.user = self._retrieve_or_create_user_from_uuid()
            self.access_token = self._create_access_token()
            self.is_logged_in = True
        elif self.access_token_input:
            self.user = self._retrieve_user_from_access_token_input()
            if self.user:
                self.is_logged_in = True
                self.access_token = self.access_token_input
        return self.is_logged_in
                
    def _create_access_token(self):
        hash_source = self.sohupassport_uuid + str(time.time())
        access_token_string = hashlib.new('sha1', hash_source).hexdigest()
        Access.objects.create(
                              access_token = access_token_string,
                              user = self.user,
                              )
        return access_token_string
    
    def _retrieve_or_create_user_from_uuid(self):
        try:
            user = User.objects.get(sohupassport_uuid = self.sohupassport_uuid)
        except User.DoesNotExist:
            user = User.objects.create(
                                       sohupassport_uuid = self.sohupassport_uuid,
                                       kan_username = '',
                                       kan_self_description = '',
                                       )
        return user
        
    def _retrieve_user_from_access_token_input(self):
        try:
            user = Access.objects.get(access_token = self.access_token_input).user
        except Access.DoesNotExist:
            return None
        else:
            return user
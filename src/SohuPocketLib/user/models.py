# -*- coding: utf-8 -*-

from django.db import models


class User(models.Model):
    """
    implement kan user
    """
    
    sohupassport_uuid = models.CharField(max_length=128)
    kan_username = models.CharField(max_length=256, blank=True)
    kan_self_description = models.CharField(max_length=1024, blank=True)
    
    def __unicode__(self):
        return self.sohupassport_uuid
    
class Access(models.Model):
    """
    keep record of permanent access to kan from mobile device or web
    """
    
    access_token = models.CharField(max_length=256)
    user = models.ForeignKey(User)
    
    def __unicode__(self):
        return self.access_token
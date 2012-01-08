# -*- coding: utf-8 -*-

from django.db import models

# Create your models here.
class User(models.Model):
    """
    implement kan user
    """
    sohupassport_uuid = models.CharField(max_length = 128)
#    user defined, more to be comlemented
    kan_username = models.CharField(max_length = 256)
    kan_self_description = models.CharField(max_length = 1024)
    
class Access(models.Model):
    """
    keep record of permanent access to kan from mobile device or web
    """
    access_token = models.CharField(max_length = 256)
    user = models.ForeignKey(User)
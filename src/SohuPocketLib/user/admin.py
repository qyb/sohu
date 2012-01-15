# -*- coding: utf-8 -*-
'''
Created on 2012-1-12

@author: diracfang
'''
from django.contrib import admin
from user.models import User, Access

admin.site.register(User)
admin.site.register(Access)
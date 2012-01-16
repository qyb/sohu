# -*- coding: utf-8 -*-

from django.contrib import admin
from user.models import User, Access

admin.site.register(User)
admin.site.register(Access)
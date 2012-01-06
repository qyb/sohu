# -*- coding: utf-8 -*-
'''
Created on 2011-12-29

@author: diracfang
'''
from django.http import HttpResponse

def passport(request):
    info = ['HTTP_X_SOHUPASSPORT_UID',
            'HTTP_X_SOHUPASSPORT_USERID',
            'HTTP_X_SOHUPASSPORT_UUID']
    page = '<br />'.join([i + ' : ' + str(request.META.get(i, '')) for i in info])
    return HttpResponse(page)

def home(request):
    return HttpResponse('welcome home!!!')
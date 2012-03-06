# -*- coding: utf-8 -*-

from django.http import HttpResponse, HttpResponsePermanentRedirect


def passport(request):
    info = ['HTTP_X_SOHUPASSPORT_UID',
            'HTTP_X_SOHUPASSPORT_USERID',
            'HTTP_X_SOHUPASSPORT_UUID']
    page = '<br />'.join([i + ' : ' + str(request.META.get(i, '')) for i in info])
    return HttpResponse(page)

def home(request):
    return HttpResponse('Welcome to sohukan!')
#    return HttpResponsePermanentRedirect('http://www.google.com')
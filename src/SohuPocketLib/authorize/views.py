# Create your views here.
from django.shortcuts import render_to_response
from models import User
import time
import hashlib

def user_info(request):
    """simple demo page shows how to use sohupassport for authorization"""
    context = dict()
    should_set_cookies = False
    context['sohupassport_uuid'] = str(request.META.get('HTTP_X_SOHUPASSPORT_UUID', ''))
    context['kan_certification'] = request.COOKIES.get('kan_certification', '')
    
    # with uuid, we create new kan_certification
    if context['sohupassport_uuid'] and not context['kan_certification']:
        should_set_cookies = True
        hash_obj = hashlib.new('sha1', context['sohupassport_uuid'] + str(time.time()))
        context['kan_certification'] = hash_obj.hexdigest()
        queryset = User.objects.filter(sohupassport_uuid = context['sohupassport_uuid'])
        if len(queryset) == 0:
            User.objects.create(sohupassport_uuid = context['sohupassport_uuid'], kan_certification = context['kan_certification'])
        else:
            queryset.update(kan_certification = context['kan_certification'])
    # with kan_certification, we retrieve uuid from db
    elif not context['sohupassport_uuid'] and context['kan_certification']:
        queryset = User.objects.filter(kan_certification = context['kan_certification'])
        if len(queryset) != 0:
            context['sohupassport_uuid'] = queryset[0].sohupassport
            
    response = render_to_response('user_info.html', context)
    
    if should_set_cookies:
        response.set_cookie('kan_certification', context['kan_certification'])
    return response

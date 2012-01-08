# -*- coding: utf-8 -*-
# Create your views here.
from django.shortcuts import render_to_response
from django.http import HttpResponse
from helper import KanUser

def login(request):
    sohupassport_uuid, access_token_input = get_user_info_for_web(request)
    kan_user = KanUser(sohupassport_uuid, access_token_input)
    is_logged_in = kan_user.login()
    if is_logged_in:
        response = HttpResponse(kan_user.sohupassport_uuid)
        set_user_info_for_web(response, kan_user.user.sohupassport_uuid, kan_user.access_token)
    else:
        response = HttpResponse('not logged in')
    return response
    
def get_user_info_for_web(request):
    sohupassport_uuid = str(request.META.get('HTTP_X_SOHUPASSPORT_UUID', ''))
    access_token_input = request.COOKIES.get('access_token', '')
    return sohupassport_uuid, access_token_input

def set_user_info_for_web(response, sohupassport_uuid = '', access_token = ''):
    response.set_cookie('access_token', access_token)
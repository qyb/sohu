# -*- coding: utf-8 -*-

from SohuPocketLib.user.helper import input_for_verify_func, input_for_show_func, \
    extract_class_instance_to_dict, input_for_update_func
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from helper import KanUser, serialize

def verify(request):
    """
    verify user infomation, return access_token
    """
    sohupassport_uuid, access_token_input = input_for_verify_func(request)
    kan_user = KanUser(sohupassport_uuid, access_token_input)
    kan_user.check_and_login()
    if kan_user.is_logged_in():
        response_dict = dict()
        response_dict['access_token'] = kan_user.get_access_token()
        response = HttpResponse(serialize(response_dict))
    else:
        response = HttpResponse(serialize(None))
        
    return response

def verify_test(request):
    
    return render_to_response('user_verify_test.html',
                              context_instance = RequestContext(request))
    
def show(request):
    """
    show user infomation
    """
    access_token_input = input_for_show_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.check_and_login()
    if kan_user.is_logged_in():
        response_dict = extract_class_instance_to_dict(kan_user.get_user())
        response = HttpResponse(serialize(response_dict))
    else:
        response = HttpResponse(serialize(None))
        
    return response

def show_test(request):
    
    return render_to_response('user_show_test.html',
                              context_instance = RequestContext(request))
    
def update(request):
    """
    update user infomation
    """
    access_token_input, user_info_dict = input_for_update_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.check_and_login()
    if kan_user.is_logged_in():
        kan_user.set_kan_username(user_info_dict.get('kan_username', ''))
        kan_user.set_kan_self_description(user_info_dict.get('kan_self_description', ''))
        response_dict = extract_class_instance_to_dict(kan_user.get_user())
        response = HttpResponse(serialize(response_dict))
    else:
        response = HttpResponse(serialize(None))
        
    return response

def update_test(request):
    
    return render_to_response('user_update_test.html',
                              context_instance = RequestContext(request))
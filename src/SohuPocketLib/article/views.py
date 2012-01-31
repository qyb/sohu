# -*- coding: utf-8 -*-

from article.helper import get_myarticle_list_to_xml_etree, \
    input_for_list_func, get_myarticle_instance_to_xml_etree, input_for_show_func, \
    input_for_update_func, generate_single_xml_etree, input_for_destroy_func, \
    input_for_modify_func, modify_or_destroy_myarticle_instance, UpdateArticleInfo
from page.tasks import PageFetchHandler
from user.helper import KanUser
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from lxml import etree


def list(request, format):
    access_token_input = input_for_list_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    if kan_user.is_logged_in():
        if format == 'xml':
            myarticle_list_etree = get_myarticle_list_to_xml_etree(kan_user.get_user_id())
            response = etree.tostring(myarticle_list_etree)
    
    return HttpResponse(response)


def show(request, key, format):
    access_token_input = input_for_show_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    if kan_user.is_logged_in():
        if format == 'xml':
            myarticle_instance_etree = get_myarticle_instance_to_xml_etree(kan_user.get_user_id(), key)
            response = etree.tostring(myarticle_instance_etree)
            
    return HttpResponse(response)
    
    
def update(request, format):
    access_token_input, url = input_for_update_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    if kan_user.is_logged_in():
        update_article_info = UpdateArticleInfo(kan_user.get_user_id())
        try:
            PageFetchHandler.delay(url, update_article_info)
        except Exception:
            pass
        else:
            if format == 'xml':
                response_etree = generate_single_xml_etree('status', 'success')
                response = etree.tostring(response_etree)
            
    return HttpResponse(response)


def update_test(request, *args, **kwargs):
    
    return render_to_response('article_update_test.html',
                              context_instance = RequestContext(request))


def destroy(request, key, format):
    access_token_input = input_for_destroy_func(request)
    modify_info = dict()
    modify_info['is_delete'] ='True'
    
    return modify_or_destroy_base(access_token_input, modify_info, key, format)
        
        
def modify(request, key, format):
    access_token_input, modify_info = input_for_modify_func(request)
    
    return modify_or_destroy_base(access_token_input, modify_info, key, format)


def modify_or_destroy_base(access_token_input, modify_info, key, format):
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    is_successful = False
    if kan_user.is_logged_in():
        user_id = kan_user.get_user_id()
        is_successful = modify_or_destroy_myarticle_instance(user_id, key, modify_info)
    if format == 'xml':
        response_etree = generate_single_xml_etree('status', 'success' if is_successful else 'fail')
        response = etree.tostring(response_etree)
        
    return HttpResponse(response)

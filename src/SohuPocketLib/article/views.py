# -*- coding: utf-8 -*-

from SohuPocketLib.article.helper import get_myarticle_list_to_xml_etree, \
    input_for_list_func, get_myarticle_instance_to_xml_etree, input_for_show_func, \
    input_for_update_func, generate_single_xml_etree
from SohuPocketLib.page.tasks import PageFetchHandler
from SohuPocketLib.user.helper import KanUser
from django.http import HttpResponse
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
        info = dict()
        info['user_id'] = kan_user.get_user_id()
        try:
            PageFetchHandler.delay(url, info)
        except Exception:
            pass
        else:
            if format == 'xml':
                response_etree = generate_single_xml_etree('status', 'success')
                response = etree.tostring(response_etree)
            
    return HttpResponse(response)

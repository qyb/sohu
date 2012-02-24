# -*- coding: utf-8 -*-

from article.helper import generate_single_xml_etree
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from folder.helper import create_or_get_folder_by_name, input_for_apiv2_add, \
    convert_folder_to_etree, input_for_apiv2_list, select_folder_list, \
    convert_folder_list_to_etree, input_for_apiv2_update, modify_folder_by_name, \
    input_for_apiv2_delete, get_folder_by_name, delete_folder
from lxml import etree
from user.helper import KanUser


def apiv2_list(request):
    access_token_input = input_for_apiv2_list(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        folder_list = select_folder_list(kan_user.get_user_id())
        folder_list_etree = convert_folder_list_to_etree(folder_list)
        response = etree.tostring(folder_list_etree, xml_declaration=True, encoding='utf-8')
    else:
        status_etree = generate_single_xml_etree('status', 'invalid input')
        response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def apiv2_add(request): 
    access_token_input, name = input_for_apiv2_add(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        folder = create_or_get_folder_by_name(kan_user.get_user_id(), name)
        folder_etree = convert_folder_to_etree(folder)
        response = etree.tostring(folder_etree, xml_declaration=True, encoding='utf-8')
    else:
        status_etree = generate_single_xml_etree('status', 'invalid input')
        response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
    
    return HttpResponse(response, mimetype=mimetype)


def apiv2_update(request):
    access_token_input, modify_info = input_for_apiv2_update(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        if modify_info:
            folder = modify_folder_by_name(kan_user.get_user_id(), modify_info['old_name'], modify_info)
            if folder:
                folder_etree = convert_folder_to_etree(folder)
                response = etree.tostring(folder_etree, xml_declaration=True, encoding='utf-8')
            else:
                status_etree = generate_single_xml_etree('status', 'violated operation')
                response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
        else:
            status_etree = generate_single_xml_etree('status', 'invalid input')
            response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
    else:
        status_etree = generate_single_xml_etree('status', 'verify failed')
        response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
    
    return HttpResponse(response, mimetype=mimetype)


def apiv2_delete(request):
    access_token_input, name = input_for_apiv2_delete(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        folder = get_folder_by_name(kan_user.get_user_id(), name)
        if folder:
            is_successful = delete_folder(folder)
            if is_successful:
                folder_etree = convert_folder_to_etree(None)
                response = etree.tostring(folder_etree, xml_declaration=True, encoding='utf-8')
            else:
                status_etree = generate_single_xml_etree('status', 'violated operation')
                response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
        else:
            status_etree = generate_single_xml_etree('status', 'invalid input')
            response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
    else:
        status_etree = generate_single_xml_etree('status', 'verify failed')
        response = etree.tostring(status_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def apiv2_set_order(request):
    pass

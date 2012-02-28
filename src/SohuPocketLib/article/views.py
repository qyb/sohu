# -*- coding: utf-8 -*-

from article.helper import get_myarticle_list_to_xml_etree, input_for_list_func, \
    get_myarticle_instance_to_xml_etree, input_for_show_func, input_for_update_func, \
    generate_single_xml_etree, input_for_destroy_func, input_for_modify_func, \
    modify_or_destroy_myarticle_instance, UpdateArticleInfo, input_for_api2_count, \
    get_myarticle_list_count, output_for_api2_count_etree
from constants import TRUE_REPR
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from lxml import etree
from page.tasks import PageFetchHandler
from user.helper import KanUser
import logging


def list(request, format):
    access_token_input, offset, limit = input_for_list_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if format == 'xml':
            myarticle_list_etree = get_myarticle_list_to_xml_etree(kan_user.get_user_id(),
                                                                   offset,
                                                                   limit)
            response = etree.tostring(myarticle_list_etree,
                                      xml_declaration=True,
                                      encoding='utf-8')
            mimetype ='text/xml'
            
    return HttpResponse(response, mimetype=mimetype)


def list_test(request, *args, **kwargs):
    
    return render_to_response('article_list_test.html',
                              context_instance = RequestContext(request))


def show(request, key, format):
    access_token_input = input_for_show_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if format == 'xml':
            myarticle_instance_etree = get_myarticle_instance_to_xml_etree(kan_user.get_user_id(),
                                                                           key)
            if myarticle_instance_etree is not None:
                response = etree.tostring(myarticle_instance_etree,
                                          xml_declaration=True,
                                          encoding='utf-8')
                mimetype = 'text/xml'
            
    return HttpResponse(response, mimetype=mimetype)
    

def show_test(request, *args, **kwargs):
    
    return render_to_response('article_show_test.html',
                              context_instance = RequestContext(request))

    
def update(request, format):
    access_token_input, url = input_for_update_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    logging.warning(str(request.POST))
    mimetype = 'text/plain'
    if kan_user.is_logged_in():
        update_article_info = UpdateArticleInfo(kan_user.get_user_id())
        try:
            PageFetchHandler.delay(url, update_article_info)
        except Exception:
            pass
        else:
            if format == 'xml':
                response_etree = generate_single_xml_etree('status', 'success')
                response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
                mimetype = 'text/xml'
                
    return HttpResponse(response, mimetype=mimetype)


def update_test(request, *args, **kwargs):
    
    return render_to_response('article_update_test.html',
                              context_instance = RequestContext(request))


def destroy(request, key, format):
    access_token_input = input_for_destroy_func(request)
    logging.warning(str(request.POST))
    modify_info = dict()
    modify_info['is_delete'] = TRUE_REPR
    
    return modify_or_destroy_base(access_token_input, modify_info, key, format)

        
def destroy_test(request, *args, **kwargs):
    
    return render_to_response('article_destroy_test.html',
                              context_instance = RequestContext(request))

        
def modify(request, key, format):
    access_token_input, modify_info = input_for_modify_func(request)
    logging.warning(str(request.POST))
    
    return modify_or_destroy_base(access_token_input, modify_info, key, format)


def modify_test(request, *args, **kwargs):
    
    return render_to_response('article_modify_test.html',
                              context_instance = RequestContext(request))


def modify_or_destroy_base(access_token_input, modify_info, key, format):
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    is_successful = False
    mimetype = 'text/plain'
    if kan_user.is_logged_in():
        user_id = kan_user.get_user_id()
        is_successful = modify_or_destroy_myarticle_instance(user_id, key, modify_info)
    if format == 'xml':
        response_etree = generate_single_xml_etree('status', 'success' if is_successful else 'fail')
        response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
        mimetype = 'text/xml'
        
    return HttpResponse(response, mimetype=mimetype)


def api2_count(request):
    access_token_input, folder_id = input_for_api2_count(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        count = get_myarticle_list_count(kan_user.get_user_id(), folder_id)
        response_etree = output_for_api2_count_etree(count)
        response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
    else:
        response_etree = generate_single_xml_etree('status', 'verify failed')
        response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
    
    return HttpResponse(response, mimetype=mimetype)
    


def api2_count_test(request, *args, **kwargs):
    
    return render_to_response('api2_bookmarks_count_test.html',
                              context_instance = RequestContext(request))


def api2_list(request):
    access_token_input, offset, limit, folder_name, order_by = input_for_api2_list(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_count(request):
    access_token_input, folder_name = input_for_api2_count(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_update_read_progress(request):
    access_token_input, bookmark_id, progress, progress_timestamp = input_for_api2_update_read_progress(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_add(request):
    access_token_input, url, title, description, folder_name, content = input_for_api2_add(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_delete(request):
    access_token_input, bookmark_id = input_for_api2_delete(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_update(request):
    access_token_input, bookmark_id, title, description = input_for_api2_update(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_star(request):
    access_token_input, bookmark_id = input_for_api2_star(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_unstar(request):
    access_token_input, bookmark_id = input_for_api2_unstar(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_archive(request):
    access_token_input, bookmark_id = input_for_api2_archive(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_unarchive(request):
    access_token_input, bookmark_id = input_for_api2_unarchive(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_move(request):
    access_token_input, bookmark_id, folder_name = input_for_api2_move(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)


def api2_get_text(request):
    access_token_input, bookmark_id = input_for_api2_get_text(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        pass
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    return HttpResponse(response, mimetype=mimetype)

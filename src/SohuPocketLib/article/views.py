# -*- coding: utf-8 -*-

from article.helper import api2_generate_article_key, api2_select_article, \
    api2_modify_article_common, api2_convert_article_to_etree, \
    api2_select_article_list, api2_convert_article_list_to_etree, \
    get_myarticle_list_to_xml_etree, input_for_list_func, \
    get_myarticle_instance_to_xml_etree, input_for_show_func, input_for_update_func, \
    generate_single_xml_etree, input_for_destroy_func, input_for_modify_func, \
    modify_or_destroy_myarticle_instance, UpdateArticleInfo, api2_input_for_count, \
    get_myarticle_list_count, api2_convert_count_to_etree, api2_input_for_list, \
    api2_input_for_update_read_progress, api2_input_for_add, api2_input_for_delete, \
    api2_input_for_update, api2_input_for_star, api2_input_for_unstar, \
    api2_input_for_archive, api2_input_for_unarchive, api2_input_for_move, \
    api2_input_for_get_text, get_or_create_article_resource, \
    generate_resource_package_etree
from common.helper import KanError
from constants import BUCKET_NAME_SOHUKAN, TRUE_REPR
from django.http import HttpResponse, HttpResponsePermanentRedirect
from django.shortcuts import render_to_response
from django.template import RequestContext
from lxml import etree
from page.tasks import PageFetchHandler
from storage.helper import get_data_url
from user.helper import KanUser
import logging


def list(request, response_format):
    access_token_input, offset, limit = input_for_list_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if response_format == 'xml':
            myarticle_list_etree = get_myarticle_list_to_xml_etree(kan_user.get_user_id(),
                                                                   offset,
                                                                   limit)
            response = etree.tostring(myarticle_list_etree,
                                      xml_declaration=True,
                                      encoding='utf-8')
            mimetype = 'text/xml'
            
    return HttpResponse(response, mimetype=mimetype)


def list_test(request, *args, **kwargs):
    
    return render_to_response('article_list_test.html',
                              context_instance=RequestContext(request))


def show(request, key, response_format):
    access_token_input = input_for_show_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if response_format == 'xml':
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
                              context_instance=RequestContext(request))

    
def update(request, response_format):
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
            if response_format == 'xml':
                response_etree = generate_single_xml_etree('status', 'success')
                response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
                mimetype = 'text/xml'
                
    return HttpResponse(response, mimetype=mimetype)


def update_test(request, *args, **kwargs):
    
    return render_to_response('article_update_test.html',
                              context_instance=RequestContext(request))


def destroy(request, key, response_format):
    access_token_input = input_for_destroy_func(request)
    logging.warning(str(request.POST))
    modify_info = dict()
    modify_info['is_delete'] = TRUE_REPR
    
    return modify_or_destroy_base(access_token_input, modify_info, key, response_format)

        
def destroy_test(request, *args, **kwargs):
    
    return render_to_response('article_destroy_test.html',
                              context_instance=RequestContext(request))

        
def modify(request, key, response_format):
    access_token_input, modify_info = input_for_modify_func(request)
    logging.warning(str(request.POST))
    
    return modify_or_destroy_base(access_token_input, modify_info, key, response_format)


def modify_test(request, *args, **kwargs):
    
    return render_to_response('article_modify_test.html',
                              context_instance=RequestContext(request))


def modify_or_destroy_base(access_token_input, modify_info, key, response_format):
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    is_successful = False
    mimetype = 'text/plain'
    if kan_user.is_logged_in():
        user_id = kan_user.get_user_id()
        is_successful = modify_or_destroy_myarticle_instance(user_id, key, modify_info)
    if response_format == 'xml':
        response_etree = generate_single_xml_etree('status', 'success' if is_successful else 'fail')
        response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
        mimetype = 'text/xml'
        
    return HttpResponse(response, mimetype=mimetype)


##############################
# views for api2
##############################


def api2_list(request):
    access_token_input, offset, limit, folder_name, order_by = api2_input_for_list(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        article_list = api2_select_article_list(kan_user.get_user_id(), folder_name, order_by, offset, limit)
        article_list_etree = api2_convert_article_list_to_etree(article_list)
        response = etree.tostring(article_list_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def api2_count(request):
    access_token_input, folder_id = api2_input_for_count(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        count = get_myarticle_list_count(kan_user.get_user_id(), folder_id)
        count_etree = api2_convert_count_to_etree(count)
        response = etree.tostring(count_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    
    return HttpResponse(response, mimetype=mimetype)
    


def api2_count_test(request, *args, **kwargs):
    
    return render_to_response('api2_bookmarks_count_test.html',
                              context_instance=RequestContext(request))


def api2_modify_common(request, modify_info):
    kan_user = KanUser('', modify_info['access_token_input'])
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        article = api2_select_article(kan_user.get_user_id(), modify_info['article_id'])
        if article:
            article = api2_modify_article_common(article, modify_info)
            if article:
                article_etree = api2_convert_article_to_etree(article)
                response = etree.tostring(article_etree, xml_declaration=True, encoding='utf-8')
            else:
#                '1002': 'Violated operation'
                error_etree = KanError('1002').get_error_etree()
                response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        else:
#            '1001': 'Invalid or missing params'
            error_etree = KanError('1001').get_error_etree()
            response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    else:
#        '1000': 'User verify failed'
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def api2_update_read_progress(request):
    access_token_input, article_id, read_progress, read_progress_timestamp = api2_input_for_update_read_progress(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['read_progress'] = read_progress
    modify_info['read_progress_timestamp'] = read_progress_timestamp

    return api2_modify_common(request, modify_info)


def api2_add(request):
    access_token_input, url, title, description, folder_name, content = api2_input_for_add(request)
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
    access_token_input, article_id = api2_input_for_delete(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_delete'] = True

    return api2_modify_common(request, modify_info)


def api2_update(request):
    access_token_input, article_id, title, description = api2_input_for_update(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['title'] = title
    modify_info['description'] = description

    return api2_modify_common(request, modify_info)


def api2_view(request):
    access_token_input, article_id = api2_input_for_update(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id

    return api2_modify_common(request, modify_info)

    

def api2_star(request):
    access_token_input, article_id = api2_input_for_star(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_star'] = True

    return api2_modify_common(request, modify_info)


def api2_unstar(request):
    access_token_input, article_id = api2_input_for_unstar(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_star'] = False

    return api2_modify_common(request, modify_info)


def api2_archive(request):
    access_token_input, article_id = api2_input_for_archive(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_archive'] = True

    return api2_modify_common(request, modify_info)


def api2_unarchive(request):
    access_token_input, article_id = api2_input_for_unarchive(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_archive'] = False

    return api2_modify_common(request, modify_info)


def api2_move(request):
    access_token_input, article_id, folder_name = api2_input_for_move(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['folder_name'] = folder_name

    return api2_modify_common(request, modify_info)


def api2_get_text(request):
    access_token_input, article_id = api2_input_for_get_text(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    if kan_user.is_logged_in():
        article_key = api2_generate_article_key(article_id)
        text_url = get_data_url(BUCKET_NAME_SOHUKAN, article_key)
        return HttpResponsePermanentRedirect(text_url)
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        mimetype = 'text/xml'
        
    return HttpResponse(response, mimetype=mimetype)


def api2_get_resource(request):
    access_token_input, article_id = api2_input_for_get_text(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        article_resource = get_or_create_article_resource(kan_user.get_user_id(), article_id)
        images = article_resource.get_images()
        resource_package_etree = generate_resource_package_etree(images=images)
        response = etree.tostring(resource_package_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)

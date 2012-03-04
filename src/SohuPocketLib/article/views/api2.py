# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def list(request):
    access_token_input, offset, limit, folder_name, order_by = input_for_list(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        article_list = select_article_list(kan_user.get_user_id(), folder_name, order_by, offset, limit)
        article_list_etree = convert_article_list_to_etree(article_list)
        response = etree.tostring(article_list_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def count(request):
    access_token_input, folder_id = input_for_count(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        count = get_myarticle_list_count(kan_user.get_user_id(), folder_id)
        count_etree = convert_count_to_etree(count)
        response = etree.tostring(count_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    
    return HttpResponse(response, mimetype=mimetype)


def modify_common(request, modify_info):
    kan_user = KanUser('', modify_info['access_token_input'])
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        article = select_article(kan_user.get_user_id(), modify_info['article_id'])
        if article:
            article = modify_article_common(kan_user.get_user_id(), article, modify_info)
            if article:
                article_etree = convert_article_to_etree(article)
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


def update_read_progress(request):
    access_token_input, article_id, read_progress, read_progress_timestamp = input_for_update_read_progress(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['read_progress'] = read_progress
    modify_info['read_progress_timestamp'] = read_progress_timestamp

    return modify_common(request, modify_info)


def add(request):
    access_token_input, url, title, description, folder_name, content = input_for_add(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/xml'
    if kan_user.is_logged_in():
        runtime_article_info = RuntimeArticleInfo(kan_user.get_user_id(),
                                                  title,
                                                  description,
                                                  folder_name,
                                                  content)
        if runtime_article_info.is_param_valid():
            article_result = PageFetchHandler.delay(runtime_article_info)
#            wait until the task is done
            article = article_result.get()
            if article:
                article_etree = convert_article_to_etree(article)
                response = etree.tostring(article_etree, xml_declaration=True, encoding='utf-8')
            else:
                error_etree = KanError('1002').get_error_etree()
                response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        else:
            error_etree = KanError('1001').get_error_etree()
            response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        
    return HttpResponse(response, mimetype=mimetype)


def delete(request):
    access_token_input, article_id = input_for_delete(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_delete'] = True

    return modify_common(request, modify_info)


def update(request):
    access_token_input, article_id, title, description = input_for_update(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['title'] = title
    modify_info['description'] = description

    return modify_common(request, modify_info)


def view(request):
    access_token_input, article_id = input_for_update(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id

    return modify_common(request, modify_info)
    

def star(request):
    access_token_input, article_id = input_for_star(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_star'] = True

    return modify_common(request, modify_info)


def unstar(request):
    access_token_input, article_id = input_for_unstar(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_star'] = False

    return modify_common(request, modify_info)


def archive(request):
    access_token_input, article_id = input_for_archive(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_archive'] = True

    return modify_common(request, modify_info)


def unarchive(request):
    access_token_input, article_id = input_for_unarchive(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['is_archive'] = False

    return modify_common(request, modify_info)


def move(request):
    access_token_input, article_id, folder_name = input_for_move(request)
    modify_info = dict()
    modify_info['access_token_input'] = access_token_input
    modify_info['article_id'] = article_id
    modify_info['folder_name'] = folder_name

    return modify_common(request, modify_info)


def get_text(request):
    access_token_input, article_id = input_for_get_text(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    if kan_user.is_logged_in():
        article_key = generate_article_key(article_id)
        text_url = get_data_url(BUCKET_NAME_SOHUKAN, article_key)
        return HttpResponsePermanentRedirect(text_url)
    else:
        error_etree = KanError('1000').get_error_etree()
        response = etree.tostring(error_etree, xml_declaration=True, encoding='utf-8')
        mimetype = 'text/xml'
        
    return HttpResponse(response, mimetype=mimetype)


def get_resource(request):
    access_token_input, article_id = input_for_get_text(request)
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

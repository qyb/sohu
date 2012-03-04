# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def input_for_article_id_common(request):
    if request.method == 'POST':
        article_id = long(request.POST.get('bookmark_id', None))
    else:
        article_id = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, article_id


def input_for_count(request):
    if request.method == 'POST':
        folder_id = long(request.POST.get('folder_id', None))
    else:
        folder_id = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, folder_id


def input_for_list(request):
    if request.method == 'POST':
        offset = request.POST.get('offset', None)
        try:
            offset = int(offset)
        except:
            offset = 0
        limit = request.POST.get('limit', None)
        try:
            limit = int(offset)
        except:
            limit = 20
        folder_name = request.POST.get('folder_name', None)
        order_by = request.POST.get('order_by', None)
    else:
        offset = 0
        limit = 20
        folder_name = None
        order_by = None
    access_token_input = api2_input_for_access_token_common(request)
        
    return access_token_input, offset, limit, folder_name, order_by


def input_for_update_read_progress(request):
    if request.method == 'POST':
        progress = request.POST.get('read_progress', None)
        try:
            progress = float(progress)
        except:
            progress = None
        progress_timestamp = request.POST.get('read_progress_timestamp', None)
        try:
            progress_timestamp = datetime.fromtimestamp(float(progress_timestamp))
        except:
            progress_timestamp = None
    else:
        progress = None
        progress_timestamp = None
    access_token_input, article_id = input_for_article_id_common(request)
    
    return access_token_input, article_id, progress, progress_timestamp


def input_for_add(request):
    if request.method == 'POST':
        url = request.POST.get('url', None)
        title = request.POST.get('title', None)
        description = request.POST.get('description', None)
        folder_name = request.POST.get('folder_name', None)
        content = request.POST.get('content', None)
    else:
        url = None
        title = None
        description = None
        folder_name = None
        content = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, url, title, description, folder_name, content


def input_for_delete(request):
    
    return input_for_article_id_common(request)


def input_for_update(request):
    if request.method == 'POST':
        title = request.POST.get('title', None)
        description = request.POST.get('description', None)
    else:
        title = None
        description = None
    access_token_input, article_id = input_for_article_id_common(request)
    
    return access_token_input, article_id, title, description


def input_for_view(request):
    
    return input_for_article_id_common(request)


def input_for_star(request):
    
    return input_for_article_id_common(request)


def input_for_unstar(request):
    
    return input_for_article_id_common(request)


def input_for_archive(request):
    
    return input_for_article_id_common(request)


def input_for_unarchive(request):
    
    return input_for_article_id_common(request)


def input_for_move(request):
    if request.method == 'POST':
        folder_name = request.POST.get('folder_name', None)
    else:
        folder_name = None
    access_token_input, article_id = input_for_article_id_common(request)
    
    return access_token_input, article_id, folder_name


def input_for_get_text(request):
    
    return input_for_article_id_common(request)


def input_for_get_resource(request):
    
    return input_for_article_id_common(request)
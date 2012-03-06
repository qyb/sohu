# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def input_for_list_func(request):
    if request.method == 'GET':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.GET.get('access_token', '')
        offset = request.GET.get('offset', '')
        try:
            offset = int(offset)
        except:
            offset = 0
        limit = request.GET.get('limit', '')
        try:
            limit = int(limit)
        except:
            limit = DEFAULT_ARTICLE_LIST_LIMIT
    else:
        access_token_input = ''
        offset = 0
        limit = DEFAULT_ARTICLE_LIST_LIMIT
        
    return access_token_input, offset, limit


def input_for_show_func(request):
    if request.method == 'GET':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.GET.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_update_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
        url = request.POST.get('url', '')
    else:
        access_token_input = ''
        url = ''
        
    return access_token_input, url


def input_for_destroy_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_modify_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
        modify_info = dict()
        args = ('is_delete', 'is_read', 'is_star')
        for arg in args:
            modify_info[arg] = request.POST.get(arg, '')
    else:
        access_token_input = ''
        modify_info = dict()
        
    return access_token_input, modify_info
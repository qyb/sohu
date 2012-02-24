# -*- coding: utf-8 -*-

from constants import KEY_FOLDER
from article.helper import choose_a_db
from django.core.cache import cache
from folder.models import Folder
from lxml import etree


def input_for_apiv2_list(request):
    if request.method == 'POST':
        access_token_input = request.COOKIES.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_apiv2_add(request):
    """
    param: name
        name must not start with '_' or have ',' inside
    """
    if request.method == 'POST':
        access_token_input = request.COOKIES.get('access_token', '')
        name = request.POST.get('name', '')
        if name.startswith('_') or ',' in name:
            name = ''
    else:
        access_token_input = ''
        name = ''
        
    return access_token_input, name


def input_for_apiv2_update(request):
    if request.method == 'POST':
        access_token_input = request.COOKIES.get('access_token', '')
        modify_info = dict()
        valid_params = ('old_name, new_name')
        try:
            for param in valid_params:
                dict[param] =  request.POST[param]
        except:
            modify_info = dict()
    else:
        access_token_input = ''
        modify_info = dict()
    
    return access_token_input, modify_info


def input_for_apiv2_delete(request):
    if request.method == 'POST':
        access_token_input = request.COOKIES.get('access_token', '')
        name = request.POST.get('name', '')
    else:
        access_token_input = ''
        name = ''
        
    return access_token_input, name


def generate_folder_key(user_id, folder_name):
    folder_key = KEY_FOLDER % (user_id, folder_name)
    
    return folder_key


def select_folder_list(user_id): 
    chosen_db = choose_a_db(user_id)
    folder_list = Folder.objects.using(chosen_db) \
                        .filter(user_id=user_id) \
                        .order_by('order')
    return folder_list


def convert_folder_to_etree(folder):
#    always have root node instead of None by default
    node_folder = etree.Element('folder')
    if folder:
        node_name = etree.SubElement(node_folder, 'name')
        node_name.text = folder.name
    
    return node_folder


def convert_folder_list_to_etree(folder_list):
#    always have root node instead of None by default
    node_package = etree.Element('package')
    for folder in folder_list:
        node_folder = convert_folder_to_etree(folder)
        if node_folder:
            node_package.append(node_folder)
    
    return node_package
        

def get_folder_by_name(user_id, name):
    if name:
        folder_key = generate_folder_key(user_id, name)
        folder = cache.get(folder_key, None)
        if folder is None:
            chosen_db = choose_a_db(user_id)
            try:
                folder = folder.objects.using(chosen_db).get(user_id=user_id, name=name)
            except:
                pass
    else:
        folder = None
        
    return folder


def create_or_get_folder_by_name(user_id, name):
    if name:
        folder = get_folder_by_name(user_id, name)
        if folder is None:
            folder = Folder()
            folder.name = name
            folder.save()
    else:
        folder = None
    
    return folder


def modify_folder_by_name(user_id, folder_name, modify_info):
    if folder_name:
        folder = get_folder_by_name(user_id, folder_name)
        if folder is not None:
            folder.name = modify_info['new_name']
            folder.save()
    else:
        folder = None
    
#    if original folder is not found, return None
    return folder


def delete_folder(folder):
    is_successful = True
    try:
        folder.delete()
    except:
        is_successful = False
    
    return is_successful

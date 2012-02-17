# -*- coding: utf-8 -*-

from article.models import MyArticleInstance
from constants import LIMIT_USERS_ONE_DB, BUCKET_NAME_ARTICLE, BUCKET_NAME_IMAGE, \
    KEY_ARTICLE_INSTANCE, DEFAULT_ARTICLE_LIST_LIMIT, TRUE_REPR, FALSE_REPR
from datetime import datetime
from django.core.cache import cache
from image.models import MyImageInstance
from lxml import etree
from storage.helper import get_data_url
import hashlib
import logging


class UpdateArticleInfo(object):
    """
    stores variables used when update article
    """
    
    def __init__(self, user_id):
        self.user_id = user_id
        self.url = None
        self.article_title = None
        self.article_content = None
        self.mime = None
        self.article_id = None
        self.article_instance_key = None
        self.image_url_list = None
        
        return None


def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2 * LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3 * LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db


def create_myarticle_instance(user_id, key, title, url):
    chosen_db = choose_a_db(user_id)
    try:
        myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
    except MyArticleInstance.DoesNotExist:
        myarticle_instance = MyArticleInstance()
    myarticle_instance.user_id = user_id
    myarticle_instance.key = key
    myarticle_instance.title = title
    myarticle_instance.url = url
    myarticle_instance.save()
    
    return myarticle_instance


def get_myarticle_instance(user_id, key):
    myarticle_instance = cache.get(key, None)
    if myarticle_instance is None:
        chosen_db = choose_a_db(user_id)
        try:
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
        except MyArticleInstance.DoesNotExist:
            pass
        else:
            myarticle_instance.update_cache()
        
    return myarticle_instance  


def get_myarticle_instance_with_image_list(user_id, key):
    myarticle_instance = get_myarticle_instance(user_id, key)
    if myarticle_instance and not hasattr(myarticle_instance, 'image_list'):
        myarticle_instance.image_list = []
        if myarticle_instance.is_ready:
            chosen_db = choose_a_db(user_id)
            images = MyImageInstance.objects \
                                    .using(chosen_db) \
                                    .filter(myarticle_instance_id=myarticle_instance.id)
            image_list = [image.key for image in images]
            myarticle_instance.image_list = image_list
        myarticle_instance.update_cache()
    
    return myarticle_instance


def delete_myarticle_instance_in_db(user_id, key):
    is_successful = True
    chosen_db = choose_a_db(user_id)
    try:
        myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
    except MyArticleInstance.DoesNotExist:
        is_successful = False
    else:
        myarticle_instance.delete()
        
    return is_successful

                                            
def modify_or_destroy_myarticle_instance(user_id, key, modify_info):
    is_successful = True
    chosen_db = choose_a_db(user_id)
    try:
        myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
    except MyArticleInstance.DoesNotExist:
        is_successful = False
    else:
        if modify_info.get('is_delete', None) == TRUE_REPR:
            myarticle_instance.is_delete = True
            myarticle_instance.delete_time = datetime.now()
        if modify_info.get('is_read', None) == TRUE_REPR:
            myarticle_instance.is_read = True
            myarticle_instance.read_time = datetime.now()
        elif modify_info.get('is_read', None) == FALSE_REPR:
            myarticle_instance.is_read = False
            myarticle_instance.read_time = None
        if modify_info.get('is_star', None) == TRUE_REPR:
            myarticle_instance.is_star = True
        elif modify_info.get('is_star', None) == FALSE_REPR:
            myarticle_instance.is_star = False
        myarticle_instance.save()
        
    return is_successful        


def generate_article_instance_key(url, user_id):
    hash_source = url
    url_hash = hashlib.new('sha1', hash_source).hexdigest()
    key = KEY_ARTICLE_INSTANCE % (user_id, url_hash)
    
    return key


def get_myarticle_instance_to_xml_etree(user_id, key):
    myarticle_instance = get_myarticle_instance_with_image_list(user_id, key)
    article = etree.Element('article', key=key)
    
    if not myarticle_instance.is_delete:
    
        title = etree.SubElement(article, 'title')
        title.text = myarticle_instance.title
        
        url = etree.SubElement(article, 'url')
        url.text = myarticle_instance.url
        
        download_url = etree.SubElement(article, 'download_url')
        download_url.text = get_data_url(BUCKET_NAME_ARTICLE, myarticle_instance.key)
        
        image_urls = etree.SubElement(article, 'image_urls')
        image_urls.text = '|'.join([get_data_url(BUCKET_NAME_IMAGE, image_key) \
                                for image_key in myarticle_instance.image_list])
        
#        for image_key in myarticle_instance.image_list:
#            image_url = etree.SubElement(image_urls, 'image_url', key=image_key)
#            image_url.text = get_data_url(BUCKET_NAME_IMAGE, image_key)
            
        cover = etree.SubElement(article, 'cover')
        cover.text = myarticle_instance.cover
        
        is_star = etree.SubElement(article, 'is_star')
        is_star.text = TRUE_REPR if myarticle_instance.is_star else FALSE_REPR
        
        is_read = etree.SubElement(article, 'is_read')
        is_read.text = TRUE_REPR if myarticle_instance.is_read else FALSE_REPR
        
        create_time = etree.SubElement(article, 'create_time')
        create_time.text = unicode(myarticle_instance.create_time)
        
    return article


def get_myarticle_list(user_id, offset, limit):
    chosen_db = choose_a_db(user_id)
    myarticle_list = MyArticleInstance.objects \
                                      .using(chosen_db) \
                                      .filter(user_id=user_id, is_delete=False, is_ready=True) \
                                      .order_by('-id') \
                                      [offset : offset + limit]
    
    return myarticle_list


def get_myarticle_list_to_xml_etree(user_id, offset, limit):
    myarticle_list = get_myarticle_list(user_id, offset, limit)
    articles = etree.Element('articles')
    for myarticle_instance in myarticle_list:
        articles.append(get_myarticle_instance_to_xml_etree(user_id, myarticle_instance.key))
    
    return articles


def generate_single_xml_etree(tag, text, **kwargs):
    element = etree.Element(tag, **kwargs) 
    element.text = text
    
    return element


def input_for_list_func(request):
    if request.method == 'GET':
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
        access_token_input = request.GET.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_update_func(request):
    if request.method == 'POST':
        access_token_input = request.POST.get('access_token', '')
        url = request.POST.get('url', '')
    else:
        access_token_input = ''
        url = ''
        
    return access_token_input, url


def input_for_destroy_func(request):
    if request.method == 'POST':
        access_token_input = request.POST.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input    


def input_for_modify_func(request):
    if request.method == 'POST':
        access_token_input = request.POST.get('access_token', '')
        modify_info = dict()
        args = ('is_delete', 'is_read', 'is_star')
        for arg in args:
            modify_info[arg] = request.POST.get(arg, '')
    else:
        access_token_input = ''
        modify_info = dict()
        
    return access_token_input, modify_info


def mark_article_as_done(update_article_info):
    is_successful = True
    try:
        chosen_db = choose_a_db(update_article_info.user_id)
        article_instance = MyArticleInstance.objects \
                                            .using(chosen_db) \
                                            .get(id=update_article_info.article_id)
        article_instance.is_ready = True
        article_instance.save()
    except MyArticleInstance.DoesNotExist:
        is_successful = False
    
    return is_successful
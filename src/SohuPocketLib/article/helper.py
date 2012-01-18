# -*- coding: utf-8 -*-

from SohuPocketLib.article.models import MyArticleInstance
from SohuPocketLib.constants import LIMIT_USERS_ONE_DB, KEY_ARTICLE_INSTANCE, \
    KEY_IMAGE_INSTANCE
from SohuPocketLib.image.models import MyImageInstance
from SohuPocketLib.storage.helper import get_data_url
from django.core.cache import cache
from lxml import etree
import hashlib


def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2*LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3*LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db


def create_myarticle_instance(user_id, key, title, url):
    myarticle_instance = MyArticleInstance(
                                           user_id=user_id,
                                           key=key,
                                           title=title,
                                           url=url
                                           )
    myarticle_instance.save()
    
    return myarticle_instance


def get_myarticle_instance(user_id, key):
    myarticle_instance = cache.get(key, None)
    if myarticle_instance is None:
        try:
            chosen_db = choose_a_db(user_id)
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
            cache.set(key, myarticle_instance)
        except MyArticleInstance.DoesNotExist:
            pass
        
    return myarticle_instance  


def get_myarticle_instance_with_image_list(user_id, key):
    myarticle_instance = cache.get(key, None)
    if myarticle_instance is None:
        try:
            myarticle_instance = get_myarticle_instance(user_id, key)
        except MyArticleInstance.DoesNotExist:
            pass
    if myarticle_instance and not hasattr(myarticle_instance, 'image_list'):
        chosen_db = choose_a_db(user_id)
        myimage_instance_list = MyImageInstance.objects.using(chosen_db) \
                                            .filter(myarticle_instance_id=myarticle_instance.id)
        image_list = [image.key for image in myimage_instance_list]
        myarticle_instance.image_list = image_list
    
    return myarticle_instance
                                            

def update_myarticle_instance_cache(user_id, key, myarticle_instance):
    cache.set(key, myarticle_instance)
    
    return None


def generate_article_instance_key(url, user_id):
    hash_source = url
    url_hash = hashlib.new('sha1', hash_source).hexdigest()
    key = KEY_ARTICLE_INSTANCE % (user_id, url_hash)
    
    return key


def get_myarticle_instance_to_xml_etree(user_id, key):
    myarticle_instance = get_myarticle_instance_with_image_list(user_id, key)
    article = etree.Element('article', key=key)
    
    title = etree.SubElement(article, 'title')
    title.text = myarticle_instance.title
    
    url = etree.SubElement(article, 'url')
    url.text = myarticle_instance.url
    
    download_url = etree.SubElement(article, 'download_url')
    download_url.text = get_data_url(KEY_ARTICLE_INSTANCE, myarticle_instance.key)
    
    image_urls = etree.SubElement(article, 'image_urls')
    image_urls.text = '|'.join([get_data_url(KEY_IMAGE_INSTANCE, image_key) \
                                for image_key in myarticle_instance.image_list])
    
    is_read = etree.SubElement(article, 'is_read')
    is_read.text = 'YES' if myarticle_instance.is_read else 'NO'
    
    cover = etree.SubElement(article, 'cover')
    cover.text = myarticle_instance.cover
    
    is_star = etree.SubElement(article, 'is_star')
    is_star.text = 'YES' if myarticle_instance.is_star else 'NO'
    
    is_ready = etree.SubElement(article, 'is_ready')
    is_ready.text = 'YES' if myarticle_instance.is_ready else 'NO'
    
    return article


def get_myarticle_list(user_id):
    chosen_db = choose_a_db(user_id)
    myarticle_list = MyArticleInstance.objects.using(chosen_db).filter(user_id = user_id)
    
    return myarticle_list


def get_myarticle_list_to_xml_etree(user_id):
    myarticle_list = get_myarticle_list(user_id)
    articles = etree.Element('articles')
    for myarticle_instance in myarticle_list:
        articles.append(get_myarticle_instance_to_xml_etree(user_id, myarticle_instance.key))
    
    return articles

def input_for_show_func(request):
    access_token_input = get_GET_dict(request).get('access_token', '')
    
    return access_token_input
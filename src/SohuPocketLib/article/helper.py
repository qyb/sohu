# -*- coding: utf-8 -*-

from BeautifulSoup import BeautifulSoup
from django.core.cache import cache
from constants import *
from article.models import *

def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2*LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3*LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db

def get_myarticle_instance(user_id, key):
    key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
    myarticle_instance = cache.get(key, None)
    if myarticle_instance is None:
        try:
            chosen_db = choose_a_db(user_id)
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(user_id=user_id, key=key)
            cache.set(key, myarticle_instance)
        except MyArticleInstance.DoesNotExist:
            pass 

    return myarticle_instance  

def update_myarticle_instance_cache(user_id, key, myarticle_instance):
    key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
    cache.set(key, myarticle_instance)
    
    return None

def delete_html_tag_attribute(html_string): 
    soup = BeautifulSoup(html_string)
    allTags = soup.findAll(True)
    for tag in allTags:
        for attr in tag.attrs:
            if attr[0] in ['src', 'href', 'alt']:
                continue
            elif attr[0] == 'target':
                tag.attrs[tag.attrs.index(attr)] = ('target', '_blank')
            else:
                tag.attrs.remove(attr)

    return allTags[0].contents[0]

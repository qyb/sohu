# -*- coding: utf-8 -*-

from BeautifulSoup import BeautifulSoup
from SohuPocketLib.article.models import MyArticleInstance
from SohuPocketLib.constants import *
from django.core.cache import cache


def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2*LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3*LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db


def create_myarticle_instance(user_id, key, title, url):
    global_key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
    chosen_db = choose_a_db(user_id)
    myarticle_instance = MyArticleInstance.objects.using(chosen_db) \
                                                  .create(
                                                          user_id=user_id,
                                                          key = global_key,
                                                          title = title,
                                                          url = url
                                                          )
    cache.set(global_key, myarticle_instance)
    
    return myarticle_instance


def get_myarticle_instance(user_id, key):
    global_key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
    myarticle_instance = cache.get(global_key, None)
    if myarticle_instance is None:
        try:
            chosen_db = choose_a_db(user_id)
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(user_id=user_id, key=global_key)
            cache.set(global_key, myarticle_instance)
        except MyArticleInstance.DoesNotExist:
            pass 

    return myarticle_instance  


def update_myarticle_instance_cache(user_id, key, myarticle_instance):
    global_key = CACHE_KEY_USER_ARTICLE_INSTANCE % (user_id, key)
    cache.set(global_key, myarticle_instance)
    
    return None

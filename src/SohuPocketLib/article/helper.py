# -*- coding: utf-8 -*-

from SohuPocketLib.article.models import MyArticleInstance
from SohuPocketLib.constants import LIMIT_USERS_ONE_DB, KEY_ARTICLE_INSTANCE
from django.core.cache import cache
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
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(user_id=user_id, key=key)
            cache.set(key, myarticle_instance)
        except MyArticleInstance.DoesNotExist:
            pass 

    return myarticle_instance  


def update_myarticle_instance_cache(user_id, key, myarticle_instance):
    cache.set(key, myarticle_instance)
    
    return None


def generate_article_instance_key(url, user_id):
    hash_source = url
    url_hash = hashlib.new('sha1', hash_source).hexdigest()
    key = KEY_ARTICLE_INSTANCE % (user_id, url_hash)
    
    return key


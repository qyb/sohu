# -*- coding: utf-8 -*-

import Image
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

class PageFetcher(object):
    '''
    fetch page from page
    '''
    
    def __init__(self):
        '''
        Constructor
        '''
        pass
    
    def single_page(self, url):
        '''
        fetch a single page, no cookies, etc.
        '''
        page = ''
        try:
            page = urllib2.urlopen(url).read()
        except IOError:
            page = '404 not found'
        return page

def scale_image(img_path, width=None, height=None):
    if not isinstance(img_path, basestring):
        return 'parameter error'

    try:
        im = Image.open(img_path)
    except:
        return 'file can not found'
    if not im:
        return 'file can not found'

    if not height and not width:
        return im

    old_width, old_height = im.size
    if height and width:
        im = im.resize((width, height), Image.ANTIALIAS)
    elif width:
        new_height = old_height * (old_width / width)
        im = im.resize((width, new_height), Image.ANTIALIAS)
    elif height:
        new_width = old_width * (old_height / height)
        im = im.resize((new_width, height), Image.ANTIALIAS)
    im = im.convert('RGB')

    return im

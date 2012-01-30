# -*- coding: utf-8 -*-

from BeautifulSoup import BeautifulSoup
from constants import KEY_IMAGE_INSTANCE, KEY_IMAGE_TOBEDONE, \
    BUCKET_NAME_IMAGE
from image.models import MyImageInstance
from storage.helper import get_data_url
from django.core.cache import cache
import Image
import hashlib
import urlparse
import logging


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


def parse_and_replace_image_url_list(url, html, info):
    """
    return all image urls in a html, and convert them into s3 url
    """
    soup = BeautifulSoup(html)
    image_url_list = []
    for tag in soup.findAll('img'):
        try:
            old_image_url = urlparse.urljoin(url, tag['src'])
        except Exception:
            pass
        else:
            image_url_list.append(old_image_url)
            image_instance_key = generate_image_instance_key(info['article_id'], old_image_url)
            new_image_url = get_data_url(BUCKET_NAME_IMAGE, image_instance_key)
            tag['src'] = new_image_url
    
    return image_url_list
            

def generate_image_tobedone_key(article_id):
    
    return KEY_IMAGE_TOBEDONE % article_id


def set_image_tobedone(image_tobedone_key, amount):
    cache.set(image_tobedone_key, amount)
    logging.warning('set_image_tobedone:' + str(image_tobedone_key) + ':' + str(get_image_tobedone(image_tobedone_key)))
    
    return None


def get_image_tobedone(image_tobedone_key):
    
    return cache.get(image_tobedone_key)


def increase_image_tobedone(image_tobedone_key, delta=1):
    cache.incr(image_tobedone_key, delta)
    
    return None


def decrease_image_tobedone(image_tobedone_key, delta=1):
    cache.decr(image_tobedone_key, delta)
    
    return None


def generate_image_instance_key(article_id, image_url):
    hash_source = image_url
    image_url_hash = hashlib.new('sha1', hash_source).hexdigest()
    image_instance_key = KEY_IMAGE_INSTANCE % (article_id, image_url_hash)
    
    return image_instance_key


def create_myimage_instance(user_id, key, url, myarticle_instance_id, title='', description=''):
    myimage_instance = MyImageInstance(
                                       user_id=user_id,
                                       key=key,
                                       url=url,
                                       myarticle_instance_id = myarticle_instance_id,
                                       title=title,
                                       description=description
                                       )
    myimage_instance.save()
    
    return myimage_instance

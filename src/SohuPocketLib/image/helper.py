# -*- coding: utf-8 -*-

from SohuPocketLib.constants import KEY_IMAGE_INSTANCE, KEY_IMAGE_TOBEDONE
from SohuPocketLib.image.models import MyImageInstance
from django.core.cache import cache
import Image
import hashlib


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


def parse_and_replace_image_url_list():
    """
    return all image urls in a html, and tranlate them into s3 address
    """
    pass


def generate_image_tobedone_key(article_id):
    
    return KEY_IMAGE_TOBEDONE % article_id


def set_image_tobedone(image_tobedone_key, amount):
    cache.set(image_tobedone_key)
    
    return None


def get_image_tobedone(image_tobedone_key, ammout):
    cache.get(image_tobedone_key)
    
    return None


def increase_image_tobedone(image_tobedone_key, amount=1):
    cache.incr(image_tobedone_key, amount)
    
    return None


def decrease_image_tobedone(image_tobedone_key, amount=1):
    cache.decr(image_tobedone_key, amount)
    
    return None


def generate_image_instance_key(article_id, image_url):
    hash_source = image_url
    image_url_hash = hashlib.new('sha1', hash_source).hexdigest()
    image_instance_key = KEY_IMAGE_INSTANCE % (article_id, image_url_hash)
    
    return image_instance_key


def create_myimage_instance(key, url, title, description):
    myimage_instance = MyImageInstance(
                                       key=key,
                                       url=url,
                                       title=title,
                                       description=description
                                       )
    myimage_instance.save()
    
    return myimage_instance
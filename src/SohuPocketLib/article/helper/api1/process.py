# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


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
    myarticle_instance.is_read = False
    myarticle_instance.is_star = False
    myarticle_instance.is_delete = False
    myarticle_instance.is_ready = False
    myarticle_instance.create_time = datetime.now()
    myarticle_instance.read_time = None
    myarticle_instance.delete_time = None
    myarticle_instance.save()
    
    return myarticle_instance


def get_myarticle_instance(user_id, key):
    is_authorized = True
    myarticle_instance = cache.get(key, None)
    if myarticle_instance and myarticle_instance.user_id != user_id:
        is_authorized = False
    if myarticle_instance is None:
        chosen_db = choose_a_db(user_id)
        try:
            myarticle_instance = MyArticleInstance.objects.using(chosen_db).get(key=key)
        except MyArticleInstance.DoesNotExist:
            pass
        else:
            if myarticle_instance.user_id != user_id:
                is_authorized = False
            else:
                myarticle_instance.update_cache()
    if not is_authorized:
        myarticle_instance = None

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
        if myarticle_instance.user_id != user_id:
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


def get_myarticle_list(user_id, offset, limit):
    chosen_db = choose_a_db(user_id)
    myarticle_list = MyArticleInstance.objects \
                                      .using(chosen_db) \
                                      .filter(user_id=user_id, is_delete=False, is_ready=True) \
                                      .order_by('-create_time') \
                                      [offset : offset + limit]
    
    return myarticle_list


def get_myarticle_list_count(user_id, folder_id):
    chosen_db = choose_a_db(user_id)
    myarticle_list = MyArticleInstance.objects \
                                      .using(chosen_db) \
                                      .filter(user_id=user_id, is_delete=False, is_ready=True)
                                      
    return len(myarticle_list)


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
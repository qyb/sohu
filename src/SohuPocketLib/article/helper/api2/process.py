# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def generate_article_key(article_id):
    article_key = KEY_FOLDER % article_id
    
    return article_key

def convert_count_to_etree(article_count):
    meta = etree.Element('meta')
    
    count = etree.SubElement(meta, 'count')
    count.text = str(article_count)
    
    return meta


def select_article_list(user_id, folder_name, order_by, offset, limit):
    chosen_db = choose_a_db(user_id)
    article_list = MyArticleInstance.objects \
                                      .using(chosen_db) \
                                      .filter(user_id=user_id, is_delete=False)
    if folder_name == '_archive':
        article_list = article_list.filter(is_archive=True)
    else:
        article_list = article_list.fileter(is_archive=False)
        if folder_name == '_recent_read':
            article_list = article_list.filter(is_read=True).order_by('-read_time')
        else:
            if folder_name in ('_read', '_unread'):
                is_read = True if '_read' else False
                article_list = article_list.filter(is_read=is_read)
            elif folder_name == 'starred':
                article_list = article_list.filter(is_star=True)
            elif folder_name == '_recent_read':
                article_list = article_list.filter(is_read)
            if order_by in ('create_time', '-create_time',
                            'read_time', '-read_time',
                            'url', '-url',
                            'title', '-title'):
                article_list = article_list.order_by(order_by)
    article_list = article_list[offset : offset + limit]
    for article in article_list:
        article.update_cache()
    
    return article_list
        

def select_article(user_id, article_id):
    article_key = generate_article_key(article_id)
    article = cache.get(article_key, None)
    if article is None:
        chosen_db = choose_a_db(user_id)
        try:
            article = MyArticleInstance.objects \
                                       .using(chosen_db) \
                                       .get(user_id=user_id, id=article_id, is_delete=False)
        except:
            article = None
        else:
            article.update_cache()
    
    return article


def modify_article_common(user_id, article, modify_info):
    is_modified = False
    is_successful = True
    if modify_info.get('read_progress', None) and modify_info.get('read_progress_timestamp', None):
        if article.read_time < modify_info['read_progress_timestamp']:
            article.read_progress = modify_info['read_progress']
            article.read_progress_timestamp = modify_info['read_progress_timestamp']
            is_modified = True
    if modify_info.get('is_delete', None) == True:
        article.is_delete = True
        article.delete_time = datetime.now()
        is_modified = True
    if modify_info.get('title', None) is not None:
        article.title = modify_info['title']
        is_modified = True
    if modify_info.get('description', None) is not None:
        article.description = modify_info['description']
        is_modified = True 
    if modify_info.get('is_star', None):
        article.is_star = modify_info['is_star']
        is_modified = True
    if modify_info.get('is_archive', None):
        article.is_archive = modify_info['is_archive']
        is_modified = True
    if modify_info.get('folder_name', None):
        folder = get_folder_by_name(user_id, modify_info['folder_name'])
        if folder:
            article.folder_name = modify_info['folder_name']
            is_modified = True
        else:
            is_successful = False
    if is_modified:
        article.save()
    
    if is_successful:
        return article
    else:
        return None
            

def generate_resource_key(article_id):
    resource_key = KEY_RESOURCE % article_id
    
    return resource_key


def get_or_create_article_resource(user_id, article_id):
    resource_key = generate_resource_key(article_id)
    resource = cache.get(resource_key, None)
    if resource is None:
        resource = ArticleResource(user_id, article_id)
        
    return resource
# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


class RuntimeArticleInfo(object):
    """
    stores variables used when processing article
    """
    
    def __init__(self, user_id, url, title, description, folder_name, content):
        self.user_id = user_id
        self.url = url
        self.article_title = title
        self.article_description = description
        self.article_folder_name = folder_name
        self.article_content = content
        self.mime = None
        self.article_id = None
        self.article_instance_key = None
        self.image_url_list = None
        
        return None
    
    def is_param_valid(self):
        is_valid = True
        scheme, netloc, path, params, query, fragment = urlparse.urlparse(self.url)
        if not scheme or not netloc:
            is_valid = False
        
        return is_valid


class ArticleResource(object):
    """
    caching article related resource
    """
    
    def __init__(self, user_id, article_id):
        self.user_id = user_id
        self.article_id = article_id
        self.key = generate_resource_key(self.article_id)
        self.images = None
        self.audios = None
        self.videos = None
    
    def get_images(self):
        if self.images is None:
            article = self._get_article()
            if article.is_ready:
                self.images = self._select_images()
                self._update_cache(self)
        
        return self.images
    
    def get_audios(self):
#        will be implemented later on
        pass
    
    def ge_videos(self):
#        will be implemented later on
        pass
    
    def _get_article(self):
        article = select_article(self.user_id, self.article_id)
        
        return article
    
    def _update_cache(self):
        cache.set(self.key, self)
        
        return None
    
    def _delete_cache(self):
        cache.delete(self.key)
    
    def _select_images(self):
        chosen_db = choose_a_db(self.user_id)
        images = MyImageInstance.objects \
                                .using(chosen_db) \
                                .filter(myarticle_instance_id=self.article_id)
        return images

def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2 * LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3 * LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db
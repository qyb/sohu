# -*- coding: utf-8 -*-

from SohuPocketLib.folder.helper import get_folder_by_name
from article.models import MyArticleInstance
from constants import ABSOLUTE_FLOAT_ERROR, KEY_FOLDER, LIMIT_USERS_ONE_DB, \
    BUCKET_NAME_ARTICLE, BUCKET_NAME_IMAGE, KEY_ARTICLE_INSTANCE, \
    DEFAULT_ARTICLE_LIST_LIMIT, TRUE_REPR, FALSE_REPR, KEY_RESOURCE
from datetime import datetime
from django.core.cache import cache
from image.models import MyImageInstance
from lxml import etree
from storage.helper import get_data_url
from user.helper import api2_input_for_access_token_common
import hashlib
import logging
import time


class UpdateArticleInfo(object):
    """
    stores variables used when processing article
    """
    
    def __init__(self, user_id):
        self.user_id = user_id
        self.url = None
        self.article_title = None
        self.article_content = None
        self.mime = None
        self.article_id = None
        self.article_instance_key = None
        self.image_url_list = None
        
        return None


def choose_a_db(user_id):
    if user_id <= LIMIT_USERS_ONE_DB:
        chosen_db = 'default'
    elif user_id <= 2 * LIMIT_USERS_ONE_DB:
        chosen_db = 'second'
    elif user_id <= 3 * LIMIT_USERS_ONE_DB:
        chosen_db = 'third'

    return chosen_db


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


def get_myarticle_instance_to_xml_etree(user_id, key):
    myarticle_instance = get_myarticle_instance_with_image_list(user_id, key)
    article = None
    if myarticle_instance:
        article = etree.Element('article', key=key)
        if not myarticle_instance.is_delete:
    
            title = etree.SubElement(article, 'title')
            title.text = myarticle_instance.title
        
            url = etree.SubElement(article, 'url')
            url.text = myarticle_instance.url
        
            download_url = etree.SubElement(article, 'download_url')
            download_url.text = get_data_url(BUCKET_NAME_ARTICLE, myarticle_instance.key)
        
            image_urls = etree.SubElement(article, 'image_urls')
            image_urls.text = '|'.join([get_data_url(BUCKET_NAME_IMAGE, image_key) \
                                        for image_key in myarticle_instance.image_list])
        
#            for image_key in myarticle_instance.image_list:
#                image_url = etree.SubElement(image_urls, 'image_url', key=image_key)
#                image_url.text = get_data_url(BUCKET_NAME_IMAGE, image_key)
            
            cover = etree.SubElement(article, 'cover')
            cover.text = myarticle_instance.cover
        
            is_star = etree.SubElement(article, 'is_star')
            is_star.text = TRUE_REPR if myarticle_instance.is_star else FALSE_REPR
        
            is_read = etree.SubElement(article, 'is_read')
            is_read.text = TRUE_REPR if myarticle_instance.is_read else FALSE_REPR
        
            create_time = etree.SubElement(article, 'create_time')
#            create_time.text = unicode(int(time.mktime(myarticle_instance.create_time.timetuple())))
            create_time.text = unicode(myarticle_instance.create_time)
        
    return article


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


def get_myarticle_list_to_xml_etree(user_id, offset, limit):
    myarticle_list = get_myarticle_list(user_id, offset, limit)
    articles = etree.Element('articles')
    for myarticle_instance in myarticle_list:
        myarticle_instance_xml_etree = get_myarticle_instance_to_xml_etree(user_id,
                                                                           myarticle_instance.key)
        if myarticle_instance_xml_etree is not None:
            articles.append(myarticle_instance_xml_etree)
    
    return articles


def generate_single_xml_etree(tag, text, **kwargs):
    element = etree.Element(tag, **kwargs) 
    element.text = text
    
    return element


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


def input_for_list_func(request):
    if request.method == 'GET':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.GET.get('access_token', '')
        offset = request.GET.get('offset', '')
        try:
            offset = int(offset)
        except:
            offset = 0
        limit = request.GET.get('limit', '')
        try:
            limit = int(limit)
        except:
            limit = DEFAULT_ARTICLE_LIST_LIMIT
    else:
        access_token_input = ''
        offset = 0
        limit = DEFAULT_ARTICLE_LIST_LIMIT
        
    return access_token_input, offset, limit


def input_for_show_func(request):
    if request.method == 'GET':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.GET.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_update_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
        url = request.POST.get('url', '')
    else:
        access_token_input = ''
        url = ''
        
    return access_token_input, url


def input_for_destroy_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
    else:
        access_token_input = ''
        
    return access_token_input


def input_for_modify_func(request):
    if request.method == 'POST':
        try:
            access_token_input = request.COOKIES['access_token']
        except:
            access_token_input = request.POST.get('access_token', '')
        modify_info = dict()
        args = ('is_delete', 'is_read', 'is_star')
        for arg in args:
            modify_info[arg] = request.POST.get(arg, '')
    else:
        access_token_input = ''
        modify_info = dict()
        
    return access_token_input, modify_info


##############################
# api2 input
##############################

    
def api2_input_for_article_id_common(request):
    if request.method == 'POST':
        article_id = long(request.POST.get('bookmark_id', None))
    else:
        article_id = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, article_id


def api2_input_for_count(request):
    if request.method == 'POST':
        folder_id = long(request.POST.get('folder_id', None))
    else:
        folder_id = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, folder_id


def api2_input_for_list(request):
    if request.method == 'POST':
        offset = request.POST.get('offset', None)
        try:
            offset = int(offset)
        except:
            offset = 0
        limit = request.POST.get('limit', None)
        try:
            limit = int(offset)
        except:
            limit = 20
        folder_name = request.POST.get('folder_name', None)
        order_by = request.POST.get('order_by', None)
    else:
        offset = 0
        limit = 20
        folder_name = None
        order_by = None
    access_token_input = api2_input_for_access_token_common(request)
        
    return access_token_input, offset, limit, folder_name, order_by


def api2_input_for_update_read_progress(request):
    if request.method == 'POST':
        progress = request.POST.get('read_progress', None)
        try:
            progress = float(progress)
        except:
            progress = None
        progress_timestamp = request.POST.get('read_progress_timestamp', None)
        try:
            progress_timestamp = datetime.fromtimestamp(float(progress_timestamp))
        except:
            progress_timestamp = None
    else:
        progress = None
        progress_timestamp = None
    access_token_input, article_id = api2_input_for_article_id_common(request)
    
    return access_token_input, article_id, progress, progress_timestamp


def api2_input_for_add(request):
    if request.method == 'POST':
        url = request.POST.get('url', None)
        title = request.POST.get('title', None)
        description = request.POST.get('description', None)
        folder_name = request.POST.get('folder_name', None)
        content = request.POST.get('content', None)
    else:
        url = None
        title = None
        description = None
        folder_name = None
        content = None
    access_token_input = api2_input_for_access_token_common(request)
    
    return access_token_input, url, title, description, folder_name, content


def api2_input_for_delete(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_update(request):
    if request.method == 'POST':
        title = request.POST.get('title', None)
        description = request.POST.get('description', None)
    else:
        title = None
        description = None
    access_token_input, article_id = api2_input_for_article_id_common(request)
    
    return access_token_input, article_id, title, description


def api2_input_for_view(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_star(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_unstar(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_archive(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_unarchive(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_move(request):
    if request.method == 'POST':
        folder_name = request.POST.get('folder_name', None)
    else:
        folder_name = None
    access_token_input, article_id = api2_input_for_article_id_common(request)
    
    return access_token_input, article_id, folder_name


def api2_input_for_get_text(request):
    
    return api2_input_for_article_id_common(request)


def api2_input_for_get_resource(request):
    
    return api2_input_for_article_id_common(request)

##############################
# api2 process
##############################


def api2_generate_article_key(article_id):
    article_key = KEY_FOLDER % article_id
    
    return article_key

def api2_convert_count_to_etree(article_count):
    meta = etree.Element('meta')
    
    count = etree.SubElement(meta, 'count')
    count.text = str(article_count)
    
    return meta


def api2_select_article_list(user_id, folder_name, order_by, offset, limit):
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
        

def api2_select_article(user_id, article_id):
    article_key = api2_generate_article_key(article_id)
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


def api2_modify_article_common(user_id, article, modify_info):
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
            

def api2_generate_resource_key(article_id):
    resource_key = KEY_RESOURCE % article_id
    
    return resource_key


class ArticleResource(object):
    """
    caching article related resource
    """
    
    def __init__(self, user_id, article_id):
        self.user_id = user_id
        self.article_id = article_id
        self.key = api2_generate_resource_key(self.article_id)
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
        article = api2_select_article(self.user_id, self.article_id)
        
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


def api2_get_or_create_article_resource(user_id, article_id):
    resource_key = api2_generate_resource_key(article_id)
    resource = cache.get(resource_key, None)
    if resource is None:
        resource = ArticleResource(user_id, article_id)
        
    return resource

    
##############################
# api2 output
##############################


def api2_convert_article_to_etree(article):
#    always have root node instead of None by default
    if article:
        bookmark_node = etree.Element('bookmark', id=article.id)
        
        url_node = etree.SubElement(bookmark_node)
        url_node.text = article.url
        
        title_node = etree.SubElement(bookmark_node)
        title_node.text = article.title
        
        description_node = etree.SubElement(bookmark_node)
        description_node.text = article.description
        
        is_star_node = etree.SubElement(bookmark_node)
        is_star_node.text = TRUE_REPR if article.is_star else FALSE_REPR
        
        is_read_node = etree.SubElement(bookmark_node)
        is_read_node.text = TRUE_REPR if (article.read_progress - 0 < ABSOLUTE_FLOAT_ERROR) else FALSE_REPR
        
        create_time_node = etree.SubElement(bookmark_node)
        create_time_node.text = unicode(int(time.mktime(article.create_time.timetuple())))
        
        read_time_node = etree.SubElement(bookmark_node)
        read_time_node.text = unicode(int(time.mktime(article.read_time.timetuple())))
        
        folder_name_node = etree.SubElement(bookmark_node)
        folder_name_node.text = article.folder_name
        
        progress_node = etree.SubElement(bookmark_node)
        progress_node.text = unicode(article.progress)
        
        version_node = etree.SubElement(bookmark_node)
        version_node.text = unicode(article.version)
        
        text_version_node = etree.SubElement(bookmark_node)
        text_version_node.text = unicode(article.text_version)
        
        is_ready_node = etree.SubElement(bookmark_node)
        is_ready_node.text = TRUE_REPR if article.is_ready else FALSE_REPR
    else:
        bookmark_node = etree.Element('bookmark')
    
    return bookmark_node


def api2_convert_article_list_to_etree(article_list):
#    always have root node instead of None by default
    package_node = etree.Element('package')
    for article in article_list:
        article_node = api2_convert_article_to_etree(article)
        package_node.append(article_node)
    
    return package_node


def api2_generate_resource_package_etree(images=None, audios=None, videos=None):
    package_node = etree.Element('package')
    
    for image in images:
        etree.SubElement(package_node, 'image', id=image.id)
        
    return package_node
# -*- coding: utf-8 -*-

from article.helper import generate_article_instance_key, \
    create_myarticle_instance, delete_myarticle_instance_in_db, mark_article_as_done
from celery.exceptions import SoftTimeLimitExceeded
from celery.task import Task
from celery.task.sets import TaskSet, subtask
from constants import BUCKET_NAME_ARTICLE, PAGE_FETCH_MAX_RETRIES, \
    PAGE_FETCH_DEFAULT_RETRY_DELAY, UPLOAD_ARTICLE_MAX_RETRIES, \
    UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY, PAGE_FETCH_TIME_LIMIT, \
    UPLOAD_ARTICLE_TIME_LIMIT, CLEAN_UP_TIME_BEFORE_KILLED
from image.helper import parse_and_replace_image_url_list, set_image_tobedone, \
    generate_image_tobedone_key
from image.tasks import DownloadImageHandler
from page.helper import delete_html_tag_attribute
from readability.readability import Document
from storage.helper import store_data_from_string
import logging
import urllib2

class PageFetchHandler(Task):
    '''
    fetch a single html page
    '''
    
    time_limit = PAGE_FETCH_TIME_LIMIT
    soft_time_limit = 10
    max_retries = PAGE_FETCH_MAX_RETRIES
    default_retry_delay = PAGE_FETCH_DEFAULT_RETRY_DELAY
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info):
        try:
            resource = urllib2.urlopen(update_article_info.url)
            raw_html = resource.read() 
            try:
                mime = resource.info()['Content-Type']
            except:
                mime = None
        except Exception, exc:
            try:
                PageFetchHandler.retry(exc=exc)
            except Exception, e:
                logging.warning(str(type(e)))
        else:
            update_article_info.mime = mime
#            call next step
            ReadableArticleHandler.delay(raw_html,
                                         update_article_info,
                                         callback=subtask(StoreArticleInfoHandler,
                                         callback=subtask(ImageUrlListHandler,
                                         callback=subtask(UploadArticleHandler,
                                         callback=subtask(BulkImageDownloadHandler,
                                         callback=subtask(DownloadImageHandler))))))
            
        return None
    

class ReadableArticleHandler(Task):
    '''
    make html readable
    '''
    
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def get_title(self, doc):
        
        return doc.short_title()
    
    def get_content(self, doc):
        
        return doc.summary()
    
    def run(self, raw_html, update_article_info, callback=None):
        try:
            doc = Document(raw_html)
            article_title = self.get_title(doc)
            article_content = self.get_content(doc)
        except Exception:
            pass
        else:
            update_article_info.article_title = article_title
            update_article_info.article_content = article_content
#            call next step
            subtask(callback).delay(update_article_info)
            
        return None


class StoreArticleInfoHandler(Task):
    """
    store article info to local db
    """
    
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info, callback=None):
        article_instance_key = generate_article_instance_key(update_article_info.url, update_article_info.user_id)
        try:
            article_instance = create_myarticle_instance(update_article_info.user_id, article_instance_key, update_article_info.article_title, update_article_info.url)
            article_id = article_instance.id
        except Exception:
            raise
            pass
        else:
            update_article_info.article_id = article_id
            update_article_info.article_instance_key = article_instance_key
#            call next step
            subtask(callback).delay(update_article_info)
            
        return None


class ImageUrlListHandler(Task):
    """
    parse image url list and replace them with identification in s3
    """
    
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info, callback=None):
        try:
            image_url_list, new_article_content = parse_and_replace_image_url_list(update_article_info.url, update_article_info.article_content, update_article_info)
        except Exception, exc:
            raise exc
        else:
            update_article_info.image_url_list = image_url_list
            update_article_info.article_content = new_article_content
#            call next step
            subtask(callback).delay(update_article_info)
            
        return None
    
    def on_failure(self, exc, task_id, args, kwargs, einfo):
        update_article_info, = args
        RollbackArticleInDbHandler.delay(update_article_info)
        
        return None


class UploadArticleHandler(Task):
    """
    upload article html to s3
    """
    
    time_limit = UPLOAD_ARTICLE_TIME_LIMIT
    soft_time_limit = time_limit - CLEAN_UP_TIME_BEFORE_KILLED
    max_retries = UPLOAD_ARTICLE_MAX_RETRIES
    default_retry_delay = UPLOAD_ARTICLE_DEFAULT_RETRY_DELAY
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info, callback=None):
        try:
            headers = dict()
            if update_article_info.mime:
                headers['Content-Type'] = update_article_info.mime
            store_data_from_string(BUCKET_NAME_ARTICLE,
                                   update_article_info.article_instance_key,
                                   update_article_info.article_content,
                                   headers=headers)
        except SoftTimeLimitExceeded, exc:
            raise exc
        except Exception, exc:
            logging.warn(str(exc))
            UploadArticleHandler.retry(exc=exc)
        else:
#            call next step
            subtask(callback).delay(update_article_info)
            
        return None
    
    def on_failure(self, exc, task_id, args, kwargs, einfo):
        update_article_info, = args
        RollbackArticleInDbHandler.delay(update_article_info)
        
        return None
    
    
class BulkImageDownloadHandler(Task):
    """
    start bulk image download
    """
    
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info, callback=None):
        try:
            image_tobedone_key = generate_image_tobedone_key(update_article_info.article_id)
            set_image_tobedone(image_tobedone_key, len(update_article_info.image_url_list))
        except Exception:
            pass
        else:
#            call next step
            if update_article_info.image_url_list:
                tasks = []
                for image_url in update_article_info.image_url_list:
                    image_task = subtask(callback)
#                    hack into this subtask to change its 'args'
                    image_task['args'] = (image_url, image_tobedone_key, update_article_info)
                    tasks.append(image_task)
                image_job = TaskSet(tasks=tasks)
                image_job.apply_async()
            else:
                mark_article_as_done(update_article_info)
        
        return None


class RollbackArticleInDbHandler(Task):
    """
    rollback article instance in db
    """
    
    ignore_result = True
    store_errors_even_if_ignored = True
    
    def run(self, update_article_info):
        delete_myarticle_instance_in_db(update_article_info.user_id,
                                        update_article_info.article_instance_key)
        
        return None

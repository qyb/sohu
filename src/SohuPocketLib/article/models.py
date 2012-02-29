# -*- coding: utf-8 -*-

from django.core.cache import cache
from django.db import models
from storage.models import MultiDB
from article.helper import generate_article_key


class MyArticleInstance(MultiDB):
    
#    user_id = models.IntegerField()
    key = models.CharField(max_length=256)
    title = models.CharField(max_length=512)
    url = models.URLField()
    is_read = models.BooleanField(default=False)   
    cover = models.CharField(max_length=128, blank=True)
    is_star = models.BooleanField(default=False)
#    is_archive = models.BooleanField(default=False)
    is_delete = models.BooleanField(default=False)
    create_time = models.DateTimeField(null=True, blank=True)
    read_time = models.DateTimeField(null=True, blank=True)
    delete_time = models.DateTimeField(null=True, blank=True)
    is_ready = models.BooleanField(default=False)
#    folder_name = models.CharField(max_length=256)

    def __unicode__(self):
        
        return u'%s,%s' % (self.user_id, self.title)
    
    def update_cache(self):
        cache.set(self.key, self)
        key = generate_article_key(self.id)
        cache.set(key, self)
        
        return None

    def delete_cache(self):
        cache.delete(self.key)
        key = generate_article_key(self.id)
        cache.set(key, self)
        
        return None

    def save(self):
        super(MyArticleInstance, self).save()
        self.update_cache()
        
        return None

    def delete(self):
        super(MyArticleInstance, self).delete()
        self.delete_cache()
        
        return None



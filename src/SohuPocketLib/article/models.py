# -*- coding: utf-8 -*-

from django.core.cache import cache
from django.db import models
from storage.models import MultiDB
import article


class MyArticleInstance(MultiDB):
    
#    user_id = models.IntegerField()
    key = models.CharField(max_length=256) # will be removed in the future
    title = models.CharField(max_length=512)
    url = models.URLField()
#    description = models.CharField(max_length=1024, blank=True)
    is_read = models.BooleanField(default=False) # will be removed in the future
    cover = models.CharField(max_length=128, blank=True) # will be removed in the future
    is_star = models.BooleanField(default=False)
#    is_archive = models.BooleanField(default=False)
    is_delete = models.BooleanField(default=False)
    create_time = models.DateTimeField(auto_now_add=True)
    read_time = models.DateTimeField(null=True, blank=True)
    delete_time = models.DateTimeField(null=True, blank=True)
    is_ready = models.BooleanField(default=False)
#    read_progress = models.FloatField(default=0.0)
#    folder_name = models.CharField(max_length=256, blank=True)
#    version = models.IntegerField(default=0)
#    text_version = models.IntegerField(default=0)

    def __unicode__(self):
        
        return u'%s,%s' % (self.user_id, self.title)
    
    def update_cache(self):
        self.generate_key()
        cache.set(self.key, self)
        
        return None

    def delete_cache(self):
        self.generate_key()
        cache.delete(self.key)
        
        return None

    def save(self):
        super(MyArticleInstance, self).save()
        self.update_cache()
        
        return None

    def delete(self):
        super(MyArticleInstance, self).delete()
        self.delete_cache()
        
        return None
    
    def generate_key(self):
        if self.key is None:
            self.key = article.helper.api2_generate_article_key(self.id)
            
        return None


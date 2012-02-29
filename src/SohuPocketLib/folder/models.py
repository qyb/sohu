# -*- coding: utf-8 -*-

from django.core.cache import cache
from django.db import models
from folder.helper import generate_folder_key
from storage.models import MultiDB


class Folder(MultiDB):
    
#    user_id = models.IntegerField()
    name = models.CharField(max_lenth=256)
    order = models.IntegerField(null=True, blank=True)
    
    def __unicode__(self):
        
        return u'name' % self.name
    
    def update_cache(self):
        key = generate_folder_key(self.user_id, self.name)
        cache.set(key, self)
        
        return None
    
    def delete_cache(self):
        key = generate_folder_key(self.user_id, self.name)
        cache.delete(key)
        
        return None
    
    def save(self):
        super(Folder, self).save()
        self.update_cache()
        
        return None
    
    def delete(self):
        super(Folder, self).save()
        self.delete_cache()
        
        return None
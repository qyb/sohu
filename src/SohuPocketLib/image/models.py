# -*- coding: utf-8 -*-

from storage.models import MultiDB
from django.core.cache import cache
from django.db import models


class MyImageInstance(MultiDB):
    """
    represent image, may include exif data in the future
    """

#    user_id = models.IntegerField()
    key = models.CharField(max_length=256)
    url = models.URLField()
    myarticle_instance_id = models.IntegerField()
    title = models.CharField(max_length=256, blank=True)
    description = models.CharField(max_length=1024, blank=True)
    width = models.IntegerField(null=True, blank=True)
    height = models.IntegerField(null=True, blank=True)
    
    def __unicode__(self):
        
        return unicode(self.url)
    
    def update_cache(self):
        cache.set(self.key, self)
        
        return None
    
    def delete_cache(self):
        cache.delete(self.key)
        
        return None
    
    def save(self):
        super(MyImageInstance, self).save()
        self.update_cache()
        
        return None
    
    def delete(self):
        super(MyImageInstance, self).save()
        self.delete_cache()
        
        return None
    
# -*- coding: utf-8 -*-

from storage.models import MultiDB
from django.core.cache import cache
from django.db import models


class MyArticleInstance(MultiDB):
    
#    user_id = models.IntegerField()
    key = models.CharField(max_length=256)                      #从S3获取静态文件
    title = models.CharField(max_length=512)                    #文章的标题
    url = models.URLField()                                     #文章原始URL
    is_read = models.BooleanField(default=False)                #是否已读   
    cover = models.CharField(max_length=128, blank=True)        #网页HEADER图片
    is_star = models.BooleanField(default=False)                #是否标记星标
    is_delete = models.BooleanField(default=False)              #是否删除
    create_time = models.DateTimeField(auto_now_add=True)       #文章创建时间
    read_time = models.DateTimeField(null=True, blank=True)     #标记为已读时间
    delete_time = models.DateTimeField(null=True, blank=True)   #标记为删除时间
    is_ready = models.BooleanField(default=False)               #是否已处理完毕

    def __unicode__(self):
        
        return u'%s,%s' % (self.user_id, self.title)
    
    def update_cache(self):
        cache.set(self.key, self)
        
        return None

    def delete_cache(self):
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

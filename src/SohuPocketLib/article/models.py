# -*- coding: utf-8 -*-

from django.db import models
from django.core.cache import cache

class MultiDB(models.Model):
    user_id = models.IntegerField()

    def save(self):
        if self.user_id <= LIMIT_USERS_ONE_DB:
            super(MultiDB,self).save(using='default')
        elif self.user_id <= 2*LIMIT_USERS_ONE_DB:
            super(MultiDB,self).save(using='second')
        elif self.user_id <= 3*LIMIT_USERS_ONE_DB:
            super(MultiDB,self).save(using='third')

    def delete(self):
        if self.user_id <= LIMIT_USERS_ONE_DB:
            super(MultiDB,self).delete(using='default')
        elif self.user_id <= 2*LIMIT_USERS_ONE_DB:
            super(MultiDB,self).delete(using='second')
        elif self.user_id <= 3*LIMIT_USERS_ONE_DB:
            super(MultiDB,self).delete(using='third')

    class Meta:
        abstract = True

class MyArticleInstance(MultiDB):
    #user_id = models.IntegerField()
    key = models.CharField(max_length=32)        #从S3获取静态文件
    title = models.CharField(max_length=512)     #文章的标题
    url = models.CharField(max_length=512)       #文章原始URL
    is_read = models.BooleanField(default=False) #是否已读   
    cover = models.CharField(max_length=128)     #网页HEADER图片
    is_star = models.BooleanField(default=False) #是否标记星标

    def __unicode__(self):
        return u'%s,%s' % (self.user_id, self.title)
    
    def update_cache(self):
        key = CACHE_KEY_USER_ARTICLE_INSTANCE % (self.user_id, self.key)
        cache.set(key, self)

    def delete_cache(self):
        key = CACHE_KEY_USER_ARTICLE_INSTANCE % (self.user_id, self.key)
        cache.delete(key)

    def save(self):
        super(MyArticleInstance, self).save()
        self.update_cache()

    def delete(self):
        self.delete_cache()
        super(MyArticleInstance, self).delete()
    
    class Meta:
        unique_together = (('user_id', 'key'),)
        db_table = 'user_article_instance'

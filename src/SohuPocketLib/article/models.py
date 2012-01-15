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
    key = models.CharField(max_length=32)                       #从S3获取静态文件
    title = models.CharField(max_length=512)                    #文章的标题
    url = models.CharField(max_length=512)                      #文章原始URL
    is_read = models.BooleanField(default=False)                #是否已读   
    cover = models.CharField(max_length=128, blank=True)                    #网页HEADER图片
    is_star = models.BooleanField(default=False)                #是否标记星标
    is_delete = models.BooleanField(default=False)              #是否删除
    create_time = models.DateTimeField(auto_now_add=True)       #文章创建时间
    readed_time = models.DateTimeField(null=True, blank=True)   #标记为已读时间
    delete_time = models.DateTimeField(null=True, blank=True)   #标记为删除时间

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

class MyArticleInstanceStatus(models.Model):
    """
    record article transcode status
    """
    ready = models.BooleanField(default = False)
    number_of_images_to_download = models.IntegerField()
    number_of_images_downloaded = models.IntegerField()
    number_of_images_download_failed = models.IntegerField()
    
    def __unicode__(self):
        return unicode(self.ready)
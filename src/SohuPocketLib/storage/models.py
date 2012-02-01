# -*- coding: utf-8 -*-

from constants import LIMIT_USERS_ONE_DB
from django.db import models


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
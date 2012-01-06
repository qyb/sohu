# -*- coding: utf-8 -*-
'''
Created on 2012-1-5

@author: diracfang
'''
BROKER_URL = "amqp://guest:guest@localhost:5672//"
CELERY_RESULT_BACKEND = "amqp"
CELERY_IMPORTS = ("readable_article", )
CELERY_RESULT_BACKEND = "amqp"

#: We want the results to expire in 5 minutes, note that this requires
#: RabbitMQ version 2.1.1 or higher, so please comment out if you have
#: an earlier version.
CELERY_TASK_RESULT_EXPIRES = 300
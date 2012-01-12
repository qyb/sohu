# -*- coding: utf-8 -*-

import hashlib
import logging

from django.http import HttpResponse

from SohuPocketLib.storage import helper

from readable import ReadableArticleHandler
from article.tasks import PageFetcher

bucketName = 'sohu_kan_test'

def post(request, url):
    '''
    post a url to cloud
    '''
    readableTitle, readableArticle = get_and_clean_article(url)
    keyName = hashlib.new('sha1', readableTitle).hexdigest()
    returnKey = helper.store_private_data_from_string(bucketName, keyName, readableArticle)
    return HttpResponse(str(returnKey))

def get_string_from_url(request, url):
    '''
    get article string from cloud
    '''
    readableTitle, readableArticle = get_and_clean_article(url)
#    keyName = hashlib.new('sha1', readableTitle).hexdigest()
#    dataString = helper.get_private_data_to_string(bucketName, keyName)
#    return HttpResponse(dataString)
    return HttpResponse(readableArticle)

def get_and_clean_article(url):
    '''
    return article title and body
    '''
    originalPage = PageFetcher.delay(url)
    rah = ReadableArticleHandler()
    readableTitle = ''
    readableArticle = ''
    try:
        rah.feed(originalPage.get())
        readableArticle = rah.get_readable_article()
        readableTitle = rah.get_readable_title()
    except Exception:
        logging.warning('error when parsing article')
    return readableTitle, readableArticle
# -*- coding: utf-8 -*-

import hashlib

from django.http import HttpResponse

from readable import ReadableArticleHandler
from fetch import PageFetcher

from SohuPocketLib.storage import helper

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
    keyName = hashlib.new('sha1', readableTitle).hexdigest()
    dataString = helper.get_private_data_to_string(bucketName, keyName)
    return HttpResponse(dataString)

def get_and_clean_article(url):
    '''
    return article title and body
    '''
    fetcher = PageFetcher()
    originalPage = fetcher.single_page(url)
    rah = ReadableArticleHandler()
    rah.feed(originalPage)
    readableArticle = rah.get_readable_article()
    readableTitle = rah.get_readable_title()
    return (readableTitle, readableArticle)
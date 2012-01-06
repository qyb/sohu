# -*- coding: utf-8 -*-

import hashlib

from django.http import HttpResponse

from readable import ReadableArticleHandler
from fetch import PageFetcher

from SohuPocketLib.storage import helper

def post(request, url):
    '''
    post a url to db
    '''
    readableTitle, readableArticle = get_and_clean_article(url)
    bucketName = helper.get_or_create_bucket('sohu_kan_test')
    keyName = hashlib.new('sha1', readableTitle).hexdigest()
    returnKey = helper.store_private_data_from_string(bucketName, keyName, readableArticle)
    return HttpResponse('key : ' + str(returnKey))

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
    return (readableArticle, readableTitle)
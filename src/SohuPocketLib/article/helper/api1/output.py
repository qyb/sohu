# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def get_myarticle_instance_to_xml_etree(user_id, key):
    myarticle_instance = get_myarticle_instance_with_image_list(user_id, key)
    article = None
    if myarticle_instance:
        article = etree.Element('article', key=key)
        if not myarticle_instance.is_delete:
    
            title = etree.SubElement(article, 'title')
            title.text = myarticle_instance.title
        
            url = etree.SubElement(article, 'url')
            url.text = myarticle_instance.url
        
            download_url = etree.SubElement(article, 'download_url')
            download_url.text = get_data_url(BUCKET_NAME_ARTICLE, myarticle_instance.key)
        
            image_urls = etree.SubElement(article, 'image_urls')
            image_urls.text = '|'.join([get_data_url(BUCKET_NAME_IMAGE, image_key) \
                                        for image_key in myarticle_instance.image_list])
        
#            for image_key in myarticle_instance.image_list:
#                image_url = etree.SubElement(image_urls, 'image_url', key=image_key)
#                image_url.text = get_data_url(BUCKET_NAME_IMAGE, image_key)
            
            cover = etree.SubElement(article, 'cover')
            cover.text = myarticle_instance.cover
        
            is_star = etree.SubElement(article, 'is_star')
            is_star.text = TRUE_REPR if myarticle_instance.is_star else FALSE_REPR
        
            is_read = etree.SubElement(article, 'is_read')
            is_read.text = TRUE_REPR if myarticle_instance.is_read else FALSE_REPR
        
            create_time = etree.SubElement(article, 'create_time')
#            create_time.text = unicode(int(time.mktime(myarticle_instance.create_time.timetuple())))
            create_time.text = unicode(myarticle_instance.create_time)
        
    return article


def get_myarticle_list_to_xml_etree(user_id, offset, limit):
    myarticle_list = get_myarticle_list(user_id, offset, limit)
    articles = etree.Element('articles')
    for myarticle_instance in myarticle_list:
        myarticle_instance_xml_etree = get_myarticle_instance_to_xml_etree(user_id,
                                                                           myarticle_instance.key)
        if myarticle_instance_xml_etree is not None:
            articles.append(myarticle_instance_xml_etree)
    
    return articles


def generate_single_xml_etree(tag, text, **kwargs):
    element = etree.Element(tag, **kwargs) 
    element.text = text
    
    return element
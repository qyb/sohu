# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def convert_article_to_etree(article):
#    always have root node instead of None by default
    if article:
        bookmark_node = etree.Element('bookmark', id=article.id)
        
        url_node = etree.SubElement(bookmark_node)
        url_node.text = article.url
        
        title_node = etree.SubElement(bookmark_node)
        title_node.text = article.title
        
        description_node = etree.SubElement(bookmark_node)
        description_node.text = article.description
        
        is_star_node = etree.SubElement(bookmark_node)
        is_star_node.text = TRUE_REPR if article.is_star else FALSE_REPR
        
        is_read_node = etree.SubElement(bookmark_node)
        is_read_node.text = TRUE_REPR if (article.read_progress - 0 < ABSOLUTE_FLOAT_ERROR) else FALSE_REPR
        
        create_time_node = etree.SubElement(bookmark_node)
        create_time_node.text = unicode(int(time.mktime(article.create_time.timetuple())))
        
        read_time_node = etree.SubElement(bookmark_node)
        read_time_node.text = unicode(int(time.mktime(article.read_time.timetuple())))
        
        folder_name_node = etree.SubElement(bookmark_node)
        folder_name_node.text = article.folder_name
        
        progress_node = etree.SubElement(bookmark_node)
        progress_node.text = unicode(article.progress)
        
        version_node = etree.SubElement(bookmark_node)
        version_node.text = unicode(article.version)
        
        text_version_node = etree.SubElement(bookmark_node)
        text_version_node.text = unicode(article.text_version)
        
        is_ready_node = etree.SubElement(bookmark_node)
        is_ready_node.text = TRUE_REPR if article.is_ready else FALSE_REPR
    else:
        bookmark_node = etree.Element('bookmark')
    
    return bookmark_node


def convert_article_list_to_etree(article_list):
#    always have root node instead of None by default
    package_node = etree.Element('package')
    for article in article_list:
        article_node = convert_article_to_etree(article)
        package_node.append(article_node)
    
    return package_node


def generate_resource_package_etree(images=None, audios=None, videos=None):
    package_node = etree.Element('package')
    
    for image in images:
        etree.SubElement(package_node, 'image', id=image.id)
        
    return package_node

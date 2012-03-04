# -*- coding: utf-8 -*-

'''
Created on Mar 4, 2012

@author: diracfang
'''


def list(request, response_format):
    access_token_input, offset, limit = input_for_list_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if response_format == 'xml':
            myarticle_list_etree = get_myarticle_list_to_xml_etree(kan_user.get_user_id(),
                                                                   offset,
                                                                   limit)
            response = etree.tostring(myarticle_list_etree,
                                      xml_declaration=True,
                                      encoding='utf-8')
            mimetype = 'text/xml'
            
    return HttpResponse(response, mimetype=mimetype)


def list_test(request, *args, **kwargs):
    
    return render_to_response('article_list_test.html',
                              context_instance=RequestContext(request))


def show(request, key, response_format):
    access_token_input = input_for_show_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    mimetype = 'text/plain'
    logging.warning(str(request.GET))
    if kan_user.is_logged_in():
        if response_format == 'xml':
            myarticle_instance_etree = get_myarticle_instance_to_xml_etree(kan_user.get_user_id(),
                                                                           key)
            if myarticle_instance_etree is not None:
                response = etree.tostring(myarticle_instance_etree,
                                          xml_declaration=True,
                                          encoding='utf-8')
                mimetype = 'text/xml'
            
    return HttpResponse(response, mimetype=mimetype)
    

def show_test(request, *args, **kwargs):
    
    return render_to_response('article_show_test.html',
                              context_instance=RequestContext(request))

    
def update(request, response_format):
    access_token_input, url = input_for_update_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    logging.warning(str(request.POST))
    mimetype = 'text/plain'
    if kan_user.is_logged_in():
        update_article_info = RuntimeArticleInfo(kan_user.get_user_id(), url)
        try:
            PageFetchHandler.delay(update_article_info)
        except Exception:
            pass
        else:
            if response_format == 'xml':
                response_etree = generate_single_xml_etree('status', 'success')
                response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
                mimetype = 'text/xml'
                
    return HttpResponse(response, mimetype=mimetype)


def update_test(request, *args, **kwargs):
    
    return render_to_response('article_update_test.html',
                              context_instance=RequestContext(request))


def destroy(request, key, response_format):
    access_token_input = input_for_destroy_func(request)
    logging.warning(str(request.POST))
    modify_info = dict()
    modify_info['is_delete'] = TRUE_REPR
    
    return modify_or_destroy_base(access_token_input, modify_info, key, response_format)

        
def destroy_test(request, *args, **kwargs):
    
    return render_to_response('article_destroy_test.html',
                              context_instance=RequestContext(request))

        
def modify(request, key, response_format):
    access_token_input, modify_info = input_for_modify_func(request)
    logging.warning(str(request.POST))
    
    return modify_or_destroy_base(access_token_input, modify_info, key, response_format)


def modify_test(request, *args, **kwargs):
    
    return render_to_response('article_modify_test.html',
                              context_instance=RequestContext(request))


def modify_or_destroy_base(access_token_input, modify_info, key, response_format):
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    response = None
    is_successful = False
    mimetype = 'text/plain'
    if kan_user.is_logged_in():
        user_id = kan_user.get_user_id()
        is_successful = modify_or_destroy_myarticle_instance(user_id, key, modify_info)
    if response_format == 'xml':
        response_etree = generate_single_xml_etree('status', 'success' if is_successful else 'fail')
        response = etree.tostring(response_etree, xml_declaration=True, encoding='utf-8')
        mimetype = 'text/xml'
        
    return HttpResponse(response, mimetype=mimetype)

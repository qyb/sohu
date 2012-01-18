# -*- coding: utf-8 -*-

from django.http import HttpResponse

def list(request, format='xml'):
    access_token_input = input_for_list_func(request)
    kan_user = KanUser('', access_token_input)
    kan_user.verify_and_login()
    

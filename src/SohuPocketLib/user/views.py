# -*- coding: utf-8 -*-

from django.http import HttpResponse
from helper import KanUser
from helper import get_user_info_for_web, set_user_info_for_web
from helper import extract_class_instance_to_dict
from helper import serialize

def check_and_login(request):
    sohupassport_uuid, access_token_input = get_user_info_for_web(request)
    kan_user = KanUser(sohupassport_uuid, access_token_input)
    kan_user.check_and_login()
    if kan_user.is_logged_in():
        response_dict = extract_class_instance_to_dict(kan_user.get_user())
        response_dict['access_token'] = kan_user.get_access_token()
        response = HttpResponse(serialize(response_dict))
        set_user_info_for_web(response, kan_user.get_sohupassport_uuid(), kan_user.get_access_token())
    else:
        response = HttpResponse(serialize(None))
    return response
# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    (r'^admin/doc/', include('django.contrib.admindocs.urls')),
    (r'^admin/', include(admin.site.urls)),
    
    (r'^$', 'SohuPocketLib.views.home'),
    (r'^passport/$', 'SohuPocketLib.views.passport'),
    (r'^user/$', 'SohuPocketLib.user.views.check_and_login'),
#   (r'^user/me/$', 'SohuPocketLib.user.views.me_handler'),
#   (r'^user/(?P<user_id>\d+)/$', 'SohuPocketLib.user.view.user_handler'),
#   (r'^article/$', 'SohuPocketLib.user.views.article_list_handler'),
#   (r'^article/(?P<article_id>\d+)/$', 'SohuPocketLib.user.views.article_handler'),
)

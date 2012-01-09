# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),
    url(r'^admin/', include(admin.site.urls)),
    
    url(r'^$', 'SohuPocketLib.views.home'),
    
    url(r'^passport/$', 'SohuPocketLib.views.passport'),
    
#    url(r'^user/$',
#        'SohuPocketLib.user.views.check_and_login'),
#    url(r'^user/me/$',
#        'SohuPocketLib.user.views.me_handler'),
#    url(r'^user/(?P<user_id>\d+)/$',
#        'SohuPocketLib.user.view.user_handler'),
#    url(r'^article/$',
#        'SohuPocketLib.user.views.article_list_handler'),
#    url(r'^article/(?P<article_id>\d+)/$',
#        'SohuPocketLib.user.views.article_handler'),
)
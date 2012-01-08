# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    url(r'^$', 'SohuPocketLib.views.home', name = 'home'),
    # url(r'^SohuPocketLib/', include('SohuPocketLib.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
    
    url(r'^passport/$', 'SohuPocketLib.views.passport', name = 'passport'),
    
    url(r'^user/login/$',
        'SohuPocketLib.user.views.login',
        name = 'login'),
                       
    url(r'^article/post/(?P<url>.*)$',
        'SohuPocketLib.article.views.post',
        name = 'article post'),
    url(r'^article/get/(?P<url>.*)$',
        'SohuPocketLib.article.views.get_string_from_url',
        name = 'article get'),
)

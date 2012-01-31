# -*- coding: utf-8 -*-
from django.conf.urls.defaults import patterns, include

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    (r'^admin/doc/', include('django.contrib.admindocs.urls')),
    (r'^admin/', include(admin.site.urls)),
    
    (r'^$', 'SohuPocketLib.views.home'),
    (r'^passport/$', 'SohuPocketLib.views.passport'),
    
    (r'^user/verify\.json/$', 'SohuPocketLib.user.views.verify'),
    (r'^user/verify\.json/test/$', 'SohuPocketLib.user.views.verify_test'),
    
    (r'^user/show\.json/$', 'SohuPocketLib.user.views.show'),
    (r'^user/show\.json/test/$', 'SohuPocketLib.user.views.show_test'),

    (r'^user/update\.json/$', 'SohuPocketLib.user.views.update'),
    (r'^user/update\.json/test/$', 'SohuPocketLib.user.views.update_test'),
    
    (r'^article/update\.(?P<format>\w+)/$', 'SohuPocketLib.article.views.update'),
    (r'^article/update\.(?P<format>\w+)/test/$', 'SohuPocketLib.article.views.update_test'),
    
    (r'^article/list\.(?P<format>\w+)/$', 'SohuPocketLib.article.views.list'),
    (r'^article/list\.(?P<format>\w+)/test/$', 'SohuPocketLib.article.views.list_test'),
    
    (r'^article/show/(?P<key>\w+)\.(?P<format>\w+)/$', 'SohuPocketLib.article.views.show'),
    (r'^article/modify/(?P<key>\w+)\.(?P<format>\w+)/$', 'SohuPocketLib.article.views.modify'),
    (r'^article/destroy/(?P<key>\w+)\.(?P<format>\w+)/$', 'SohuPocketLib.article.views.destroy'),
)

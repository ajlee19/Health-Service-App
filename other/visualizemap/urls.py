from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^base.html$', views.main, name='main'),
    url(r'^_intro.html$', views.intro, name='intro'),
    url(r'^_content.html$', views.content, name='content'),
    url(r'^_contact.html$', views.contact, name='contact'),
]

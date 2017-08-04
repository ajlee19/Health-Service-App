from django.http import HttpResponse
from django.template import Context, Template, loader

def index(request):
    template = loader.get_template('base.html')
    return HttpResponse(template.render())

def intro(request):
    template = loader.get_template('_intro.html')
    return HttpResponse(template.render())

def content(request):
    template = loader.get_template('_content.html')
    return HttpResponse(template.render())

def contact(request):
    template = loader.get_template('_contact.html')
    return HttpResponse(template.render())

def main(request):
    template = loader.get_template('base.html')
    return HttpResponse(template.render())

# -*- coding: utf-8 -*-

import Image

def scale_image(img_path, width=None, height=None):
    if not isinstance(img_path, basestring):
        return 'parameter error'

    try:
        im = Image.open(img_path)
    except:
        return 'file can not found'
    if not im:
        return 'file can not found'

    if not height and not width:
        return im

    old_width, old_height = im.size
    if height and width:
        im = im.resize((width, height), Image.ANTIALIAS)
    elif width:
        new_height = old_height * (old_width / width)
        im = im.resize((width, new_height), Image.ANTIALIAS)
    elif height:
        new_width = old_width * (old_height / height)
        im = im.resize((new_width, height), Image.ANTIALIAS)
    im = im.convert('RGB')

    return im

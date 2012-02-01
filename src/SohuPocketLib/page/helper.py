# -*- coding: utf-8 -*-

from BeautifulSoup import BeautifulSoup


def delete_html_tag_attribute(html_string): 
    soup = BeautifulSoup(html_string)
    allTags = soup.findAll(True)
    for tag in allTags:
        for attr in tag.attrs:
            if attr[0] in ['src', 'href', 'alt']:
                continue
            elif attr[0] == 'target':
                tag.attrs[tag.attrs.index(attr)] = ('target', '_blank')
            else:
                tag.attrs.remove(attr)

    return allTags[0].contents[0]

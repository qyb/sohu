'''
Created on 2012-2-29

@author: Leon

'''

#-*-encoding=gbk-*-
from interfaces import IFilter

class Purifier(IFilter):
    '''
                            建立一个链表，任何一个请求清洗、转换的过程，需要通过这样一个链表，逐步走下去，这样便于我们通过配置不同的过滤部件，来改变清洗和转换的流程.
                            最为重要的是，需要建立一个集合，对已经清洗，未清洗，和清洗效果，建立一个评价系统，通过评价，调整策略，通过评价来反馈改变未来清洗的方式，这可以说是一种迭代进步的过程。
    '''
    def __init__(self):
        IFilter.__init__(self)
    
    def onProcess(self, request):
        IFilter.onProcess(self, request)
    
    

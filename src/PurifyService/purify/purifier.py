'''
Created on 2012-2-29

@author: Leon

'''

#-*-encoding=gbk-*-
from interfaces import IFilter

class Purifier(IFilter):
    '''
                            ����һ�������κ�һ��������ϴ��ת���Ĺ��̣���Ҫͨ������һ������������ȥ��������������ͨ�����ò�ͬ�Ĺ��˲��������ı���ϴ��ת��������.
                            ��Ϊ��Ҫ���ǣ���Ҫ����һ�����ϣ����Ѿ���ϴ��δ��ϴ������ϴЧ��������һ������ϵͳ��ͨ�����ۣ��������ԣ�ͨ�������������ı�δ����ϴ�ķ�ʽ�������˵��һ�ֵ��������Ĺ��̡�
    '''
    def __init__(self):
        IFilter.__init__(self)
    
    def onProcess(self, request):
        IFilter.onProcess(self, request)
    
    

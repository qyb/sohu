#!/usr/bin/env python
# coding = utf-8

'''
Created on 2012-2-15

@author: Samuel
'''
import unittest
from scss import helper

class ObjectTest(unittest.TestCase):


    def setUp(self):
        pass


    def tearDown(self):
        pass


#    def testPostObject(self):
#        key = helper.store_data_from_string("boto-test", "obj1", "Test stroe_data_from_string")
#        print "store_data_from_string : ", key
#        assert(None != key)
#
#    def testGetObject(self):
#        data = helper.get_data_to_string("boto-test", "obj1")
#        print "get_data_to_string : ", data
#        assert("Test stroe_data_from_string" == data)
#
#    def test_store_data_from_filename(self):
#        key = helper.store_data_from_filename("boto-test", "obj2", "Sunset.jpg")
#        print "store_data_from_filename : ", key
#        assert(None != key)
#
#    def test_get_data_to_filename(self):
#        import os
#        try:
#            os.remove("Sunset.Download.jpg");
#        except:
#            pass
#        data = helper.get_data_to_filename("boto-test", "obj2", "Sunset.Download.jpg")
#        print "get_data_to_filename : ", data
#        assert (os.path.exists("Sunset.Download.jpg"));
#        st = os.stat("Sunset.Download.jpg")
#        assert(None != st)
#        assert(st[6] == 71189)
        
    def test_800K_img(self):
        import os
        key = helper.store_data_from_filename("boto-test", "obj3", "800K.jpg")
        print "store_data_from_filename : ", key
        assert(None != key)
        
        try:
            os.remove("800K.Download.jpg");
        except:
            pass
        data = helper.get_data_to_filename("boto-test", "obj3", "800K.Download.jpg")
        print "get_data_to_filename : ", data
        assert (os.path.exists("800K.Download.jpg"));
        st = os.stat("800K.Download.jpg")        
        assert(None != st)
        assert(st[6] == 879394)

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
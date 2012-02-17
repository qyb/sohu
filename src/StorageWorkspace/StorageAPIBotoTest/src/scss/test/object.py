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


    def testPostObject(self):
        key = helper.store_data_from_string("sam_bucket", "obj1", "Test stroe_data_from_string")
        print key


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
#!/usr/bin/env python
# coding = utf-8

'''
Created on 2012-2-15

@author: Samuel
'''
import unittest
import scss.helper as helper

class AuthorizationTest(unittest.TestCase):


    def setUp(self):
        pass


    def tearDown(self):
        pass


    def testAuthorization(self):
        s3 = helper.build_connect_s3();


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
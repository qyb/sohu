#!/usr/bin/env python
# coding = utf-8

'''
Created on 2012-2-15

@author: Samuel
'''
import unittest

from scss import helper

class BucketTest(unittest.TestCase):


    def setUp(self):
        pass


    def tearDown(self):
        pass


    def testGetOrCreateBucket(self):
        bucket = helper.get_or_create_bucket("boto-test")
        print bucket
        assert (None != bucket)
        bucket = helper.get_or_create_bucket("not-exist-bucket")
        assert (None == bucket)

        


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
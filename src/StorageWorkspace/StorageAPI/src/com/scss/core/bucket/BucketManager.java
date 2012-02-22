/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.util.List;

import com.scss.core.object.ObjectResource;
import com.scss.core.region.LocationSubRes;

/**
 * @author Samuel
 *
 */
public class BucketManager {
	
	// TODO: Think using separate API classes (PUT_BUCKET.java) instead. easy to maintain.
	
	/*
	 * GET SERVICE
	 */
	public static List<String> getAllBuckets() {
		return null;
	}
	
	/*
	 * PUT Bucket
	 */
	public static Boolean createBucket(String name) {
		//TODO: Async or sync ? Need to consider.
		String path = "./fakebfs/" + name;
		java.io.File f = new java.io.File(path);
		if (f.exists())
			System.out.printf(">> --- Bucket '%s' is existing!\n", name);
		else {
			f.mkdir();
			System.out.printf(">> --- Bucket '%s' is created.\n", name);
		}
			
		return true;
	}
	
	/*
	 * GET Bucket
	 * TODO: re-think if it need to be put into ObjectManager.
	 */
	public static List<String> getBucketObjects(String name) {
		return null;
	}
	
	/*
	 * GET Bucket location
	 */
	public static LocationSubRes getBucketLocation(String name) {
		return null;
	}
	
}

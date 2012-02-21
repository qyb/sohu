package com.scss.db;

import java.util.List;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;
import com.scss.db.service.DBServiceHelper;

public class BucketBussiness {
	
	
	 /**
     * delete a Bucket
     * @param Name
     * @param access_key
     */
	public static void deleteBucket(String Name, String access_key) 
	{
		
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		if(null!=scssUser)
		{
			DBServiceHelper.deleteBucket(Name, scssUser.getId());
		}
		
		
	}
	
	/**
	 * create a bucket
	 * @param name
	 * @param access_key
	 * @param meta
	 * @return
	 */
	public static boolean putBucket(String name,String access_key, String meta)
	{
		
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		if(null!=scssUser)
		{	
			try {
				
				DBServiceHelper.putBucket(name, scssUser.getId(), meta);
			} catch (SameNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		
		return true;
		
	}
	
	/**
	 * retrieve a object list belongs to a bucket
	 * @param access_key
	 * @param name
	 * @return
	 */
	public static List<ScssObject> getBucket(String access_key, String name)
	{
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		List<ScssObject> bucket_objects=null;
		
		if(null!=scssUser)
		{
		   bucket_objects = DBServiceHelper.getBucket(scssUser.getId(), name);
		}
		
		return bucket_objects;
		
	}
	

}

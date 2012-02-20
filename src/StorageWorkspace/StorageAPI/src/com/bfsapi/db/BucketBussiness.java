package com.bfsapi.db;

import java.util.List;

import com.bfsapi.db.model.ScssBucket;
import com.bfsapi.db.model.ScssObject;
import com.bfsapi.db.model.ScssUser;
import com.bfsapi.db.service.DBServiceHelper;

/**
 * 
 * @author yangwei
 *
 */
public class BucketBussiness {
	
	
    /**
     * 用户删除一个bucket
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
	 * 用户存入一个bucket
	 * @param name
	 * @param access_key
	 * @param meta
	 * @return
	 */
	public static ScssBucket putBucket(String name,String access_key, String meta)
	{
		
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		ScssBucket bucket=null;
		
		if(null!=scssUser)
		{
		    bucket = (ScssBucket)DBServiceHelper.putBucket(name, scssUser.getId(), meta);
		}
		
		return bucket;
		
	}
	
	/**
	 * 根绝accesskey得到一个bucket
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

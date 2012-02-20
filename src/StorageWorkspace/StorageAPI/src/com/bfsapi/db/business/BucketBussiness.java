package com.bfsapi.db.business;

import com.bfsapi.db.model.ScssBucket;
import com.bfsapi.db.model.ScssUser;
import com.bfsapi.db.service.DBServiceHelper;

/**
 * 
 * @author yangwei
 *
 */
public class BucketBussiness {
	
	
    /**
     * �û�ɾ��һ��bucket
     * @param Name
     * @param access_key
     */
	public static void deleteBucket(String Name, String access_key) 
	{
		
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		DBServiceHelper.deleteBucket(Name, scssUser.getId());
		
		
	}
	
	/**
	 * �û�����һ��bucket
	 * @param name
	 * @param access_key
	 * @param meta
	 * @return
	 */
	public static ScssBucket putBucket(String name,String access_key, String meta)
	{
		
		ScssUser scssUser = DBServiceHelper.getUserByAccessKey(access_key);
		
		ScssBucket bucket = (ScssBucket)DBServiceHelper.putBucket(name, scssUser.getId(), meta);
		
		return bucket;
		
	}
	
	
	
	

}

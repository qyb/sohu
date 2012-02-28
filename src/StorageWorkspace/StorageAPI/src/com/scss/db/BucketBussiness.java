package com.scss.db;

import java.util.List;

import com.scss.core.object.BfsClientWrapper;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
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
	/**
	 * 根据objectkey,ower_id和bucketName删除一个对象
	 * @param key
	 * @param owner_ID
	 * @param bucketName
	 * @return
	 */
	public static String deleteObject(String key, Long owner_ID, String bucketName)
	{
		
		ScssBucket  scssBucket=DBServiceHelper.getBucketByName(bucketName,owner_ID) ;
		
		if(null!=scssBucket)
		{
			
			ScssObject obj = DBServiceHelper.getObject(bucketName, key);
			
			if(null!=obj&&null != obj.getKey() && null != obj.getBfsFile())
			{
				
				try {
					DBServiceHelper.deleteObject(key, owner_ID, scssBucket.getId());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				 try {
					 
					  BfsClientWrapper.getInstance().deleteFile(obj.getBfsFile());
					  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//如果失败需要记录下来
				}
					
				return "ok"; 
				
			}
			else
			{
				return "NoSuchObject"; 
			}
			
		}
		else
		{
			return "NoSuchBucket";
		}
		
	}
	
	/**
	 * 删除bucket
	 * @param owner_ID
	 * @param bucketName
	 * @return error Message
	 */
	public static String deleteBucket(Long owner_ID, String bucketName)
	{
		ScssBucket  scssBucket=DBServiceHelper.getBucketByName(bucketName,owner_ID);
		
		if(null!= scssBucket)
		{
			List<ScssObject> scssObjectList = DBServiceHelper.getBucket(bucketName);
			
			if(null!=scssObjectList&&scssObjectList.size()>0)
			{
				return "DeleteBucketBeforeDeleteObject";   //删除这个bucket前它下面的object必须为空
			}
			else
			{
				DBServiceHelper.deleteBucket(bucketName, owner_ID);
				
				return "ok";
			}
			
		}
		else
		{
			   return "NoSuchBucket";
		}
		
		
		
	}
	

	

}

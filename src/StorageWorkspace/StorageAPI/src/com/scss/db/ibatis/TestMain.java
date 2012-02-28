package com.scss.db.ibatis;

import java.sql.SQLException;
import java.util.List;

import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssGroupDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;

public class TestMain {
	@SuppressWarnings( { "unused", "unchecked" })
	public static void main(String args[]) throws SQLException,
			SameNameException {

		// User Test ing
		ScssUserDaoImpl sud = ScssUserDaoImpl.getInstance();
		// List userList = sud.getUserList();
		// ScssUser user = new ScssUser("sohuId+++++@", "access_key++++", "B");
		ScssUser scssUser = sud.getUserById(1l);
		// user = sud.getUserBySohuId("sohu.com.jack");
		// user = sud.getUserByAccessKey("access_key++++");
		// user.setAccessKey("aaaa&&&&&a");
		// sud.updateUser(user);

		// Test Group
		// ScssGroupDaoImpl sgd = ScssGroupDaoImpl.getInstance();
		// ScssGroup group = sgd.getGroupById(1l);
		// group.setName("ÓÃ»§×é");
		// sgd.updateGroup(group);
		// List<ScssUser> usersByGroup = sud.getUsersByGroup(group);
		// System.out.println();

		// Test Bucket
		ScssBucketDaoImpl sbd = ScssBucketDaoImpl.getInstance();
		List<ScssBucket> bucketsByUser = sbd.getBucketsByUser(scssUser);

		ScssBucket bucket = sbd.getBucket("boto-test122111");
		bucket = sbd.getBucket(bucket.getId());
		bucket = sbd.getBucket(bucket);
		bucket.setName("boto-test1111");
		bucket = sbd.insertBucket(bucket);
		sbd.deleteBucket(bucket);
		bucket.setName("boto-test122111");
		sbd.updateBucket(bucket);
//		sbd.deleteBucket(bucket);

		// Test Object
		ScssObjectDaoImpl sod = ScssObjectDaoImpl.getInstance();
		ScssObject object = sod.getObjectById(43);
		object.setMeta("Meta00000000000000");
		sod.updateObject(object);
		object = sod.getObjectByBFSFile(30064775142l);
		List<ScssObject> list = sod.getObjectsByBucketId(22l);
		List<ScssObject> objects = sod.getObjectsByUserId(1l);
		object.setKey("/test");
		object = sod.insertObject(object);
		sod.deleteObject(object);
	}

}

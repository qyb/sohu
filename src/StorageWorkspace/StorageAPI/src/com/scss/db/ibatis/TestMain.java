package com.scss.db.ibatis;

import java.sql.SQLException;
import java.util.List;

import com.scss.db.dao.ScssAclDaoImpl;
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssGroupDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssAcl;
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
		ScssBucket bucket = new ScssBucket();
		bucket.setName("test_bucket");
		bucket.setOwnerId(1l);
		bucket.setMeta("Meta_test");
		bucket.setDeleted((byte)1);
		bucket.setLoggingEnabled((byte)1);
		bucket = sbd.insertBucket(bucket);
		bucket = sbd.getBucket(bucket.getName());
		bucket = sbd.getBucket(bucket.getId());
		bucket = sbd.getBucket(bucket);
		bucket.setName("test_bucket2");
		sbd.updateBucket(bucket);
		sbd.deleteBucket(bucket);

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

		// Test Acl
		ScssAclDaoImpl sad = ScssAclDaoImpl.getInstance();
		ScssAcl acl = new ScssAcl();
		acl.setResourceId(43l);
		acl.setAccessorId(1l);
		acl.setResourceType("O");
		acl.setAccessorType("U");
		acl.setPermission("R");
		acl = sad.insertAcl(acl);
		acl.setPermission("W");
		sad.updateAcl(acl);
		acl = sad.getAcl(acl.getId());
		sad.deleteAcl(acl);
	}

}

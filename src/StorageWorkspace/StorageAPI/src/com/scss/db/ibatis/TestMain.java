package com.scss.db.ibatis;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.scss.db.dao.ScssAclDaoImpl;
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssBucketLifecycleDaoImpl;
import com.scss.db.dao.ScssGroupDaoImpl;
import com.scss.db.dao.ScssLogDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.exception.SameNameException;
import com.scss.db.exception.UserInGroupException;
import com.scss.db.model.ScssAcl;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssBucketLifecycle;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssLog;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;
/**
 * 
 * @author Jack.wu.xu
 */
public class TestMain {
	@SuppressWarnings( { "unused", "unchecked", "deprecation" })
	public static void main(String args[]) throws SQLException,
			SameNameException, UserInGroupException {

		// User Test ing
		ScssUserDaoImpl sud = ScssUserDaoImpl.getInstance();
		List userList = sud.getUserList();
		ScssUser user = new ScssUser();
		user.setSohuId("sohuId");
		user.setAccessId("accessId");
		user.setAccessKey("accessKey");
		user = sud.insertUser(user);
		user = sud.getUserById(user.getId());
		user = sud.getUserBySohuId(user.getSohuId());
		user = sud.getUserByAccessKey(user.getAccessKey());
		user.setAccessKey("aaaa&&&&&a");
		sud.updateUser(user);
		

		// Test Group
		ScssGroupDaoImpl sgd = ScssGroupDaoImpl.getInstance();
		ScssGroup group = new ScssGroup();
		group.setName("用户组");
		group = sgd.insertGroup(group);
		group = sgd.getGroupByName(group.getName());
		group = sgd.getGroupById(group.getId());
		group.setName(group.getName() + "1");
		sgd.updateGroup(group);
		
		sgd.putUserIdsToGroup(user.getId()+"", group);
		sgd.removeUserFromGroup(user,group);
		sgd.putUserToGroup(user, group);
		List<ScssUser> usersByGroup = sud.getUsersByGroup(group);
		sgd.removeUserFromGroup(user,group);
		
		
		
		// Test Bucket
		ScssBucketDaoImpl sbd = ScssBucketDaoImpl.getInstance();
		List<ScssBucket> bucketsByUser = sbd.getBucketsByUser(user);
		ScssBucket bucket = new ScssBucket();
		bucket.setName("test_bucket");
		bucket.setOwnerId(1l);
		bucket.setMeta("Meta_test");
		bucket.setDeleted((byte) 1);
		bucket.setLoggingEnabled((byte) 1);
		bucket = sbd.insertBucket(bucket);
		bucket = sbd.getBucket(bucket.getName());
		bucket = sbd.getBucket(bucket.getId());
		bucket = sbd.getBucket(bucket);
		bucket.setName("test_bucket2");
		sbd.updateBucket(bucket);
		

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
		

		// Test Acl
		ScssAclDaoImpl sad = ScssAclDaoImpl.getInstance();
		ScssAcl acl = new ScssAcl();
		acl.setResourceId(43l);
		acl.setAccessorId(1l);
		acl.setResourceType("O");
		acl.setAccessorType("U");
		acl.setPermission("R");
		acl = sad.insertAcl(acl);
		List<ScssAcl> acls = sad.getAclByAccessor(acl.getAccessorId(), acl.getAccessorType());
		acls = sad.getAclOnResouce(acl.getResourceId(), acl.getResourceType());
		acls = sad.getAclByAccessorOnResouce(acl.getAccessorId(), acl.getAccessorType(),acl.getResourceId(), acl.getResourceType());
		acl.setPermission("W");
		sad.updateAcl(acl);
		acl = sad.getAcl(acl.getId());
		
		
		// Test Log 暂时不测试
//		ScssLogDaoImpl sld = ScssLogDaoImpl.getInstance();
//		ScssLog log = new ScssLog();
//		log
//		log = sld.writeLog(log);
//		sld.getLogsByAction("R");
//		acl = sad.getAcl(acl.getId());
//		sad.deleteAcl(acl);
		
		ScssBucketLifecycleDaoImpl sbld = ScssBucketLifecycleDaoImpl.getInstance();
		ScssBucketLifecycle sbl = new ScssBucketLifecycle();
		sbl.setBucketId(bucket.getId());
		sbl.setExpirationRule("rule");
		sbl = sbld.insertBucketLifecycle(sbl);
		sbl.setExpirationRule("rule1");
		sbld.updateBucketLifecycle(sbl);
		
		
		//delete all of test data.
		sbld.deleteBucketLifecycle(sbl);
		sbd.deleteBucket(bucket);
		sod.deleteObject(object);
		sud.deleteUser(user);
		sgd.deleteGroup(group);
		sad.deleteAcl(acl);
		
	}

}

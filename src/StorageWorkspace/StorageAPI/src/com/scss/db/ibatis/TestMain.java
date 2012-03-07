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
import com.scss.db.exception.DBException;
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
	public static void main(String args[]) throws SameNameException,
			UserInGroupException, DBException, SQLException {

		// User Test ing
		ScssUserDaoImpl sud = ScssUserDaoImpl.getInstance();
		List userList = sud.getUserList();
		ScssUser user = new ScssUser();
		user.setSohuId("sohuId");
		user.setAccessId("accessId");
		user.setAccessKey("accessKey");
		user = sud.insertUser(user);
		user.setAccessKey("aaaa&&&&&a");
		sud.updateUser(user);
		user = sud.getUserById(user.getId());
		user = sud.getUserById(user.getId());
		user = sud.getUserBySohuId("sohuId");
		user = sud.getUserByAccessKey("aaaa&&&&&a");

		// Test Group
		ScssGroupDaoImpl sgd = ScssGroupDaoImpl.getInstance();
		ScssGroup group = new ScssGroup();
		group.setName("用户组");
		group.setOwnerId(user.getId());
		group = sgd.insert(group);
		List<ScssGroup> groups = sgd.getGroupByName(group.getName());
		group = sgd.getGroupByName(group.getName(), user.getId());
		group = sgd.getGroupById(group.getId());
		group.setName(group.getName() + "1");
		sgd.update(group);

		sgd.putUserIdsToGroup(user.getId() + "", group);
		sgd.removeUserFromGroup(user, group);
		sgd.putUserToGroup(user, group);
		List<ScssUser> usersByGroup = sud.getUsersByGroup(group);
		sgd.removeUserFromGroup(user, group);

		// Test Bucket
		ScssBucketDaoImpl sbd = ScssBucketDaoImpl.getInstance();
		List<ScssBucket> bucketsByUser = sbd.getByUser(user);
		ScssBucket bucket = new ScssBucket();
		bucket.setName("test_bucket");
		bucket.setOwnerId(1l);
		bucket.setMeta("Meta_test");
		bucket.setDeleted((byte) 0);
		bucket.setLoggingEnabled((byte) 1);
		bucket = sbd.insertBucket(bucket);
		bucket = sbd.get(bucket.getName());
		bucket = sbd.get(bucket.getId());
		bucket = sbd.get(bucket);
		bucket.setName(bucket.getName() + 1);
		sbd.update(bucket);

		// Test Object
		ScssObjectDaoImpl sod = ScssObjectDaoImpl.getInstance();
		ScssObject object = sod.getObjectById(68);
		object.setMeta("Meta00000000000000");
		sod.updateObject(object);
		object = sod.getObjectByBFSFile(object.getBfsFile());
		List<ScssObject> list = sod.getObjectsByBucketId(22l);
		List<ScssObject> objects = sod.getObjectsByUserId(1l);
		object.setKey(object.getKey() + 1);
		object.setBfsFile(object.getBfsFile() + 1);
		object = sod.insertObject(object);

		// Test Acl
		ScssAclDaoImpl sad = ScssAclDaoImpl.getInstance();
		ScssAcl acl = new ScssAcl();
		acl.setResourceId(object.getId());
		acl.setAccessorId(user.getId());
		acl.setResourceType("O");
		acl.setAccessorType("U");
		acl.setPermission("R");
		acl = sad.insert(acl);
		List<ScssAcl> acls = sad.getByAccessor(acl.getAccessorId(), acl
				.getAccessorType());
		acls = sad.getOnResouce(acl.getResourceId(), acl.getResourceType());
		acl = sad.getByAccessorOnResouce(acl.getAccessorId(), acl
				.getAccessorType(), acl.getResourceId(), acl.getResourceType());
		acl.setPermission("W");
		sad.update(acl);
		acl = sad.get(acl.getId());

		// Test Log 暂时不测试
		// ScssLogDaoImpl sld = ScssLogDaoImpl.getInstance();
		// ScssLog log = new ScssLog();
		// log
		// log = sld.writeLog(log);
		// sld.getLogsByAction("R");
		// acl = sad.getAcl(acl.getId());
		// sad.deleteAcl(acl);

		ScssBucketLifecycleDaoImpl sbld = ScssBucketLifecycleDaoImpl
				.getInstance();
		ScssBucketLifecycle sbl = new ScssBucketLifecycle();
		sbl.setBucketId(bucket.getId());
		sbl.setExpirationRule("rule");
		sbl = sbld.insert(sbl);
		sbl.setExpirationRule("rule1");
		sbld.update(sbl);

		// delete all of test data.
		sbld.delete(sbl);
		sbd.delete(bucket);
		sgd.delete(group);
		sud.deleteUser(user);
		sad.delete(acl);

	}

}

package com.scss.db.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;

public class DBUnitTest {
	private Logger log = Logger.getLogger(DBUnitTest.class);

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		try {

			// Bucket test
			// ScssBucket putBucket = DBServiceHelper.putBucket("娱乐", 1232l,
			// "");
			// DBServiceHelper.deleteBucket("娱乐");
			// putBucket.setName("娱乐2");
			// DBServiceHelper.modifyBucket(putBucket);
			// DBServiceHelper.modifyObject(new ScssObject());
			// DBServiceHelper.modifyUser(new ScssUser());
			// DBServiceHelper.modifyGroup(new ScssGroup());
			//
			// ScssBucket bucket = DBServiceHelper.putBucket("娱乐", 12322l, true,
			// true, "meta", true, new Date(), new Date());
			// DBServiceHelper.deleteBucket("娱乐2", 12322l);
			//
			// // ScssUser test
			// ScssUser user = DBServiceHelper.putUser("xuyongping",
			// "sohu.com.jack+++++++++++");
			// user = DBServiceHelper
			// .getUserByAccessKey("sohu.com.jack+++++++++++");
			// user = DBServiceHelper.getUserBySohuId("xuyongping");
			// user.setSohuId("jack");
			// DBServiceHelper.modifyUser(user);
			// DBServiceHelper.deleteUser(user);
			// ScssObject test
			ScssObject putObject = DBServiceHelper.putObject("key1", 123456789l,
					123l, 23l, "jav", 123l, "images");

			ScssBucket bucket2 = DBServiceHelper.getBucketById(putObject
					.getBucketId());
			putObject = DBServiceHelper.getObject(bucket2.getName(), putObject
					.getKey());

			// ScssGroup test
			ScssGroup putGroup = DBServiceHelper.putGroup("createGroup");
			// DBServiceHelper.putUserToGroup(user, putGroup);
			DBServiceHelper.deleteGroup(putGroup.getId());

			// ScssGroup test
			// ScssObject object = DBServiceHelper.putObject("ks", 1234l, 123l,
			// 1234l,
			// "meta", 12l, "images" );

			System.out.println();
		} catch (SameNameException e) {
			e.printStackTrace();
		}
	}
}

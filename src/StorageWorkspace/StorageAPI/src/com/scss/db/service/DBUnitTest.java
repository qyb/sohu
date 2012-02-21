package com.scss.db.service;

import java.util.Date;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public class DBUnitTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		try {

			// Bucket test
			ScssBucket putBucket = DBServiceHelper.putBucket("娱乐", 1232l, "");
			DBServiceHelper.deleteBucket("娱乐", 1232l);

			ScssBucket bucket = DBServiceHelper.putBucket("娱乐", 12322l, true,
					true, "meta", true, new Date(), new Date());
			DBServiceHelper.deleteBucket("娱乐", 12322l);

			// ScssUser test
			ScssUser user = DBServiceHelper.putUser("xuyongping",
					"sohu.com.jack+++++++++++");
			user = DBServiceHelper.getUserByAccessKey("sohu.com.jack+++++++++++");
			user = DBServiceHelper.getUserBySohuId("xuyongping");
			DBServiceHelper.deleteUser(user);
			
			
			
			// ScssGroup test
			ScssGroup putGroup = DBServiceHelper.putGroup("createGroup");
			DBServiceHelper.putUserToGroup(user, putGroup);
			DBServiceHelper.deleteGroup(putGroup.getId());
			

			System.out.println();
		} catch (SameNameException e) {
			e.printStackTrace();
		}
	}
}

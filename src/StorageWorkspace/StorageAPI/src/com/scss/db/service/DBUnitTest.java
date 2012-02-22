package com.scss.db.service;

import java.util.Date;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;

public class DBUnitTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		try {

			// Bucket test
			ScssBucket putBucket = DBServiceHelper.putBucket("娱乐", 1232l, "");
			DBServiceHelper.deleteBucket("娱乐");
			DBServiceHelper.modifyBucket(putBucket);
			DBServiceHelper.modifyObject(new ScssObject());
			DBServiceHelper.modifyUser(new ScssUser());
			DBServiceHelper.modifyGroup(new ScssGroup());

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
			
			// ScssGroup test
			ScssObject object = DBServiceHelper.putObject("ks", 1234l, 123l, 1234l, "meta", 12l, "images" );
			

			System.out.println();
		} catch (SameNameException e) {
			e.printStackTrace();
		}
	}
}

package com.scss.db.dao.i;

import java.util.List;

import com.scss.db.exception.DBException;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public interface IUser {

	List getUserList();

	void deleteUser(ScssUser user) throws DBException;

	void deleteUser(Long id) throws DBException;

	ScssUser insertUser(ScssUser user) throws SameNameException;

	ScssUser getUserByAccessKey(String access_key);

	ScssUser getUserByAccessId(String access_id);

	List<ScssUser> getUsersByGroupId(Long groupId);

	List<ScssUser> getUsersByGroup(ScssGroup group);

	ScssUser getUserById(long id);

	ScssUser getUserBySohuId(String sohuId);

	void updateUser(ScssUser scssUser) throws DBException;

}

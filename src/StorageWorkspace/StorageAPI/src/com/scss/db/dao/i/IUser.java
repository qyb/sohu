package com.scss.db.dao.i;

import java.util.List;

import com.scss.db.exception.DBException;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public interface IUser {

	List<ScssUser> getUserList();

	void delete(ScssUser user) throws DBException;

	void delete(Long id) throws DBException;

	ScssUser insert(ScssUser user) throws SameNameException;

	ScssUser getUserByAccessKey(String access_key);

	ScssUser getUserByAccessId(String access_id);

	List<ScssUser> getUsersByGroupId(Long groupId);

	List<ScssUser> getUsersByGroup(ScssGroup group);

	ScssUser get(long id);

	List<ScssUser> getUsersBySohuId(String sohuId);

	void update(ScssUser scssUser) throws DBException;

}

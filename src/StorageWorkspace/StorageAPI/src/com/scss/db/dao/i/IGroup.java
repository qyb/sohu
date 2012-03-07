package com.scss.db.dao.i;

import java.sql.SQLException;
import java.util.List;

import com.scss.db.exception.DBException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public interface IGroup {

	ScssGroup getGroupById(Long groupId);

	List<ScssGroup> getGroupByName(String name);

	ScssGroup insert(ScssGroup group);

	void putUserToGroup(ScssUser user, ScssGroup sg) throws DBException ;

	void removeUserFromGroup(ScssUser user, ScssGroup sg) throws DBException ;

	void update(ScssGroup sg) throws  DBException;

	void putUserIdsToGroup(String ids, ScssGroup sg) throws DBException ;

	void delete(ScssGroup sg) throws DBException ;

	void delete(Long gid) throws DBException ;

	ScssGroup getGroupByName(String name, Long ownerId);

}

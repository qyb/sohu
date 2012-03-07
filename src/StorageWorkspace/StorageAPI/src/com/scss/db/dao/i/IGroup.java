package com.scss.db.dao.i;

import java.util.List;

import com.scss.db.exception.DBException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public interface IGroup {

	ScssGroup get(Long groupId);

	List<ScssGroup> getAll(Long user_id);
	List<ScssGroup> getAll(String access_id);

	ScssGroup insert(ScssGroup group);

	void putUserToGroup(ScssUser user, ScssGroup sg) throws DBException ;

	void removeUserFromGroup(ScssUser user, ScssGroup sg) throws DBException ;

	void update(ScssGroup sg) throws  DBException;

	void putUserIdsToGroup(String ids, ScssGroup sg) throws DBException ;

	void delete(ScssGroup sg) throws DBException ;

	void delete(Long gid) throws DBException ;

	ScssGroup getGroupByName(String name, Long ownerId);

}

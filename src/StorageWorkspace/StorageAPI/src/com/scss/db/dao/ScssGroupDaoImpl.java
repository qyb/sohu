package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.i.IGroup;
import com.scss.db.exception.DBException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssGroupDaoImpl implements IGroup {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssGroupDaoImpl instance = new ScssGroupDaoImpl();

	private ScssGroupDaoImpl() {
	}

	public static ScssGroupDaoImpl getInstance() {
		return instance;
	}

	@Override
	public ScssGroup get(Long groupId) {
		ScssGroup su = null;
		try {
			su = (ScssGroup) sqlMap.queryForObject("getGroupById", groupId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public ScssGroup getGroupByName(String name, Long ownerId) {
		ScssGroup su = new ScssGroup();
		su.setName(name);
		su.setOwnerId(ownerId);
		try {
			return (ScssGroup) sqlMap.queryForObject("getGroupByName", su);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScssGroup insert(ScssGroup group) {
		try {
			group.setId((Long) sqlMap.insert("putGroup", group));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	@Override
	public void putUserToGroup(ScssUser user, ScssGroup sg) throws DBException {
		String userIds = sg.getUserIds();
		if (userIds.indexOf("," + user.getId() + ",") != -1) {
			return;
		}
		putUserIdsToGroup(user.getId() + "", sg);
	}

	@Override
	public void removeUserFromGroup(ScssUser user, ScssGroup sg)
			throws DBException {
		String userIds = sg.getUserIds();
		userIds = userIds.replaceAll("," + user.getId() + ",", ",");
		sg.setUserIds(userIds);
		try {
			sqlMap.update("updateGroup", sg);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void update(ScssGroup sg) throws DBException {
		try {
			sqlMap.update("updateGroup", sg);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void putUserIdsToGroup(String ids, ScssGroup sg) throws DBException {
		if ((ids == null) || ("".equals(ids))) {
			return;
		}
		String newIds = sg.getUserIds();
		if ((newIds == null) || ("".equals(newIds)))
			newIds = "," + ids + ",";
		else
			newIds = newIds + ids + ",";
		sg.setUserIds(newIds);
		try {
			sqlMap.update("updateGroup", sg);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void delete(ScssGroup sg) throws DBException {
		delete(sg.getId());
	}

	@Override
	public void delete(Long gid) throws DBException {
		try {
			sqlMap.delete("deleteGroup", gid);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScssGroup> getGroupByName(String name) {
		try {
			return sqlMap.queryForList("getGroupsByName", name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}

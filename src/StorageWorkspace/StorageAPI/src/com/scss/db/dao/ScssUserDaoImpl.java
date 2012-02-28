package com.scss.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.inter.ScssUserDaoInter;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public class ScssUserDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssUserDaoImpl instance = new ScssUserDaoImpl();
	private static ScssGroupDaoImpl groupDao = ScssGroupDaoImpl.getInstance();

	private ScssUserDaoImpl() {
	}

	public List getUserList() throws SQLException {
		List userList = sqlMap.queryForList("getScssUsers");
		return userList;
	}

	public static ScssUserDaoImpl getInstance() {
		return instance;
	}
	public void deleteUser(ScssUser user) throws SQLException {
		sqlMap.delete("deleteUser", user.getId());
	}
	public void deleteUser(Long id) throws SQLException {
		sqlMap.delete("deleteUser", id);
	}
	public ScssUser insertUser(ScssUser user) throws SameNameException {
		try {
			user.setId((Long) sqlMap.insert("putUser", user));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public ScssUser getUserByAccessKey(String access_key) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserByAccessKey", access_key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}
	public  List<ScssUser> getUsersByGroupId(Long groupId) {
		ScssGroup groupById = groupDao.getGroupById(groupId);
		return getUsersByGroup(groupById);
	}
	public  List<ScssUser> getUsersByGroup(ScssGroup group) {
		List result = new ArrayList();
		String userIds = group.getUserIds();
		if ((userIds == null) || ("".equals(userIds))) {
			return result;
		}
		String[] split = userIds.split(",");
		for (int i = 1; i < split.length; ++i) {
			result.add(getUserById(Long.parseLong(split[i])));
		}
		return result;
	}
	public ScssUser getUserById(long id) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserById", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public static ScssUser getUserBySohuId(String sohuId) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserBySohuId", sohuId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}
	public  void updateUser(ScssUser scssUser) throws SQLException {
		sqlMap.update("updateUser", scssUser);
	}
}

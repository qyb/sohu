package com.scss.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.i.IUser;
import com.scss.db.exception.DBException;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssUserDaoImpl implements IUser {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssUserDaoImpl instance = new ScssUserDaoImpl();

	public static ScssUserDaoImpl getInstance() {
		return instance;
	}

	private final Logger logger = Logger.getLogger(this.getClass());

	private ScssUserDaoImpl() {
	}

	@Override
	public List getUserList() {
		List userList = null;
		try {
			userList = sqlMap.queryForList("getScssUsers");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public void deleteUser(ScssUser user) throws DBException {
		try {
			sqlMap.delete("deleteUser", user.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteUser(Long id) throws DBException {
		try {
			sqlMap.delete("deleteUser", id);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public ScssUser insertUser(ScssUser user) throws SameNameException {
		try {
			user.setId((Long) sqlMap.insert("putUser", user));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException("UserExists",
					"User name or User`accesskey is exists");
			throw ename;
		} catch (SQLException e) {
			String message = e.getMessage();
			logger.debug(message);
			if (message.indexOf("Duplicate entry") != -1) {
				if (message.indexOf("access_key") != -1) {
					SameNameException ename = new SameNameException(
							"access_key", message);
					throw ename;
				}
				if (message.indexOf("Sohu_ID") != -1) {
					SameNameException ename = new SameNameException("Sohu_ID",
							message);
					throw ename;
				}
			}
		}
		return user;
	}

	@Override
	public ScssUser getUserByAccessKey(String access_key) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserByAccessKey",
					access_key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public ScssUser getUserByAccessId(String access_id) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserByAccessId",
					access_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public List<ScssUser> getUsersByGroupId(Long groupId) {
		ScssGroup su;
		try {
			su = (ScssGroup) sqlMap.queryForObject("getGroupById", groupId);
			return getUsersByGroup(su);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ScssUser> getUsersByGroup(ScssGroup group) {
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

	@Override
	public ScssUser getUserById(long id) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserById", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public ScssUser getUserBySohuId(String sohuId) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserBySohuId", sohuId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public void updateUser(ScssUser scssUser) throws DBException {
		try {
			sqlMap.update("updateUser", scssUser);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
}

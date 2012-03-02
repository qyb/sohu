package com.scss.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;
/**
 * 
 * @author Jack.wu.xu
 */
public class ScssUserDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssUserDaoImpl instance = new ScssUserDaoImpl();
	private static ScssGroupDaoImpl groupDao = ScssGroupDaoImpl.getInstance();
//	private static final Logger logger = Logger.getLogger("DAO/SCSSUSER", 0,
//			true);

	private final Logger logger = Logger.getLogger(this.getClass());
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
	public List<ScssUser> getUsersByGroupId(Long groupId) {
		ScssGroup groupById = groupDao.getGroupById(groupId);
		return getUsersByGroup(groupById);
	}

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

	public ScssUser getUserById(long id) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserById", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public ScssUser getUserBySohuId(String sohuId) {
		ScssUser su = null;
		try {
			su = (ScssUser) sqlMap.queryForObject("getUserBySohuId", sohuId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public void updateUser(ScssUser scssUser) throws SQLException {
		sqlMap.update("updateUser", scssUser);
	}
}

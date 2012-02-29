package com.scss.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssAcl;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssUser;

public class ScssAclDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssAclDaoImpl instance = new ScssAclDaoImpl();

	private ScssAclDaoImpl() {
	}

	public static ScssAclDaoImpl getInstance() {
		return instance;
	}

	public ScssAcl insertAcl(ScssAcl acl) throws SameNameException {
		try {
			acl.setId((Long) sqlMap.insert("putAcl", acl));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException(
					"Same Acl setting is exist",
					"Same Acl setting is exist for accessor` "
							+ acl.getAccessorId() + " on "
							+ acl.getResourceId());
			throw ename;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acl;
	}

	public ScssAcl getAcl(Long id) {
		ScssAcl acl = null;
		try {
			acl = (ScssAcl) sqlMap.queryForObject("getAcl", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acl;
	}

	public void updateAcl(ScssAcl acl) throws SQLException {
		sqlMap.update("updateAcl", acl);
	}
	public void deleteAcl(ScssAcl acl) throws SQLException {
		sqlMap.update("deleteAcl", acl);
	}
}

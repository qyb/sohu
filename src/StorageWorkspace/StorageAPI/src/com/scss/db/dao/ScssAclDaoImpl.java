package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssAcl;

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


	public List<ScssAcl> getAclByAccessor(Long acc,String accType) {
		List<ScssAcl> acls = null;
		try {
			ScssAcl acl = new ScssAcl();
			acl.setAccessorId(acc);
			acl.setAccessorType(accType);
			acls = sqlMap.queryForList("getAclByAccessor", acl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acls;
	}
	public List<ScssAcl> getAclOnResouce(Long res,String resType) {
		List<ScssAcl> acls = null;
		try {
			ScssAcl acl = new ScssAcl();
			acl.setResourceId(res);
			acl.setResourceType(resType);
			acls = sqlMap.queryForList("getAclOnResouce", acl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acls;
	}

	public List<ScssAcl> getAclByAccessorOnResouce(Long acc,String accType, Long res,String resType) {
		List<ScssAcl> acls = null;
		try {
			ScssAcl acl = new ScssAcl();
			acl.setAccessorId(acc);
			acl.setAccessorType(accType);
			acl.setResourceId(res);
			acl.setResourceType(resType);
			acls = sqlMap.queryForList("getAclByAccessorOnResouce", acl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acls;
	}

	public void updateAcl(ScssAcl acl) throws SQLException {
		sqlMap.update("updateAcl", acl);
	}

	public void deleteAcl(ScssAcl acl) throws SQLException {
		sqlMap.update("deleteAcl", acl);
	}
}

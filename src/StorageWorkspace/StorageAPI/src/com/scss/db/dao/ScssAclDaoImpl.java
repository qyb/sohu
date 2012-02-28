package com.scss.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
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

}

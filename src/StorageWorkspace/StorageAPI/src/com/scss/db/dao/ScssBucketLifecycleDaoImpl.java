package com.scss.db.dao;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.i.IBucketLifecycle;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucketLifecycle;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssBucketLifecycleDaoImpl implements IBucketLifecycle {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssBucketLifecycleDaoImpl instance = new ScssBucketLifecycleDaoImpl();

	private ScssBucketLifecycleDaoImpl() {
	}

	public static ScssBucketLifecycleDaoImpl getInstance() {
		return instance;
	}

	@Override
	public ScssBucketLifecycle insert(ScssBucketLifecycle bucketLifecycle)
			throws SameNameException {
		try {
			bucketLifecycle.setId((Long) sqlMap.insert("putBucketLifecycle",
					bucketLifecycle));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException(
					"ScssBucketLifecycle is exist",
					"ScssBucketLifecycle is UNIQUE");
			throw ename;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bucketLifecycle;
	}

	@Override
	public ScssBucketLifecycle get(Long id) {
		ScssBucketLifecycle acl = null;
		try {
			acl = (ScssBucketLifecycle) sqlMap.queryForObject(
					"getBucketLifecycle", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acl;
	}

	@Override
	public void update(ScssBucketLifecycle sbl) throws SQLException {
		sqlMap.update("updateBucketLifecycle", sbl);
	}

	@Override
	public void delete(ScssBucketLifecycle sbl) throws SQLException {
		sqlMap.update("deleteBucketLifecycle", sbl);
	}
}

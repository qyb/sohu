package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssUser;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssBucketDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssBucketDaoImpl instance = new ScssBucketDaoImpl();

	private ScssBucketDaoImpl() {
	}

	public ScssBucket insertBucket(ScssBucket bucket) throws SameNameException,
			SQLException {
		try {
			bucket.setId((Long) sqlMap.insert("putBucket", bucket));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException("BucketExists",
					"Bucket name is exists");
			throw ename;
		} catch (SQLException e) {
			String message = e.getMessage();
			if ((message.indexOf("Duplicate entry") != -1)
					&& (message.indexOf("'name'") != -1)) {
				SameNameException ename = new SameNameException(
						"name and user", "Duplicate entry name and user");
				throw ename;
			}
			throw e;
		}
		return bucket;
	}

	public ScssBucket getBucket(ScssBucket sb) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucket", sb.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ScssBucket> getBuckets() {
		try {
			return sqlMap.queryForList("getBuckets");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ScssBucket> getBucketsByUser(ScssUser user) {
		try {
			return sqlMap.queryForList("getBucketsByUser", user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ScssBucket getBucket(Long id) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucket", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ScssBucket getBucket(String name) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucketByName", name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateBucket(ScssBucket sb) throws SQLException {
		sqlMap.update("updateBucket", sb);
	}

	public void deleteBucket(ScssBucket sb) throws SQLException {
		sqlMap.update("deleteBucket", sb);
	}

	public static ScssBucketDaoImpl getInstance() {
		return instance;
	}
}

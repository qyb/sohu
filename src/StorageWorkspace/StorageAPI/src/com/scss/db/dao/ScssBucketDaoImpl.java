package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.i.IBucket;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssUser;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssBucketDaoImpl implements IBucket{
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssBucketDaoImpl instance = new ScssBucketDaoImpl();

	private ScssBucketDaoImpl() {
	}
	@Override
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
	@Override
	public ScssBucket get(ScssBucket sb) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucket", sb.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<ScssBucket> get() {
		try {
			return sqlMap.queryForList("getBuckets");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<ScssBucket> getByUser(ScssUser user) {
		try {
			return sqlMap.queryForList("getBucketsByUser", user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public ScssBucket get(Long id) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucket", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public ScssBucket get(String name) {
		try {
			return (ScssBucket) sqlMap.queryForObject("getBucketByName", name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void update(ScssBucket sb) throws SQLException {
		sqlMap.update("updateBucket", sb);
	}
	@Override
	public void delete(ScssBucket sb) throws SQLException {
		sqlMap.update("deleteBucket", sb);
	}

	public static ScssBucketDaoImpl getInstance() {
		return instance;
	}
}

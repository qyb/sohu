package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssUser;

public class ScssBucketDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssBucketDaoImpl instance = new ScssBucketDaoImpl();
	private static ScssGroupDaoImpl groupDao = ScssGroupDaoImpl.getInstance();

	private ScssBucketDaoImpl() {
	}

	public ScssBucket insertBucket(ScssBucket bucket) throws SameNameException {
		try {
			bucket.setId((Long) sqlMap.insert("putBucket", bucket));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bucket;
	}

	public ScssBucket getBucket(ScssBucket sb) throws SQLException {
		return (ScssBucket) sqlMap.queryForObject("getBucket", sb.getId());
	}

	public List<ScssBucket> getBuckets() throws SQLException {
		return sqlMap.queryForList("getBuckets");
	}

	public List<ScssBucket> getBucketsByUser(ScssUser user) throws SQLException {
		return sqlMap.queryForList("getBucketsByUser", user);
	}

	public ScssBucket getBucket(Long id) throws SQLException {
		return (ScssBucket) sqlMap.queryForObject("getBucket", id);
	}

	public ScssBucket getBucket(String name) throws SQLException {
		return (ScssBucket) sqlMap.queryForObject("getBucketByName", name);
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

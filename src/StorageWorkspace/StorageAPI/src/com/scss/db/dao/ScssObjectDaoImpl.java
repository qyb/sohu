package com.scss.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.dao.i.IObject;
import com.scss.db.exception.DBException;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;

/**
 * 
 * @author Jack.wu.xu
 */
public class ScssObjectDaoImpl implements IObject {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssObjectDaoImpl instance = new ScssObjectDaoImpl();

	private final Logger logger = Logger.getLogger(this.getClass());

	private ScssObjectDaoImpl() {
	}

	public static ScssObjectDaoImpl getInstance() {
		return instance;
	}

	public void deleteObject(Long id) throws DBException {
		try {
			sqlMap.delete("deleteObject", id);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public ScssObject insertObject(ScssObject object) throws SameNameException {
		try {
			object.setId((Long) sqlMap.insert("putObject", object));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException("ObjectExists",
					"Object is exists in  the bucket Which id is "
							+ object.getBucketId() + "");
			throw ename;
		} catch (SQLException e) {
			String message = e.getMessage();
			logger.debug(message);
			if (message.indexOf("Duplicate entry") != -1) {
				SameNameException ename = new SameNameException(
						"key,user and BucketName", "Duplicate entry");
				throw ename;
			}
		}
		return object;
	}

	public ScssObject getObjectById(long id) {
		ScssObject su = null;
		try {
			su = (ScssObject) sqlMap.queryForObject("getObjectById", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public ScssObject getObjectByKey(String key, Long bucketId) {
		try {
			ScssObject su = new ScssObject();
			su.setKey(key);
			su.setBucketId(bucketId);
			return (ScssObject) sqlMap.queryForObject("getObjectByKey", su);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ScssObject> getObjectByKey(String key, ScssUser user) {
		try {
			ScssObject su = new ScssObject();
			su.setKey(key);
			su.setOwnerId(user.getId());
			return sqlMap.queryForList("getObjectByKeyAndUser", su);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ScssObject> getObjectsByBucketId(Long id) {
		try {
			return sqlMap.queryForList("getObjectsByBucketId", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ScssObject> getObjectsByUserId(Long id) {
		try {
			return sqlMap.queryForList("getObjectsByUserId", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ScssObject getObjectByBFSFile(Long key) {
		ScssObject su = null;
		try {
			su = (ScssObject) sqlMap.queryForObject("getObjectByBFSFile", key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public void updateObject(ScssObject scssObject) throws DBException {
		try {
			sqlMap.update("updateObject", scssObject);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void delete(ScssObject obj) throws DBException {
		try {
			sqlMap.delete("deleteObject", obj.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void delete(String object_key, String bucket_name)
			throws DBException {
		Map m = new HashMap();
		m.put("object_key", object_key);
		m.put("bucket_name", bucket_name);
		try {
			sqlMap.delete("deleteObjectByBucketNameAndObjectKey", m);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void delete(String object_key, Long bucket_id) throws DBException {
		Map m = new HashMap();
		m.put("object_key", object_key);
		m.put("bucket_id", bucket_id);
		try {
			sqlMap.delete("deleteObjectByBucketIdAndObjectKey", m);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteAll(String bucket_name) throws DBException {
		try {
			sqlMap.delete("deleteObjectAllByBucketName", bucket_name);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteAll(Long bucket_id) throws DBException {
		try {
			sqlMap.delete("deleteObjectAll", bucket_id);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteAllByOwner(String access_key) throws DBException {
		try {
			sqlMap.delete("deleteObjectAllByAccessKey", access_key);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteAllByOwner(ScssUser user) throws DBException {
		try {
			sqlMap.delete("deleteObjectAllByUserId", user.getId());
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public void deleteAllByOwner(Long user_id) throws DBException {
		try {
			sqlMap.delete("deleteObjectAllByUserId", user_id);
		} catch (SQLException e) {
			throw new DBException(e);
		}

	}

	@Override
	public ScssObject get(long id) {
		ScssObject su = null;
		try {
			su = (ScssObject) sqlMap.queryForObject("getObjectById", id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	@Override
	public ScssObject get(String object_key, String bucket_name) {
		try {
			ScssBucket sb = (ScssBucket) sqlMap.queryForObject(
					"getBucketByName", bucket_name);
			return this.get(object_key, sb.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScssObject get(String object_key, Long bucket_id) {
		try {
			ScssObject su = new ScssObject();
			su.setKey(object_key);
			su.setBucketId(bucket_id);
			return (ScssObject) sqlMap.queryForObject("getObjectByKey", su);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScssObject get(String object_key, String bucket_name, Long owner_id) {
		try {
			ScssBucket sb = (ScssBucket) sqlMap.queryForObject(
					"getBucketByName", bucket_name);
			ScssObject scssObject = this.get(object_key, sb.getId());
			if (scssObject != null && scssObject.getOwnerId() == owner_id)
				return scssObject;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ScssObject> getAll(String object_key, String owner_access_id) {
		Map m = new HashMap();
		m.put("object_key", object_key);
		m.put("owner_access_id", owner_access_id);
		try {
			return sqlMap.queryForList("getObjectByAccessId", m);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ScssObject> getAll(String object_key, Long owner_id) {
		Map m = new HashMap();
		m.put("object_key", object_key);
		m.put("owner_id", owner_id);
		try {
			return sqlMap.queryForList("getObjectByOwnerId", m);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScssObject insert(ScssObject object) throws SameNameException {
		try {
			object.setId((Long) sqlMap.insert("putObject", object));
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			SameNameException ename = new SameNameException("ObjectExists",
					"Object is exists in  the bucket Which id is "
							+ object.getBucketId() + "");
			throw ename;
		} catch (SQLException e) {
			String message = e.getMessage();
			logger.debug(message);
			if (message.indexOf("Duplicate entry") != -1) {
				SameNameException ename = new SameNameException(
						"key,user and BucketName", "Duplicate entry");
				throw ename;
			}
		}
		return object;
	}

	@Override
	public void update(ScssObject obj) throws DBException {
		try {
			sqlMap.update("updateObject", obj);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public ScssObject insert(String object_key, String bucket_name, Long bfsFile)
			throws SameNameException, DBException {
		try {
			ScssBucket sb = (ScssBucket) sqlMap.queryForObject(
					"getBucketByName", bucket_name);
			ScssObject o = new ScssObject();
			o.setBfsFile(bfsFile);
			o.setBucketId(sb.getId());
			o.setOwnerId(sb.getOwnerId());
			return insert(o);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public ScssObject insert(String object_key, Long bucket_id, Long bfs_num)
			throws SameNameException, DBException {
		try {
			ScssBucket sb = (ScssBucket) sqlMap.queryForObject(
					"getBucket", bucket_id);
			ScssObject o = new ScssObject();
			o.setBfsFile(bfs_num);
			o.setBucketId(bucket_id);
			o.setOwnerId(sb.getOwnerId());
			return insert(o);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
}

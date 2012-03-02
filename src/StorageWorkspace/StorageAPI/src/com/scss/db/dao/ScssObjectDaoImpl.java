package com.scss.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.scss.db.connpool.config.IbatisConfig;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssObject;
/**
 * 
 * @author Jack.wu.xu
 */
public class ScssObjectDaoImpl {
	private static final SqlMapClient sqlMap = IbatisConfig.getSqlMapInstance();
	private static ScssObjectDaoImpl instance = new ScssObjectDaoImpl();
//	private static final Logger logger = Logger.getLogger("DAO/SCSSOBJECT", 0,
//			true);
	
	private final Logger logger = Logger.getLogger(this.getClass());
	private ScssObjectDaoImpl() {
	}

	public static ScssObjectDaoImpl getInstance() {
		return instance;
	}

	public void deleteObject(ScssObject object) throws SQLException {
		sqlMap.delete("deleteObject", object.getId());
	}

	public void deleteObjectByBFSKey(Long key) throws SQLException {
		sqlMap.delete("deleteObjectByBFSKey", key);
	}

	public void deleteObject(Long id) throws SQLException {
		sqlMap.delete("deleteObject", id);
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

	@SuppressWarnings("unchecked")
	public static List<ScssObject> getObjectsByBucketId(Long id)
			throws SQLException {
		return sqlMap.queryForList("getObjectsByBucketId", id);
	}

	@SuppressWarnings("unchecked")
	public static List<ScssObject> getObjectsByUserId(Long id)
			throws SQLException {
		return sqlMap.queryForList("getObjectsByUserId", id);
	}

	public static ScssObject getObjectByBFSFile(Long key) {
		ScssObject su = null;
		try {
			su = (ScssObject) sqlMap.queryForObject("getObjectByBFSFile", key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return su;
	}

	public void updateObject(ScssObject scssObject) throws SQLException {
		sqlMap.update("updateObject", scssObject);
	}
}

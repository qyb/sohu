package com.scss.db.dao.i;

import java.util.List;

import com.scss.db.exception.DBException;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;

/**
 * 
 * @autho Samuel
 * 
 */
public interface IObject {

	ScssObject get(long id);

	ScssObject get(String object_key, String bucket_name);

	ScssObject get(String object_key, Long bucket_id);

	ScssObject get(String object_key, String bucket_name, Long owner_id);
	List<ScssObject> getAll(String object_key, String owner_access_id);
	List<ScssObject> getAll(String object_key, Long owner_id);
	ScssObject insert(ScssObject obj) throws SameNameException;
	ScssObject insert(String object_key, String bucket_name, Long bfs_num) throws SameNameException, DBException;
	ScssObject insert(String object_key, Long bucket_id, Long bfs_num)
			throws SameNameException, DBException;
	void update(ScssObject obj) throws DBException;
	void delete(ScssObject obj) throws DBException;
	void delete(String object_key, String bucket_name) throws DBException;
	void delete(String object_key, Long bucket_id) throws DBException;
	void deleteAll(String bucket_name) throws DBException;
	void deleteAll(Long bucket_id) throws  DBException;
	void deleteAllByOwner(String access_key) throws DBException;
	void deleteAllByOwner(Long bucket_id) throws DBException;
	void deleteAllByOwner(ScssUser user) throws DBException;
}

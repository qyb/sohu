package com.scss.db;

import java.util.List;

import com.scss.db.model.ScssObject;

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
	
	void set(ScssObject obj);
	
	void insert(ScssObject obj);
	void insert(String object_key, String bucket_name, Long owner_id);
	void insert(String object_key, Long bucket_id, Long owner_id);
	
	void update(ScssObject obj);
	void update(String object_key, String bucket_name, Long owner_id);
	void update(String object_key, Long bucket_id, Long owner_id);
	
	void delete(ScssObject obj);
	void delete(String object_key, String bucket_name);
	void delete(String object_key, Long bucket_id);
	
	void deleteAll(String bucket_name);
	void deleteAll(Long bucket_id);

	void deleteAllByOwner(String bucket_name);
	void deleteAllByOwner(Long bucket_id);
}

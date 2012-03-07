package com.scss.db.dao.i;

import java.sql.SQLException;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucketLifecycle;

public interface IBucketLifecycle {

	ScssBucketLifecycle insert(
			ScssBucketLifecycle bucketLifecycle) throws SameNameException;

	ScssBucketLifecycle get(Long id);

	void update(ScssBucketLifecycle sbl) throws SQLException;

	void delete(ScssBucketLifecycle sbl) throws SQLException;

}

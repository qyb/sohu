package com.scss.db.dao.i;

import java.sql.SQLException;
import java.util.List;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssUser;

public interface IBucket {

	ScssBucket insert(ScssBucket bucket) throws SameNameException,
			SQLException;

	ScssBucket get(Long id);

	List<ScssBucket> getAll(ScssUser user);

	ScssBucket get(ScssBucket sb);

	void delete(ScssBucket sb) throws SQLException;

	void update(ScssBucket sb) throws SQLException;

	ScssBucket get(String name);

}

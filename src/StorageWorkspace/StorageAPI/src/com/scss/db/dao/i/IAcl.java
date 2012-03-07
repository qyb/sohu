package com.scss.db.dao.i;

import java.sql.SQLException;
import java.util.List;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssAcl;

public interface IAcl {

	public void update(ScssAcl acl) throws SQLException;
	public void delete(ScssAcl acl) throws SQLException;

	public ScssAcl getByAccessorOnResouce(Long acc, String accType,
			Long res, String resType);

	public List<ScssAcl> getOnResouce(Long res, String resType);

	public List<ScssAcl> getByAccessor(Long acc, String accType);

	public ScssAcl get(Long id);

	public ScssAcl insert(ScssAcl acl) throws SameNameException;

}

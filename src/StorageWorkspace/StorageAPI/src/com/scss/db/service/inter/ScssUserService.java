package com.scss.db.service.inter;

import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssUser;

public interface ScssUserService {
	public ScssUser putUser(ScssUser user) throws SameNameException;
}

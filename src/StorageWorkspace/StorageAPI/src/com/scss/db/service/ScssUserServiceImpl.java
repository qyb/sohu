package com.scss.db.service;

import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.dao.inter.ScssUserDaoInter;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssUser;
import com.scss.db.service.inter.ScssUserService;

public class ScssUserServiceImpl implements ScssUserService {
	private ScssUserDaoInter sudi = ScssUserDaoImpl.getInstance();

	public ScssUser putUser(ScssUser user) throws SameNameException {
		return sudi.insertUser(user);
	}
}

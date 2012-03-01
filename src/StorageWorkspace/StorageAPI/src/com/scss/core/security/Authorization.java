/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.security;

import java.security.SignatureException;

import com.scss.core.APIRequest;
import com.scss.core.CommonRequestHeader;
import com.scss.db.User;
import com.scss.db.model.ScssUser;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/**
 * Authorization implementation base
 * 
 * @author Samuel
 *
 */
public class Authorization extends AuthorizationBase {

	public Authorization(APIRequest req) {
		super(req);
	}


}

package com.scss.core.security;

import com.scss.core.APIRequest;

public class QueryStringAuthorization extends AuthorizationBase {

	public QueryStringAuthorization(APIRequest req) {
		super(req);
	}

}

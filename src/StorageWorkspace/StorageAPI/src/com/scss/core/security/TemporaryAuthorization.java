package com.scss.core.security;

import com.scss.NotImplementedException;
import com.scss.core.APIRequest;

public class TemporaryAuthorization extends Authorization {

	public TemporaryAuthorization(APIRequest req) {
		super(req);
	}

	@Override
	protected String getStringToSign() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	protected String getSignature(Credential cred) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

}

package com.scss.core.security;

import com.scss.NotImplementedException;

public class TemporarySigner extends Signature {
	
	public TemporarySigner(String sign_key) {
		super(SignatureVersion.V1, sign_key);
		throw new NotImplementedException();
	}

	public TemporarySigner(SignatureVersion ver, String sign_key) {
		super(ver, sign_key);
		throw new NotImplementedException();
	}

}

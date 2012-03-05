package com.scss.core.security;

public class TemporarySigner extends Signature {
	
	public TemporarySigner(String sign_key) {
		super(SignatureVersion.V1, sign_key);
	}

	public TemporarySigner(SignatureVersion ver, String sign_key) {
		super(ver, sign_key);
	}

}

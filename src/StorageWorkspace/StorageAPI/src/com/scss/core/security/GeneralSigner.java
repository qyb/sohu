package com.scss.core.security;

public class GeneralSigner extends Signature {
	
	public GeneralSigner(String sign_key) {
		super(SignatureVersion.V1, sign_key);
	}

	public GeneralSigner(SignatureVersion ver, String sign_key) {
		super(ver, sign_key);
	}

}

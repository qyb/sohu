package com.scss.core.security;

public class QueryStringSigner extends Signature {
	
	public QueryStringSigner(String sign_key) {
		super(SignatureVersion.V2, sign_key);
	}

	public QueryStringSigner(SignatureVersion ver, String sign_key) {
		super(ver, sign_key);
	}
	
	public static void main(String[] args) {
		String access_id = "AKIAIXEPRIJSQA4A2KOA";
		Signature signer = new QueryStringSigner(access_id);
		java.util.Date exp = new java.util.Date();
		exp.setSeconds(exp.getSeconds()+30);
		Long expires = exp.getTime();
		String str_to_sign = String.format("GET\n\n\n%d\n/boto-test/obj3", expires);
		String sig = signer.sign(str_to_sign);
		System.err.println(String.format("http://boto-test.s3.amazonaws.com/obj3?"
				+ "AWSAccessKeyId=%s&Expires=%d&Signature=%s", access_id, expires, sig)); 
				
	}

}

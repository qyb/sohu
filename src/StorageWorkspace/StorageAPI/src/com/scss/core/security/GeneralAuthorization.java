/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.security;

import java.security.SignatureException;
import java.util.Map;

import com.scss.core.APIRequest;
import com.scss.core.CommonRequestHeader;

/**
 * Authorization implementation base
 * 
 * @author Samuel
 *
 */
public class GeneralAuthorization extends Authorization {

	public GeneralAuthorization(APIRequest req) {
		super(req);
		logger.debug("General authorization starting...");
	}

	@Override
	public Boolean authorize() {
		return super.authorize();
	}

	@Override
	protected String getSignature(Credential cred) {
		Signature signature = new GeneralSigner(cred.getAccessId());
		String str_to_sign = this.getStringToSign();
		logger.debug(String.format(" String to sign : \n%s", str_to_sign));
		return signature.sign(str_to_sign);
	}

	@Override
	protected String getStringToSign() {
		String canonical_res = this.getCanonicalizedResource();
		String canonical_headers = this.getCanonicalizedHeaders();
		Map<String, String> headers = this.getRequest().getHeaders();
		StringBuilder sb = new StringBuilder();
		String val = "";
		
		sb.append(this.getRequest().Method).append("\n");
		val = headers.get(CommonRequestHeader.CONTENT_MD5);
		sb.append(null==val?"":val).append("\n");
		val = headers.get(CommonRequestHeader.CONTENT_TYPE);
		sb.append(null==val?"":val).append("\n");
		val = headers.get(CommonRequestHeader.DATE);
		sb.append(null==val?"":val).append("\n");
		sb.append(canonical_headers);
		sb.append(canonical_res);
		
		return sb.toString();
	}
}

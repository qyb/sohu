package com.scss.core.security;

import java.util.Date;
import java.util.Map;

import com.scss.Headers;
import com.scss.core.APIRequest;

public class QueryStringAuthorization extends Authorization {

	public QueryStringAuthorization(APIRequest req) {
		super(req);
		logger.debug("Query string authorization starting...");
	}
	
	@Override
	public Boolean authorize() {
		Boolean rc = super.authorize();
		if (rc) {
			Credential cred = this.getCredential();
		 	Date now = new Date();
		 	logger.debug(String.format("Expires on %s, now is %s", new Date(cred.getExpires()), now));
		 	if (now.getTime() > cred.getExpires())
		 		rc = false;
		}
		return rc;
	}
	
	@Override
	protected String getSignature(Credential cred) {
		Signature signature = new QueryStringSigner(cred.getAccessId());
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
		val = headers.get(Headers.CONTENT_MD5);
		sb.append(null==val?"":val).append("\n");
		val = headers.get(Headers.CONTENT_TYPE);
		sb.append(null==val?"":val).append("\n");
		val = this.getRequest().Querys.get(Authorization.QUERY_EXPIRES);
		assert (null != val);
		sb.append(null==val?"":val).append("\n");
		sb.append(canonical_headers);
		sb.append(canonical_res);
		
		return sb.toString();
	}
}

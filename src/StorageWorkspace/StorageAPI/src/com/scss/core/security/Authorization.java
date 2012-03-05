/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.security;

import java.security.SignatureException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.scss.Headers;
import com.scss.Headers;
import com.scss.core.APIRequest;
import com.scss.core.ResponseHeaderOverrides;
import com.scss.db.User;
import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.model.ScssUser;

/**
 * General authorization implementation
 * 
 * @author Samuel
 *
 */
public abstract class Authorization implements IAuth {
	protected final static String AuthPrefix = "AWS";
	protected final static Set<String> AcceptedResources = new HashSet<String>();
	public final static String QUERY_AWS_ACCESS_KEY_ID = "AWSAccessKeyId";
	public final static String QUERY_EXPIRES = "Expires";
	public final static String QUERY_SIGNATURE = "Signature";
	protected final static Logger logger = Logger.getLogger(Authorization.class);
	
	static {
		String[] resources = {
			"acl", "lifecycle", "location", "logging", "notification", 
			"partNumber".toLowerCase(), "policy", "requestPayment".toLowerCase(),
			"torrent", "uploadId".toLowerCase(), "uploads", 
			"versionId".toLowerCase(), "versioning", "versions", "website",
			ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL,
			ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION, 
			ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING, 
			ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE,
			ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE, 
			ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES,
		};
		for (String val: resources)
			AcceptedResources.add(val);
	}
	
	private APIRequest request;
	private Credential credential;
	

	protected Authorization(APIRequest req) {
		this.request = req;
	}

	public static IAuth createInstace(APIRequest req) {
		Credential cred = Authorization.createCredentail(req);
		IAuth auth = null;
		switch(cred.getAuthorizationType()) {
			case GENERAL: {
				auth = new GeneralAuthorization(req);
				((GeneralAuthorization)auth).setCredential(cred);
				break;
			}
			case TEMPORARY: {
				auth = new TemporaryAuthorization(req);
				((TemporaryAuthorization)auth).setCredential(cred);
				break;
			}
			case QUERY: {
				auth = new QueryStringAuthorization(req);
				((QueryStringAuthorization)auth).setCredential(cred);
				break;
			}
		}
		return auth;		
	}

	public static Credential createCredentail(APIRequest req) {
		assert (null != req);
		
		Credential cred = new Credential();
		String req_auth = req.getHeaders().get(Headers.AUTHORIZATION);
		if (null != req_auth) {
			cred.setAuthorizationType(AuthorizationTypes.GENERAL);
			String[] authes = req_auth.split("\\s|:");
			if (3 == authes.length && Authorization.AuthPrefix.equals(authes[0])){
				cred.setAccessId(authes[1]);
				cred.setClientSignature(authes[2]);
			}
		} else {
			String access_id = req.Querys.get(Authorization.QUERY_AWS_ACCESS_KEY_ID);
			if (null != access_id && access_id.length() > 0) {
				cred.setAuthorizationType(AuthorizationTypes.QUERY);
				
				String signature = req.Querys.get(Authorization.QUERY_SIGNATURE);
				String expires = req.Querys.get(Authorization.QUERY_EXPIRES);
				if (null != signature && null != expires) {
					Long exp = Long.parseLong(expires);
					cred.setAccessId(access_id);
					cred.setClientSignature(signature);
					cred.setExpires(exp);
				}
			}
		}
		
		return cred;
	}
	
	@Override
	public Boolean authorize() {
		
		Credential cred = getCredential();
		ScssUser suser = ScssUserDaoImpl.getInstance().getUserByAccessId(cred.getAccessId());
		if (null != suser) {
			logger.info(String.format("User %s retrived.", suser.getAccessId()));
			cred.setAccessKey(suser.getAccessKey());
			User user = new User(suser);
			String our_signature = this.getSignature(cred);
			logger.debug(String.format("Computed Signature : %s", our_signature));
			logger.debug(String.format("Client Signature : %s", cred.getClientSignature()));
			if (null != user && our_signature.equals(cred.getClientSignature())) {
				request.setUser(user);
				return true;
			} else
				logger.info(String.format("User of access_id (%s) is not found.", cred.getAccessId()));
		}
		return false;
	}
	
	protected abstract String getSignature(Credential cred);
//	protected String getSignature(String access_secret_key) throws SignatureException {
//		Signature signature = new GeneralSigner(access_secret_key);
//		String str_to_sign = this.getStringToSign();
//		logger.debug(String.format(" String to sign : \n%s", str_to_sign));
//		return signature.sign(str_to_sign);
//	}
	
	protected abstract String getStringToSign();
	
//	protected String getStringToSign() {
//		String canonical_res = this.getCanonicalizedResource();
//		String canonical_headers = this.getCanonicalizedHeaders();
//		Map<String, String> headers = this.request.getHeaders();
//		StringBuilder sb = new StringBuilder();
//		String val = "";
//		
//		sb.append(this.request.Method).append("\n");
//		val = headers.get(CommonRequestHeader.CONTENT_MD5);
//		sb.append(null==val?"":val).append("\n");
//		val = headers.get(CommonRequestHeader.CONTENT_TYPE);
//		sb.append(null==val?"":val).append("\n");
//		val = headers.get(CommonRequestHeader.DATE);
//		sb.append(null==val?"":val).append("\n");
//		sb.append(canonical_headers);
//		sb.append(canonical_res);
//		
//		return sb.toString();
//	}
	
	protected String getCanonicalizedResource() {
		StringBuilder sb = new StringBuilder();
		
		if (null != this.request.BucketName && this.request.BucketName.length() > 0) {
			sb.append("/");
			sb.append(this.request.BucketName);
		}
		
		sb.append(this.request.Path);
		if (this.request.Querys.size()>0) {
			// sort the parameters
			TreeSet<String> keys = new TreeSet<String> (this.request.Querys.keySet());
			char separator = '?';
			for (String key: keys) {
				String k = key.toLowerCase();
				if (Authorization.AcceptedResources.contains(k)) {
					sb.append(separator).append(k);
					String value = this.request.Querys.get(key);
					if (null != value)
						sb.append("=").append(value);
				}
				separator = '&';
			}
		}
		
		String rc = sb.toString();
		
		return rc;
	}
	
	protected String getCanonicalizedHeaders() {
		Map<String, String> headers = this.request.getHeaders();
		StringBuilder sb = new StringBuilder();
		
		TreeSet<String> keys = new TreeSet<String>(headers.keySet());
		for (String key: keys) {
			String k = key.toLowerCase();
			if (k.startsWith(Headers.S3_PREFIX)){
				//TODO: Need to combine all values with "," of this header
				sb.append(k).append(":").append(headers.get(key).trim()).append("\n"); 
			}
		}
		
		return sb.toString();
	}

	/**
	 * @return the request
	 */
	protected APIRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	protected void setRequest(APIRequest request) {
		this.request = request;
	}

	/**
	 * @return the credential
	 */
	protected Credential getCredential() {
		return credential;
	}

	/**
	 * @param credential the credential to set
	 */
	protected void setCredential(Credential credential) {
		this.credential = credential;
	}

}

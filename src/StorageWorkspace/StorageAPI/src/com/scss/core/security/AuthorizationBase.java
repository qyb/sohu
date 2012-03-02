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

import com.scss.core.APIRequest;
import com.scss.core.CommonRequestHeader;
import com.scss.db.User;
import com.scss.db.dao.ScssUserDaoImpl;
import com.scss.db.model.ScssUser;

/**
 * General authorization implementation
 * 
 * @author Samuel
 *
 */
public class AuthorizationBase implements IAuth {
	private final static String AuthPrefix = "AWS";
	private final static String CanonicalizedHeaderPrefix = "x-amz-";
	private final static Set<String> AcceptedResources = new HashSet<String>();
	static {
		String[] resources = {
			"acl", "lifecycle", "location", "logging", "notification", 
			"partNumber".toLowerCase(), "policy", "requestPayment".toLowerCase(),
			"torrent", "uploadId".toLowerCase(), "uploads", 
			"versionId".toLowerCase(), "versioning", "versions", "website"
		};
		for (String val: resources)
			AcceptedResources.add(val);
	}
	private final Logger logger = Logger.getLogger(getClass());
	
	private APIRequest request;
	
	public AuthorizationBase(APIRequest req) {
		this.request = req;
	}
	
	public static IAuth createInstace(APIRequest req) {
		return AuthorizationBase.createInstace(req, AuthorizationTypes.GENERAL);
	}
	public static IAuth createInstace(APIRequest req, AuthorizationTypes auth_type) {
		switch(auth_type) {
			case GENERAL:
				return new Authorization(req);
			case TEMPORARY:
				return new TemporaryAuthorization(req);
			case QUERY:
				return new QueryStringAuthorization(req);
		}
		return null;
	}
	
	@Override
	public Boolean authorize() {
		String req_auth = request.getHeaders().get(CommonRequestHeader.AUTHORIZATION);
		if (null != req_auth) {
			String[] authes = req_auth.split("\\s|:");
			if (3 == authes.length && AuthorizationBase.AuthPrefix.equals(authes[0])){
				String access_id = authes[1];
				String signature = authes[2];
				
				ScssUser suser = ScssUserDaoImpl.getInstance().getUserByAccessId(access_id);
				try {
					User user = new User(suser);
					String our_signature = this.getSignature(user.getAccessKey());
					logger.debug(String.format("Computed Signature : %s", our_signature));
					if (null != user && our_signature.equals(signature)) {
						request.setUser(user);
						return true;
					} else
						logger.info(String.format("User of access_id (%s) is not found.", access_id));
				} catch (SignatureException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return false;
	}
	
	private String getSignature(String access_secret_key) throws SignatureException {
		String str_to_sign = this.getStringToSign();
		logger.debug(String.format(" String to sign : \n%s", str_to_sign));
		return Credential.calculateRFC2104HMAC(str_to_sign, access_secret_key);
	}
	
	private String getStringToSign() {
		String canonical_res = this.getCanonicalizedResource();
		String canonical_headers = this.getCanonicalizedHeaders();
		Map<String, String> headers = this.request.getHeaders();
		StringBuilder sb = new StringBuilder();
		String val = "";
		
		sb.append(this.request.Method).append("\n");
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
	
	private String getCanonicalizedResource() {
		StringBuilder sb = new StringBuilder();
		
		if (null != this.request.BucketName && this.request.BucketName.length() > 0) {
			sb.append("/");
			sb.append(this.request.BucketName);
		}
		
		sb.append(this.request.Path);
		if (this.request.Querys.size()>0) {
			// sort the querys
			TreeSet<String> keys = new TreeSet<String> (this.request.Querys.keySet());
			char separator = '?';
			for (String key: keys) {
				String k = key.toLowerCase();
				if (AuthorizationBase.AcceptedResources.contains(k)) {
					sb.append(separator).append(k).append("=").append(this.request.Querys.get(key));
				}
				separator = '&';
			}
		}
		
		String rc = sb.toString();
		
		return rc;
	}
	
	private String getCanonicalizedHeaders() {
		Map<String, String> headers = this.request.getHeaders();
		StringBuilder sb = new StringBuilder();
		
		TreeSet<String> keys = new TreeSet<String>(headers.keySet());
		for (String key: keys) {
			String k = key.toLowerCase();
			if (k.startsWith(AuthorizationBase.CanonicalizedHeaderPrefix)){
				//TODO: Need to combine all values with "," of this header
				sb.append(k).append(":").append(headers.get(key).trim()).append("\n"); 
			}
		}
		
		return sb.toString();
	}

}

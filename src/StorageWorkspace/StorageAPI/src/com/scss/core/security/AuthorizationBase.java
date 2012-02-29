/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.security;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.scss.core.APIRequest;
import com.scss.core.CommonRequestHeader;
import com.scss.db.User;
import com.scss.db.model.ScssUser;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

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
	
	
	private APIRequest request;
	
	public AuthorizationBase(APIRequest req) {
		this.request = req;
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
				
				ScssUser user = DBServiceHelper.getUserByAccessKey(access_id); //TODO: ByAccessID not availible yet
				
				try {
					if (null != user && this.getSignature(user.getAccessKey()) == signature) {
						request.setUser((User) user); //TODO: change the User type later depends on DB design.
						return true;
					}
				} catch (SignatureException e) {
					// TODO: Add post-process for SignatureException.
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private String getSignature(String access_id) throws SignatureException {
		String str_to_sign = this.getStringToSign();
		return CommonUtilities.calculateRFC2104HMAC(str_to_sign, access_id);
	}
	
	private String getStringToSign() {
		String canonical_res = this.getCanonicalizedResource();
		String canonical_headers = this.getCanonicalizedHeaders();
		Map<String, String> headers = this.request.getHeaders();
		StringBuilder sb = new StringBuilder();
		String val = "";
		
		sb.append(this.request.Method);
		sb.append("\n");
		val = headers.get(CommonRequestHeader.CONTENT_MD5);
		sb.append(null==val?"":val);
		sb.append("\n");
		val = headers.get(CommonRequestHeader.CONTENT_TYPE);
		sb.append(null==val?"":val);
		sb.append("\n");
		val = headers.get(CommonRequestHeader.DATE);
		sb.append(null==val?"":val);
		sb.append("\n");
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
			sb.append("?");
			for (String key: keys) {
				String k = key.toLowerCase();
				if (AuthorizationBase.AcceptedResources.contains(k)) {
					sb.append(key);
					sb.append("&");
					sb.append(this.request.Querys.get(key));
				}
			}
		}
		
		String rc = sb.toString();
		if (rc.equals("?"))
			rc = "";
		
		return rc;
	}
	
	private String getCanonicalizedHeaders() {
		Map<String, String> headers = this.request.getHeaders();
		StringBuilder sb = new StringBuilder();
		
		TreeSet<String> keys = new TreeSet<String>(headers.keySet());
		for (String key: keys) {
			String k = key.toLowerCase();
			if (k.startsWith(AuthorizationBase.CanonicalizedHeaderPrefix)){
				sb.append(k);
				sb.append(":");
				sb.append(headers.get(key).trim()); //TODO: Combine all values of this header
			}
		}
		
		return sb.toString();
	}

}

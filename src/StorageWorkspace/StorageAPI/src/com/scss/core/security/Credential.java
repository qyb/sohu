package com.scss.core.security;

import org.apache.log4j.Logger;

public class Credential {
	private String accessId = null;
	private String accessKey = null;
	private String clientSignature = null;
	private AuthorizationTypes type = AuthorizationTypes.GENERAL;
	private Long expires = -1L;
//	private Boolean illegal = null;
	protected final static Logger logger = Logger.getLogger(Credential.class);
	
	public Credential(){}
	
	public Credential(String access_id, String signature) {
		accessId = access_id;
		clientSignature = signature;
		type = AuthorizationTypes.GENERAL;
		logger.debug(String.format("Credential created <%s, %s>", access_id, signature));
	}

	public Credential(String access_id, String signature, Long expires) {
		accessId = access_id;
		clientSignature = signature;
		this.expires = expires;
		type = AuthorizationTypes.QUERY;
		logger.debug(String.format("Credential created <%s, %d, %s>", access_id, expires, signature));
	}
	
//	protected ISigner getSigner() {
//		ISigner signer = null;
//		switch (this.getAuthorizationType()) {
//			case GENERAL: { signer = new GeneralSigner(this.getAccessKey()); break; }
//			case QUERY: { signer = new QueryStringSigner(this.getAccessId()); break; }
//			case TEMPORARY: { signer = new TemporarySigner(this.getAccessKey()); break; }
//		}
//		return signer;
//		 
//	}

//	public Boolean isLegal() {
//		if (null == illegal) {
////			ISigner signer = getSigner();
////			signer.sign(data)
//		}
//		
//		return !illegal ;
//	}

	/**
	 * @return the accessId
	 */
	public String getAccessId() {
		return accessId;
	}

	/**
	 * @param accessId the accessId to set
	 */
	protected void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	/**
	 * @return the clientSignature
	 */
	public String getClientSignature() {
		return clientSignature;
	}

	/**
	 * @param clientSignature the clientSignature to set
	 */
	public void setClientSignature(String clientSignature) {
		this.clientSignature = clientSignature;
	}

	/**
	 * @return the expires
	 */
	public Long getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(Long expires) {
		this.expires = expires;
	}

	/**
	 * @return the type
	 */
	public AuthorizationTypes getAuthorizationType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setAuthorizationType(AuthorizationTypes type) {
		this.type = type;
	}

	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}


}

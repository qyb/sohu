package com.scss.core.security;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.scss.Const;
import com.scss.utility.CommonUtilities;

/**
 * 
 * @author samuel
 */
public abstract class Signature implements ISigner {
	protected SignatureVersion version = SignatureVersion.V1;
	protected SignatureAlgorithmic algorithm = SignatureAlgorithmic.V1;
	private String key = null;
	protected final static Logger logger = Logger.getLogger(Signature.class);
	
	public Signature(SignatureVersion ver, String sign_key) {
		version = ver;
		switch (version) {
			case V1: {algorithm = SignatureAlgorithmic.V1; break;}
			case V2: {algorithm = SignatureAlgorithmic.V2; break;}
		}
		key = sign_key;
		assert(null != key);
	}
	
	@Override
	public String sign(String data) {
		return sign(data, Const.DEFAULT_ENCODING);
	}
	
	@Override
	public String sign(String data, String encoding) {
		if (null == encoding || encoding.length() == 0)
			encoding = Const.DEFAULT_ENCODING;
		try {
			return CommonUtilities.calculateRFC2104HMAC(
					data.getBytes(encoding), 
					key.getBytes(encoding), 
					algorithm.toString());
		} catch (SignatureException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Encoding %s is not supported.", encoding));
		}
		return null;
	}

	@Override
	public String sign(byte[] data) {
		return sign(data, Const.DEFAULT_ENCODING);
	}


	@Override
	public String sign(byte[] data, String encoding) {
		if (null == encoding || encoding.length() == 0)
			encoding = Const.DEFAULT_ENCODING;
		try {
			return CommonUtilities.calculateRFC2104HMAC(
					data,
					key.getBytes(encoding), 
					algorithm.toString());
		} catch (SignatureException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Encoding %s is not supported.", encoding));
		}
		return null;
	}	
	
	/**
	 * @return the version
	 */
	public SignatureVersion getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(SignatureVersion version) {
		this.version = version;
	}

	/**
	 * @return the algorithm
	 */
	public SignatureAlgorithmic getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(SignatureAlgorithmic algorithm) {
		this.algorithm = algorithm;
	}

}

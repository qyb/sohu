/**
 * 
 */
package com.scss.core.security;

/**
 * @author Samuel
 *
 */
public interface ISigner {
	/**
	 * Sign the given data string with credential
	 * key in default encoding (UTF-8)
	 * 
	 * @param data
	 * 		data to be signed.
	 * @return
	 * 		signed string.
	 */
	String sign(String data);
	
	/**
	 * Sign the given data string with credential
	 * key in given encoding
	 * 
	 * 
	 * @param data
	 * 		data to be signed.
	 * @param encoding
	 * 		key and data encoding.
	 * @return
	 * 		signed string.
	 */
	String sign(String data, String encoding);
	
	/**
	 * Sign the given data with credential
	 * key in given encoding
	 * 
	 * 
	 * @param data
	 * 		data to be signed.
	 * @return
	 * 		signed string.
	 */	
	String sign(byte[] data);
	
	/**
	 * Sign the given data with credential
	 * key in given encoding
	 * 
	 * 
	 * @param data
	 * 		data to be signed.
	 * @param encoding
	 * 		key and data encoding.
	 * @return
	 * 		signed string.
	 */
	String sign(byte[] data, String encoding);
}

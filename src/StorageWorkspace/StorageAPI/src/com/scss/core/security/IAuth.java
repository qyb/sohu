/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.security;

import com.scss.core.APIRequest;

/**
 * Authorization interface
 * 
 * @author Samuel
 *
 */
public interface IAuth {

	/*
	 * Authorize a request.
	 * 
	 * @return true if the request is authorized. Otherwise false.
	 */
	Boolean authorize();
}

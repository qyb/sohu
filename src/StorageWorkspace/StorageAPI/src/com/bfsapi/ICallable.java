/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi;

import com.bfsapi.server.APIRequest;
import com.bfsapi.server.APIResponse;

/**
 * @author Samuel
 *
 */
public interface ICallable {
	APIResponse Invoke(APIRequest req);
	Boolean CanInvoke(APIRequest req, IAccessor invoker);
}

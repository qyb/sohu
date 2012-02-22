/**
 * Copyright Sohu Inc. 2012
 */
package com.scss;

import com.scss.core.APIRequest;
import com.scss.core.APIResponse;

/**
 * @author Samuel
 *
 */
public interface ICallable {
	APIResponse Invoke(APIRequest req);
	Boolean CanInvoke(APIRequest req, IAccessor invoker);
}

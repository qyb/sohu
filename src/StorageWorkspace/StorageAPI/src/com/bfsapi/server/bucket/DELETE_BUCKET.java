/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server.bucket;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import com.bfsapi.IAccessor;
import com.bfsapi.db.BucketBussiness;
import com.bfsapi.server.APIRequest;
import com.bfsapi.server.APIResponse;
import com.bfsapi.server.CommonRequestHeader;
import com.bfsapi.server.CommonResponseHeader;
import com.bfsapi.server.MediaTypes;

/**
 * @author Samuel
 *
 */
public class DELETE_BUCKET extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(com.bfsapi.server.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		// TODO Auto-generated method stub
		
		Map<String, String> req_headers = req.getHeaders();
		
		String authorization = req_headers.get(CommonRequestHeader.AUTHORIZATION);
		
		String access_key= authorization.split(":")[0];
		
		BucketBussiness.deleteBucket(req.BucketName, access_key);
		
		APIResponse resp = new BucketAPIResponse();
		
		Map<String, String> resp_headers = resp.getHeaders();
		
		// set common response header
		// TODO: change the temporary values
		resp_headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
		
		resp_headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");	
		
		resp_headers.put(CommonResponseHeader.CONTENT_TYPE, MediaTypes.APPLICATION_XML);
		
		resp_headers.put(CommonResponseHeader.CONNECTION, "close");
		
		resp_headers.put(CommonResponseHeader.SERVER, "SohuS4");
		
		//TODO: set user meta
		// user_meta key-value pair -> header
		
		// TODO: set system meta
		resp_headers.put(CommonResponseHeader.DATE, DateFormat.getDateTimeInstance().format(new Date()));
		
		// generate representation
		resp.Repr = new org.restlet.representation.EmptyRepresentation();
		
		resp.MediaType = MediaTypes.APPLICATION_XML;
		
		return resp;
		
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#CanInvoke(com.bfsapi.server.APIRequest, com.bfsapi.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return true;
	}

}

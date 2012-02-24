/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.BucketBussiness;
import com.scss.db.model.ScssBucket;
import com.scss.db.service.DBServiceHelper;

/**
 * @author Samuel
 *
 */
public class DELETE_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		// TODO Auto-generated method stub
		
        Map<String, String> req_headers = req.getHeaders();
		
		APIResponse resp = new BucketAPIResponse();
		Map<String, String> resp_headers = resp.getHeaders();
		
		boolean deleteObjectFlag = BucketBussiness.deleteObject(req.ObjectKey, req.getUser().getId(), req.BucketName);
		
		if(deleteObjectFlag)
		{
			// set common response header
			// TODO: change the temporary values
			resp_headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp_headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");	
			resp_headers.put(CommonResponseHeader.CONTENT_TYPE, Mimetypes.APPLICATION_XML);
			resp_headers.put(CommonResponseHeader.CONNECTION, "close");
			resp_headers.put(CommonResponseHeader.SERVER, "SohuS4");
			//TODO: set user meta
			// user_meta key-value pair -> header
			// TODO: set system meta
			resp_headers.put(CommonResponseHeader.DATE, DateFormat.getDateTimeInstance().format(new Date()));
			
			// generate representation
			resp.Repr = new org.restlet.representation.EmptyRepresentation();
			resp.MediaType = Mimetypes.APPLICATION_XML;
			
			return resp;
		
		}
		
		return ErrorResponse.AccessDenied(req);
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#CanInvoke(com.scss.core.APIRequest, com.bfsapi.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		
		
		
		
		
		return null;
	}

}

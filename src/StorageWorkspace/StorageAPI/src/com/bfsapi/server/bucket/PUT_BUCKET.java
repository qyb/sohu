/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server.bucket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.text.DateFormatter;

import org.restlet.engine.util.DateUtils;

import com.bfsapi.IAccessor;
import com.bfsapi.db.Bucket;
import com.bfsapi.db.business.BucketBussiness;
import com.bfsapi.db.model.ScssBucket;
import com.bfsapi.db.service.DBServiceHelper;
import com.bfsapi.server.APIRequest;
import com.bfsapi.server.APIResponse;
import com.bfsapi.server.APIResponseHeader;
import com.bfsapi.server.CommonRequestHeader;
import com.bfsapi.server.CommonResponseHeader;
import com.bfsapi.server.MediaTypes;

/**
 * @author Samuel
 *
 */
public class PUT_BUCKET extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(java.lang.Object)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		//Date createTime = CommonUtilities.parseHeaderDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date createTime = new Date();
		createTime = DateUtils.parse(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		
		//TODO: Check whether Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.
		
		String authorization = req_headers.get(CommonRequestHeader.AUTHORIZATION);
		
		String access_key= authorization.split(":")[0];
		
		ScssBucket bucket = (ScssBucket)BucketBussiness.putBucket(req.BucketName, access_key,user_meta);
		
		// set response headers
		if (null != bucket) {
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			resp_headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp_headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");				
			resp_headers.put(CommonResponseHeader.CONTENT_TYPE, MediaTypes.APPLICATION_XML);
			resp_headers.put(CommonResponseHeader.CONNECTION, "close");
			resp_headers.put(CommonResponseHeader.SERVER, "SohuS4");
			
			// Set API response header
			resp_headers.put(APIResponseHeader.LOCATION, "/" + req.BucketName);

			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			resp_headers.put(CommonResponseHeader.DATE, DateFormat.getDateTimeInstance().format(bucket.getModifyTime()));
			resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0"); // PUT_BUCKET has no content
			
			// generate representation
			resp.Repr = new org.restlet.representation.EmptyRepresentation();
			resp.MediaType = MediaTypes.APPLICATION_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.NoSuchBucket(req);
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

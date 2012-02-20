/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.util.Date;
import java.util.Map;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.MediaTypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.exception.SameNameDirException;
import com.scss.db.model.ScssBucket;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;


/**
 * @author Samuel
 *
 */
public class PUT_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(com.bfsapi.server.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		String media_type = req_headers.get(CommonResponseHeader.MEDIA_TYPE);
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		
		//TODO: Check whether Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// get request
				
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.
		
		BfsClientResult rt = BfsClientWrapper.putFromStream(req.ContentStream);
		if (rt.FileNumber > 0)
			try{
				ScssBucket bucket = DBServiceHelper.getBucketByName(req.BucketName, req.getUser().getId());
				assert(null != bucket);
				DBServiceHelper.putObject(req.ObjectKey, rt.FileNumber, req.getUser().getId(), 
						bucket.getId(), user_meta, rt.Size, media_type);
			} catch (SameNameDirException e) {
				return ErrorResponse.BucketAlreadyExists(req);
			}
		
		ScssBucket bucket = DBServiceHelper.getBucketByName(req.BucketName, req.getUser().getId());
		
		
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
			resp_headers.put(CommonResponseHeader.DATE, CommonUtilities.formatResponseHeaderDate(bucket.getModifyTime()));
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
		return null;
	}

}

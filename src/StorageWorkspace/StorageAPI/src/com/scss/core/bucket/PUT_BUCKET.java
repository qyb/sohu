/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssUser;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/**
 * @author Samuel
 *
 */
public class PUT_BUCKET extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(java.lang.Object)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		String media_type = req_headers.get(CommonResponseHeader.CONTENT_TYPE);
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		
		//TODO: Check whether Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.
		ScssBucket bucket=new ScssBucket();
		
		try{
			
			bucket.setName(req.BucketName);
			
			bucket.setOwnerId(req.getUser().getId());
			
			bucket.setMeta(user_meta);
			
			bucket = ScssBucketDaoImpl.getInstance().insertBucket(bucket);
			
		} catch (SameNameException e) {
			return ErrorResponse.BucketAlreadyExists(req);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set response headers
		if (null != bucket) {
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
			
			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			//resp_headers.put(CommonResponseHeader.DATE, );
			//resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0"); // PUT_BUCKET has no content
			
			// generate representation
			resp.Repr = new org.restlet.representation.EmptyRepresentation();
			resp.Repr.setSize(0);
			resp.Repr.setModificationDate(bucket.getModifyTime());
			resp.MediaType = Mimetypes.APPLICATION_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.InternalError(req);
	}

	/* (non-Javadoc)
	 * @see com.scss.ICallable#CanInvoke(com.scss.core.APIRequest, com.scss.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return true;
	}


}

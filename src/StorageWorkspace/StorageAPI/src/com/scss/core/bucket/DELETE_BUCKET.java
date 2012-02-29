/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;

/**
 * @author Samuel
 *
 */
public class DELETE_BUCKET extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		APIResponse resp = new BucketAPIResponse();
		Map<String, String> resp_headers = resp.getHeaders();
		
        ScssBucket  scssBucket=DBServiceHelper.getBucketByName(req.BucketName,req.getUser().getId());
		
		if(null!= scssBucket)
		{
			List<ScssObject> scssObjectList = DBServiceHelper.getBucket(req.BucketName);
			
			if(null!=scssObjectList&&scssObjectList.size()>0)
			{
				return ErrorResponse.DeleteBucketBeforeDeleteObject(req);  //删除这个bucket前它下面的object必须为空
			}
			else
			{
				DBServiceHelper.deleteBucket(req.BucketName, req.getUser().getId());
				
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
			
		}
		else
		{
			   return ErrorResponse.NoSuchBucket(req);
		}
		
		
	}

	/* (non-Javadoc)
	 * @see com.scss.ICallable#CanInvoke(com.scss.core.APIRequest, com.scss.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return null;
	}

}

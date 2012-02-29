/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;

/**
 * @author Samuel
 *
 */
public class DELETE_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		// TODO Auto-generated method stub
      
		
		APIResponse resp = new BucketAPIResponse();
		Map<String, String> resp_headers = resp.getHeaders();
		
        ScssBucket  scssBucket=DBServiceHelper.getBucketByName(req.BucketName,req.getUser().getId()) ;
		
		if(null!=scssBucket)
		{
			
			ScssObject obj = DBServiceHelper.getObject(req.BucketName,req.ObjectKey);
			
			if(null!=obj&&null != obj.getKey() && null != obj.getBfsFile())
			{
				
				try {
					DBServiceHelper.deleteObject(req.ObjectKey, req.getUser().getId(), scssBucket.getId());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				 try {
					 
					  BfsClientWrapper.getInstance().deleteFile(obj.getBfsFile());
					  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//如果失败需要记录下来
				}
				 
				CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
				resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0");
				//TODO: set user meta
				// user_meta key-value pair -> header
				// TODO: set system meta
				resp_headers.put(CommonResponseHeader.DATE, DateFormat.getDateTimeInstance().format(new Date()));
				
				// generate representation
				resp.Repr = new org.restlet.representation.EmptyRepresentation();
				resp.MediaType = Mimetypes.APPLICATION_XML;
					
				return resp; 
				
			}
			else
			{
				return ErrorResponse.NoSuchObject(req);
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

/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scss.Headers;
import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;


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
		
        ScssBucket scssBucket=null;
		scssBucket = ScssBucketDaoImpl.getInstance().getBucket(req.BucketName);
		
		if(null!= scssBucket)
		{
			List<ScssObject> scssObjectList=null;
			scssObjectList = ScssObjectDaoImpl.getInstance().getObjectsByBucketId(scssBucket.getId());
			
			if(null!=scssObjectList&&scssObjectList.size()>0)
			{
				return ErrorResponse.DeleteBucketBeforeDeleteObject(req);  //删除这个bucket前它下面的object必须为空
			}
			else
			{
				try {
					ScssBucketDaoImpl.getInstance().deleteBucket(scssBucket);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				setCommResponseHeaders(resp_headers,req);
				
				//TODO: set user meta
				// user_meta key-value pair -> header
				
				// TODO: set system meta
				resp_headers.put(Headers.DATE, DateFormat.getDateTimeInstance().format(new Date()));
				
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

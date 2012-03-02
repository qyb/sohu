/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.sql.SQLException;
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
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
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
		
        ScssBucket scssBucket=null;
		try {
			scssBucket = ScssBucketDaoImpl.getInstance().getBucket(req.BucketName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(null!= scssBucket)
		{
			List<ScssObject> scssObjectList=null;
			try {
				scssObjectList = ScssObjectDaoImpl.getObjectsByBucketId(scssBucket.getId());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(null!=scssObjectList&&scssObjectList.size()>0)
			{
				return ErrorResponse.DeleteBucketBeforeDeleteObject(req);  //删除这个bucket前它下面的object必须为空
			}
			else
			{
				DBServiceHelper.deleteBucket(req.BucketName, req.getUser().getId());
				
				CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
				
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

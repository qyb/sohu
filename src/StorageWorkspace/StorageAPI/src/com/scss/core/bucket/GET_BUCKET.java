/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.restlet.engine.util.DateUtils;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.MediaTypes;
import com.scss.db.User;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/**
 * @author Samuel
 *
 */
public class GET_BUCKET extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(com.bfsapi.server.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		//Date createTime = CommonUtilities.parseHeaderDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date createTime = new Date();
		String date_str = req_headers.get(CommonResponseHeader.DATE);
		if (null != date_str) 
			createTime = DateUtils.parse(date_str);
		Date modifyTime = createTime;
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		
		//TODO: Check ACL
		//TODO: Check whether Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.
		List<ScssObject> bucket_objects = DBServiceHelper.getBucket(req.getUser().getId(), req.BucketName);
		
		// set response headers
		if (null != bucket_objects) {
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
			resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0"); // GET_SERVICE has no content
			
			// generate representation
			resp.Repr = new org.restlet.representation.StringRepresentation(this.getResponseText(req, bucket_objects));
			resp.MediaType = MediaTypes.APPLICATION_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.NoSuchBucket(req);
	}
	
	private String getResponseText(APIRequest req, List<ScssObject> bucket_objects) {
		// TODO: Use template?
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		// TODO: change xmlns
		sb.append("<ListBucketResult xmlns=\"http://doc.s3.amazonaws.com/2006-03-01\">\n");
		sb.append("  <Name>" + req.BucketName + "</Name>\n");
		sb.append("  <Prefix />\n"); // TODO: Not implemented
		sb.append("  <Marker />\n"); // TODO: Not implemented
		sb.append("  <MaxKeys />\n"); // TODO: Not implemented
		sb.append("  <IsTruncated />\n"); // TODO: Not implemented
		
		sb.append("  <Contents>\n");
		for(ScssObject obj: bucket_objects){
		    sb.append("    <Key>" + obj.getKey() + "</Key>\n");
		    sb.append("    <LastModified>" + CommonUtilities.formatResponseDatetime(obj.getModifyTime())+ "</LastModified>\n");
		    sb.append("    <ETag />"); // TODO: Not implemented
		    sb.append("    <Size>" + obj.getSize().toString() + "</Size>\n");
		    sb.append("    <StorageClass />"); // TODO: Not implemented
			sb.append("    <Owner>\n");
			sb.append("      <ID>" + req.getUser().getSohuId() + "</ID>\n");
			sb.append("      <DisplayName>" + req.getUser().getSohuId() + "</DisplayName>\n");
			sb.append("    </Owner>\n");		    
		}
		sb.append("  </Contents>\n");
		sb.append("</ListBucketResult>");
		return sb.toString();
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

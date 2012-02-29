/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;

import com.scss.Const;
import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
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
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		
		//TODO: Check ACL
		//TODO: Check whether Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.
		
		ScssBucket bucket = DBServiceHelper.getBucketByName(req.BucketName);
		if (null == bucket)
			return ErrorResponse.NoSuchBucket(req);
		List<ScssObject> bucket_objects = (List<ScssObject>) DBServiceHelper.getBucket(req.getUser().getId(), req.BucketName);
		
		// set response headers
		if (null != bucket_objects) {
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
			
			// Set API response header
			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
	
			
			// generate representation
			resp.Repr = new org.restlet.representation.StringRepresentation(this.getResponseText(req, bucket_objects),MediaType.TEXT_PLAIN);
			
			resp.MediaType = Mimetypes.APPLICATION_XML;
			
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.InternalError(req);
	}
	
	private String getResponseText(APIRequest req, List<ScssObject> bucket_objects) {
		// TODO: Use template? or make the string static
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<ListBucketResult xmlns=\"" + Const.XMLNS + "\">");
		sb.append("  <Name>" + req.BucketName + "</Name>");
		sb.append("  <Prefix />"); // TODO: Not implemented
		sb.append("  <Marker />"); // TODO: Not implemented
		sb.append("  <MaxKeys />"); // TODO: Not implemented
		sb.append("  <IsTruncated />"); // TODO: Not implemented
		
		sb.append("  <Contents>");
		for(ScssObject obj: bucket_objects){
		    sb.append("    <Key>" + obj.getKey() + "</Key>");
		    sb.append("    <LastModified>" + CommonUtilities.formatResponseTextDate(obj.getModifyTime())+ "</LastModified>");
		    sb.append("    <ETag />"); // TODO: Not implemented
		    sb.append("    <Size>" + obj.getSize().toString() + "</Size>");
		    sb.append("    <StorageClass />"); // TODO: Not implemented
			sb.append("    <Owner>");
			sb.append("      <ID>" + req.getUser().getSohuId() + "</ID>");
			sb.append("      <DisplayName>" + req.getUser().getSohuId() + "</DisplayName>");
			sb.append("    </Owner>");		    
		}
		sb.append("  </Contents>");
		sb.append("</ListBucketResult>");
		return sb.toString();
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

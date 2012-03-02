/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

import com.scss.Const;
import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.db.model.ScssBucket;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/**
 * @author Samuel
 *
 */
public class GET_SERVICE extends BucketAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		logger.info("Invoking GET_SERVICE ...");
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
		List<ScssBucket> buckets = DBServiceHelper.getBucketsByUserID(req.getUser().getId());
		
		// set response headers
		if (null != buckets) {
			logger.debug(String.format("%d buckets found", buckets.size()));
			
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
			
			// Set API response header
			//resp_headers.put(APIResponseHeader.LOCATION, );

			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			//resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0"); // GET_SERVICE has no content
			
			// generate representation
			String resp_xml = this.getResponseText(req, buckets);
			logger.debug(String.format("GET_SERVICE Response : \n%s", resp_xml));
			StringRepresentation repr = new StringRepresentation(resp_xml, MediaType.TEXT_XML);
			resp.Repr = repr;
			resp.MediaType = Mimetypes.MIMETYPE_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.NoSuchBucket(req);
	}

	private String getResponseText(APIRequest req, List<ScssBucket> buckets) {
		// TODO: make the string static
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<ListAllMyBucketsResult xmlns=\"" + Const.XMLNS + "\">");
		sb.append("  <Owner>");
		sb.append("    <ID>" + req.getUser().getSohuId() + "</ID>");
		sb.append("    <DisplayName>" + req.getUser().getSohuId() + "</DisplayName>");
		sb.append("  </Owner>");
		
		
		sb.append("  <Buckets>");
		for(ScssBucket bucket: buckets){
			sb.append("    <Bucket>");
		    sb.append("      <Name>" + bucket.getName() + "</Name>");
		    sb.append("      <CreationDate>" + CommonUtilities.formatResponseTextDate(bucket.getCreateTime()) + "</CreationDate>");
		    sb.append("    </Bucket>");
		}
		sb.append("  </Buckets>");
		sb.append("</ListAllMyBucketsResult>");
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

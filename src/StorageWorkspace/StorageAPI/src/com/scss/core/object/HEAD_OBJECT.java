package com.scss.core.object;

import java.util.Date;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Tag;
import org.restlet.representation.EmptyRepresentation;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/*
 * The HEAD operation retrieves metadata from an object 
 * without returning the object itself.This operation 
 * is useful if you're only interested in an object's metadata.
 * To use HEAD, you must have READ access to the object.
 * A HEAD request has the same options as a GET operation
 * on an object.The response is identical to the GET response, 
 * except that there is no response body.
 */
public class HEAD_OBJECT extends ObjectAPI {

	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public APIResponse Invoke(APIRequest req) {
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		String media_type = req_headers.get(CommonResponseHeader.CONTENT_TYPE);
		// TODO: GET size if required. long size = req_headers.get(CommonResponseHeader.CONTENT_LENGTH)
		// TODO: Server side md5 check. not supported now. String content_md5 = req_headers.get()
		
		//TODO: Check whether Bucket Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		
		// get request
				
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).

		// TODO: get by bucket name and key
		ScssObject obj = DBServiceHelper.getObject(req.BucketName, req.ObjectKey);
		if (null == obj || null == obj.getKey() || null == obj.getBfsFile())
			return ErrorResponse.NoSuchKey(req);

		// HEAD do not retrieve from BFS
		
		// set response headers
		APIResponse resp = new BucketAPIResponse();
		Map<String, String> resp_headers = resp.getHeaders();
		
		// set common response header
		// TODO: change the temporary values
		CommonResponseHeader.setCommHeaderInfoToRespHeader(resp_headers,req);
		
		// Set API response header
		resp_headers.put(APIResponseHeader.LOCATION, "/" + req.BucketName + req.Path);

		//TODO: set user meta
		// user_meta key-value pair -> header
		
		// TODO: set system meta
		resp_headers.put(CommonResponseHeader.DATE, CommonUtilities.formatResponseHeaderDate(new Date()));
		resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, String.valueOf(obj.getSize()));
		resp_headers.put(CommonResponseHeader.ETAG, obj.getEtag());

		// generate representation.
		resp.Repr = null;
//		resp.Repr = new EmptyRepresentation();
//		resp.Repr.setTag(new Tag(obj.getEtag()));
//		resp.Repr.setSize(obj.getSize());
//		resp.Repr.setMediaType(MediaType.TEXT_PLAIN);
		resp.MediaType = Mimetypes.MIMETYPE_TEXT;
		return resp;

	}

}

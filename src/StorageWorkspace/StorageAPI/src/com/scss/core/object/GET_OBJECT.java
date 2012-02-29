/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Tag;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.DynamicStreamRepresentation;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;

/**
 * @author Samuel
 *
 */
public class GET_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#CanInvoke(com.scss.core.APIRequest, com.scss.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
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

		// retrieve from BFS
		BfsClientResult bfsresult = BfsClientWrapper.getInstance().getFile(obj.getBfsFile());
		
		// set response headers
		if (null != bfsresult.File) {
			
			logger.info(String.format("Successfully read BFS file : %d (size=%d)\n", bfsresult.FileNumber, bfsresult.Size));
			
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			resp_headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp_headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");				
			resp_headers.put(CommonResponseHeader.CONTENT_TYPE, media_type);
			resp_headers.put(CommonResponseHeader.CONNECTION, "close");
			resp_headers.put(CommonResponseHeader.SERVER, "SohuS4");
			
			// Set API response header
			resp_headers.put(APIResponseHeader.LOCATION, "/" + req.BucketName + req.Path);

			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			resp_headers.put(CommonResponseHeader.DATE, CommonUtilities.formatResponseHeaderDate(obj.getModifyTime()));
			resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, String.valueOf(bfsresult.Size));
			// TODO: make the ETAG computing hooked in progress or use DigestRepresentation.
			resp_headers.put(CommonResponseHeader.ETAG, obj.getEtag());

			// generate representation.
			// TODO: if DigesterRepresentation can get ETAG, use it. 
			//ByteBuffer buffer = ByteBuffer.wrap(bfsresult.File);
			//resp.Repr = new ReadableRepresentation((ReadableByteChannel) buffer, MediaType.APPLICATION_OCTET_STREAM);
			ByteArrayInputStream stream = new ByteArrayInputStream(bfsresult.File);
			resp.Repr = new DynamicStreamRepresentation(stream, MediaType.APPLICATION_OCTET_STREAM);
			resp.Repr.setTag(new Tag(obj.getEtag()));
			resp.Repr.setMediaType(MediaType.valueOf(obj.getMediaType()));
			resp.MediaType = obj.getMediaType();
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.InternalError(req);
	}

}

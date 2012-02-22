/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.util.Date;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Tag;

import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.APIResponseHeader;
import com.scss.core.CommonRequestHeader;
import com.scss.core.CommonResponseHeader;
import com.scss.core.ErrorResponse;
import com.scss.core.MD5DigestInputStream;
import com.scss.core.Mimetypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;
import com.scss.db.service.DBServiceHelper;
import com.scss.utility.CommonUtilities;


/**
 * @author Samuel
 *
 */
public class PUT_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	@Override
	public APIResponse Invoke(APIRequest req) {
		// TODO: !!! need to re-organize. extract to pre-invoke post-invoke !!! 
		// TODO: !!! and use reslet metheod to process headers there !!!
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(CommonResponseHeader.DATE));
		Date modifyTime = createTime;
		String media_type = req_headers.get(CommonResponseHeader.MEDIA_TYPE);
		Long size = Long.parseLong(req_headers.get(CommonResponseHeader.CONTENT_LENGTH));
		if (size <= 0) {
			return ErrorResponse.MissingContentLength(req);
		}
		String contentMD5 = req_headers.get(CommonRequestHeader.CONTENT_MD5);
		Boolean version_enabled = false;
		String version = null;
		Date expireTime = null;
		String sys_meta = null; //extra system meta
		boolean server_check = false; // server md5 check
		// TODO: Server side md5 check. not supported now. String content_md5 = req_headers.get()
		
		//TODO: Check whether Bucket Logging is enabled 
				
		String user_meta = this.getUserMeta(req);
		
		// get request
				
		// DB process
		// TODO: consider a manager because there might be some logical process ?
		// TODO: Add transaction support if required (some apis need).
		// TODO: Use Bucket instead ScssBucket. temporary using.

		ScssBucket bucket = DBServiceHelper.getBucketByName(req.BucketName, req.getUser().getId());
		if (null == bucket)
			return ErrorResponse.NoSuchBucket(req);
		
		ScssObject obj = null;
		InputStream stream = this.hookMD5Stream(req.ContentStream);
		BfsClientResult bfsresult = BfsClientWrapper.getInstance().putFromStream(stream);
		String etag = this.getBase64ContentMD5();
		if (bfsresult.FileNumber > 0) {
			
			// post body is not match the size
			if (bfsresult.Size < size) {
				BfsClientWrapper.getInstance().deleteFile(bfsresult.FileNumber);
				return ErrorResponse.IncompleteBody(req);
			}
			
			// MD5 check failed.
			if (server_check && !etag.equals(contentMD5)){
				BfsClientWrapper.getInstance().deleteFile(bfsresult.FileNumber);
				return ErrorResponse.BadDigest(req);
			}
			
			try{
				// Start transaction
	
				// TODO: consider which is first, insert db or insert file.
				// TODO: do need to delete the old BFS file?
				System.out.printf("BFS file no : %d (size=%d)\n", bfsresult.FileNumber, bfsresult.Size);
				// TODO: db needs to lock the record?
				obj = DBServiceHelper.getObject(req.BucketName, req.ObjectKey);
				if (null != obj) {
					long old_bfs = obj.getBfsFile();
					obj.setBfsFile(bfsresult.FileNumber);
					obj.setEtag(etag);
					DBServiceHelper.modifyObject(obj);
					if (old_bfs > 0)
						BfsClientWrapper.getInstance().deleteFile(obj.getBfsFile());
					
				} else 
//					obj = DBServiceHelper.putObject(req.ObjectKey, bfsresult.FileNumber, 
//						req.getUser().getId(),
					obj = DBServiceHelper.putObject(req.ObjectKey, bfsresult.FileNumber, 
							req.getUser().getId(), bucket.getId(), user_meta, bfsresult.Size, 
							media_type, sys_meta, etag, version_enabled, version, false, expireTime, 
							createTime, modifyTime);
				 
				// Stop transaction
			} catch (SameNameException e) {
				//TODO: update the object.
			} catch (BufferOverflowException e) {
				return ErrorResponse.EntityTooLarge(req);
			}
		}

		// set response headers
		if (null != obj) {
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			// TODO: change the temporary values
			resp_headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp_headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");				
			resp_headers.put(CommonResponseHeader.CONTENT_TYPE, Mimetypes.APPLICATION_XML);
			resp_headers.put(CommonResponseHeader.CONNECTION, "close");
			resp_headers.put(CommonResponseHeader.SERVER, "SohuS4");
			
			// Set API response header
			resp_headers.put(APIResponseHeader.LOCATION, "/" + req.BucketName + req.Path);

			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			resp_headers.put(CommonResponseHeader.DATE, CommonUtilities.formatResponseHeaderDate(bucket.getModifyTime()));
			resp_headers.put(CommonResponseHeader.CONTENT_LENGTH, "0");
			resp_headers.put(CommonResponseHeader.ETAG, etag);
			System.out.printf("Computed ETAG : %s\n", etag );
			
			// generate representation
			resp.Repr = new org.restlet.representation.EmptyRepresentation();
			resp.Repr.setTag(new Tag(etag));
			resp.Repr.setMediaType(MediaType.APPLICATION_ALL_XML);
			resp.MediaType = Mimetypes.APPLICATION_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		return ErrorResponse.InternalError(req);
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.ICallable#CanInvoke(com.scss.core.APIRequest, com.bfsapi.IAccessor)
	 */
	@Override
	public Boolean CanInvoke(APIRequest req, IAccessor invoker) {
		// TODO Auto-generated method stub
		return true;
	}

}

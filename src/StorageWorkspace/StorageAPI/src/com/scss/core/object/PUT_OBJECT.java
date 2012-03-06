/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Tag;

import com.scss.Headers;
import com.scss.IAccessor;
import com.scss.core.APIRequest;
import com.scss.core.APIResponse;
import com.scss.core.ErrorResponse;
import com.scss.core.Mimetypes;
import com.scss.core.bucket.BucketAPIResponse;
import com.scss.db.dao.ScssBucketDaoImpl;
import com.scss.db.dao.ScssObjectDaoImpl;
import com.scss.db.exception.SameNameException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssObject;
import com.scss.utility.CommonUtilities;


/**
 * @author Samuel
 *
 */
public class PUT_OBJECT extends ObjectAPI {

	/* (non-Javadoc)
	 * @see com.scss.ICallable#Invoke(com.scss.core.APIRequest)
	 */
	
	

	public APIResponse Invoke(APIRequest req) {
		// TODO: !!! need to re-organize. extract to pre-invoke post-invoke !!! 
		// TODO: !!! and use reslet metheod to process headers there !!!
		
		Map<String, String> req_headers = req.getHeaders();
		
		// get system meta
		Date createTime = CommonUtilities.parseResponseDatetime(req_headers.get(Headers.DATE));
		Date modifyTime = createTime;
		String media_type = req_headers.get(Headers.CONTENT_TYPE);
		long size = Long.parseLong(req_headers.get(Headers.CONTENT_LENGTH));
		if (size <= 0) {
			return ErrorResponse.MissingContentLength(req);
		}
		String contentMD5 = req_headers.get(Headers.CONTENT_MD5);
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

		if (size > BfsClientWrapper.MAX_SIZE)
			return ErrorResponse.EntityTooLarge(req);
		
		ScssBucket bucket = null;

		bucket = ScssBucketDaoImpl.getInstance().getBucket(req.BucketName);

		if (null == bucket)
			return ErrorResponse.NoSuchBucket(req);
		
		ScssObject obj = null;

		InputStream stream = this.hookMD5Stream(req.ContentStream);
		BfsClientResult bfsresult = BfsClientWrapper.getInstance().putFromStream(stream, (int)size);
		String etag = null;
		
		if (bfsresult.FileNumber > 0) {
			etag = this.getContentMD5();
			
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
			
			logger.info(String.format("BFS file no : %d (size=%d)\n", bfsresult.FileNumber, bfsresult.Size));

			// Start transaction

			// TODO: consider which is first, insert db or insert file.
			// TODO: do need to delete the old BFS file?
			// TODO: db needs to lock the record?
			
			obj = new ScssObject();
			obj.setKey(req.ObjectKey);
			obj.setBfsFile(bfsresult.FileNumber);
			obj.setOwnerId(req.getUser().getId());
			obj.setBucketId(bucket.getId());
			obj.setMeta(user_meta);
			obj.setSize(bfsresult.Size);
			obj.setMediaType(media_type);
			obj.setSysMeta(sys_meta);
			obj.setEtag(etag);
			obj.setVersionEnabled(version_enabled ? (byte) 1 : 0);
			obj.setVersion(version);
			obj.setDeleted((byte) 0);
			obj.setExpirationTime(expireTime);
			obj.setCreateTime(createTime);
			obj.setModifyTime(modifyTime);
			try {
				obj = ScssObjectDaoImpl.getInstance().insertObject(obj);
				
			} catch (SameNameException e) {
				
				logger.debug("Object is already existed. Trying to update it.");
				
				obj = ScssObjectDaoImpl.getInstance().getObjectByKey(req.ObjectKey, req.getUser()).get(0);				
				if (null != obj) {
					long old_bfs = obj.getBfsFile();
					obj.setBfsFile(bfsresult.FileNumber);
					obj.setEtag(etag);
					try {
						ScssObjectDaoImpl.getInstance().updateObject(obj);
						// TODO: delete un-referenced file
						//if (old_bfs > 0)
						//	BfsClientWrapper.getInstance().deleteFile(obj.getBfsFile());
					} catch (SQLException e1) {
						logger.error(e1, e1);
					}
				
				} else {
					logger.error(String.format("Object /%s%s duplicate when inserting. But fail to get.", req.BucketName, req.ObjectKey));
				}
			}
			
			// Stop transaction

		}

		// set response headers
		if (null != obj) {
			APIResponse resp = new BucketAPIResponse();
			Map<String, String> resp_headers = resp.getHeaders();
			
			// set common response header
			setCommResponseHeaders(resp_headers,req);
			
			// Set API response header
			resp_headers.put(Headers.LOCATION, "/" + req.BucketName + req.Path);

			//TODO: set user meta
			// user_meta key-value pair -> header
			
			// TODO: set system meta
			resp_headers.put(Headers.DATE, CommonUtilities.formatResponseHeaderDate(bucket.getModifyTime()));
			resp_headers.put(Headers.CONTENT_LENGTH, "0");
			resp_headers.put(Headers.ETAG, etag);
			logger.debug(String.format("Computed ETAG : %s\n", etag ));

			
			// generate representation
			resp.Repr = new org.restlet.representation.EmptyRepresentation();
			resp.Repr.setTag(new Tag(etag, false));
			resp.Repr.setMediaType(MediaType.TEXT_XML);
			resp.MediaType = Mimetypes.MIMETYPE_XML;
			return resp;
		}

		// TODO: return appropriate error response. DB access should return a value to determine status.
		logger.warn("PUT_OBJECT is returning Interal error due to unexpected result");
		return ErrorResponse.InternalError(req);
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

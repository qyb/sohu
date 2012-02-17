/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server.bucket;

import java.io.File;

import com.bfsapi.IAccessor;
import com.bfsapi.Operation;
import com.bfsapi.OperationResult;
import com.bfsapi.Resource;
import com.bfsapi.db.Bucket;
import com.bfsapi.server.APIResponseHeader;
import com.bfsapi.server.CommonRequestHeader;
import com.bfsapi.server.CommonResponseHeader;
import com.bfsapi.server.Handler;
import org.restlet.representation.StringRepresentation;

/**
 * @author Samuel
 *
 */
public class BucketResource extends Resource {
	protected String bucket;
	
	public BucketResource(String bucket) {
		this.bucket = bucket;
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.IAccessable#CanRead(com.bfsapi.IAccessor)
	 */
	@Override
	public Boolean CanRead(IAccessor who) {
		return false;
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.IOperatable#Operate(com.bfsapi.Operation)
	 */
	@Override
	public OperationResult Operate(Operation op) {
		OperationResult result = null;
		switch(op.Operator) {
			case CREATE:
				result = this.put_bucket(op);
			case READ:
				return this.get_bucket(op);
			
		}
		
		return result;
	}
	
	protected OperationResult put_bucket(Operation op) {
		OperationResult result = new OperationResult();
		BucketResponse resp = new BucketResponse();
		result.Value = BucketManager.getBucketObjects(this.bucket);
		if (null != result.Value) {
			result.Succeed = true;
			resp.Headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp.Headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");				
			resp.Headers.put(CommonResponseHeader.CONTENT_LENGTH, "302");
			resp.Headers.put(CommonRequestHeader.CONTENT_TYPE, "application/xml");
			resp.Headers.put(APIResponseHeader.LOCATION, "/" + this.bucket);
			File f = new File("./fakebfs/get_bucket.xml");
			FileStream fs = new FileStream()
			xml =     
			resp.Repr = new StringRepresentation(bucket, null);
			
		}
		
		return result;
	}
	
	protected OperationResult get_bucket(Operation op) {
		OperationResult result = new OperationResult();
		BucketResponse resp = new BucketResponse();
		result.Value = BucketManager.createBucket(this.bucket);
		if (null != result.Value) {
			result.Succeed = true;
			resp.Headers.put(CommonResponseHeader.X_SOHU_ID_2, "test_id_remember_to_change");
			resp.Headers.put(CommonResponseHeader.X_SOHU_REQUEST_ID, "test_id_remember_to_change");				
			resp.Headers.put(APIResponseHeader.LOCATION, "/" + this.bucket);
			resp.Repr = null;
			result.Value = resp;
		}
		
		return result;
	}	
	
}

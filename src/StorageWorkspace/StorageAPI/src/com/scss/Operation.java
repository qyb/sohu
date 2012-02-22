/**
 * Copyright Sohu Inc. 2012
 */
package com.scss;

import java.net.URI;

import com.scss.core.APIRequest;
import com.scss.core.InvaildRequestException;
import com.scss.core.OpenAPI;
import com.scss.core.bucket.BucketResource;
import com.scss.core.object.ObjectResource;

/**
 * @author Samuel
 *
 */
public class Operation {
	// TODO: make it thread safe.
	
	public IOperatable Target;
	public IAccessor Performer;
	public ICallable Operator;
	
	/*
	 * Output result
	 */
	public OperationResult Result;
	
	/*
	 * Input parameters
	 */
	public APIRequest Request;
	
	
	
	/*
	 * Perform the operation
	 */
	public OperationResult perform() {
		return Target.Operate(this);
	}
	
	// protected constructor
	protected Operation() {	}
	/*
	 * Create a operation by given request.
	 */
	public static Operation create(APIRequest req) throws InvaildRequestException {
		Operation op = new Operation();
		op.Request = req;
		op.Performer = (IAccessor) req.getUser();

		String bucket_name = req.BucketName;
		String object_key = req.ObjectKey;
		
		// parse and generate API
		if (0 == bucket_name.length() && 1 >= object_key.length()) {
			// operation for service
			if (!req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET)) 
				throw new InvaildRequestException("Request 'GET SERVICE' must be with HTTP GET method.");
			// TODO: consider to use individual ServiceResource
			op.Target = new BucketResource(bucket_name) ; 
			op.Operator = OpenAPI.GET_SERVICE;
			
		} else if (1 >= object_key.length()) {
			// operation for bucket
			op.Target = new BucketResource(bucket_name);
			if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET))
				op.Operator = OpenAPI.GET_BUCKET;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.PUT))
				op.Operator = OpenAPI.PUT_BUCKET;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.DELETE))
				op.Operator = OpenAPI.DELETE_BUCKET;
			else
				throw new InvaildRequestException("Invaild HTTP method on bucket.");
			
		} else {
			// operation for object
			op.Target = new ObjectResource(object_key, bucket_name);
			if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET))
				op.Operator = OpenAPI.GET_OBJECT;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.PUT))
				op.Operator = OpenAPI.PUT_OBJECT;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.POST))
				op.Operator = OpenAPI.POST_OBJECT;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.DELETE))
				op.Operator = OpenAPI.DELETE_OBJECT;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.HEAD))
				op.Operator = OpenAPI.HEAD_OBJECT;
			else
				throw new InvaildRequestException("Invaild HTTP method on object.");
		}
		
		return op;
	}
}

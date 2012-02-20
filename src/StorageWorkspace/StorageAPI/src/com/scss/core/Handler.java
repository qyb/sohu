/**
 * Copyright (c) Sohu Inc. 2012
 * 
 * Handle the requests
 */
package com.scss.core;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.regex.*;

import org.restlet.Request;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Product;
import org.restlet.data.Method;

import com.scss.Const;
import com.scss.EnumOperator;
import com.scss.Operation;
import com.scss.OperationResult;
import com.scss.Resource;
import com.scss.core.bucket.BucketResource;
import com.scss.core.object.ObjectResource;
import com.scss.db.User;


/**
 * API request handler. To dispatch requests.
 * 
 * @author Samuel
 *
 */
public class Handler extends ServerResource {

	@Get
	public Representation RequestGET() throws InvaildRequestException{
		return this.RequestPUT();
		
		/*
		Request req = this.getRequest();
		
		// TODO: Assign a monitor to monitor the tracfic, request times and so on.
		// com.bfsapi.utility.Monitor montior = new com.bfsapi.utility.Monitor(req);

		// header process
		Map<String, String> headers = this.getRequestHeaders();
		
		// Authorize
		if (!this.Authorize(headers)){
			System.out.printf("Fail to authorize.\n");
		}
		
		if (Method.HEAD.equals(getMethod())) {
			// HEAD request 
//			Representation rep = new EmptyRepresentation(); 
//			rep.setModificationDate(cmd.getLastModif().getTime()); 
//			rep.setSize(cmd.getSize()); 
//			rep.setMediaType(new MediaType(cmd.getMimeType())); 
//			return rep; 
		} else { 
			// GET request
			
			
		} 
				
		return null;
		*/
	}
	
	
	@Post
	public Representation RequestPOST() throws InvaildRequestException {
		//Request request = this.getRequest();
		APIRequest req = new APIRequest(this.getRequest());
		
		// TODO: Assign a monitor to monitor the tracfic, request times and so on.
		// com.bfsapi.utility.Monitor montior = new com.bfsapi.utility.Monitor(req);
		
		
		// header process
		req.setHeaders(this.getRequestHeaders());
		
		// Authorize
		if (!this.Authorize(req))
			System.out.printf(">> Fail to authorize.\n");
		else
			System.out.printf(">> Request authorized.\n");
			// TODO: process to quit flow.
		
		// Operation
		Operation op = Operation.create(req);
		OperationResult result = op.perform();
		
		if (null != result && result.Succeed) {
			APIResponse resp = (APIResponse)result.Value; 			
			Form resp_headers = (Form)this.getResponse().getAttributes().get("org.restlet.http.headers");
			if (resp_headers == null)  {  
				resp_headers = new Form();  
				getResponse().getAttributes().put("org.restlet.http.headers", resp_headers);  
			} 
			for (String key: resp.getHeaders().keySet()) {
				//TODO: fix the warning
				//2012-2-20 12:05:47 org.restlet.engine.http.header.HeaderUtils addExtensionHeaders
				//警告: Addition of the standard header "Content-Length" is not allowed. Please use the equivalent property in the Restlet API.
				resp_headers.set(key, resp.getHeaders().get(key));
			}
			return resp.Repr;
		}
		
		return null;
		
	}
	
	@Put
	public Representation RequestPUT() throws InvaildRequestException {
		return this.RequestPOST();
	}
	
	@Delete
	public Representation RequestDELETE() {
		return null;
	}
	
	/*
	 * collect required request headers
	 * TODO: Is it necessary? Make it a class ? refactor it.
	 */
	protected Map<String, String> getRequestHeaders(){
		Request req = getRequest();
		Form form_headers = (Form)req.getAttributes().get("org.restlet.http.headers");
		Map<String, String> headers = form_headers.getValuesMap();
		
		System.out.printf("\nMethod : %s\n", req.getMethod().toString());
		System.out.printf("HostRef : %s\n", req.getHostRef().toUri());
		System.out.printf("RootRef : %s\n", req.getRootRef());
		System.out.printf("OriginalRef : %s\n", req.getOriginalRef());
		System.out.printf("ResourceRef : %s\n", req.getResourceRef());
		System.out.printf("Ranges : %s\n", req.getRanges().toString());
		
		for (String key:form_headers.getNames()) {
			System.out.printf("%s : %s\n", key, form_headers.getValues(key).toString());
		}
		System.out.printf("data: %s\n", req.getEntityAsText());
		
		
		
		return headers;
	}
	
	/*
	 * Authorize the request
	 * TODO: convert to class or module
	 */
	protected Boolean Authorize(APIRequest req) {
		// TODO: to invoke Authorization system
		System.out.printf("Access Key ID : %s\n", req.getHeaders().get(CommonRequestHeader.AUTHORIZATION));
		req.setUser(User.EveryOne);
		return true;
	}
	
	
	/*
	 * Parse operation and target resource
	 * TODO: convert to class or module
	 */
	/*
	protected Operation createOperation(APIRequest req) throws InvaildRequestException {
		Operation op = new Operation();

		String path = null;
		URI uri = req.URI;

		String bucket_name = uri.getHost();
		if (null != bucket_name) {
			bucket_name = bucket_name.replace("." + Const.HOST, "");
		}
		
		if (null == bucket_name || 0 == bucket_name.trim().length()) {
			String[] pathes = path.split("/", 3);
			if (3 == pathes.length) {
				bucket_name = pathes[1];
				path = "/" + pathes[2];
			} else if (2 == pathes.length){
				bucket_name = pathes[1];
				path = "/";
			} else {
				throw new InvaildRequestException("Invaild reqeust uri.");
			}
		}
		
		bucket_name = bucket_name.trim();
		String object_key = path.trim();
		
		op.Performer = req.getUser();
		if (0 == bucket_name.length() && 1 >= object_key.length()) {
			if (!req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET)) 
				throw new InvaildRequestException("GET SERVICE must use HTTP GET method.");
			op.Target = null; // GET Service
			op.Operator = EnumOperator.READ;
			
		} else if (1 >= object_key.length()) {
			op.Target = new BucketResource(bucket_name);
			if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET))
				op.Operator = EnumOperator.READ;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.PUT))
				op.Operator = EnumOperator.CREATE;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.DELETE))
				op.Operator = EnumOperator.DELETE;
			else
				throw new InvaildRequestException("Invaild HTTP method on bucket.");
			
		} else {
			op.Target = new ObjectResource(object_key, bucket_name);
			if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.GET))
				op.Operator = EnumOperator.READ;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.PUT))
				op.Operator = EnumOperator.CREATE;
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.POST))
				op.Operator = EnumOperator.CREATE; // TODO: POST PUT all point to CREATE 
			else if (req.Method.equalsIgnoreCase(Const.REQUEST_METHOD.DELETE))
				op.Operator = EnumOperator.DELETE;
			else
				throw new InvaildRequestException("Invaild HTTP method on object.");
		}
		
		return op;
	}
	*/
}

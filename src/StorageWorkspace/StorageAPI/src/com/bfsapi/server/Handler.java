/**
 * Copyright (c) Sohu Inc. 2012
 * 
 * Handle the requests
 */
package com.bfsapi.server;

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

import com.bfsapi.Const;
import com.bfsapi.EnumOperator;
import com.bfsapi.Operation;
import com.bfsapi.OperationResult;
import com.bfsapi.Resource;
import com.bfsapi.db.User;
import com.bfsapi.server.bucket.BucketResource;
import com.bfsapi.server.object.ObjectResource;


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
		Request req = this.getRequest();
		
		// TODO: Assign a monitor to monitor the tracfic, request times and so on.
		// com.bfsapi.utility.Monitor montior = new com.bfsapi.utility.Monitor(req);
		
		// header process
		Map<String, String> headers = this.getRequestHeaders();
		
		// Authorize
		User user = this.Authorize(headers);
		if (null == user)
			System.out.printf(">> Fail to authorize.\n");
		else
			System.out.printf(">> Request authorized.\n");
		
		// Operation
		Operation op = this.getOperation(this.getMethod(), user);
		
		OperationResult result = op.perform();
		if (result.Succeed) {
			APIResponse resp = (APIResponse)result.Value; 			
			Form resp_headers = (Form)this.getResponse().getAttributes().get("org.restlet.http.headers");
			if (resp_headers == null)  {  
				resp_headers = new Form();  
				getResponse().getAttributes().put("org.restlet.http.headers", resp_headers);  
			}  
			for (String key: resp.Headers.keySet()) {
				resp_headers.set(key, resp.Headers.get(key));
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
	protected User Authorize(Map<String, String> headers) {
		System.out.printf("Access Key ID : %s\n", headers.get(CommonRequestHeader.AUTHORIZATION));
		return User.EveryOne;
	}
	
	/*
	 * Compiled Regular Expression
	 */
	protected static class Regex {
		public static final Pattern SplitUri = Pattern.compile("http?://(\\w*\\.)" + Const.HOST);
		
	}
	
	/*
	 * Parse operation and target resource
	 * TODO: convert to class or module
	 */
	protected Operation getOperation(Method method, User user) throws InvaildRequestException {
		Request req = this.getRequest();
		Operation op = new Operation();
		
		String path = req.getOriginalRef().getPath();
		URI uri = req.getOriginalRef().toUri();
		if (!uri.isAbsolute()) {
			//TODO: try get absolute uri
		}
		System.out.printf("uri : \n", uri);

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
		
		op.Performer = user;
		if (0 == bucket_name.length() && 1 >= object_key.length()) {
			if (!Method.GET.equals(method)) 
				throw new InvaildRequestException("GET SERVICE must use HTTP GET method.");
			op.Target = null; // GET Service
			op.Operator = EnumOperator.READ;
			
		} else if (1 >= object_key.length()) {
			op.Target = new BucketResource(bucket_name);
			if (Method.GET.equals(method))
				op.Operator = EnumOperator.READ;
			else if (Method.PUT.equals(method))
				op.Operator = EnumOperator.CREATE;
			else if (Method.DELETE.equals(method))
				op.Operator = EnumOperator.DELETE;
			else
				throw new InvaildRequestException("Invaild HTTP method on bucket.");
			
		} else {
			op.Target = new ObjectResource(object_key, bucket_name);
			if (Method.GET.equals(method))
				op.Operator = EnumOperator.READ;
			else if (Method.PUT.equals(method))
				op.Operator = EnumOperator.CREATE;
			else if (Method.POST.equals(method))
				op.Operator = EnumOperator.CREATE;
			else if (Method.DELETE.equals(method))
				op.Operator = EnumOperator.DELETE;
			else
				throw new InvaildRequestException("Invaild HTTP method on object.");
		}
		
		return op;
	}
}

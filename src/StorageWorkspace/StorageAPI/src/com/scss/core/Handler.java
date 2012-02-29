/**
 * Copyright (c) Sohu Inc. 2012
 * 
 * Handle the requests
 */
package com.scss.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.scss.Operation;
import com.scss.OperationResult;
import com.scss.db.User;


/**
 * API request handler. To dispatch requests.
 * 
 * @author Samuel
 *
 */
public class Handler extends ServerResource {
	
	public Handler() {
		super();
		// TODO: move per resource in the future when use real restlet resource
		this.Init();
	}

	@Get
	public Representation RequestGET() throws InvaildRequestException{
		return this.Process();
		
		/*
		Request req = this.getRequest();
		
		// TODO: Assign a monitor to monitor the tracfic, request times and so on.
		// com.scss.utility.Monitor montior = new com.scss.utility.Monitor(req);

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
	public Representation RequestPOST(Representation entity) throws InvaildRequestException{
		return this.Process();
	}
	
	@Put
	public Representation RequestPUT(Representation entity) throws InvaildRequestException {
		this.getVariants().add(new VariantInfo(MediaType.APPLICATION_OCTET_STREAM));
		return this.Process();
		
	}
	
	@Delete
	public Representation RequestDELETE() throws InvaildRequestException {
		return this.Process();
	}
	
	
	
	/*
	 * TODO: override handle() later.
	 */
	protected Representation Process() throws InvaildRequestException {
		
		APIRequest req = new APIRequest(this.getRequest());
		
		// TODO: Assign a monitor to monitor the tracfic, request times and so on.
		// com.scss.utility.Monitor montior = new com.scss.utility.Monitor(req);
		
		
		// header process
		req.setHeaders(this.getRequestHeaders());
		
		// Authorize
		if (!this.Authorize(req))
			System.out.printf(">> Fail to authorize.\n");
		else
			System.out.printf(">> Request authorized.\n");
			// TODO: process to quit flow.
		
		// Operation
		Operation op =  Operation.create(req);
		OperationResult result = op.perform();
		
		if (null != result) {
			APIResponse resp = (APIResponse)result.Value;
			Form resp_headers = (Form)this.getResponse().getAttributes().get("org.restlet.http.headers");
			if (result.Succeed) {
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
			} else {
				ErrorResponse err_resp = (ErrorResponse)resp;
				this.getResponse().setStatus(new Status(err_resp.getHttp_status()));
			}
			return resp.Repr;
		} 
		
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
		String auth_str = req.getHeaders().get(CommonRequestHeader.AUTHORIZATION);
		//String[] keys = auth_str.split(":");
		//System.out.printf("Access Key ID : %s - %s\n", keys[0], keys[1]);
		//User user = new User(DBServiceHelper.getUserByAccessKey(keys[0]));
		req.setUser(User.EveryOne);
		return true;
	}
	
	/*
	 * Add supported methods and media types 
	 * TODO : Promote per resource.
	 */
	protected void Init () {
		Set<Method> allowedMethods = new HashSet<Method>();
		allowedMethods.add(Method.GET);
		allowedMethods.add(Method.PUT);
		allowedMethods.add(Method.POST);
		allowedMethods.add(Method.DELETE);
		allowedMethods.add(Method.HEAD);
		this.setAllowedMethods(new CopyOnWriteArraySet<Method>(allowedMethods));
		
	}
	
	
}

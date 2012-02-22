/**
 * 
 */
package com.scss.server.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.routing.Filter;

import com.scss.server.AccessBean;

/**
 * @author Leon
 *
 */
public class AccessFilter extends Filter {
    private AccessBean access;
    
	public AccessFilter(AccessBean __access) {
        super();
        access = __access;
	}
    
	@Override
	protected int beforeHandle(Request request, Response response) {
        int rtn = STOP;
		
        if (access.judgeAccess(request.getClientInfo().getAddress())) {
        	rtn = CONTINUE;
        }
        
        response.setEntity("IP forbidden", MediaType.TEXT_PLAIN);
        
		return rtn;
	}

}





/**
 * 
 */
package com.scss.server.apps;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;

import com.scss.server.resources.TestResource;

/**
 * @author Leon
 *
 */
public class TestApplication extends Application {
    

    public TestApplication(Context context__) { }
    
    public synchronized Restlet createRoot() {
    	Router router = new Router(getContext());
//        router.attachDefault(TestResource.class);
        router.attach("/zhou", TestResource.class);
        
        MapVerifier verifier = new MapVerifier();
        verifier.getLocalSecrets().put("leon", "leon1".toCharArray());
        
        ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_AWS_S3, "test.com");
        au.setVerifier(verifier);
        au.setNext(router);
        
        return au;
    }
    
    
}

package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.sohu.xml.model.Resource;
  
public class PullResourceParser implements ResourceParser {  
      
    @Override  
    public List<Resource> parse(InputStream is) throws Exception {  
        List<Resource> resources = null;  
        Resource resource = null;  
          
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");                
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	resources = new ArrayList<Resource>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("package")) { 
                	
                } else if (parser.getName().equals("image")) {  
                	resource = new Resource();
                    resource.setType("image");
                    resource.setKey(parser.getAttributeValue(null, "key"));
                    resources.add(resource);
                    resource = null;  
                } 
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("package")) { 
                	    
                }  
                break;  
            }  
            eventType = parser.next();  
        }  
        return resources;  
    }  
}  

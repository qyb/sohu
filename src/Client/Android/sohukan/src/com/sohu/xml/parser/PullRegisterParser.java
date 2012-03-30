package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.sohu.xml.model.RegisterXML;
  
public class PullRegisterParser implements RegisterParser {  
      
    @Override  
    public List<RegisterXML> parse(InputStream is) throws Exception {
    	List<RegisterXML> Registers = null;  
        RegisterXML Register = null;  
          
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");                
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:
            	Registers = new ArrayList<RegisterXML>();
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("result")) { 
                	Register = new RegisterXML();
                } else if (parser.getName().equals("uid")) {  
                	eventType = parser.next(); 
                    Register.setUid(parser.getText());
                } else if (parser.getName().equals("status")) {
                	eventType = parser.next(); 
                    Register.setStatus(Integer.parseInt(parser.getText()));
                } else if (parser.getName().equals("token")) {  
                	eventType = parser.next(); 
                    Register.setToken(parser.getText());
                } else if (parser.getName().equals("uniqname")) {  
                	eventType = parser.next(); 
                    Register.setUniqname(parser.getText());
                } 
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("result")) { 
                	Registers.add(Register);  
                	Register = null;
                }  
                break;  
            }  
            eventType = parser.next();  
        }  
        return Registers;  
    }  
}  

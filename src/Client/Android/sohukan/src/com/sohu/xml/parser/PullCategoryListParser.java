package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.sohu.xml.model.CategoryList;
  
public class PullCategoryListParser implements CategoryListParser {  
      
    @Override  
    public List<CategoryList> parse(InputStream is) throws Exception {  
        List<CategoryList> CategoryList = null;  
        CategoryList category = null;  
          
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlPullParser parser = factory.newPullParser();  
          
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");                
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	CategoryList = new ArrayList<CategoryList>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("name")) { 
                	category = new CategoryList();
                    eventType = parser.next();  
                    category.setFolder(parser.getText());
                    CategoryList.add(category);
                } 
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("package")) {  
                	category = null;      
                }  
                break;  
            }  
            eventType = parser.next();  
        }  
        return CategoryList;  
    }  
}  

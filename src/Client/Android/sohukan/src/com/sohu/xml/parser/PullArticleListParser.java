package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.sohu.xml.model.ArticleList;
  
public class PullArticleListParser implements ArticleListParser {  
      
    @Override  
    public List<ArticleList> parse(InputStream is) throws Exception {  
        List<ArticleList> articleList = null;  
        ArticleList articles = null;  
          
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlPullParser parser = factory.newPullParser();  
          
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");                
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	articleList = new ArrayList<ArticleList>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("article")) {  
                	articles = new ArticleList();
                	articles.setKey(parser.getAttributeValue(null, "key"));
                } else if (parser.getName().equals("title")) {  
                    eventType = parser.next();  
                    articles.setTitle(parser.getText());
                } else if (parser.getName().equals("url")) {  
                    eventType = parser.next();  
                    articles.setUrl(parser.getText());
                } else if (parser.getName().equals("download_url")) {  
                    eventType = parser.next();  
                    articles.setDownloadUrl(parser.getText());
                    //articleList.setPrice(Float.parseFloat(parser.getText()));  
                }  else if (parser.getName().equals("image_urls")) {  
                    eventType = parser.next();  
                    try {
						articles.setImageUrls(parser.getText());
						System.out.println(parser.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						articles.setImageUrls("0");
						//e.printStackTrace();
					}
                }   else if (parser.getName().equals("is_read")) {  
                    eventType = parser.next();  
                    articles.setIsRead(parser.getText());
                }   else if (parser.getName().equals("cover")) {  
                    eventType = parser.next();  
                    try {
                    	articles.setCover(parser.getText());
						System.out.println(parser.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						articles.setCover("0");
						//e.printStackTrace();
					}
                }   else if (parser.getName().equals("is_star")) {  
                    eventType = parser.next();  
                    articles.setIsStar(Integer.parseInt(parser.getText()));
                }   else if (parser.getName().equals("create_time")) {  
                    eventType = parser.next();  
                    articles.setCreateTime(parser.getText());
                }   
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("article")) {  
                	articleList.add(articles);  
                	articles = null;      
                }  
                break;  
            }  
            eventType = parser.next();  
        }  
        return articleList;  
    }  
}  

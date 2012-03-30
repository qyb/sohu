package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Xml;

import com.sohu.xml.model.Bookmark;
  
public class PullBookmarkParser implements BookmarkParser {  
    
	private String last_sync_record="";
	
    @Override  
    public List<Bookmark> parse(InputStream is, SharedPreferences preferences) throws Exception {  
        List<Bookmark> Bookmarks = null;  
        Bookmark Bookmark = null;  
          
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");                
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	Bookmarks = new ArrayList<Bookmark>();  
                break;  
            case XmlPullParser.START_TAG: 
                if (parser.getName().equals("package")) { 
                	try {
                		parser.getAttributeValue(null, "last_sync_record");
                		last_sync_record = parser.getAttributeValue(null, "last_sync_record");
                		System.out.println("last_sync_record数:"+last_sync_record);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println(e);
						e.printStackTrace();
					}
                } else if (parser.getName().equals("bookmark")) {
                	Bookmark = new Bookmark();
                	Bookmark.setId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                } else if (parser.getName().equals("title")) {
                	eventType = parser.next();
                	Bookmark.setTitle(parser.getText());
                } else if (parser.getName().equals("url")) {  
                	eventType = parser.next();
                	Bookmark.setUrl(parser.getText());
                } else if (parser.getName().equals("title")) {  
                    eventType = parser.next();  
                    Bookmark.setTitle(parser.getText());
                } else if (parser.getName().equals("description")) {  
                    eventType = parser.next();
                    try {
                    	Bookmark.setDescription(parser.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Bookmark.setDescription("0");
						e.printStackTrace();
					}
                } else if (parser.getName().equals("is_star")) {  
                    eventType = parser.next();  
                    Bookmark.setIsStar(Integer.parseInt(parser.getText()));
                } else if (parser.getName().equals("create_time")) {  
                    eventType = parser.next();  
                	Bookmark.setCreateTime(parser.getText());
                } else if (parser.getName().equals("read_time")) {  
                    eventType = parser.next();  
                    try {
                    	if(parser.getText()=="9999999999"){
                    		Bookmark.setReadTime(null);
                    	}else{
                    		Bookmark.setReadTime(parser.getText());
                    	}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Bookmark.setReadTime(null);
						e.printStackTrace();
					}
                } else if (parser.getName().equals("folder_name")) {  
                    eventType = parser.next();  
                    try {
                    	Bookmark.setFolderName(parser.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Bookmark.setFolderName("0");
						e.printStackTrace();
					}
                } else if (parser.getName().equals("read_progress")) {  
                    eventType = parser.next();  
                    Bookmark.setReadProgress(parser.getText());
                } else if (parser.getName().equals("version")) {  
                    eventType = parser.next();  
                    Bookmark.setVersion(Integer.parseInt(parser.getText()));
                } else if (parser.getName().equals("text_version")) {  
                    eventType = parser.next();  
                    Bookmark.setTextVersion(Integer.parseInt(parser.getText()));
                } else if (parser.getName().equals("is_ready")) {  
                    eventType = parser.next();  
                    Bookmark.setIsReady(Integer.parseInt(parser.getText()));
                }   
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("bookmark")) {  
                	Bookmarks.add(Bookmark);  
                	Bookmark = null;      
                }  
                break;  
            }  
            eventType = parser.next();  
        }
        if(!"".equals(last_sync_record)){
	        //存储到preference
			Editor editor=preferences.edit();  
		    //更改配置数据  
		    editor.putInt("last_sync_record", Integer.parseInt(last_sync_record));  
		    //提交存储  
		    editor.commit();
        }
        return Bookmarks;  
    }  
}  

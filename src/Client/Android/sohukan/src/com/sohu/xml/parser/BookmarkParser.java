package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.List;

import android.content.SharedPreferences;

import com.sohu.xml.model.Bookmark;
  
public interface BookmarkParser {  
    /** 
     * 文章列表接口
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<Bookmark> parse(InputStream is, SharedPreferences preferences) throws Exception;  
}  

package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.List;

import android.content.SharedPreferences;

import com.sohu.xml.model.RegisterXML;
  
public interface RegisterParser {  
    /** 
     * 文章列表接口
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<RegisterXML> parse(InputStream is) throws Exception;  
}  

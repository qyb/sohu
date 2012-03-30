package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.List;

import com.sohu.xml.model.Resource;
  
public interface ResourceParser {  
    /** 
     * 文章列表接口
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<Resource> parse(InputStream is) throws Exception;  
}  

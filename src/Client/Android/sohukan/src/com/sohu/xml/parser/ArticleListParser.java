package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.List;

import com.sohu.xml.model.ArticleList;
  
public interface ArticleListParser {  
    /** 
     * 文章列表接口
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<ArticleList> parse(InputStream is) throws Exception;  
}  

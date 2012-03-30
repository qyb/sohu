package com.sohu.xml.parser;

import java.io.InputStream;
import java.util.List;

import com.sohu.xml.model.CategoryList;
  
public interface CategoryListParser {  
    /** 
     * 文章列表接口
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<CategoryList> parse(InputStream is) throws Exception;  
}  

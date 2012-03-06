package com.sohu.xml.parser;

import java.io.InputStream;  
import java.util.List;  
  
import com.sohu.xml.model.Book;  
  
public interface BookParser {  
    /** 
     * ���������� �õ�Book���󼯺� 
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<Book> parse(InputStream is) throws Exception;  
      
    /** 
     * ���л�Book���󼯺� �õ�XML��ʽ���ַ��� 
     * @param books 
     * @return 
     * @throws Exception 
     */  
    public String serialize(List<Book> books) throws Exception;  
}  

package com.sohu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
  
public class HttpDownloader {   
    private FileUtils fileUtils;  
    
    public HttpDownloader(){
    	fileUtils = new FileUtils();  
    }
    
    public HttpDownloader(String type){  
    	if(!"sd".equals(type.toLowerCase())){
    		fileUtils = new FileUtils();  
    	}else{
	        fileUtils = new FileUtils("sd");  
    	}
    }
    
    public String download(String urlStr){  
  
        URL url = null;  
        StringBuffer sb = new StringBuffer();  
        String line = null;  
        BufferedReader buffer = null;  
          
        try {  
            url = new URL(urlStr);  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
            buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));  
              
            while((line=buffer.readLine())!=null){  
                sb.append(line);  
            }  
              
        }catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                buffer.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return sb.toString();  
    }  
    //通过url下载文件
    public int downFile(String urlStr,String path,String fileName){  
        InputStream inputStream = null;  
        if(fileUtils.isFileExist(fileName)){  
            return 1;  
        }else{  
            try {  
                inputStream = getInputSteamFromUrl(urlStr);  
            } catch (IOException e) {  
                e.printStackTrace();  
                return -1;  
            }  
            File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
            if(resultFile == null){  
                return -1;  
            }  
        }  
        try {  
            inputStream.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return 0;  
    }  
  
    private InputStream getInputSteamFromUrl(String urlStr) throws MalformedURLException, IOException {  
        URL url = null;  
        url = new URL(urlStr);  
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
//        urlConn.setRequestProperty("Charset", "UTF-8");  
//        urlConn.connect();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
//        String line ;
//        while((line = reader.readLine())!=null){
//        	System.out.println(line);
//        }
        InputStream inputStream = urlConn.getInputStream();  
        return inputStream;  
    }  
}  

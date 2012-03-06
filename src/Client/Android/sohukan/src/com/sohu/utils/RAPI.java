package com.sohu.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sohu.database.DBHelper;
import com.sohu.wuhan.Constant;
import com.sohu.wuhan.HttpRead;
import com.sohu.wuhan.IReadable;
import com.sohu.xml.model.ArticleList;
import com.sohu.xml.parser.ArticleListParser;
import com.sohu.xml.parser.PullArticleListParser;


public class RAPI {

	private boolean wifi;
	
	private IReadable ir;
	String token = "649cfef6a94ee38f0c82a26dc8ad341292c7510e";
	
	
	private DBHelper db;
	
	ArrayList<ArticleList> articleList;
	
	
	public RAPI(Context context) {  
		this.wifi = checkNetworkConnection(context) ;
		db = new DBHelper(context);
	}
	
	//数据同步
    public ArrayList<ArticleList> dataSync(boolean img_status){
    	if(wifi){
    		final boolean img_flag = img_status;
    		articleList = new ArrayList<ArticleList>();
        	//wifi情况
        	System.out.println("正在下载");
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return articleList;
    		}else{
    			ir.asyncProbeArticle(new Handler(){
    				public void  handleMessage(Message msg) {
    					
    					Bundle data = msg.getData();
    					Constant.Error error = (Constant.Error)data.getSerializable("error");
    					if (error != Constant.Error.OK) {
    						System.out.println("-------------报错---------------");
    						System.out.println(error);
    						System.out.println("-------------报错结束---------------");
    						
    						//hint to users.
    					} else {
    						String rtns = data.getString("result");
    						System.out.println("-------------取list数据---------------");
    						if (null != rtns){
    							ByteArrayInputStream is;
    							try {  
    								is = new ByteArrayInputStream(rtns.getBytes("utf-8"));
    								ArticleListParser parser; 
    			                    parser = new PullArticleListParser(); 
    			                    
    			                    List<ArticleList> list = parser.parse(is);
    			                    if(list.size()>0)
    			                    db.truncateArticle();
    			                    for(int i=0;i<list.size();i++){
    			                    	//根据download_url下html和image_url下图片
    			                    	Thread download = new downloadHtmlAndImages(list.get(i).getKey(),list.get(i).getDownloadUrl(),list.get(i).getImageUrls(),img_flag);
    			                    	download.start();
    			                        //用户传递到下个activity的arraylist
    			                    	db.insertArticle(list.get(i));
    			                    	articleList.add(list.get(i));
    			                    }
    			                    for(int b=0;b<articleList.size();b++){
			                        	System.out.println("文章标题:"+articleList.get(b).getTitle());
			                        }
    			                } catch (UnsupportedEncodingException e1) {
    								// TODO Auto-generated catch block
    								e1.printStackTrace();
    								System.out.println(e1+"UnsupportedEncodingException e1有这样的错1111111111111111111111111");
    							} catch (Exception e) {  
    			                	System.out.println(e+"Exception有这样的错1111111111111111111111111");
    			                } 
    						}
//    						db.close();
    						System.out.println("-------------取数据结束---------------");
    					}
    				}
    			});
    			return articleList;
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	return articleList;
        }
    	
    }
    
    class downloadHtmlAndImages extends Thread
    {        
    	private String key;
    	private String download_url;
    	private String[] image_url;
    	private boolean img_flag;

    	public downloadHtmlAndImages(String key, String download_url, String image_urls, boolean img_flag) {
    		this.key = key;
    		this.download_url = download_url;
    		image_url = image_urls.trim().split("\\|");
    		this.img_flag = img_flag;
    	}
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            synchronized(this){
//	            System.out.println("机身内存路径:"+getFilesDir());
	            HttpDownloader httpDownloader = new HttpDownloader();
	            httpDownloader.downFile(download_url, "/"+token+"/", key+".html");
	            
	//          String lrc = httpDownloader.download("http://192.168.1.101:8080/20111021/a.lrc");  
	            if(img_flag){
		            for(int m=0;m<image_url.length;m++){
		            	if(!"0".equals(image_url[m])){
		            		httpDownloader.downFile(image_url[m], "/"+token+"/", key+"_"+m);
		            	}
		            }
	            }
            }
        }
        
    }    
    
    public void deleteArticle(String key){
    	if(wifi){
    		ir = HttpRead.instance();
    		if (!ir.init(token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return;
    		}else{
    			ir.deleteArticle(key);
    		}
    	}
    }
    
    public void updateArticle(String key, Hashtable ht){
    	if(wifi){
    		ir = HttpRead.instance();
    		if (!ir.init(token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return;
    		}else{
    			ir.updateArticle(key, ht);
    		}
    	}
    }
	
  //判断是否连接wifi
    public static boolean checkNetworkConnection(Context context)   
    {   
    	WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
    	int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
    	if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
    		System.out.println("**** WIFI is on");
    		return true;
    	} else {
    		System.out.println("**** WIFI is off");
    	   	return false;   
    	}
   
    }
	
}

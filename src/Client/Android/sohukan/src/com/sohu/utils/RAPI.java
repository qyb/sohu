package com.sohu.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sohu.database.DBHelper;
import com.sohu.kan.Global;
import com.sohu.wuhan.Constant;
import com.sohu.wuhan.HttpRead;
import com.sohu.wuhan.IReadable;
import com.sohu.xml.model.Bookmark;
import com.sohu.xml.model.CategoryList;
import com.sohu.xml.model.Resource;
import com.sohu.xml.parser.BookmarkParser;
import com.sohu.xml.parser.CategoryListParser;
import com.sohu.xml.parser.PullBookmarkParser;
import com.sohu.xml.parser.PullCategoryListParser;
import com.sohu.xml.parser.PullResourceParser;
import com.sohu.xml.parser.ResourceParser;


public class RAPI {

	private boolean wifi;
	
	private IReadable ir;
	private String access_token;
	private String userid;
	
	
	private DBHelper db;
	
	private Context context;
	
	ArrayList<Bookmark> bookmarks;
	
	private ProgressDialog dialog;
	
	public RAPI(Context context, String access_token, String userid) {  
		this.wifi = checkNetworkConnection(context) ;
		this.context = context;
		this.access_token = access_token;
		this.userid = userid;
	}
	
	//数据同步
    public void dataSync(SharedPreferences sharePreferences, TextView count_unread, TextView count_read, TextView count_category, boolean saveToSDFlag){
    	if(wifi){
    		bookmarks = new ArrayList<Bookmark>();
        	//wifi情况
        	final SharedPreferences preferences = sharePreferences;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    		}else{
    			System.out.println("这是access_token:"+access_token);
    			
    	        int last_sync_record = preferences.getInt("last_sync_record", 0);
    	        SyncHandler syncHandler = new SyncHandler(preferences, count_unread, count_read); 
    			ir.asyncSyncArticle(last_sync_record, null, syncHandler);
    			//清空s_category表  listFolder方法取所有category插入到s_category表
    			SyncFolderHandler syncFolderHandler = new SyncFolderHandler(count_category);
    			
    			ir.asyncListFolder(syncFolderHandler);
    			//同步离线操作
    			db = new DBHelper(context);
        		Cursor cur = db.loadAllOperation(userid);
        		if(cur.getCount()!=0){
	        		if(cur.moveToFirst()){
	        			do{
	        				if("asyncDeleteArticle".equals(cur.getString(2))){
	        					asyncDeleteArticle(Integer.parseInt(cur.getString(0)), saveToSDFlag);
	        				}else if("updateBookmark".equals(cur.getString(2))){
	        					Hashtable ht = new Hashtable();
	        					ht.put("title", cur.getString(4).split("\\|")[0]);
	        					ht.put("category_name", cur.getString(4).split("\\|")[1]);
	        					updateBookmark(Integer.parseInt(cur.getString(0)),ht);
	        				}else if("asyncCreateFolder".equals(cur.getString(2))){
	        					asyncCreateFolder(cur.getString(4));
	        				}else if("asyncUpdateFolder".equals(cur.getString(2))){
	        					asyncUpdateFolder(cur.getString(1),cur.getString(4));
	        				}else if("asyncDeleteFolder".equals(cur.getString(2))){
	        					asyncDeleteFolder(cur.getString(1));
	        				}else if("asyncUpdateReadProgress".equals(cur.getString(2))){
	        					Bookmark bookmark = new Bookmark();
	        					bookmark.setId(Integer.parseInt(cur.getString(4).split("\\|")[0]));
	        					bookmark.setReadProgress(cur.getString(4).split("\\|")[1]);
	        					asyncUpdateReadProgress(bookmark);
	        				}else if("asyncDeleteArticleReadTime".equals(cur.getString(2))){
	        					Bookmark bookmark = new Bookmark();
	        					bookmark.setId(Integer.parseInt(cur.getString(4).split("\\|")[0]));
	        					bookmark.setReadProgress(cur.getString(4).split("\\|")[1]);
	        					asyncDeleteArticleReadTime(bookmark);
	        				}
	        			}
	        			while(cur.moveToNext());
	        		}
        		}
        		cur.close();
        		db.close();
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        }
    }
    
    class SyncFolderHandler extends Handler {
    	
    	private TextView count_category;
    	
    	public SyncFolderHandler(TextView count_category) {
			this.count_category = count_category;
        }
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
				System.out.println(rtns);
				//解析xml-package
    			if (null != rtns){
    				ByteArrayInputStream is;
    				try{
    					is = new ByteArrayInputStream(rtns.getBytes("utf-8"));
    					CategoryListParser parser; 
    	                parser = new PullCategoryListParser(); 
    	                List<CategoryList> list = parser.parse(is);
    	                db = new DBHelper(context);
    	                if(list.size()>0)
    		                db.truncateCategory(userid);
    	                for(int i=0;i<list.size();i++){
    	    				db.addCategory(list.get(i).getFolder(),userid);
    	                }
    	                count_category.setText("("+list.size()+")");
    	                db.close();
    				} catch (UnsupportedEncodingException e1) {    
    					e1.printStackTrace();
    					System.out.println(e1);
    				} catch (Exception e) {
    					System.out.println(e);
    					e.printStackTrace();
    				}
    			}
			}
		}
    }
    
    class SyncHandler extends Handler {
    	
    	private SharedPreferences preferences;
    	private TextView count_unread;
    	private TextView count_read;
        public SyncHandler(SharedPreferences preferences, TextView count_unread, TextView count_read) {
			this.preferences = preferences;
			this.count_unread = count_unread;
			this.count_read = count_read;
        }
        
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
				System.out.println(rtns);
				System.out.println("-------------取list数据---------------");
				if (null != rtns){
					ByteArrayInputStream is;
					ByteArrayInputStream resource_is;
					try {
						is = new ByteArrayInputStream(rtns.getBytes("utf-8"));
						BookmarkParser parser; 
	                    parser = new PullBookmarkParser(); 
	                    List<Bookmark> list = parser.parse(is,preferences);
	                    if(list.size()>0){
	                    	db = new DBHelper(context);
		                    for(int i=0;i<list.size();i++){
		        				//用bookmarkid跟本地数据库比对,先select 先比对version(更新本条记录) 在比对text-version(下载)
		        				db.syncBookmark(list.get(i), userid);
		        				//通过bookmarkid先get-resource得到key
		        				String resource = ir.getResource(list.get(i).getId()+"");
		        				if(resource!=null){
			        				resource_is = new ByteArrayInputStream(resource.getBytes("utf-8"));
			        				ResourceParser resourceParser; 
			        				resourceParser = new PullResourceParser(); 
			        				List<Resource> resourceList = resourceParser.parse(resource_is);
			        				for(int j=0;j<resourceList.size();j++){
			        					resourceList.get(j).setBookmarkId(list.get(i).getId()+"");
			        					db.syncResource(resourceList.get(j),userid);
			        				}
		        				}
		                    }
		                    db.close();
		        			ir.asyncSyncArticle(preferences.getInt("last_sync_record", 0), null, this);
	                    }else{
	                    	DBHelper db = new DBHelper(context);
	                    	Cursor curUnread = db.loadAllBookmark("0", userid);
	                    	Cursor curRead = db.loadAllBookmark("1", userid);
	                    	count_unread.setText("("+curUnread.getCount()+")");
	                    	count_read.setText("("+curRead.getCount()+")");
	                    	curUnread.close();
	                    	curRead.close();
	                    	db.close();
	                    }
	                } catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println(e1+"UnsupportedEncodingException e1有这样的错1111111111111111111111111");
					} catch (Exception e) {  
						System.out.println(e+"方法sync");
						e.printStackTrace();
					} 
				}
				
				System.out.println("-------------取数据结束---------------");
			}
		}
    }
    
    public void downloadResource(boolean img_status, List<Bookmark> bookmarkList, String uid, ListView listView){
    	if(wifi){
        	//wifi情况
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    		}else{
    			for(int i=0;i<bookmarkList.size();i++){
					Log.d("", "a:" + new Date().getTime());
					if(bookmarkList.get(i).getIsDownload()==0){
						MyHandler myHandler = new MyHandler(i, uid, listView, bookmarkList);
						ir.asyncGetText(bookmarkList.get(i).getId()+"", myHandler);
					}
    				Log.d("", "b:" + new Date().getTime());
    				List<Resource> resourceList = new ArrayList<Resource>();
    				//下载图片
    				DBHelper db = new DBHelper(context);
    				Cursor cur = db.getResourceById(bookmarkList.get(i).getId()+"");
    				
					if(cur.moveToFirst()){
						Resource resource = new Resource();
						do{
							resource.setBookmarkId(cur.getString(0));
							resource.setType(cur.getString(1));
							resource.setKey(cur.getString(2));
							resource.setIsDownload(cur.getInt(3));
							resourceList.add(resource);
							resource =  new Resource();
						}
						while(cur.moveToNext());
					}
					cur.close();
					db.close();
					if(img_status){
						for(int m=0;m<resourceList.size();m++){
							if(resourceList.get(m).getIsDownload()==0){
								//根据download_url下html和image_url下图片
								Thread download = new downloadResource(uid,resourceList.get(m).getKey()+".png");
								download.start();
							}
						}
					}
    			}
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        }
    }
    
    class MyHandler extends Handler {
		private int pos;
		private String uid;
		private ListView listView;
		private List<Bookmark> bookmarkList;
        public MyHandler(int pos, String uid, ListView listView, List<Bookmark> bookmarkList) {
			this.pos = pos;
			this.uid = uid;
			this.listView = listView;
			this.bookmarkList = bookmarkList;
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Bundle data = msg.getData();
			Constant.Error error = (Constant.Error)data.getSerializable("error");
			if (error != Constant.Error.OK) {
				System.out.println("-------------报错---------------");
				System.out.println(error);
				System.out.println("-------------报错结束---------------");
				
				//hint to users.
			} else {
				byte[] rtns = data.getByteArray("result");
				//解析xml-package
				if (null != rtns){
					DBHelper db = new DBHelper(context);
					if(bookmarkList.get(pos).getIsDownload()==0){
						HttpDownloader httpDownloader = new HttpDownloader();
						httpDownloader.downFileByString(rtns, "/"+uid+"/", bookmarkList.get(pos).getId()+".html");
						bookmarkList.get(pos).setIsDownload(1);
						db.setBookmarkIsDownload(bookmarkList.get(pos).getId());
						if(listView!=null)
							((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
					}
					db.close();
					
				}
			}

        }
    }
    
    class downloadResource extends Thread
    {        
    	private String uid;
    	private String key;

    	public downloadResource(String uid, String key) {
    		this.uid = uid;
    		this.key = key;
    	}
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            synchronized(this){
	        	ByteBuffer image = (ByteBuffer)ir.getImgRaw(key.replace(".png", ""), 500, 500, null);
	        	if(image!=null){
		            HttpDownloader httpDownloader = new HttpDownloader();
			        httpDownloader.downFileByString(image.array(), "/"+uid+"/", key);
			        DBHelper db = new DBHelper(context);
		            db.setResourceIsDownload(key.replace(".png", ""));
		            db.close();
	        	}
            }
        }
        
    }    
    
    public void asyncDeleteArticle(int id, boolean saveToSDFlag){
    	if(wifi){
    		ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return;
    		}else{
    			final int bookmarkid = id;
    			final boolean saveToSD_flag = saveToSDFlag;
    			ir.asyncDeleteArticle(id+"", new Handler(){
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
    						System.out.println(rtns);
    						//本地删除
    						db = new DBHelper(context);
    						Cursor cur = db.getResourceById(bookmarkid+"");
 	                	    db.deleteBookmark(bookmarkid);
 	                	    //删除文件
 	                	    FileUtils fileUtils;
 	                	    if(saveToSD_flag){
	// 	          				Toast.makeText(Settings.this, "删除机身文件", Toast.LENGTH_SHORT).show();
	 	          				fileUtils = new FileUtils("sd");
	 	          			}else{
	// 	          				Toast.makeText(Settings.this, "删除sd卡文件", Toast.LENGTH_SHORT).show();
	 	          				fileUtils = new FileUtils();
	 	          			}
	 	              		try {
	 	          				fileUtils.del(fileUtils.getSDPATH()+userid+"/"+bookmarkid+".html");
	 	          				if(cur.getCount()!=0){
	 	          			        if(cur.moveToFirst()){
	 	          			    		do{
	 	          			    			fileUtils.del(fileUtils.getSDPATH()+userid+"/"+cur.getString(2)+".png");
	 	          			    		}
	 	          			    		while(cur.moveToNext());
	 	          			    	}
	 	          		        }
	 	          			} catch (IOException e) {
	 	          				// TODO Auto-generated catch block
	 	          				e.printStackTrace();
	 	          			}
	 	              		db.deleteResourceById(bookmarkid+"");
	 	              		cur.close();
	 	              		db.close();
    					}
    					
    				}
    			});
    		}
    	}else{
    		DBHelper db = new DBHelper(context);
    		db.addOperation(userid,id+"","delete","asyncDeleteArticle",id+"");
    		db.close();
    	}
    }
    
    public void asyncCreateArticle(String url){
    	if(wifi){
    		ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return;
    		}else{
    			ir.asyncCreateArticle(url, new Handler(){
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
    						System.out.println(rtns);
    						
    					}
    					
    				}
    			});
    		}
    	}
    }
    
    public void updateBookmark(int id, Hashtable ht){
    	if(wifi){
    		ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    			return;
    		}else{
    			ir.asyncUpdateArticle(id+"", ht.get("title").toString(), "", new Handler(){
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
    						System.out.println("更新标题描述"+rtns);
    					}
    				}
    			});
    			ir.asyncMoveArticle(id+"", ht.get("category_name").toString(),  new Handler(){
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
    						System.out.println("更新分类"+rtns);
    					}
    				}
    			});
    		}
    	}else{
    		DBHelper db = new DBHelper(context);
    		db.addOperation(userid,id+"","update","updateBookmark",ht.get("title")+"|"+ht.get("category_name"));
    		db.close();
    	}
    }
    
    //登录通过临时token获取access_token
    public String getAccessToken(String userid, String gid, String token){
    	ir = HttpRead.instance();
    	String  access_token = ir.getAccessToken(userid, gid, token);
    	return access_token;
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
    
    //添加分类
    public void asyncCreateFolder(String __folder_name){
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			showProgressDialogInfo();
    			ir.asyncCreateFolder(__folder_name,new Handler(){
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
    						System.out.println("结果："+rtns);
    					}
    					dismissProgressDialog();
    				}
    			});
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	DBHelper db = new DBHelper(context);
    		db.addOperation(userid,__folder_name,"insert","asyncCreateFolder",__folder_name);
    		db.close();
        	
        }
    }
    
    //修改分类
    public void asyncUpdateFolder(String __old_name, String __new_name){
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			showProgressDialogInfo();
    			ir.asyncUpdateFolder(__old_name,__new_name,new Handler(){
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
    						System.out.println("结果："+rtns);
    					}
    					dismissProgressDialog();
    				}
    			});
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	DBHelper db = new DBHelper(context);
    		db.addOperation(userid,__old_name,"update","asyncUpdateFolder",__new_name);
    		db.close();
        }
    }
    
    //删除分类
    public void asyncDeleteFolder(String __folder_name){
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			showProgressDialogInfo();
    			ir.asyncDeleteFolder(__folder_name,new Handler(){
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
    						System.out.println("结果："+rtns);
    					}
    					dismissProgressDialog();
    				}
    			});
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	DBHelper db = new DBHelper(context);
    		db.addOperation(userid,__folder_name,"delete","asyncDeleteFolder",__folder_name);
    		db.close();
        }
    }
    
    //更新阅读进度
    public void asyncUpdateReadProgress(Bookmark bookmark){
    	wifi = true;
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
//    			showProgressDialogInfo();
    		    final int bookmarkid = bookmark.getId();
    			ir.asyncUpdateReadProgress(bookmark.getId()+"",Float.parseFloat(bookmark.getReadProgress()),(new Date().getTime()/1000),new Handler(){
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
    						System.out.println("更新文章标题,描述和阅读时间："+rtns);
    						db = new DBHelper(context);
    						db.updateReadTimeById(bookmarkid,(new Date().getTime()/1000)+"");
    						db.close();
    					}
//    					dismissProgressDialog();
    				}
    			});
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	DBHelper db = new DBHelper(context);
    		db.addOperation(userid,bookmark.getId()+"","update","asyncUpdateReadProgress",bookmark.getId()+"|"+bookmark.getReadProgress());
    		db.close();
        }
    }
    
    //清空阅读时间
    public void asyncDeleteArticleReadTime(Bookmark bookmark){
    	wifi = true;
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
//    			showProgressDialogInfo();
    		    final int bookmarkid = bookmark.getId();
    			ir.asyncUpdateReadProgress(bookmark.getId()+"",Float.parseFloat(bookmark.getReadProgress()),Long.parseLong("9999999999"),new Handler(){
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
    						System.out.println("更新文章标题,描述和阅读时间："+rtns);
    						db = new DBHelper(context);
    						db.updateReadTimeById(bookmarkid,null);
    						db.close();
    					}
//    					dismissProgressDialog();
    				}
    			});
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	DBHelper db = new DBHelper(context);
    		db.addOperation(userid,bookmark.getId()+"","update","asyncDeleteArticleReadTime",bookmark.getId()+"|"+bookmark.getReadProgress());
    		db.close();
        }
    }
    
    //列出所有分类
    public List<Map<String, Object>> listFolder(String ii){//没有调用这个方法
    	String result;
    	List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
    	showProgressDialogInfo();
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			
    			result = ir.listFolder();
    			System.out.println("结果:"+result);
    			//解析xml-package
    			if (null != result){
    				ByteArrayInputStream is;
    				try{
    					is = new ByteArrayInputStream(result.getBytes("utf-8"));
    					CategoryListParser parser; 
    	                parser = new PullCategoryListParser(); 
    	                List<CategoryList> list = parser.parse(is);
    	                Map<String, Object> map = new HashMap<String, Object>();
    	                for(int i=0;i<list.size();i++){
    	    				map.put("category", list.get(i).getFolder());
    	    				categoryList.add(map);
    	    				map = new HashMap<String, Object>();
    	                }
    				} catch (UnsupportedEncodingException e1) {    
    					e1.printStackTrace();
    					System.out.println(e1+"UnsupportedEncodingException e1有这样的错");
    				} catch (Exception e) {
    					System.out.println(e);
    				}
    			}
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        	Map<String, Object> map = new HashMap<String, Object>();
    		db = new DBHelper(context);
    		Cursor cur = db.loadAllCategory(userid);
    		if(cur.moveToFirst()){
    			do{
    				map.put("id", cur.getInt(0));
    				map.put("category", cur.getString(1));
    				map.put("time", cur.getString(2));
    				categoryList.add(map);
    			
    				map = new HashMap<String, Object>();
    			}
    			while(cur.moveToNext());
    		}
    		cur.close();
    		db.close();
        }
    	dismissProgressDialog();
    	return categoryList;
    }
	
    private ProgressDialog showProgressDialogInfo(){
    	if(dialog == null){
    		ProgressDialog progressDialog = new ProgressDialog(context);
    		progressDialog.setTitle("请稍后");//
    		progressDialog.setMessage("处理中...");//设置title和message报错
    		progressDialog.setIndeterminate(true);
    		progressDialog.setCancelable(true);
    		dialog = progressDialog;
    	}
    	dialog.show();
    	
    	return dialog;
    }
    
    private void dismissProgressDialog() {
        try {
        	dialog.dismiss();
        } catch (IllegalArgumentException e) {
          
        }
    }
    
    public void refreshList(SharedPreferences preferences, Global global){
    	if(wifi){
    		bookmarks = new ArrayList<Bookmark>();
        	//wifi情况
        	System.out.println("正在下载refreshlist");
        	final IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    			// Log.Error(" 初始化失败!");
    		}else{
    			boolean flag = true;
    			do{
    				if(global.getLastSyncRecord()==null){
    					global.setLastSyncRecord(0);
    				}
    				if(preferences.getInt("last_sync_record", 0)>global.getLastSyncRecord()){
    					global.setLastSyncRecord(preferences.getInt("last_sync_record", 0));
    				}
    				System.out.println("last_sync_record的值"+global.getLastSyncRecord());
	    			String rtns = ir.syncArticle(global.getLastSyncRecord(), null);
					System.out.println(rtns);
					if (null != rtns){
						ByteArrayInputStream is;
						ByteArrayInputStream resource_is;
						try {
							is = new ByteArrayInputStream(rtns.getBytes("utf-8"));
							BookmarkParser parser; 
		                    parser = new PullBookmarkParser(); 
		                    List<Bookmark> list = parser.parse(is,preferences);
		                    if(list.size()>0){
			                    db = new DBHelper(context);
			                    System.out.println("userid:"+userid);
			                    for(int i=0;i<list.size();i++){
			        				//用bookmarkid跟本地数据库比对,先select 先比对version(更新本条记录) 在比对text-version(下载)
			        				db.syncBookmark(list.get(i), userid);
			        				//通过bookmarkid先get-resource得到key
			        				String resource = ir.getResource(list.get(i).getId()+"");
			        				if(resource!=null){
				        				resource_is = new ByteArrayInputStream(resource.getBytes("utf-8"));
				        				ResourceParser resourceParser; 
				        				resourceParser = new PullResourceParser(); 
				        				List<Resource> resourceList = resourceParser.parse(resource_is);
				        				for(int j=0;j<resourceList.size();j++){
				        					resourceList.get(j).setBookmarkId(list.get(i).getId()+"");
				        					db.syncResource(resourceList.get(j),userid);
				        				}
			        				}
			                    }
			                    db.close();
		                    }else{
		                    	flag = false;
		                    }
		                    
		                } catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							System.out.println(e1+"UnsupportedEncodingException e1有这样的错1111111111111111111111111");
						} catch (Exception e) {  
							System.out.println(e+"方法sync");
							e.printStackTrace();
						} 
					}
    			}while(flag);
    			//清空s_category表  listFolder方法取所有category插入到s_category表
				String result = ir.listFolder();
				System.out.println(result);
				//解析xml-package
    			if (null != result){
    				ByteArrayInputStream is;
    				try{
    					is = new ByteArrayInputStream(result.getBytes("utf-8"));
    					CategoryListParser parser; 
    	                parser = new PullCategoryListParser(); 
    	                List<CategoryList> list = parser.parse(is);
    	                db = new DBHelper(context);
    	                if(list.size()>0)
    		                db.truncateCategory(userid);
    	                for(int i=0;i<list.size();i++){
    	    				db.addCategory(list.get(i).getFolder(),userid);
    	                }
    	                db.close();
    				} catch (UnsupportedEncodingException e1) {    
    					e1.printStackTrace();
    					System.out.println(e1);
    				} catch (Exception e) {
    					System.out.println(e);
    				}
    			}
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        }
    }
    
    //阅读中刷新
    public void readArticle(String __bookmark_id, SharedPreferences preferences){
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			String result = ir.readArticle(__bookmark_id);
    			System.out.println("单篇文章："+result);
    			ByteArrayInputStream is;
				ByteArrayInputStream resource_is;
				try {
					is = new ByteArrayInputStream(result.getBytes("utf-8"));
					BookmarkParser parser; 
                    parser = new PullBookmarkParser(); 
                    List<Bookmark> list = parser.parse(is,preferences);
                	db = new DBHelper(context);
                    for(int i=0;i<list.size();i++){
        				//用bookmarkid跟本地数据库比对,先select 先比对version(更新本条记录) 在比对text-version(下载)
        				db.syncBookmark(list.get(i), userid);
        				//通过bookmarkid先get-resource得到key
        				String resource = ir.getResource(list.get(i).getId()+"");
        				resource_is = new ByteArrayInputStream(resource.getBytes("utf-8"));
        				ResourceParser resourceParser; 
        				resourceParser = new PullResourceParser(); 
        				List<Resource> resourceList = resourceParser.parse(resource_is);
        				for(int j=0;j<resourceList.size();j++){
        					resourceList.get(j).setBookmarkId(list.get(i).getId()+"");
        					db.syncResource(resourceList.get(j),userid);
        				}
                    db.close();
                    }
                } catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println(e1);
				} catch (Exception e) {  
					System.out.println(e);
					e.printStackTrace();
				} 
    		}
        }
    }
    
  //阅读中刷新
    public void asyncFeedback(String __content, String __email){
    	if(wifi){
        	//wifi情况
        	IReadable ir;
        	ir = HttpRead.instance();
    		if (!ir.init(access_token)) {
    			System.out.println("初始化失败");
    		}else{
    			ir.asyncFeedback(__content, __email, new Handler(){
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
    						System.out.println("结果："+rtns);
    					}
    				}
    			});
    			
    		}
        }else{
        	//离线情况  记录离线操作
        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
        }
    }
}

package com.sohu.kan;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.kan.Global;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.ArticleList;

public class SohuKan extends Activity {
	
	private TextView unRead;
	private TextView latest;
	private TextView read;
	private TextView articleClass;
	private TextView add;
	private TextView settings;
	
	private DBHelper db;
	
	ArrayList<ArticleList> articleList;
	
	String token = "649cfef6a94ee38f0c82a26dc8ad341292c7510e";
	
	private boolean wifi;
	private RAPI rapi;
	
	private Global global; 
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        //子线程
//        db = new DBHelper(this);
        global = (Global)getApplication();
        db = new DBHelper(this);
        Cursor cursor = db.getSettings(token);
        if(cursor.getCount()!=0){
	        if(cursor.moveToFirst()){
	    		do{
	    			if(cursor.getInt(1)==1){
	    				global.setImgFlag(true);
	    			}else{
	    				global.setImgFlag(false);
	    			}
	    			if(cursor.getInt(2)==1){
	    				//sd卡
	    				global.setSaveFlag(true);
	    				global.setSavePath(Environment.getExternalStorageDirectory()+"/sohukan/");
	    			}else{
	    				//机身
	    				global.setSaveFlag(false);
	    				global.setSavePath("/data/data/com.sohu.kan/files/");
	    			}
	    		}
	    		while(cursor.moveToNext());
	    	}
        }else{
        	global.setImgFlag(true);
        	global.setSaveFlag(false);
        	global.setSavePath("/data/data/com.sohu.kan/files/");
        }
		
        cursor.close();
        db.close();
        
        wifi = RAPI.checkNetworkConnection(this);
        rapi = new RAPI(this);
        rapi.dataSync(global.getImgFlag());
        if(wifi){
        	//wifi情况
        	Toast.makeText(this, "正在下载", Toast.LENGTH_SHORT).show();
        }else{
        	//离线情况
        	Toast.makeText(this, "离线状态,连接WIFI即可同步最新文章!", Toast.LENGTH_SHORT).show();
        }
        ensureUi();
//        db.close();
    }
    
    public void ensureUi(){
    	unRead = (TextView)findViewById(R.id.unRead);
    	unRead.setText("未读");
    	latest = (TextView)findViewById(R.id.latest);
    	latest.setText("最近阅读");
    	read = (TextView)findViewById(R.id.read);
    	read.setText("已读");
    	articleClass = (TextView)findViewById(R.id.articleClass);
    	articleClass.setText("文章分类");
    	add = (TextView)findViewById(R.id.add);
    	add.setText("如何添加文章");
    	settings = (TextView)findViewById(R.id.settings);
    	settings.setText("设置");
    	
    	unRead.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putSerializable("list", articleList);
    			bundle.putString("type", "0");
    			intent.putExtras(bundle);
    			startActivity(intent);
    			//finish();
    		}
    	});
    	
    	latest.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
//    			Intent intent = new Intent(SohuRead.this,ReadList.class);
//    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    			Bundle bundle = new Bundle();
//    			bundle.putSerializable("list", new ArrayList<ArticleList>());
//    			bundle.putString("type", "1");
//    			intent.putExtras(bundle);
//    			startActivity(intent);
    		}
    	});
    	
    	read.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putSerializable("list", articleList);
    			bundle.putString("type", "1");
    			intent.putExtras(bundle);
    			startActivity(intent);
    			//finish();
    		}
    	});
    	
    	articleClass.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Toast.makeText(SohuKan.this, "进入分类页面", Toast.LENGTH_SHORT).show(); 
    			Intent intent;
    			if(getData().size()==0){
    				intent = new Intent(SohuKan.this,Category.class);
    			}else{
    				intent = new Intent(SohuKan.this,CategoryList.class);
    			}
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			intent.putExtras(bundle);
    			startActivity(intent);
    		}
    	});
    	
    	add.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Toast.makeText(SohuKan.this, "进入添加文章引导页面", Toast.LENGTH_SHORT).show(); 
    		}
    	});
    	
    	settings.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,Settings.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
    	});
    }
    
    public List<Map<String, Object>> getData(){
		
    	List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
    	
		Map<String, Object> map = new HashMap<String, Object>();
		
		db = new DBHelper(this);
		Cursor cur = db.loadAllCategory();
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
//		db.close();
		return categoryList;
	}

}
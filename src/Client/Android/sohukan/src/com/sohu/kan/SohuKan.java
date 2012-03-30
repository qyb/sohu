package com.sohu.kan;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.RAPI;

public class SohuKan extends Activity {
	
	private RelativeLayout unRead;
	private RelativeLayout latest;
	private RelativeLayout read;
	private RelativeLayout articleClass;
	private RelativeLayout index_guide;
	private Button add;
	private Button settings;
	
	private DBHelper db;
	
	String userid;
	
	private boolean wifi;
	private RAPI rapi;
	
	private Global global; 
	
	private TextView count_unread;
	private TextView count_read;
	private TextView count_category;
	
	public void onStart(){
		super.onStart();
		
        db = new DBHelper(this);
        Cursor curUnread = db.loadAllBookmark("0", userid);
    	Cursor curRead = db.loadAllBookmark("1", userid);
    	Cursor curCategory = db.loadAllCategory(userid);
        
    	count_unread.setText("("+curUnread.getCount()+")");
    	count_read.setText("("+curRead.getCount()+")");
    	count_category.setText("("+curCategory.getCount()+")");
    	
    	curUnread.close();
    	curRead.close();
    	curCategory.close();
    	db.close();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Bundle bundle = this.getIntent().getExtras();
        userid = bundle.getString("userid");
        boolean truncate_last_sync_record = false;
        truncate_last_sync_record = bundle.getBoolean("truncate_last_sync_record");
        
        boolean hasSync = false;
        hasSync = bundle.getBoolean("hasSync");
        
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        if(truncate_last_sync_record){
	        Editor editor=preferences.edit();
		    //更改配置数据  
		    editor.putInt("last_sync_record", 0);
		    //提交存储  
		    editor.commit();
        }
        int last_sync_record=preferences.getInt("last_sync_record", 0);
        //显示数据  
        Toast.makeText(this, "last_sync_record:"+String.valueOf(last_sync_record), Toast.LENGTH_SHORT).show();
        
        //子线程
        global = (Global)getApplication();
        db = new DBHelper(this);
        Cursor cursor = db.getSettings(userid);
        if(cursor.getCount()!=0){
	        if(cursor.moveToFirst()){
	    		do{
	    			global.setUserId(cursor.getString(0));
	    			global.setAccessToken(cursor.getString(1));
	    			global.setIndexGuide(cursor.getString(5));
	    			global.setUnreadlistGuide(cursor.getString(6));
	    			global.setReadGuide(cursor.getString(7));
	    			global.setCategoryGuide(cursor.getString(8));
	    			global.setLastSyncRecord(last_sync_record);
	    			System.out.println(cursor.getString(0));
	    			System.out.println(cursor.getString(1));
	    			System.out.println(cursor.getString(2));
	    			System.out.println(cursor.getString(3));
	    			System.out.println(cursor.getString(4));
	    			System.out.println(cursor.getString(5));
	    			System.out.println(cursor.getString(6));
	    			System.out.println(cursor.getString(7));
	    			System.out.println(cursor.getString(8));
	    			if(cursor.getInt(2)==1){
	    				global.setWifiFlag(true);
	    			}else{
	    				global.setWifiFlag(false);
	    			}
	    			if(cursor.getInt(3)==1){
	    				global.setImgFlag(true);
	    			}else{
	    				global.setImgFlag(false);
	    			}
	    			if(cursor.getInt(4)==1){
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
        	global.setWifiFlag(false);
        	global.setImgFlag(true);
        	global.setSaveFlag(false);
        	global.setSavePath("/data/data/com.sohu.kan/files/");
        }
        cursor.close();
    	
        count_unread = (TextView)findViewById(R.id.count_unread);
        count_read = (TextView)findViewById(R.id.count_read);
        count_category = (TextView)findViewById(R.id.count_category);
    	
    	db.close();
    	if(!truncate_last_sync_record){
    		if(!hasSync){
		        rapi = new RAPI(this,global.getAccessToken(),global.getUserId());
		        rapi.dataSync(preferences,count_unread,count_read,count_category,global.getImgFlag());
		        wifi = rapi.checkNetworkConnection(this);
		        if(wifi){
		        	//wifi情况
		        	Toast.makeText(this, "正在下载", Toast.LENGTH_SHORT).show();
		        }else{
		        	//离线情况
		        	Toast.makeText(this, "离线状态,连接WIFI即可同步最新文章!", Toast.LENGTH_SHORT).show();
		        }
    		}
    	}
        ensureUi();
//        db.close();
    }
    
    public void ensureUi(){
    	
    	index_guide = (RelativeLayout)findViewById(R.id.index_guide);
    	if("1".equals(global.getIndexGuide()))
    		index_guide.setVisibility(View.GONE);
    	index_guide.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			DBHelper db = new DBHelper(SohuKan.this);
    			db.setGuideRead(global.getUserId(), "index_guide");
    			db.close();
    			global.setIndexGuide("1");
    			index_guide.setVisibility(View.GONE);
    		}
    	});
    	unRead = (RelativeLayout)findViewById(R.id.unRead);
    	latest = (RelativeLayout)findViewById(R.id.latest);
    	read = (RelativeLayout)findViewById(R.id.read);
    	articleClass = (RelativeLayout)findViewById(R.id.articleClass);
    	add = (Button)findViewById(R.id.add);
    	settings = (Button)findViewById(R.id.settings);
    	
    	unRead.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("type", "0");
    			intent.putExtras(bundle);
    			startActivity(intent);
    			//finish();
    		}
    	});
    	
    	latest.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("latest", "1");
    			intent.putExtras(bundle);
    			startActivity(intent);
    		}
    	});
    	
    	read.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("type", "1");
    			intent.putExtras(bundle);
    			startActivity(intent);
    			//finish();
    		}
    	});
    	
    	articleClass.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent;
    			Bundle bundle = new Bundle();
    			if(getData().size()==0){
    				intent = new Intent(SohuKan.this,Category.class);
    				bundle.putString("empty", "1");
    			}else{
    				intent = new Intent(SohuKan.this,CategoryList.class);
    			}
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			intent.putExtras(bundle);
    			startActivity(intent);
    		}
    	});
    	
    	add.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,Collection.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
    	});
    	
    	settings.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(SohuKan.this,Setting.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
    	});
    }
    
    public List<Map<String, Object>> getData(){
		
    	List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
    	
		Map<String, Object> map = new HashMap<String, Object>();
		
		db = new DBHelper(this);
		Cursor cur = db.loadAllCategory(userid);
		if(cur.moveToFirst()){
			do{
				map.put("category", cur.getString(0));
				categoryList.add(map);
			
				map = new HashMap<String, Object>();
			}
			while(cur.moveToNext());
		}
		cur.close();
		db.close();
		return categoryList;
	}
    
//    public SharedPreferences getPerference(){
//    	SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
//    	return preferences;
//    }

}
package com.sohu.kan;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.FileUtils;

public class Setting extends Activity {
	
	private TextView logout_title;
	private CheckBox wifi_sync;
	private CheckBox display_img;
	private CheckBox save_to_sd;
	private TextView trun_cache;
	private TextView check_version;
	private TextView how_to_use;
	private TextView suggestion_box;
	
	private TextView account_title;
	
	private boolean wifi_sync_flag = false;
	private boolean img_flag = false;
	private boolean saveToSD_flag = false;
	
	private ProgressDialog dialog;
	
	private Global global;
	
	String access_token;
	String userid;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        
        global = (Global)getApplication();
        access_token = global.getAccessToken();
        userid = global.getUserId();
        wifi_sync_flag = global.getWifiFlag();
        img_flag = global.getImgFlag();
        saveToSD_flag = global.getSaveFlag();
        ensureUi();
	}
	
	public void ensureUi(){
		
		account_title = (TextView)findViewById(R.id.account_title);
		account_title.setText("账号:"+userid);
		logout_title = (TextView)findViewById(R.id.logout_title);
		logout_title.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
    			AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
    	    	builder.setMessage("确认要注销登录?")
    	    	       .setCancelable(false)
    	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	        	   DBHelper db = new DBHelper(Setting.this);
    	    					db.logout(userid);
    	    					db.close();
    	    					global = new Global();
    	    					Intent intent = new Intent(Setting.this,Login.class);
    	    	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	    			startActivity(intent);
    	    	    			finish();
    	    	           }
    	    	       })
    	    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	                dialog.cancel();
    	    	           }
    	    	       });
    	    	builder.create().show();
			}
		});
		
		wifi_sync = (CheckBox)findViewById(R.id.wifi_sync);
		if(wifi_sync_flag){
			wifi_sync.setChecked(true);
		}
		wifi_sync.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DBHelper db = new DBHelper(Setting.this);
				if(!wifi_sync_flag){
					Toast.makeText(Setting.this, "仅wifi同步", Toast.LENGTH_SHORT).show();
					wifi_sync_flag = true;
					global.setWifiFlag(true);
					db.setSettingsWifi(userid, 1);
				}else{
					Toast.makeText(Setting.this, "非wifi也可同步", Toast.LENGTH_SHORT).show();
					wifi_sync_flag = false;
					global.setWifiFlag(false);
					db.setSettingsWifi(userid, 0);
				}
				db.close();
			}
		});
		
		display_img = (CheckBox)findViewById(R.id.display_img);
		if(img_flag){
			display_img.setChecked(true);
		}
		display_img.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DBHelper db = new DBHelper(Setting.this);
				if(!img_flag){
					Toast.makeText(Setting.this, "文章显示图片", Toast.LENGTH_SHORT).show();
					img_flag = true;
					global.setImgFlag(true);
					db.setSettingsImg(userid, 1);
				}else{
					Toast.makeText(Setting.this, "文章不显示图片", Toast.LENGTH_SHORT).show();
					img_flag = false;
					global.setImgFlag(false);
					db.setSettingsImg(userid, 0);
				}
				db.close();
			}
		});
		
		save_to_sd = (CheckBox)findViewById(R.id.save_to_sd);
		if(saveToSD_flag){
			save_to_sd.setChecked(true);
		}
		save_to_sd.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				new CopyToSD().execute();
				
			}
		});
		
		trun_cache = (TextView)findViewById(R.id.trun_cache);
		trun_cache.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
    	    	builder.setMessage("确认清空本地所有缓存?")
    	    	       .setCancelable(false)
    	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	        	   new TrunkCache().execute();
    	    	           }
    	    	       })
    	    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	                dialog.cancel();
    	    	           }
    	    	       });
    	    	builder.create().show();
			}
		});
		
		check_version = (TextView)findViewById(R.id.check_version);
		check_version.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Setting.this, "检查", Toast.LENGTH_SHORT).show();
			}
		});
		
		how_to_use = (TextView)findViewById(R.id.how_to_use);
		how_to_use.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Setting.this, "使用引导", Toast.LENGTH_SHORT).show();
				DBHelper db = new DBHelper(Setting.this);
    			db.resetGuide(global.getUserId());
    			db.close();
    			global.setIndexGuide("0");
    			global.setUnreadlistGuide("0");
    			global.setReadGuide("0");
    			global.setCategoryGuide("0");
    			Intent intent = new Intent(Setting.this,SohuKan.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putSerializable("userid", global.getUserId());
    			intent.putExtras(bundle);
    			startActivity(intent);
			}
		});
		
		suggestion_box = (TextView)findViewById(R.id.suggestion_box);
		suggestion_box.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(Setting.this,FeedBack.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
			}
		});
	}
	
	public ProgressDialog showProgressDialogInfo(){
    	if(dialog == null){
    		ProgressDialog progressDialog = new ProgressDialog(this);
    		progressDialog.setTitle("请稍后");//
    		progressDialog.setMessage("处理中...");//设置title和message报错
    		progressDialog.setIndeterminate(true);
    		progressDialog.setCancelable(true);
    		dialog = progressDialog;
    	}
    	dialog.show();
    	
    	return dialog;
    }
    
	public void dismissProgressDialog() {
        try {
        	dialog.dismiss();
        } catch (IllegalArgumentException e) {
          
        }
    }
	
	class CopyToSD extends AsyncTask<Void, Void, Boolean>{
    	
    	protected void onPreExecute(){
    		showProgressDialogInfo();
    	}
    	
    	protected Boolean doInBackground(Void... params){
    		FileUtils fileUtils;
    		DBHelper db = new DBHelper(Setting.this);
    		if(!saveToSD_flag){
//				Toast.makeText(Settings.this, "转存到sd卡", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils("sd");
				fileUtils.copyFolder("/data/data/com.sohu.kan/files/"+userid, fileUtils.getSDPATH()+userid);
				try {
					fileUtils.del("/data/data/com.sohu.kan/files/"+userid);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveToSD_flag = true;
				global.setSaveFlag(true);
				global.setSavePath(Environment.getExternalStorageDirectory()+"/sohukan/");
				db.setSettingsSave(userid, 1);
			}else{
//				Toast.makeText(Settings.this, "转存到机身内存", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils();
				fileUtils.copyFolder(Environment.getExternalStorageDirectory()+"/sohukan/"+userid, fileUtils.getSDPATH()+access_token);
				try {
					fileUtils.del(Environment.getExternalStorageDirectory()+"/sohukan/"+userid);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveToSD_flag = false;
				global.setSaveFlag(false);
				global.setSavePath("/data/data/com.sohu.kan/files/");
				db.setSettingsSave(userid, 0);
			}
    		db.close();
    		return false;
    	}
    	
    	protected void onPostExecute(Boolean loggedIn){
    		dismissProgressDialog();
    	}
    	
    	protected void onCancelled(){
    		dismissProgressDialog();
    	}
    }
	
	class TrunkCache extends AsyncTask<Void, Void, Boolean>{
    	
    	protected void onPreExecute(){
    		showProgressDialogInfo();
    	}
    	
    	protected Boolean doInBackground(Void... params){
    		FileUtils fileUtils;
    		if(saveToSD_flag){
//				Toast.makeText(Settings.this, "删除机身文件", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils("sd");
			}else{
//				Toast.makeText(Settings.this, "删除sd卡文件", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils();
			}
    		try {
				fileUtils.del(fileUtils.getSDPATH()+userid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		DBHelper db = new DBHelper(Setting.this);
    		db.truncateBookmark(userid);
    		db.truncateResource(userid);
    		db.close();
    		
    		Intent intent = new Intent(Setting.this,SohuKan.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Bundle bundle = new Bundle();
			bundle.putBoolean("truncate_last_sync_record", true);
			bundle.putSerializable("userid", global.getUserId());
			intent.putExtras(bundle);
			startActivity(intent);
    		return false;
    	}
    	
    	protected void onPostExecute(Boolean loggedIn){
    		dismissProgressDialog();
    	}
    	
    	protected void onCancelled(){
    		dismissProgressDialog();
    	}
    }
}

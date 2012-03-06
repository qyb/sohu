package com.sohu.kan;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sohu.database.DBHelper;
import com.sohu.look.R;
import com.sohu.utils.FileUtils;

public class Settings extends Activity {
	
	private TextView modify_password_title;
	private TextView logout_title;
	private ToggleButton display_img;
	private ToggleButton save_to_sd;
	private TextView trun_cache;
	private TextView check_version;
	private TextView how_to_use;
	private TextView suggestion_box;
	
	private boolean img_flag = false;
	private boolean saveToSD_flag = false;
	
	private ProgressDialog dialog;
	
	private Global global;
	
	String token = "649cfef6a94ee38f0c82a26dc8ad341292c7510e";
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        
        global = (Global)getApplication();
        img_flag = global.getImgFlag();
        saveToSD_flag = global.getSaveFlag();
        
        ensureUi();
	}
	
	public void ensureUi(){
		modify_password_title = (TextView)findViewById(R.id.modify_password_title);
		modify_password_title.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Settings.this, "修改密码", Toast.LENGTH_SHORT).show();
			}
		});
		
		logout_title = (TextView)findViewById(R.id.logout_title);
		logout_title.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Settings.this, "注销登录", Toast.LENGTH_SHORT).show();
			}
		});
		
		display_img = (ToggleButton)findViewById(R.id.display_img);
		if(img_flag){
			display_img.setChecked(true);
		}
		display_img.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DBHelper db = new DBHelper(Settings.this);
				if(!img_flag){
					Toast.makeText(Settings.this, "文章显示图片", Toast.LENGTH_SHORT).show();
					img_flag = true;
					global.setImgFlag(true);
					db.setSettingsImg(token, 1);
				}else{
					Toast.makeText(Settings.this, "文章不显示图片", Toast.LENGTH_SHORT).show();
					img_flag = false;
					global.setImgFlag(false);
					db.setSettingsImg(token, 0);
				}
				db.close();
			}
		});
		
		save_to_sd = (ToggleButton)findViewById(R.id.save_to_sd);
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
				Toast.makeText(Settings.this, "清空缓存", Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
    	    	builder.setMessage("确认清空本地所有缓存?")
    	    	       .setCancelable(false)
    	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
//    	    	        	   new TrunkCache().execute();
//    	    	        	   DBHelper db = new DBHelper(Settings.this);
//    	    	        	   db.truncateArticle();
//    	    	        	   db.close();
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
				Toast.makeText(Settings.this, "检查", Toast.LENGTH_SHORT).show();
			}
		});
		
		how_to_use = (TextView)findViewById(R.id.how_to_use);
		how_to_use.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Settings.this, "使用引导", Toast.LENGTH_SHORT).show();
			}
		});
		
		suggestion_box = (TextView)findViewById(R.id.suggestion_box);
		suggestion_box.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(Settings.this, "意见反馈", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private ProgressDialog showProgressDialogInfo(){
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
    
    private void dismissProgressDialog() {
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
    		DBHelper db = new DBHelper(Settings.this);
    		if(!saveToSD_flag){
//				Toast.makeText(Settings.this, "转存到sd卡", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils("sd");
				fileUtils.copyFolder("/data/data/com.sohu.look/files/"+token, fileUtils.getSDPATH()+token);
				try {
					fileUtils.del("/data/data/com.sohu.look/files/"+token);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveToSD_flag = true;
				global.setSaveFlag(true);
				global.setSavePath(Environment.getExternalStorageDirectory()+"/sohukan/");
				db.setSettingsSave(token, 1);
			}else{
//				Toast.makeText(Settings.this, "转存到机身内存", Toast.LENGTH_SHORT).show();
				fileUtils = new FileUtils();
				fileUtils.copyFolder(Environment.getExternalStorageDirectory()+"/sohukan/"+token, fileUtils.getSDPATH()+token);
				try {
					fileUtils.del(Environment.getExternalStorageDirectory()+"/sohukan/"+token);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveToSD_flag = false;
				global.setSaveFlag(false);
				global.setSavePath("/data/data/com.sohu.look/files/");
				db.setSettingsSave(token, 0);
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
				fileUtils.del(fileUtils.getSDPATH()+token);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

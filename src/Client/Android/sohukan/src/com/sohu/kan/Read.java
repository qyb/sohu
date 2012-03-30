package com.sohu.kan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.HttpDownloader;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.Bookmark;
import com.sohu.xml.model.Resource;


public class Read extends Activity {
	
	private List<Bookmark> bookmarkList;
	private List<Resource> resourceList;
	private ImageView read;
	private ImageView share;
	private RelativeLayout fontsize;
	private ImageView reduce;
	private ImageView increase;
	private Button normal;
	private WebView webview;
	private ProgressDialog progressDialog;
	
	private SlideButton slideButton;
	
	private int defaultFontSize;
	
	private float bright;
	
	static private boolean nightMode = false;
	
	private boolean readStyleSwitch = false;
	
	private boolean hasRead=false;
	
	private String html;
	
	private String type;
	private String latest;
	private String folder_name;
	
	private Global global;
	
	private ProgressDialog dialog;
	
	private RelativeLayout bottom_tools;
	private RelativeLayout read_guide;
	
	private RAPI rapi;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        global = (Global)getApplication();
        Bundle bundle = this.getIntent().getExtras();
        bookmarkList = (ArrayList<Bookmark>) bundle.getSerializable("bookmark");
        type = bundle.getString("type");
        latest = bundle.getString("latest");
        folder_name = bundle.getString("folder_name");
        bright = ((float) getOldBrightness() / (float) 255);
        
        if(bookmarkList!=null){
        	resourceList = new ArrayList<Resource>();
        	setContentView(R.layout.read);
        	slideButton = (SlideButton)findViewById(R.id.btn_slide);
            normal = (Button)findViewById(R.id.normal);
            bottom_tools = (RelativeLayout)findViewById(R.id.bottom_tools);
            progressDialog = new ProgressDialog(this);
            fontsize = (RelativeLayout)findViewById(R.id.fontsize);
            
	        DBHelper db = new DBHelper(this);
	        Cursor cur = db.getProgressById(bookmarkList.get(0).getId()+"");
	        cur.moveToFirst();
	        if(cur.getFloat(0)==1)
	        	hasRead = true;
	        cur.close();
	        db.close();
	        readArticle();
	        rapi = new RAPI(this,global.getAccessToken(),global.getUserId());
	        rapi.asyncUpdateReadProgress(bookmarkList.get(0));
        }else{
        	DBHelper db = new DBHelper(this);
        	this.setTheme(R.drawable.backgroundonly);
    		Cursor cur = db.checkUserLogin();
    		if(cur.getCount()!=0){
	        	String url = bundle.getString(Intent.EXTRA_TEXT);
	        	rapi = new RAPI(this,global.getAccessToken(),global.getUserId());
	        	rapi.asyncCreateArticle(url);
	        	Toast.makeText(Read.this, "收藏成功", Toast.LENGTH_SHORT).show();
    		}else{
    			Toast.makeText(Read.this, "请先登录搜狐随身看", Toast.LENGTH_SHORT).show();
    		}
        	cur.close();
        	db.close();
        	finish();
        }
	}
	
	public void readArticle(){
		html = "";
		read_guide = (RelativeLayout)findViewById(R.id.read_guide);
		if("1".equals(global.getReadGuide()))
			read_guide.setVisibility(View.GONE);
		read_guide.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DBHelper db = new DBHelper(Read.this);
    			db.setGuideRead(global.getUserId(), "read_guide");
    			db.close();
    			global.setReadGuide("1");
				read_guide.setVisibility(View.GONE);
			}
		});
		
		String path;
		String base_url;
		
		global = (Global)getApplication();
		
		if(global.getSaveFlag()){
			//sd卡
			base_url = "file:///"+Environment.getExternalStorageDirectory()+"/sohukan/";
		}else{
			//机身
			base_url = "file:///data/data/com.sohu.kan/files/";
		}
		path = global.getSavePath();
		
		DBHelper db = new DBHelper(this);
        Cursor cur = db.getResourceById(bookmarkList.get(0).getId()+"");
        
        if(cur.moveToFirst()){
        	Resource resource = new Resource();
    		do{
    			resource.setBookmarkId(cur.getString(0));
    			resource.setType(cur.getString(1));
    			resource.setKey(cur.getString(2));
    			resource.setIsDownload(cur.getInt(3));
    			resourceList.add(resource);
    			resource = new Resource();
    		}
    		while(cur.moveToNext());
    	}
        cur.close();
        db.close();
		
        BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path+global.getUserId()+"/"+bookmarkList.get(0).getId()+".html")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        String data = null;
        
        try {
			while((data = br.readLine())!=null)  
			{  
				html+=data;
				System.out.println(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for(int i=0;i<resourceList.size();i++){
        		html = html.replace(resourceList.get(i).getKey(), path+global.getUserId()+"/"+resourceList.get(i).getKey()+".png");
        		System.out.println("进来了："+path+global.getUserId()+"/"+resourceList.get(i).getKey()+".png");
        }
        
        webview = new WebView(this);
        webview = (WebView)findViewById(R.id.webview);
        webview.setOnTouchListener(new OnTouchListener(){
        	public boolean onTouch(View v, MotionEvent event){
        		if(fontsize.getVisibility()==View.VISIBLE)
        			fontsize.setVisibility(View.GONE); 
        		return false ; 
        	}
        	
        });
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webview.setInitialScale(120);
        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true); 
//        webSettings.setDefaultZoom(WebSettings.ZoomDensity);
        defaultFontSize = webSettings.getDefaultFontSize();
        boolean wifi = rapi.checkNetworkConnection(this);
        File file;
        
        if(global.getImgFlag()){
        	//显示图片
        	if(wifi){
        		//联网
        		if(html.contains("<img")){
        			HttpDownloader httpDownloader = new HttpDownloader();
        			for(int i=0;i<resourceList.size();i++){
        				file = new File(path+"/"+global.getUserId()+"/"+resourceList.get(i).getKey());
        	        	if(!file.exists()){
        	        		//无图片文件 下载文件并显示
//        	            	httpDownloader.downFileByString(image_url[i], "/"+token+"/", resourceList.get(i).getKey());
        	        	}
        	        }
        		}
        	}else{
        		//未联网
        		if(html.contains("<img")){
        			for(int i=0;i<resourceList.size();i++){
        				file = new File(path+"/"+global.getUserId()+"/"+resourceList.get(i).getKey());
        	        	if(!file.exists()){
        	        		//无图片文件 过滤img标签
        	        		html = html.replaceAll("<a.*><img.*>.*</img></a>", "").replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        	        	}
        	        }
        		}
        	}
        }else{
        	//不显示图片
        	if(wifi){
        		//联网
        		if(html.contains("<img")){
        			for(int i=0;i<resourceList.size();i++){
        				file = new File(path+"/"+global.getUserId()+"/"+resourceList.get(i).getKey());
        	        	if(!file.exists()){
        	        		//无图片文件 过滤img标签
        	        		html = html.replaceAll("<a.*><img.*>.*</img></a>", "").replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        	        	}
        	        }
        		}
        	}else{
        		//未联网
        		if(html.contains("<img")){
        			for(int i=0;i<resourceList.size();i++){
        				file = new File(path+"/"+global.getUserId()+"/"+resourceList.get(i).getKey());
        	        	if(!file.exists()){
        	        		//无图片文件 过滤img标签
        	        		html = html.replaceAll("<a.*><img.*>.*</img></a>", "").replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        	        	}
        	        }
        		}
        	}
        }
        
        html +="<script>" +
        		"for(var i=0;i<document.images.length;i++){" +
        		"document.images[i].style.maxWidth='100%';" +
        		"}";
        
        String javascript = "var night = false;" +
        		"function nightMode(){" +
        		"if(!night){" +
        		"document.body.style.backgroundColor='#000000';document.body.text='#cccccc';night=true;" +
        		"}else{" +
        		"document.body.style.backgroundColor='#FFFFFF';document.body.text='#000000';night=false;" +
        		"}" +
        		"}";
        if(nightMode){
        	javascript += "nightMode();";
        	slideButton.changeAuto();
        	fontsize.setBackgroundResource(R.drawable.bg_bottom_black);
        	normal.setBackgroundResource(R.drawable.btn_gray_pressed);
        	bottom_tools.setBackgroundResource(R.drawable.bg_bottom_black);
        }
        html +=javascript+"</script>";
        webview.setWebViewClient(new WebViewClientDemo());
        webview.loadDataWithBaseURL(base_url,html.trim(), "text/html", "utf-8",null);
        read = (ImageView)findViewById(R.id.read);
        if(!hasRead){
	        read.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		if(!hasRead){
	        			Toast.makeText(Read.this, "操作成功", Toast.LENGTH_SHORT).show();
	        			//本地设已读
	        			DBHelper db = new DBHelper(Read.this);
	        			db.setBookmarkRead(bookmarkList.get(0).getId());
	        			db.close();
	        			//服务器设已读
	        			bookmarkList.get(0).setReadProgress(1+"");
	        			rapi.asyncUpdateReadProgress(bookmarkList.get(0));
	        			
	        			Intent intent = new Intent(Read.this,ReadList.class);
	        			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        			Bundle bundle = new Bundle();
	        			bundle.putString("type", "0");
	        			intent.putExtras(bundle);
	        			startActivity(intent);
	        			
	        		}
	        	}
	        });
        }else{
        	read.setVisibility(View.INVISIBLE);
        	ImageView delete_bookmark = (ImageView)findViewById(R.id.delete_bookmark);
        	delete_bookmark.setOnClickListener(new OnClickListener(){
        		public void onClick(View v){
        			AlertDialog.Builder builder = new AlertDialog.Builder(Read.this);
        	    	builder.setMessage("确认删除?")
        	    	       .setCancelable(false)
        	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
        	    	           public void onClick(DialogInterface dialog, int id) {
        	    	        	   	//删除文章
        	    	        	    DBHelper db = new DBHelper(Read.this);
        	                	    db.deleteBookmark(bookmarkList.get(0).getId());
        	                	    db.close();
	        	           			rapi = new RAPI(Read.this,global.getAccessToken(),global.getUserId());
	        	   	                rapi.asyncDeleteArticle(bookmarkList.get(0).getId(), global.getImgFlag());
	        	   	                
	        	           			Intent intent = new Intent(Read.this,ReadList.class);
	        	           			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	           			Bundle bundle = new Bundle();
	        	       				if(!"".equals(type) && type!=null){
	        	       					bundle.putString("type", type);
	        	       		    	}else if(!"".equals(folder_name) && folder_name!=null){
	        	       		    		bundle.putString("folder_name", folder_name);
	        	       		    	}else if(!"".equals(latest) && latest!=null){
	        	       		    		bundle.putString("latest", latest);
	        	       		    	}
	        	           			intent.putExtras(bundle);
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
        	delete_bookmark.setVisibility(View.VISIBLE);
        }
        
        share = (ImageView)findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent intent=new Intent(Intent.ACTION_SEND);
        		intent.setType("text/plain");
        		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        		intent.putExtra(Intent.EXTRA_TEXT, "搜狐随身看，与您分享！");
        		startActivity(Intent.createChooser(intent, getTitle()));
        	}
        });
        
        reduce = (ImageView)findViewById(R.id.reduce);
		reduce.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(webSettings.getDefaultFontSize()>7)
					webSettings.setDefaultFontSize(webSettings.getDefaultFontSize()-1);
			}
		});
		
		normal.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				webSettings.setDefaultFontSize(defaultFontSize); 
			}
		});
		
		increase = (ImageView)findViewById(R.id.increase);
		increase.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(webSettings.getDefaultFontSize()<18)
					webSettings.setDefaultFontSize(webSettings.getDefaultFontSize()+1);
			}
		});
		
//    	RelativeLayout list_bottom_tools = (RelativeLayout)findViewById(R.id.bottom_tools);
//    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_bottom_white);
//    	BitmapDrawable bd = new BitmapDrawable(bitmap);
//    	bd.setTileModeXY(TileMode.REPEAT , TileMode.CLAMP );
//    	bd.setDither(true);
//
//    	list_bottom_tools.setBackgroundDrawable(bd);
		
        slideButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(!readStyleSwitch){
					webview.loadUrl(bookmarkList.get(0).getUrl());
					readStyleSwitch = true;
				}else{
					webview.loadDataWithBaseURL("file:///data/data/com.sohu.kan/files/",html.trim(), "text/html", "utf-8",null);
					readStyleSwitch = false;
				}
        	}
        });
	}
	
	/**
     * 设置屏幕亮度
     */
    private void brightnessMax(float max) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = max;
            getWindow().setAttributes(lp);
    }
    
    /**
     * 取得当前用户自定义的屏幕亮度
     */
    private int getOldBrightness() {
            int brightness;
            try {
                    brightness = Settings.System.getInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS);
            } catch (SettingNotFoundException snfe) {
                    brightness = 255;
            }
            return brightness;
    }


	
	private class WebViewClientDemo extends WebViewClient {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
        	final String text_url = url;
        	new AlertDialog.Builder(Read.this)
    		.setTitle(url)
    		.setIcon(android.R.drawable.ic_dialog_info)
    		.setItems(new String[] { "收藏目标网页", "使用浏览器打开" }, 
    			new DialogInterface.OnClickListener() {
    		      	public void onClick(DialogInterface dialog, int which) {
    		      		switch(which){
//        		      		case 0:
//    	    		      		{
//    	    		      			Toast.makeText(Read.this, "直接阅读", Toast.LENGTH_SHORT).show(); 
//    	    		      			break; 
//    	    		      		}
        		      		case 0:
    	    		      		{
    	    		      			Toast.makeText(Read.this, text_url, Toast.LENGTH_SHORT).show();
    	    		      			//直接把url丢给服务器处理
    	    		      			rapi.asyncCreateArticle(text_url);
    	    		      			break; 
    	    		      		}
        		      		case 1:
    	    		      		{
    	    		      	        Uri uri = Uri.parse(text_url); //url为你要链接的地址
    	    		      	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    	    		      	        startActivity(intent);
    	    		      			break; 
    	    		      		}
    		      		}
    		      		dialog.dismiss();
    		      	}
    		    })
    		.setNegativeButton("取消", null).show();
        	return true;
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	//菊花框
    		progressDialog.setTitle("请稍后");
    		progressDialog.setMessage("正在读取文章...");//设置title和message报错
    		progressDialog.setIndeterminate(true);
    		progressDialog.setCancelable(true);
    		progressDialog.show();

    		super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
        	progressDialog.dismiss();
        	super.onPageFinished(view, url);
        } 
    }
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
	    menu.add(0, 1, 0, "刷新").setIcon(R.drawable.ic_menu_refresh);  
        menu.add(0, 2, 0, "编辑").setIcon(R.drawable.ic_menu_editor);  
        menu.add(0, 3, 0, "字体").setIcon(R.drawable.ic_menu_fontsize); 
        if(nightMode){
        	menu.add(0, 4, 0, "白天模式").setIcon(R.drawable.ic_menu_daytime_mode);
    	}else{
    		menu.add(0, 4, 0, "夜间模式").setIcon(R.drawable.ic_menu_night_mode);
    	}
        menu.add(0, 5, 0, "设置").setIcon(R.drawable.ic_menu_settings);  
        menu.add(0, 6, 0, "意见反馈").setIcon(R.drawable.ic_menu_feedback);  
	    return super.onPrepareOptionsMenu(menu);
	  }
	
	@Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
          
        menu.add(0, 1, 0, "刷新").setIcon(R.drawable.ic_menu_refresh);  
        menu.add(0, 2, 0, "编辑").setIcon(R.drawable.ic_menu_editor);  
        menu.add(0, 3, 0, "字体").setIcon(R.drawable.ic_menu_fontsize); 
        if(nightMode){
        	menu.add(0, 4, 0, "白天模式").setIcon(R.drawable.ic_menu_daytime_mode);
    	}else{
    		menu.add(0, 4, 0, "夜间模式").setIcon(R.drawable.ic_menu_night_mode);
    	}
        menu.add(0, 5, 0, "设置").setIcon(R.drawable.ic_menu_settings);  
        menu.add(0, 6, 0, "意见反馈").setIcon(R.drawable.ic_menu_feedback);  
        return super.onCreateOptionsMenu(menu);  
//        MenuInflater inflater = getMenuInflater();
//        
//        inflater.inflate(R.menu.read_menu, menu);
//        return true;
    }  
  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item)  
    {  
        switch(item.getItemId())  
        {  
            case 1:  // 刷新
            {  
            	Toast.makeText(Read.this, "刷新", Toast.LENGTH_SHORT).show();
            	new RefreshArticle().execute();
                break;  
            }  
            case 2: 
            {   
            	Intent intent = new Intent(this,EditArticle.class);
        		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		Bundle bundle = new Bundle();
        		ArrayList<Bookmark> bookmark = new ArrayList<Bookmark>();
        		bookmark.add(bookmarkList.get(0));
        		bundle.putSerializable("bookmark", bookmark);
        		intent.putExtras(bundle);
        		startActivity(intent);
                break;  
            }  
            case 3: 
            {  
            	Toast.makeText(Read.this, "字体", Toast.LENGTH_SHORT).show();  
            	fontsize.setVisibility(View.VISIBLE); 
                break;  
            } 
            case 4: 
            {  
            	if(!nightMode){
            		nightMode = true;
            		normal.setBackgroundResource(R.drawable.btn_gray_pressed);
            		fontsize.setBackgroundResource(R.drawable.bg_bottom_black);
            		bottom_tools.setBackgroundResource(R.drawable.bg_bottom_black);
            		brightnessMax(0.10f);
            	}else{
            		nightMode = false;
            		normal.setBackgroundResource(R.drawable.sohu_btn_gray);
            		fontsize.setBackgroundResource(R.drawable.bg_bottom_white);
            		bottom_tools.setBackgroundResource(R.drawable.bg_bottom_white);
            		brightnessMax(bright);
            	}
            	slideButton.changeAuto();
            	webview.loadUrl("javascript:nightMode()");

                break;  
            } 
            case 5: 
            {   
            	Intent intent = new Intent(Read.this,Setting.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
                break;  
            } 
            case 6: 
            {  
            	Intent intent = new Intent(Read.this,FeedBack.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
                break;  
            } 
        }  
        return super.onOptionsItemSelected(item);  
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
    
    class RefreshArticle extends AsyncTask<Void, Void, Boolean>{
    	
    	protected void onPreExecute(){
    		showProgressDialogInfo();
    	}
    	
    	protected Boolean doInBackground(Void... params){
    		//刷新操作
    		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(Read.this);
    		rapi.readArticle(bookmarkList.get(0).getId()+"", preferences);
    		rapi.downloadResource(global.getImgFlag(), bookmarkList, global.getUserId(), null);
    		return false;
    	}
    	
    	protected void onPostExecute(Boolean loggedIn){
    		Read.this.readArticle();
    		dismissProgressDialog();
    	}
    	
    	protected void onCancelled(){
    		dismissProgressDialog();
    	}
    }
}

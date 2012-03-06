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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sohu.database.DBHelper;
import com.sohu.utils.HttpDownloader;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.ArticleList;


public class Read extends Activity {
	
	private List<ArticleList> articleList;
	private TextView read;
	private TextView share;
	private RelativeLayout fontsize;
	private Button reduce;
	private Button increase;
	private Button normal;
	private WebView webview;
	private ToggleButton read_style;
	private ProgressDialog progressDialog;
	
	private int defaultFontSize;
	
	private float bright;
	
	static private boolean nightMode = false;
	
	private boolean readStyleSwitch = false;
	
	private boolean hasRead=false;
	
	private String html = "";
	
	private String token = "649cfef6a94ee38f0c82a26dc8ad341292c7510e";
	
	private Global global;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.read);
        
        Bundle bundle = this.getIntent().getExtras();
        articleList = (ArrayList<ArticleList>) bundle.getSerializable("article");
        
        progressDialog = new ProgressDialog(this);
        fontsize = (RelativeLayout)findViewById(R.id.fontsize);
        
        bright = ((float) getOldBrightness() / (float) 255);
        
        DBHelper db = new DBHelper(this);
        Cursor cur = db.getProgressByKey(articleList.get(0).getKey());
        cur.moveToFirst();
        if(cur.getFloat(0)==1)
        	hasRead = true;
        cur.close();
        db.close();
        readArticle();
        
	}
	
	public void readArticle(){
		BufferedReader br=null;
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
		System.out.println("读取路径"+path);
		System.out.println("基本文件路径"+base_url);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path+token+"/"+articleList.get(0).getKey()+".html")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(e1);
		}  
        String data = null;
        
        String[] image_url = articleList.get(0).getImageUrls().split("\\|");
        try {
			while((data = br.readLine())!=null)  
			{  
				html+=data;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
        
        for(int i=0;i<image_url.length;i++){
        	if(!"0".equals(image_url[i]))
        		html = html.replace(image_url[i], path+"/"+token+"/"+articleList.get(0).getKey()+"_"+i);
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
        webview.setWebViewClient(new WebViewClientDemo()); 
        
        boolean wifi = RAPI.checkNetworkConnection(this);
        File file;
        
        if(global.getImgFlag()){
        	//显示图片
        	if(wifi){
        		//联网
        		if(html.contains("<img")){
        			HttpDownloader httpDownloader = new HttpDownloader();
        			for(int i=0;i<image_url.length;i++){
        				file = new File(path+"/"+token+"/"+articleList.get(0).getKey()+"_"+i);
        	        	if(!file.exists()){
        	        		//无图片文件 下载文件并显示
        	        		if(!"0".equals(image_url[i])){
        	            		httpDownloader.downFile(image_url[i], "/"+token+"/", articleList.get(0).getKey()+"_"+i);
        	            	}
        	        	}
        	        }
        		}
        	}else{
        		//未联网
        		if(html.contains("<img")){
        			for(int i=0;i<image_url.length;i++){
        				file = new File(path+"/"+token+"/"+articleList.get(0).getKey()+"_"+i);
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
        			for(int i=0;i<image_url.length;i++){
        				file = new File(path+"/"+token+"/"+articleList.get(0).getKey()+"_"+i);
        	        	if(!file.exists()){
        	        		//无图片文件 过滤img标签
        	        		html = html.replaceAll("<a.*><img.*>.*</img></a>", "").replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        	        	}
        	        }
        		}
        	}else{
        		//未联网
        		if(html.contains("<img")){
        			for(int i=0;i<image_url.length;i++){
        				file = new File(path+"/"+token+"/"+articleList.get(0).getKey()+"_"+i);
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
        }
        html +=javascript+"</script>";
        webview.loadDataWithBaseURL(base_url,html.trim(), "text/html", "utf-8",null);
        read = (TextView)findViewById(R.id.read);
        if(hasRead)
        	read.setText("已经阅读");
        read.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(!hasRead){
        			Toast.makeText(Read.this, "操作成功", Toast.LENGTH_SHORT).show();
        			read.setText("已经阅读");
        			//本地设已读
        			DBHelper db = new DBHelper(Read.this);
        			db.setArticleRead(articleList.get(0).getKey());
        			db.close();
        			Intent intent = new Intent(Read.this,ReadList.class);
        			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			Bundle bundle = new Bundle();
        			bundle.putString("type", "0");
        			intent.putExtras(bundle);
        			startActivity(intent);
        			//服务器设已读
        			
        		}
        	}
        });
        
        share = (TextView)findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent intent=new Intent(Intent.ACTION_SEND);  
        		intent.setType("text/plain");  
        		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
        		intent.putExtra(Intent.EXTRA_TEXT, "搜狐随身看，与您分享！");  
        		startActivity(Intent.createChooser(intent, getTitle())); 
        	}
        });
        
        reduce = (Button)findViewById(R.id.reduce);
		reduce.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(webSettings.getDefaultFontSize()>7)
					webSettings.setDefaultFontSize(webSettings.getDefaultFontSize()-1);
			}
		});
		
		normal = (Button)findViewById(R.id.normal);
		normal.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				webSettings.setDefaultFontSize(defaultFontSize); 
			}
		});
		
		increase = (Button)findViewById(R.id.increase);
		increase.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(webSettings.getDefaultFontSize()<18)
					webSettings.setDefaultFontSize(webSettings.getDefaultFontSize()+1);
			}
		});
		
		read_style = (ToggleButton)findViewById(R.id.read_style);
		read_style.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(!readStyleSwitch){
					webview.loadUrl(articleList.get(0).getUrl());
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
    		.setItems(new String[] { "直接阅读", "收藏目标网页", "使用浏览器打开" }, 
    			new DialogInterface.OnClickListener() {
    		      	public void onClick(DialogInterface dialog, int which) {
    		      		switch(which){
        		      		case 0:
    	    		      		{
    	    		      			Toast.makeText(Read.this, "直接阅读", Toast.LENGTH_SHORT).show(); 
    	    		      			break; 
    	    		      		}
        		      		case 1:
    	    		      		{
    	    		      			Toast.makeText(Read.this, "收藏成功", Toast.LENGTH_SHORT).show();
    	    		      			//直接把url丢给服务器处理
    	    		      			
    	    		      			break; 
    	    		      		}
        		      		case 2:
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
	@Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
          
//        menu.add(0, 0, 0, "关于");  
//        menu.add(0, 1, 0, "退出");  
//        return super.onCreateOptionsMenu(menu);  
        MenuInflater inflater = getMenuInflater();
        
        inflater.inflate(R.menu.read_menu, menu);
        return true;
    }  
  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item)  
    {  
        switch(item.getItemId())  
        {  
            case R.id.refresh:  // 刷新
            {  
            	Toast.makeText(Read.this, "刷新", Toast.LENGTH_SHORT).show();
                break;  
            }  
            case R.id.add_category: 
            {  
            	Toast.makeText(Read.this, "添加分类", Toast.LENGTH_SHORT).show();      
                break;  
            }  
            case R.id.font_size: 
            {  
            	Toast.makeText(Read.this, "字体", Toast.LENGTH_SHORT).show();  
            	fontsize.setVisibility(View.VISIBLE); 
                break;  
            } 
            case R.id.night: 
            {  
            	Toast.makeText(Read.this, "夜间模式", Toast.LENGTH_SHORT).show();
            	if(!nightMode)
            		nightMode = true;
            	else
            		nightMode = false;
            	webview.loadUrl("javascript:nightMode()");
            	
//            	if (!nightMode) {
//	                    brightnessMax(0.10f);
//	                    nightMode = true;
//	            } else {
//	                    brightnessMax(bright);
//	                    nightMode = false;
//	            }

                break;  
            } 
            case R.id.settings: 
            {  
            	Toast.makeText(Read.this, "设置", Toast.LENGTH_SHORT).show();               
                break;  
            } 
            case R.id.about: 
            {  
            	Toast.makeText(Read.this, "关于", Toast.LENGTH_SHORT).show();             
                break;  
            } 
        }  
        return super.onOptionsItemSelected(item);  
    } 
}

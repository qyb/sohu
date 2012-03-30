package com.sohu.kan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sohu.database.DBHelper;

public class OtherLogin extends Activity {
	
	private WebView webview;
	
	private ProgressDialog progressDialog;
	
	private int num=0;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.other_login);
        
//        String result = httpUrlConnection();
        String result = test("http://kan.sohu.com/api/2/account/login");
//        String result_url = test(result);
//        System.out.println("结果:"+result_url);
        
        progressDialog = new ProgressDialog(this);
        webview = new WebView(this);
        webview = (WebView)findViewById(R.id.other_login);
        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.setWebViewClient(new OtherLoginWebView());

//        webview.postUrl("http://kan.sohu.com/api/2/account/login", "provider=renren".getBytes());
        System.out.print("------------------------"+result);
        
        webview.loadUrl(result);
	}
	
	private class OtherLoginWebView extends WebViewClient {
        @Override
        
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
//        	Toast.makeText(OtherLogin.this, url, Toast.LENGTH_SHORT).show();
            super.onLoadResource(view, url);
        }
    
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	//菊花框
//        	Toast.makeText(OtherLogin.this, url, Toast.LENGTH_SHORT).show();
//        	System.out.println("地址"+url);
        	if(url.contains("http://kan.sohu.com/api/2/account/access-token") && !url.contains("passport.sohu.com")){
        		System.out.println(url+"-------------");
        		String result = test_response(url);
        		System.out.println(result);
        		result.replace("<html><body>", "");
        		result.replace("</body></html>", "");
        		String access_token="";
        		String user_id="";
        		if(result.contains("|")){
        			user_id = result.split("\\|")[0];
                	access_token = result.split("\\|")[1];
                }
        		System.out.println("这是access_token"+access_token);
        		System.out.println("这是user_id"+user_id);
                if(!"".equals(access_token) && access_token!=null){
                	DBHelper db = new DBHelper(OtherLogin.this);
                	db.saveAccessToken(access_token, user_id);
                	db.close();
                }
        		
        		Intent intent = new Intent(OtherLogin.this,SohuKan.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("userid", user_id);
    			intent.putExtras(bundle);
    			startActivity(intent);
    			finish();
        	}
    		progressDialog.setTitle("请稍后");
    		progressDialog.setMessage("正在读取...");//设置title和message报错
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
	
    private String httpUrlConnection(String new_url){  
        try{  
         String pathUrl = new_url;  
         //建立连接  
         URL url=new URL(pathUrl);  
         HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();  
           
         ////设置连接属性  
         httpConn.setDoOutput(true);//使用 URL 连接进行输出  
         httpConn.setDoInput(true);//使用 URL 连接进行输入  
         httpConn.setUseCaches(false);//忽略缓存  
         httpConn.setRequestMethod("POST");//设置URL请求方法  
         String requestString = "renren";  
           
         //设置请求属性  
        //获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致  
              byte[] requestStringBytes = requestString.getBytes("utf-8");  
              httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);  
              httpConn.setRequestProperty("Content-Type", "application/octet-stream");  
              httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接  
              httpConn.setRequestProperty("Charset", "UTF-8");  
              //  
//              String name=URLEncoder.encode("renren","utf-8");  
              httpConn.setRequestProperty("provider", "renren");  
                
              //建立输出流，并写入数据  
              OutputStream outputStream = httpConn.getOutputStream();  
              outputStream.write(requestStringBytes);  
              outputStream.close();  
             //获得响应状态  
              int responseCode = httpConn.getResponseCode();
              System.out.println("结果zhuangtai："+responseCode);
//              if(HttpURLConnection.HTTP_OK == responseCode){//连接成功  
//                 
               //当正确响应时处理数据  
               StringBuffer sb = new StringBuffer();  
                  String readLine;  
                  BufferedReader responseReader;  
                 //处理响应流，必须与服务器响应流输出的编码一致  
                  responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));  
                  
                  while ((readLine = responseReader.readLine()) != null) {  
                   sb.append(readLine).append("\n");  
                   System.out.println(readLine);
                  }
                  responseReader.close();
                  System.out.println("1111111111111111111111"+httpConn.getHeaderField( "location" ));
                  return httpConn.getHeaderField( "location" );
//              }  
        }catch(Exception ex){  
         ex.printStackTrace();  
         System.out.println("22222222222222222222"+ex);
        }  
        System.out.println("333333333333333333333");
        return null;
       }
    public String test(String new_url){
    	String urlStr = new_url;
    	try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con
                    .getOutputStream());    
            String xmlInfo = "provider=qq";
            System.out.println("urlStr=" + urlStr);
            System.out.println("xmlInfo=" + xmlInfo);
            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            String result = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
                result += line;
            }
            System.out.println("00000000000000000000000000"+result);
            System.out.println("1111111111111111111111"+con.getHeaderField( "location" ));
            return con.getHeaderField( "location" );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return null;
    }
    
    public String test_response(String new_url){
    	String urlStr = new_url;
    	try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

//            OutputStreamWriter out = new OutputStreamWriter(con
//                    .getOutputStream());    
//            String xmlInfo = "";
            System.out.println("urlStr=====" + urlStr);
//            System.out.println("xmlInfo=" + xmlInfo);
//            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
//            out.flush();
//            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            String result = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
                result += line;
            }
            System.out.println("00000000000000000000000000"+result);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return null;
    }
}

package com.sohu.kan;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.MD5Builder;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.RegisterXML;
import com.sohu.xml.parser.PullRegisterParser;
import com.sohu.xml.parser.RegisterParser;



public class Login extends Activity {
	
	private EditText userid;
	private EditText password;
	private Button submit;
	private Button register;
	private TextView forget_psw;
	
	private ImageView qq_login;
	
	private AsyncTask task;
	
	private ProgressDialog dialog;
	
	private String user_id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        DBHelper db = new DBHelper(this);
		Cursor cur = db.checkUserLogin();
		if(cur.getCount()!=0){
			if(cur.moveToFirst()){
				Intent intent = new Intent(this,SohuKan.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putString("userid", cur.getString(0));
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		}else{
			Cursor cursor = db.getLastLoginUser();
			if(cursor.moveToFirst()){
				user_id = cursor.getString(0);
			}
			cursor.close();
			ensureUi();
		}
		cur.close();
		db.close();
        
    }
    
    public void ensureUi(){
    	
    	userid = (EditText)findViewById(R.id.userid);
    	userid.setText(user_id);
    	password = (EditText)findViewById(R.id.password);
    	
    	submit = (Button)findViewById(R.id.submit);
    	submit.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			task = new LoginTask().execute();
    		}
    	});
    	register = (Button)findViewById(R.id.register);
    	register.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(Login.this,Register.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
    	});
    	
    	TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
    		public void afterTextChanged(Editable s){
    			
    		}
    		public void beforeTextChanged(CharSequence s, int start, int count, int after){
    			
    		}
    		public void onTextChanged(CharSequence s, int start, int before, int count){
    			boolean flag = usernameValidator() && passwordValidator();
    			if(flag){
    				submit.setTextColor(Color.parseColor("#FFFFFF"));
    				submit.setBackgroundResource(R.drawable.btn_green);
    			}else{
    				submit.setTextColor(Color.parseColor("#707070"));
    				submit.setBackgroundResource(R.drawable.btn_gray);
    			}
    			submit.setEnabled(flag);
    		}
    		
    		private boolean usernameValidator(){
    			return !TextUtils.isEmpty(userid.getText());
    		}
    		private boolean passwordValidator(){
    			return !TextUtils.isEmpty(password.getText());
    		}
    	};
    	userid.addTextChangedListener(fieldValidatorTextWatcher);
        password.addTextChangedListener(fieldValidatorTextWatcher);
        
        forget_psw = (TextView)findViewById(R.id.forget_psw);
        forget_psw.setText(Html.fromHtml("<u>"+"<font color='#707070'>忘记密码</font>"+"</u>"));
        forget_psw.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Uri uri = Uri.parse("https://passport.sohu.com/web/RecoverPwdInput.action"); //url为你要链接的地址
      	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      	        startActivity(intent);
        	}
        });
        qq_login = (ImageView)findViewById(R.id.qq_login);
        qq_login.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent intent = new Intent(Login.this,OtherLogin.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
        	}
        });
    }
    
    private String getXmlInfo() {
        StringBuilder sb = new StringBuilder();
        String key = "$V0[K#-AAOc/ZWyfcNQubXO8e,)?y*G&";
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<info>");
        sb.append("<userid>"+userid.getText().toString()+"</userid>");
        sb.append("<password>"+MD5Builder.getMD5(password.getText().toString())+"</password>");
        sb.append("<appid>1088</appid>");
        sb.append("<sig>"+MD5Builder.getMD5(userid.getText().toString()+"1088"+getGid()+key)+"</sig>");
        sb.append("<gid>"+getGid()+"</gid>");
        sb.append("</info>");
        return sb.toString();
    }
    
    private ProgressDialog showProgressDialogInfo(){
    	if(dialog == null){
    		ProgressDialog progressDialog = new ProgressDialog(this);
    		progressDialog.setTitle("请稍后");//
    		progressDialog.setMessage("正在登录...");//设置title和message报错
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
    
    class LoginTask extends AsyncTask<Void, Void, Boolean>{
    	
    	protected void onPreExecute(){
    		showProgressDialogInfo();
    	}
    	
    	protected Boolean doInBackground(Void... params){
    		boolean loggedIn=false;
    		String urlStr = "https://passport.sohu.com/mobile/gettoken";
    		try {
    			System.out.println("开始请求passport");
	            URL url = new URL(urlStr);
	            URLConnection con = url.openConnection();
	            con.setDoOutput(true);
	            con.setRequestProperty("Pragma:", "no-cache");
	            con.setRequestProperty("Cache-Control", "no-cache");
	            con.setRequestProperty("Content-Type", "text/xml");
	            System.out.println("url connection了");
	            OutputStreamWriter out = new OutputStreamWriter(con
	                    .getOutputStream());    
	            System.out.println("拿到OutputStreamWriter");
	            String xmlInfo = getXmlInfo();
	            System.out.println("获得返回结果xmlInfo"+getXmlInfo());
	            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
	            out.flush();
	            out.close();
	            System.out.println("开始读返回br");
	            BufferedReader br = new BufferedReader(new InputStreamReader(con
	                    .getInputStream()));
	            String line = "";
	            String result = "";
	            System.out.println("br读取完毕");
	            for (line = br.readLine(); line != null; line = br.readLine()) {
	                result += line;
	            }
	            System.out.println(result);
	            ByteArrayInputStream is;
				is = new ByteArrayInputStream(result.toString().getBytes());
				RegisterParser parser = new PullRegisterParser(); 
				List<RegisterXML> registers = new ArrayList<RegisterXML>();
				registers = parser.parse(is);
				int status = registers.get(0).getStatus();
				System.out.println("Status:"+registers.get(0).getStatus());
				System.out.println("Token:"+registers.get(0).getToken());
	                    
	            if(status==0){
	            	String token = registers.get(0).getToken();
	            	String user_id = userid.getText().toString();
	            	String gid = getGid();
	                System.out.println("到这了"+gid);
	                RAPI rapi = new RAPI(Login.this,"",user_id);
	                System.out.println("user_id:"+user_id);
	                System.out.println("token:"+token);
	                String rtns = rapi.getAccessToken(user_id, gid, token);
	                System.out.println("过了服务器验证"+rtns);
	                String access_token = "";
	                if(rtns.contains("|")){
	                	access_token = rtns.split("\\|")[1];
	                }
	                if(!"".equals(access_token) && access_token!=null){
	                	DBHelper db = new DBHelper(Login.this);
	                	db.saveAccessToken(access_token, user_id);
	                	db.close();
	                	loggedIn = true;
	                }else{
	                	loggedIn = false;
	                }
	            }else{
	            	loggedIn = false;
	            }
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return loggedIn;
    	}
    	
    	protected void onPostExecute(Boolean loggedIn){
    		
    		if(loggedIn){
    			Intent intent = new Intent(Login.this,SohuKan.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("userid", userid.getText().toString());
    			intent.putExtras(bundle);
    			startActivity(intent);
    			finish();
    		}else{
    			Toast.makeText(Login.this, "密码不正确,请核对后重新输入",Toast.LENGTH_LONG).show();
    		}
    		dismissProgressDialog();
    	}
    	
    	protected void onCancelled(){
    		dismissProgressDialog();
    	}
    }
    
    public String getGid(){
    	String ostype = "02";
    	String modeltype = "ffff";
    	String appid = "1088";
    	TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String imsi =tm.getSubscriberId();
        String mac = getLocalMacAddress();
        String mask = "";
        UUID uuid = null;
        String gid = "";
        if(!"".equals(imei) && imei!=null){
        	mask = "1";
        }else{
        	mask = "0";
        	imei = "";
        }
        if(!"".equals(imsi) && imsi!=null){
        	mask += "1";
        }else{
        	mask += "0";
        	imsi = "";
        }
        if(!"".equals(mac) && mac!=null){
        	mask += "1";
        }else{
        	mask += "0";
        	mac = "";
        }
        if(!"000".equals(mask)){
        	mask += "0";
        }else{
        	uuid = UUID.randomUUID();
        }
        if(uuid==null)
        	gid = ostype + modeltype + appid + mask + MD5Builder.getMD5(imei + imsi + mac);
        else
        	gid = ostype + modeltype + appid + mask + MD5Builder.getMD5(uuid.toString());
        return gid;
    }
    
    public String getLocalMacAddress() {  
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress();  
    }

}

package com.sohu.kan;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.MD5Builder;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.RegisterXML;
import com.sohu.xml.parser.PullRegisterParser;
import com.sohu.xml.parser.RegisterParser;

public class Register extends Activity {
	
	private TextView number_register_gray;
	private TextView mail_register_gray;
	private RelativeLayout all_mail_register;
	private RelativeLayout all_number_register;
	
	private EditText mail;
	private EditText pwd;
	private EditText confirm_pwd;
	
	private TextView accept_text;
	
	private CheckBox accept;
	
	private Button mail_submit_register;
	
	private Button send_sms;
	
	private boolean accept_flag = true;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.resgister);
	
        ensureUi();
	}
	
	public void ensureUi(){
		
		send_sms = (Button)findViewById(R.id.send_sms);
		send_sms.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String phone = "106900601202";
			    Intent replyIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phone));  
//			    replyIntent.putExtra("sms_body", "my reply");  
			    startActivity(replyIntent);  
			}
		});
		
		accept_text = (TextView)findViewById(R.id.accept_text);
		accept_text.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Uri uri = Uri.parse("http://passport.sohu.com/web/serviceitem.jsp"); //url为你要链接的地址
      	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      	        startActivity(intent);
			}
		});
		
		all_mail_register = (RelativeLayout)findViewById(R.id.all_mail_register);
		all_number_register = (RelativeLayout)findViewById(R.id.all_number_register);
		
		
		mail_register_gray = (TextView)findViewById(R.id.mail_register_gray);
		mail_register_gray.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			all_number_register.setVisibility(View.GONE);
    			all_mail_register.setVisibility(View.VISIBLE);
    		}
    	});
		
		number_register_gray = (TextView)findViewById(R.id.number_register_gray);
		number_register_gray.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			all_mail_register.setVisibility(View.GONE);
    			all_number_register.setVisibility(View.VISIBLE);
    		}
    	});
		
		mail = (EditText)findViewById(R.id.mail);
		pwd = (EditText)findViewById(R.id.pwd);
		confirm_pwd = (EditText)findViewById(R.id.confirm_pwd);
		
		accept = (CheckBox)findViewById(R.id.accept);
		accept.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                accept_flag = arg1;
            }
        });
		
		mail_submit_register = (Button)findViewById(R.id.mail_submit_register);
		mail_submit_register.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(TextUtils.isEmpty(mail.getText()) || TextUtils.isEmpty(pwd.getText()) || TextUtils.isEmpty(confirm_pwd.getText())){
					Toast.makeText(Register.this, "请输入完整", Toast.LENGTH_SHORT).show();
				}else{
					Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$",Pattern.CASE_INSENSITIVE);
			    	Matcher m = p.matcher(mail.getText().toString());
		    		if(m.matches()){
						if(pwd.getText().toString().equals(confirm_pwd.getText().toString())){
							Pattern password = Pattern.compile("^[a-zA-Z0-9_]{6,16}$",Pattern.CASE_INSENSITIVE);
							Matcher match = password.matcher(pwd.getText().toString());
							if(match.matches()){
								if(accept_flag){
									Toast.makeText(Register.this, "开始注册逻辑", Toast.LENGTH_SHORT).show();
									postPassport();
								}else{
									Toast.makeText(Register.this, "请勾选搜狐用户注册协议", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(Register.this, "密码由6-16位字母，数字，字符构成", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(Register.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
						}
		    		}else{
		    			Toast.makeText(Register.this, "邮箱由4-16位字母，数字，字母开头", Toast.LENGTH_SHORT).show();
		    		}
				}
			}
		});
		
	}
	
	private String getLoginXmlInfo() {
        StringBuilder sb = new StringBuilder();
        String key = "$V0[K#-AAOc/ZWyfcNQubXO8e,)?y*G&";
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<info>");
        sb.append("<userid>"+mail.getText().toString()+"</userid>");
        sb.append("<password>"+MD5Builder.getMD5(pwd.getText().toString())+"</password>");
        sb.append("<appid>1088</appid>");
        sb.append("<sig>"+MD5Builder.getMD5(mail.getText().toString()+"1088"+getGid()+key)+"</sig>");
        sb.append("<gid>"+getGid()+"</gid>");
        sb.append("</info>");
        return sb.toString();
    }
	
	private String getRegisterXmlInfo() {
		//必填项：userid,password,appid,sig(userid+appid+gid+key的md5),gid
        StringBuilder sb = new StringBuilder();
        String key = "$V0[K#-AAOc/ZWyfcNQubXO8e,)?y*G&";
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<register>");
        sb.append("<userid>"+mail.getText().toString()+"@sohu.com</userid>");
        sb.append("<appid>1088</appid>");
        sb.append("<sig>"+MD5Builder.getMD5(mail.getText().toString()+"@sohu.com"+"1088"+getGid()+key)+"</sig>");
        sb.append("<password>"+pwd.getText().toString()+"</password>");
        sb.append("<gid>"+getGid()+"</gid>");
        sb.append("</register>");
        return sb.toString();
    }
	
	private String postPassport(){
		String result = "";
		String urlStr = "https://passport.sohu.com/mobile/reguser";
		try{  
	         //建立连接  
	         URL url=new URL(urlStr);  
	         HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();  
	           
	         ////设置连接属性  
	         httpConn.setDoOutput(true);//使用 URL 连接进行输出  
	         httpConn.setDoInput(true);//使用 URL 连接进行输入  
	         httpConn.setUseCaches(false);//忽略缓存  
	         httpConn.setRequestMethod("POST");//设置URL请求方法  
	         String requestString = getRegisterXmlInfo();
	         //设置请求属性  
	        //获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致  
              byte[] requestStringBytes = requestString.getBytes("GBK");
              httpConn.setRequestProperty("Pragma:", "no-cache");
              httpConn.setRequestProperty("Cache-Control", "no-cache");
//              httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
              httpConn.setRequestProperty("Content-Type", "text/xml");
              httpConn.setRequestProperty("Charset", "GBK");
//              httpConn.connect();
              //建立输出流，并写入数据  
              OutputStream outputStream = httpConn.getOutputStream();
              outputStream.write(requestStringBytes);
              outputStream.close();
             //获得响应状态  
              int responseCode = httpConn.getResponseCode();
              if(HttpURLConnection.HTTP_OK == responseCode){//连接成功  
	              //当正确响应时处理数据  
	              StringBuffer sb = new StringBuffer();
	              String readLine;
	              BufferedReader responseReader;
	             //处理响应流，必须与服务器响应流输出的编码一致  
	              responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "GBK"));
	              
	              while ((readLine = responseReader.readLine()) != null) {
		              sb.append(readLine).append("\n");
		              System.out.println(readLine);
	              }
	              responseReader.close();
	              ByteArrayInputStream is;
	              try {
						is = new ByteArrayInputStream(sb.toString().getBytes());
						RegisterParser parser = new PullRegisterParser(); 
	                    List<RegisterXML> registers = new ArrayList<RegisterXML>();
	                    registers = parser.parse(is);
	                    int status = registers.get(0).getStatus();
	                    System.out.println("Uid:"+registers.get(0).getUid());
	                    System.out.println("Status:"+registers.get(0).getStatus());
	                    System.out.println("Token:"+registers.get(0).getToken());
	                    System.out.println("Uniqname:"+registers.get(0).getUniqname());
	                    switch(status){
		                    case 0: 
		        	        {  
		        	        	Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
		        	        	boolean loggedIn = login();
		        	        	if(loggedIn){
		        	    			Intent intent = new Intent(Register.this,SohuKan.class);
		        	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        	    			Bundle bundle = new Bundle();
		        	    			bundle.putString("userid", mail.getText().toString());
		        	    			intent.putExtras(bundle);
		        	    			startActivity(intent);
		        	    			finish();
		        	    		}else{
		        	    			Toast.makeText(Register.this, "注册失败，请稍后再试",Toast.LENGTH_LONG).show();
		        	    		}
		        	            break;  
		        	        }
		                    case 1: 
		        	        {  
		        	        	Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 2: 
		        	        {  
		        	        	Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 3: 
		        	        {  
		        	        	Toast.makeText(this, "非法userid", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 4: 
		        	        {  
		        	        	Toast.makeText(this, "userid已经存在", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 5: 
		        	        {  
		        	        	Toast.makeText(this, "进入黑名单", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 6: 
		        	        {  
		        	        	Toast.makeText(this, "创建用户失败", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 7: 
		        	        {  
		        	        	Toast.makeText(this, "手机已经被绑定（wap专用）", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 8: 
		        	        {  
		        	        	Toast.makeText(this, "非法用户名uniqname", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 9: 
		        	        {  
		        	        	Toast.makeText(this, "用户名uniquename已存在 ", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 10: 
		        	        {  
		        	        	Toast.makeText(this, "调用超限（一个appid5分钟调用超过了700次）", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
		                    case 11: 
		        	        {  
		        	        	Toast.makeText(this, "不能注册vip.sohu.com的账号", Toast.LENGTH_SHORT).show();
		        	            break;  
		        	        }
	                    }
	              } catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println(e1+"UnsupportedEncodingException e1有这样的错1111111111111111111111111");
	              } catch (Exception e) {  
						System.out.println(e+"方法sync");
						e.printStackTrace();
	              } 
//	              httpConn.disconnect();
              }
        }catch(Exception ex){
        	ex.printStackTrace();
        }
		return result;
	}
	
	public boolean login(){
		boolean loggedIn=false;
		String urlStr = "https://passport.sohu.com/mobile/gettoken";
		try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");
            System.out.println("url connection了");
            OutputStreamWriter out = new OutputStreamWriter(con
                    .getOutputStream());    
            String xmlInfo = getLoginXmlInfo();
            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            String line = "";
            String result = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                result += line;
                System.out.println("line"+line);
            }
            
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
            	String user_id = mail.getText().toString();
            	String gid = getGid();
                System.out.println("到这了"+gid);
                RAPI rapi = new RAPI(Register.this,"",user_id);
                String rtns = rapi.getAccessToken(user_id, gid, token);
                System.out.println("过了服务器验证");
                String access_token = "";
                if(rtns.contains("|")){
                	access_token = rtns.split("\\|")[1];
                }
                if(!"".equals(access_token) && access_token!=null){
                	DBHelper db = new DBHelper(Register.this);
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

package com.sohu.wuhan;

import java.util.Hashtable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sohu.wuhan.R;

public class ReaderActivity extends Activity {
    /** Called when the activity is first created. */
	
	static TextView tv = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		// test event
		{
			tv =  (TextView)findViewById(R.id.text);
			
			Button btn = (Button)findViewById(R.id.btn_login);
			btn.setOnClickListener(new Button.OnClickListener() {
				
				public void onClick(View v) {
					tv.setText("");
					Button button = (Button)v;
					String token = "2ca5b4f8e98702433f4f5aa4104844a6bf3b6c38";
					String url = "http://www.baidu.com";
					
					IReadable ir = HttpRead.instance();
					
					if (ir.init(token)) {
//						constant.Error e = ir.createArticle(url);
//						if (e == constant.Error.OK) { 
//							button.setText("Every thing is ok.");
//						}
						
						String returns = ir.probeArticle();
						if (null != returns) {
							tv.setText(returns);
						}
					}
				}
			});
			
			btn = (Button)findViewById(R.id.btn_login1);
			btn.setOnClickListener(new Button.OnClickListener() {
				
				public void onClick(View v) {
					tv.setText("");
					Button button = (Button)v;
					String token = "2ca5b4f8e98702433f4f5aa4104844a6bf3b6c38";
					String url = "http://www.baidu.com";
					
					IReadable ir = HttpRead.instance();
					
					if (ir.init(token)) {
						ir.asyncProbeArticle(new ReadHandler(tv));
					}
				}
			});
			
			
			btn = (Button)findViewById(R.id.btn_login2);
			btn.setOnClickListener(new Button.OnClickListener() {
				
				public void onClick(View v) {
					tv.setText("");
					Button button = (Button)v;
					String token = "2ca5b4f8e98702433f4f5aa4104844a6bf3b6c38";
					String url = "key_article_instance_3_738ddf35b3a85a7a6ba7b232bd3d5f1e4d284ad1_v1";
					
					IReadable ir = HttpRead.instance();
					
					if (ir.init(token)) {
						ir.asyncReadArticle(url, new ReadHandler(tv));
					}
				}
			});
			
			btn = (Button)findViewById(R.id.btn_login3);
			btn.setOnClickListener(new Button.OnClickListener() {
				
				public void onClick(View v) {
					tv.setText("");
					Button button = (Button)v;
					String token = "2ca5b4f8e98702433f4f5aa4104844a6bf3b6c38";
					String url = "key_article_instance_3_738ddf35b3a85a7a6ba7b232bd3d5f1e4d284ad1_v1";
					
					IReadable ir = HttpRead.instance();
					
					if (ir.init(token)) {
						Hashtable<String, String> tbl = new Hashtable<String, String>();
						tbl.put("is_read", "1");
						ir.asyncUpdateArticle(url, tbl, new ReadHandler(tv));
					}
				}
			});
			
			btn = (Button)findViewById(R.id.btn_login4);
			btn.setOnClickListener(new Button.OnClickListener() {
				
				public void onClick(View v) {
					tv.setText("");
					Button button = (Button)v;
					String token = "2ca5b4f8e98702433f4f5aa4104844a6bf3b6c38";
					String url = "key_article_instance_3_738ddf35b3a85a7a6ba7b232bd3d5f1e4d284ad1_v1";
					
					IReadable ir = HttpRead.instance();
					
					if (ir.init(token)) {
						ir.asyncDeleteArticle(url, new ReadHandler(tv));
					}
				}
			});
		}
    }
}

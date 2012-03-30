package com.sohu.kan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

public class Collection extends Activity {
	
	private RelativeLayout phone_collection;
	private RelativeLayout browser_collection;
	
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.collection);
	     
	     ensureUi();
	}
	
	public void ensureUi(){
		phone_collection = (RelativeLayout)findViewById(R.id.phone_collection);
		phone_collection.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(Collection.this,PhoneCollection.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
			}
		});
		
		browser_collection = (RelativeLayout)findViewById(R.id.browser_collection);
		browser_collection.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(Collection.this,BrowserCollection.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
			}
		});
	}
}

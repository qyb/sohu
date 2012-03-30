package com.sohu.kan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sohu.utils.RAPI;

public class FeedBack extends Activity {
	
	private EditText feedback_content;
	private Button feedback_submit;
	
	private Global global;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback);
        
        global = (Global)getApplication();
        
        feedback_content = (EditText)findViewById(R.id.feedback_content);
        
        feedback_submit = (Button)findViewById(R.id.feedback_submit);
        feedback_submit.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if("".equals(feedback_content.getText().toString()) || feedback_content.getText()==null){
        			Toast.makeText(FeedBack.this, "输入不能为空", Toast.LENGTH_SHORT).show();
        		}else{
	        		//提交到服务器
	        		RAPI rapi = new RAPI(FeedBack.this,global.getAccessToken(),global.getUserId());
	        		rapi.asyncFeedback(feedback_content.getText().toString(), "");
	        		Intent intent = new Intent(FeedBack.this,Setting.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			startActivity(intent);
	    			Toast.makeText(FeedBack.this, "感谢你的宝贵意见，我们会及时跟进", Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        
    }

}

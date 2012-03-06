package com.sohu.kan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.look.R;

public class Category extends Activity {
	
	private Button addInput;
	private Button removeInput;
	private Button submit;
	private EditText categoryName;
	private LinearLayout category_management; 
	private TextView empty;
	
	private EditText test;
	
	private Object[] b = new Object[6];
	
	private int addCategoryNum = 2;
	
	private DBHelper db;
	
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.category);
	     db = new DBHelper(this);
	     ensureUi();
	}
	
	public void ensureUi(){
		category_management = (LinearLayout)findViewById(R.id.category_management);
		
		empty = (TextView)findViewById(R.id.empty);
		
		addInput = (Button)findViewById(R.id.add_input);
		addInput.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(addCategoryNum<6){
					test = new EditText(Category.this);
					test.setHint("输入分类标题"+addCategoryNum);
					category_management.addView(test);
					b[addCategoryNum] = test;
					addCategoryNum++;
				}else{
					Toast.makeText(Category.this, "一次最多添加五个分类", Toast.LENGTH_SHORT).show(); 
				}
			}
		});
		
		categoryName = (EditText)findViewById(R.id.category_name);
		b[1] = categoryName;
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				for(int i=1;i<addCategoryNum;i++){
					EditText edittext = (EditText)b[i];
					if(!"".equals(edittext.getText().toString())){//插入分类表
						System.out.println("第"+i+"个edittext的值:"+edittext.getText());
						db.addCategory(edittext.getText().toString());
						Intent intent = new Intent(Category.this,CategoryList.class);
		    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    			startActivity(intent);
					}
					
				}
				empty.setVisibility(View.GONE);
			}
		});
		
		removeInput = (Button)findViewById(R.id.remove_input);
		removeInput.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(addCategoryNum>0){
					category_management.removeView((View)b[addCategoryNum-1]);
					if(addCategoryNum>2){
						addCategoryNum--;
					}
				}else{
					Toast.makeText(Category.this, "至少添加一个分类", Toast.LENGTH_SHORT).show(); 
				}
			}
		});
	}
}

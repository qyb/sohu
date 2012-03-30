package com.sohu.kan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.RAPI;

public class Category extends Activity {
	
	private ImageView addInput;
	private ImageView removeInput;
	private Button submit;
	private EditText categoryName;
	private LinearLayout category_management; 
	private RelativeLayout category_guide;
	private TextView empty;
	
	private EditText test;
	
	private Object[] b = new Object[6];
	
	private int addCategoryNum = 2;
	
	private DBHelper db;
	
	private Global global; 
	
	private String access_token;
	private String userid;
	
	private String empty_category;
	
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.category);
	     getWindow().setSoftInputMode(
	                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	     global = (Global)getApplication();
	     access_token = global.getAccessToken();
	     userid = global.getUserId();
	     Bundle bundle = this.getIntent().getExtras();
	     if(bundle!=null){
	    	 empty_category = bundle.getString("empty");
	     }
	     ensureUi();
	}
	
	public void ensureUi(){
		empty = (TextView)findViewById(R.id.empty);
		if("1".equals(empty_category))
			empty.setVisibility(View.VISIBLE);
		category_management = (LinearLayout)findViewById(R.id.category_management);
		
		category_guide = (RelativeLayout)findViewById(R.id.category_guide);
		if("1".equals(global.getCategoryGuide()))
			category_guide.setVisibility(View.GONE);
		category_guide.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DBHelper db = new DBHelper(Category.this);
    			db.setGuideRead(global.getUserId(), "category_guide");
    			db.close();
    			global.setCategoryGuide("1");
				category_guide.setVisibility(View.GONE);
			}
		});
		
		addInput = (ImageView)findViewById(R.id.add_input);
		addInput.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				removeInput.setVisibility(View.VISIBLE);
				if(addCategoryNum<6){
					test = new EditText(Category.this);
					test.setHint("输入分类标题"+addCategoryNum);
					test.setHintTextColor(getResources().getColor(R.drawable.font_gray));
					test.setWidth(11);
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
				boolean flag = true;
				boolean same_name = false;
				boolean input_empty = false;
				boolean category_length = true;
				for(int j=1;j<addCategoryNum;j++){
					EditText edittext = (EditText)b[j];
					if(edittext.getText().toString().startsWith("_") || edittext.getText().toString().contains(",")){
						flag = false;
					}
					if(edittext.getText().toString().length()<0 || edittext.getText().toString().length()>20){
						category_length = false;
					}
					for(int k=j+1;k<addCategoryNum;k++){
						EditText edittext_bak = (EditText)b[k];
						if(edittext.getText().toString().equals(edittext_bak.getText().toString())){
							same_name = true;
						}
					}
				}
				if(flag){
					if(!same_name){
						if(category_length){
							for(int i=1;i<addCategoryNum;i++){
								EditText edittext = (EditText)b[i];
								if(!"".equals(edittext.getText().toString())){//插入分类表
									System.out.println("第"+i+"个edittext的值:"+edittext.getText());
									db = new DBHelper(Category.this);
									db.addCategory(edittext.getText().toString().trim().replace("'", ""),userid);
									db.close();
									RAPI rapi = new RAPI(Category.this,access_token,userid);
									rapi.asyncCreateFolder(edittext.getText().toString());
									input_empty = true;
								}
							}
							if(input_empty){
								Intent intent = new Intent(Category.this,CategoryList.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							}else{
								Toast.makeText(Category.this, "输入不能为空", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(Category.this, "分类名必须在0到20个字符内", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(Category.this, "不允许有同名分类", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(Category.this, "分类名称不能以下划线开头并且不能出现逗号", Toast.LENGTH_SHORT).show(); 
				}
				empty.setVisibility(View.INVISIBLE);
			}
		});
		
		removeInput = (ImageView)findViewById(R.id.remove_input);
		removeInput.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(addCategoryNum>0){
					category_management.removeView((View)b[addCategoryNum-1]);
					if(addCategoryNum>2){
						addCategoryNum--;
					}
					if(addCategoryNum==2)
						removeInput.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}

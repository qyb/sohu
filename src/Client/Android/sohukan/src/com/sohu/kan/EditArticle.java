package com.sohu.kan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.look.R;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.ArticleList;

public class EditArticle extends Activity {
	
	private TextView page_title;
	private TextView title_name;
	private EditText title_text;
	private TextView category_name;
	private TextView no_category;
	private TextView go_to_create_category;
	private Button submit;
	private Button add_category;
	
	private List<ArticleList> articleList; 
	
	private DBHelper db;
	
	private List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
	
	private List categoryNameList = new ArrayList();
	
	private Spinner spinner;
	
	private ArrayAdapter<String> adapter;
	
	private LinearLayout empty_category;
	private LinearLayout category_list;
	
	private String type;
	
//	private String m[];
	
	private int category_id;
	private int position;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_article);
        
        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getString("type");
        articleList = (ArrayList<ArticleList>) bundle.getSerializable("article");
        db = new DBHelper(this);
        category_id = getDefaultCategory(articleList.get(0).getKey());
        getAllCategory();
        ensureUi();
	}
	
	public int getDefaultCategory(String key){
		Cursor cur = db.getCategoryByKey(key);
		cur.moveToFirst();
		int id = cur.getInt(0);
		cur.close();
		return id;
	}
	
	public List<Map<String, Object>> getAllCategory(){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Cursor cur = db.loadAllCategory();
		if(cur.moveToFirst()){
			do{
				System.out.println(cur.getInt(0)+" : "+cur.getString(1)+" : "+cur.getString(2));
				map.put("id", cur.getInt(0));
				map.put("category", cur.getString(1));
				map.put("time", cur.getString(2));
				
				categoryNameList.add(cur.getString(1));
				categoryList.add(map);
			
				map = new HashMap<String, Object>();
			}
			while(cur.moveToNext());
		}
		
		cur.close();
//		db.close();
		return categoryList;
	}
	
	public void ensureUi(){
		page_title = (TextView)findViewById(R.id.page_title);
		
		title_name = (TextView)findViewById(R.id.title_name);
		
		title_text = (EditText)findViewById(R.id.title_text);
		title_text.setText(articleList.get(0).getTitle());
		
		category_name = (TextView)findViewById(R.id.category_name);
		no_category = (TextView)findViewById(R.id.no_category);
		
		add_category = (Button)findViewById(R.id.add_category);
		add_category.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				final EditText updateCategory = new EditText(EditArticle.this);
				new AlertDialog.Builder(EditArticle.this).setTitle("请输入分类名").setIcon(
     				     android.R.drawable.ic_dialog_info).setView(
     				    		updateCategory).setPositiveButton("确定", new DialogInterface.OnClickListener() {
     				    			
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                           int arg1) {
                                       // TODO Auto-generated method stub
                                       Toast.makeText(EditArticle.this, updateCategory.getText(), Toast.LENGTH_SHORT).show();
                                       db.addCategory(updateCategory.getText().toString());
                                       categoryList = new ArrayList<Map<String, Object>>();
                                       categoryNameList = new ArrayList();
                                       getAllCategory();
                                       position = categoryList.size()-1;
                                       adapter = new SimpleArrayAdapter();
                           			   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                           			   spinner.setAdapter(adapter);
                                       spinner.setSelection(position,true);
                                   }
                               }
                               )
     				     .setNegativeButton("取消", null).create().show();
			}
		});
		
		go_to_create_category = (TextView)findViewById(R.id.go_to_create_category);
		go_to_create_category.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(EditArticle.this,Category.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
			}
		});
		
		empty_category = (LinearLayout)findViewById(R.id.empty_category);
		category_list = (LinearLayout)findViewById(R.id.category_list);
		if(categoryList.size()>0){
			
//			m = new String[categoryList.size()];
			for(int i=0;i<categoryList.size();i++){
//				m[i] = categoryList.get(i).get("category").toString();
				if(Integer.parseInt(categoryList.get(i).get("id").toString()) == category_id)
					position = i;
			}
			spinner = (Spinner) findViewById(R.id.category_spinner);
			//将可选内容与ArrayAdapter连接起来
//			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
			
			//设置下拉列表的风格
//			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			  
			adapter = new SimpleArrayAdapter();
			
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			//将adapter 添加到spinner中
			spinner.setAdapter(adapter);
			
			spinner.setSelection(position,true);
			     
			//添加事件Spinner事件监听 
			spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
			
			//设置默认值
			spinner.setVisibility(View.VISIBLE);
			
			category_list.setVisibility(View.VISIBLE);
			empty_category.setVisibility(View.GONE);
		}
		
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				for(int i=0;i<categoryList.size();i++){
//					m[i] = categoryList.get(i).get("category").toString();
					if(Integer.parseInt(categoryList.get(i).get("id").toString()) == category_id)
						position = i;
				}
				if(categoryList.size()!=0){
					db.updateArticle(Integer.parseInt(categoryList.get(position).get("id").toString()),title_text.getText().toString(),articleList.get(0).getKey());
				}else{
					db.updateArticle(0,title_text.getText().toString(),articleList.get(0).getKey());
				}
				Hashtable ht = new Hashtable();
				if(categoryList.size()!=0){
					ht.put("category_id", categoryList.get(position).get("id").toString());
				}
				ht.put("title", title_text.getText().toString());
				RAPI rapi = new RAPI(EditArticle.this);
				rapi.updateArticle(articleList.get(0).getKey(), ht);
				
				
				Intent intent = new Intent(EditArticle.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			if(categoryList.size()!=0){
    				bundle.putString("category_id", categoryList.get(position).get("id").toString());
    				bundle.putString("category_name", categoryList.get(position).get("category").toString());
				}else{
					bundle.putString("type", type);
				}
    			intent.putExtras(bundle);
    			startActivity(intent);
			}
		});
	}
	//使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener{
	
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
		            long arg3) {
			category_id = Integer.parseInt(categoryList.get(pos).get("id").toString());
		}
		
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	
	class SimpleArrayAdapter extends ArrayAdapter{
		
		public SimpleArrayAdapter() {
			super(EditArticle.this, android.R.layout.simple_spinner_item, categoryNameList);
	    }
		
		@Override
		public int getCount() {
		    return categoryNameList.size();
		} 
	     
	    public View getView(int pos, View convertView, ViewGroup parent)
	    {
		     LayoutInflater inflater=getLayoutInflater();
	//	     LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		     View view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		     setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		     TextView category_name = (TextView) view.findViewById(android.R.id.text1);
		     category_name.setText(categoryList.get(pos).get("category").toString());
		     return view;
	    }
	}
}

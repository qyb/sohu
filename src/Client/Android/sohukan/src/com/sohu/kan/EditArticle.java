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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.Bookmark;

public class EditArticle extends Activity {
	
	private TextView page_title;
	private TextView title_name;
	private EditText title_text;
	private TextView category_name;
	private TextView no_category;
	private TextView go_to_create_category;
	private Button submit;
	private ImageView add_category;
	
	private List<Bookmark> bookmarkList; 
	
	private DBHelper db;
	
	private List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
	
	private List categoryNameList = new ArrayList();
	
	private Spinner spinner;
	
	private ArrayAdapter<String> adapter;
	
	private RelativeLayout empty_category;
	private RelativeLayout category_list;
	
	private String type;
	private String latest;
	private String folder_name;
	
//	private String m[];
	
	private String my_category_name;
	private int position;
	
	private Global global; 
	
	private RAPI rapi;
	
	private String access_token;
	private String userid;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_article);
        global = (Global)getApplication();
        access_token = global.getAccessToken();
        userid = global.getUserId();
        rapi = new RAPI(this,access_token,userid);
        
        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getString("type");
        latest = bundle.getString("latest");
        folder_name = bundle.getString("folder_name");
        
        bookmarkList = (ArrayList<Bookmark>) bundle.getSerializable("bookmark");
        
        my_category_name = getDefaultCategory(bookmarkList.get(0).getId());
        getAllCategory();
        ensureUi();
	}
	
	public String getDefaultCategory(int id){
		db = new DBHelper(EditArticle.this);
		Cursor cur = db.getCategoryById(id+"");
		cur.moveToFirst();
		String categoryName = cur.getString(0);
		cur.close();
		db.close();
		return categoryName;
	}
	
	public List<Map<String, Object>> getAllCategory(){
//		categoryList = rapi.listFolder();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("category", "选择分类");
		map.put("id", "");
		map.put("time", "");
		categoryList.add(map);
		map = new HashMap<String, Object>();
		categoryNameList.add("选择分类");
		db = new DBHelper(this);
		Cursor cur = db.loadAllCategory(userid);
		if(cur.moveToFirst()){
			do{
				map.put("category", cur.getString(1));
				map.put("id", cur.getInt(0));
				map.put("time", cur.getString(2));
				categoryNameList.add(cur.getString(1));
				categoryList.add(map);
			
				map = new HashMap<String, Object>();
			}
			while(cur.moveToNext());
		}
		
		cur.close();
		db.close();
		return categoryList;
	}
	
	public void ensureUi(){
		page_title = (TextView)findViewById(R.id.page_title);
		
		title_name = (TextView)findViewById(R.id.title_name);
		
		title_text = (EditText)findViewById(R.id.title_text);
		title_text.setText(bookmarkList.get(0).getTitle());
		
		category_name = (TextView)findViewById(R.id.category_name);
		no_category = (TextView)findViewById(R.id.no_category);
		
		add_category = (ImageView)findViewById(R.id.add_category);
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
                                	   boolean flag = true;
                                	   if(updateCategory.getText().toString().trim().length()<0 || updateCategory.getText().toString().trim().length()>20){
                       					   Toast.makeText(EditArticle.this, "分类名必须在0到20个字符内", Toast.LENGTH_SHORT).show();
                       					   flag = false;
                       				   }
                       				   if("".equals(updateCategory.getText().toString().trim())){
                       					   Toast.makeText(EditArticle.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                       					   flag = false;
                       				   }
                       				   if(updateCategory.getText().toString().startsWith("_") || updateCategory.getText().toString().contains(",")){
                       					   Toast.makeText(EditArticle.this, "分类名称不能以下划线开头并且不能出现逗号", Toast.LENGTH_SHORT).show();
                       					   flag = false;
                       				   }
                       				   for(int p=0;p<categoryNameList.size();p++){
                       					   if(categoryNameList.get(p).equals(updateCategory.getText().toString())){
                       						   Toast.makeText(EditArticle.this, "已存在分类"+updateCategory.getText().toString(), Toast.LENGTH_SHORT).show();
                       						   flag = false;
                       					   }
                       				   }
                       				   if(flag){
	                                       db = new DBHelper(EditArticle.this);
	                                       db.addCategory(updateCategory.getText().toString().trim().replace("'", ""),userid);
	                                       db.close();
	                                       rapi.asyncCreateFolder(updateCategory.getText().toString());
	                                       
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
		
		empty_category = (RelativeLayout)findViewById(R.id.empty_category);
		category_list = (RelativeLayout)findViewById(R.id.category_list);
		if(categoryList.size()>1){
			
//			m = new String[categoryList.size()];
			for(int i=1;i<categoryList.size();i++){
//				m[i] = categoryList.get(i).get("category").toString();
				System.out.println(categoryList.get(i).get("category").toString()+"=?"+my_category_name);
				if(categoryList.get(i).get("category").toString().equals(my_category_name))
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
				for(int i=1;i<categoryList.size();i++){
//					m[i] = categoryList.get(i).get("category").toString();
					if(categoryList.get(i).get("category").toString().equals(my_category_name))
						position = i;
				}
				db = new DBHelper(EditArticle.this);
				if(categoryList.size()!=1){
					db.updateBookmark(categoryList.get(position).get("category").toString(),title_text.getText().toString(),bookmarkList.get(0).getId()+"");
				}else{
					db.updateBookmark("",title_text.getText().toString(),bookmarkList.get(0).getId()+"");
				}
				db.close();
				Hashtable ht = new Hashtable();
				if(categoryList.size()!=1){
					ht.put("category_name", categoryList.get(position).get("category").toString());
				}else{
					ht.put("category_name", "");
				}
				ht.put("title", title_text.getText().toString());
				rapi.updateBookmark(bookmarkList.get(0).getId(), ht);
				
				
				Intent intent = new Intent();
				
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
				if(!"".equals(type) && type!=null){
					intent.setClass(EditArticle.this,ReadList.class);
					bundle.putString("type", type);
		    	}else if(!"".equals(folder_name) && folder_name!=null){
		    		intent.setClass(EditArticle.this,ReadList.class);
		    		bundle.putString("folder_name", folder_name);
		    	}else if(!"".equals(latest) && latest!=null){
		    		intent.setClass(EditArticle.this,ReadList.class);
		    		bundle.putString("latest", latest);
		    	}else{
		    		intent.setClass(EditArticle.this,Read.class);
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
			my_category_name = categoryList.get(pos).get("category").toString();
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

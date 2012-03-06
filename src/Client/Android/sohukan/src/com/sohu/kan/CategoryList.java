package com.sohu.kan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.look.R;

public class CategoryList extends Activity {
	
private List<Map<String, Object>> categoryList = new ArrayList<Map<String, Object>>();
	
	private ListView listView;
	private SimpleAdapter sa = null;
	
	private DBHelper db;
	
	private Button addCategory;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.categorylist);
        
        addCategory = (Button)findViewById(R.id.add_category);
        addCategory.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent intent = new Intent(CategoryList.this,Category.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
        	}
        });
        
        listView = (ListView)findViewById(R.id.categorylist);
		
		sa = new SimpleAdapter(this,getData(),R.layout.categorylist_item,
	        	    new String[]{"category"},
	        	    new int[]{R.id.category});
		 
		listView.setAdapter(sa);
		 
		listView.setOnItemClickListener(new OnItemClickListener()  {  
	  
            @Override  
            public void onItemClick(AdapterView<?> parent,  
                    View view, int position, long id)  
            {  
        		
            	Intent intent = new Intent(CategoryList.this,ReadList.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			bundle.putString("category_id", categoryList.get(position).get("id").toString());
    			bundle.putString("category_name", categoryList.get(position).get("category").toString());
    			intent.putExtras(bundle);
    			startActivity(intent);
//	                Toast.makeText(Category.this, "位置"+position, Toast.LENGTH_SHORT).show();  
                  
            }  
	    });  
		 
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
		 public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
//				 Toast.makeText(Category.this, "长按位置"+position, Toast.LENGTH_SHORT).show();  
			 
			 final int pos = position;
			 new AlertDialog.Builder(CategoryList.this)
				.setTitle("编辑分类")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setItems(new String[] { "编辑", "删除" }, 
					new DialogInterface.OnClickListener() {
				      	public void onClick(DialogInterface dialog, int which) {
				      		final EditText updateCategory = new EditText(CategoryList.this);
    		      			updateCategory.setText(categoryList.get(pos).get("category").toString());
    		      			final int category_id = Integer.parseInt(categoryList.get(pos).get("id").toString());
				      		switch(which){
		    		      		case 0:
			    		      		{
			    		      			Toast.makeText(CategoryList.this, "编辑", Toast.LENGTH_SHORT).show(); 
			    		      			new AlertDialog.Builder(CategoryList.this).setTitle("请输入分类名").setIcon(
			    		      				     android.R.drawable.ic_dialog_info).setView(
			    		      				    		updateCategory).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			    		                                    @Override
			    		                                    public void onClick(DialogInterface arg0,
			    		                                            int arg1) {
			    		                                        // TODO Auto-generated method stub
			    		                                        Toast.makeText(CategoryList.this, updateCategory.getText(), Toast.LENGTH_SHORT).show();
			    		                                        db.updateCategoryName(category_id, updateCategory.getText().toString());
			    		                                        categoryList.get(pos).put("category",updateCategory.getText());
			    		                                        sa.notifyDataSetChanged();
			    		                                    }
			    		                                }
			    		                                )
			    		      				     .setNegativeButton("取消", null).show();
			    		      			break; 
			    		      		}
		    		      		case 1:
			    		      		{
			    		      			Toast.makeText(CategoryList.this, "删除", Toast.LENGTH_SHORT).show();
			    		      			AlertDialog.Builder builder = new AlertDialog.Builder(CategoryList.this);
			    		    	    	builder.setMessage("确认删除?")
			    		    	    	       .setCancelable(false)
			    		    	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			    		    	    	           public void onClick(DialogInterface dialog, int id) {
			    		    	    	        	   db.deleteCategory(category_id);
	    		                                       categoryList.remove(pos);
	    		                                       sa.notifyDataSetChanged();
			    		    	    	           }
			    		    	    	       })
			    		    	    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
			    		    	    	           public void onClick(DialogInterface dialog, int id) {
			    		    	    	                dialog.cancel();
			    		    	    	           }
			    		    	    	       });
			    		    	    	builder.create().show();
			    		      			break; 
			    		      		}
				      		}
				      		dialog.dismiss();
				      	}
				    })
				.setNegativeButton("取消", null).show();
			 return false;
		 	}
		});
	}
	
	public List<Map<String, Object>> getData(){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		db = new DBHelper(this);
		Cursor cur = db.loadAllCategory();
		if(cur.moveToFirst()){
			do{
				map.put("id", cur.getInt(0));
				map.put("category", cur.getString(1));
				map.put("time", cur.getString(2));
				categoryList.add(map);
			
				map = new HashMap<String, Object>();
			}
			while(cur.moveToNext());
		}
		cur.close();
//		db.close();
		return categoryList;
	}

}

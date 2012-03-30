package com.sohu.kan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.utils.RAPI;
import com.sohu.xml.model.Bookmark;

public class ReadList extends Activity {
	
	private TextView list_title; 
    private ArrayList<Integer> idList = new ArrayList<Integer>();  
    private ListViewAdapter myAdapter; 
	private ListView listView;
	private boolean visflag = false; 
    private ImageView refresh;
    private TextView emptylist;
    private Button guide;
    private EditText filter;
    private ImageView sort;
    
    private RelativeLayout confirm;
    private RelativeLayout set_articles_read;
    private RelativeLayout unreadlist_guide;
    private RelativeLayout empty_layout;
    private RelativeLayout relative_list;
    
    private Button cancel;
    private Button cancel_set_read;
    private Button delete;
    private TextView sign_num;
    private TextView sign_num_read;
     
    private List<Bookmark> bookmarkList; 
    private List<Bookmark> bookmarkListBak;
	
	private String type;
	private String folder_name;
	private String latest;
	
	boolean fileExist;
	
	private DBHelper db;
	
	private RAPI rapi;
	
	private Global global;
	private String userid;
	
	private ProgressDialog dialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        global = (Global)getApplication();
        userid = global.getUserId();
        bookmarkListBak = new ArrayList<Bookmark>();
        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getString("type");
        
        folder_name = bundle.getString("folder_name");
        latest = bundle.getString("latest");
        rapi = new RAPI(ReadList.this,global.getAccessToken(),userid);
        
        getData();
        
        // 隐藏软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //网址排序算法需要一个备份list
        bookmarkListBak = bookmarkList;
        
        setContentView(R.layout.readlist);
        ensureUi();
        empty_layout = (RelativeLayout)findViewById(R.id.empty_layout);
        relative_list = (RelativeLayout)findViewById(R.id.relative_list);
        listView = (ListView)findViewById(R.id.list);
        listView.setDivider(null);
		myAdapter = new ListViewAdapter(ReadList.this);
		listView.setAdapter(myAdapter);
        listView.setScrollBarStyle(1);
        
        
        listView.setOnItemClickListener(new OnItemClickListener()  
        {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent,  
                    View view, int position, long id)  
            {  
            	Intent intent = new Intent(ReadList.this,Read.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			Bundle bundle = new Bundle();
    			ArrayList<Bookmark> bookmark = new ArrayList<Bookmark>();
    			bookmark.add(bookmarkList.get(position));
    			bundle.putSerializable("bookmark", bookmark);
    			bundle.putString("type", type);
    			bundle.putString("latest", latest);
    			bundle.putString("folder_name", folder_name);
    			intent.putExtras(bundle);
    			startActivity(intent);
            }  
        });  
        //长按
        listView.setOnItemLongClickListener(new OnItemLongClickListener()
        {
        	public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
        		longClickItem(position);
        		return false;
        	}
        });
        if(bookmarkList.size()>0){
        	 
	        empty_layout.setVisibility(View.GONE);
	        if("0".equals(type)){
	        	rapi.downloadResource(global.getImgFlag(), bookmarkList, global.getUserId(), listView);
	    	}
        }else{
        	relative_list.setVisibility(View.GONE);
        }
        
        //写入xml
//        String xml = parser.serialize(books);  //序列化
//        FileOutputStream fos = openFileOutput("books.xml", Context.MODE_PRIVATE);  
//        fos.write(xml.getBytes("UTF-8"));  
    }
    
    public void getData(){
    	bookmarkList = new ArrayList<Bookmark>();
    	db = new DBHelper(this);
    	Cursor cur = null;
    	if(!"".equals(type) && type!=null){
    		cur = db.loadAllBookmark(type, userid);
    	}else if(!"".equals(folder_name) && folder_name!=null){
    		cur = db.loadBookmarkByFolderName(folder_name, userid);
    	}else if(!"".equals(latest) && latest!=null){
    		cur = db.loadBookmarkByTime(userid);
    	}
    	Bookmark bookmark;
    	if(cur.moveToFirst()){
    		do{
    			bookmark = new Bookmark();
    			bookmark.setId(cur.getInt(0));
    			bookmark.setUrl(cur.getString(1));
    			bookmark.setTitle(cur.getString(2));
    			bookmark.setDescription(cur.getString(3));
    			bookmark.setIsStar(cur.getInt(4));
    			bookmark.setCreateTime(cur.getString(5));
    			bookmark.setReadTime(cur.getString(6));
    			bookmark.setFolderName(cur.getString(7));
    			bookmark.setReadProgress(cur.getString(8));
    			bookmark.setVersion(cur.getInt(9));
    			bookmark.setTextVersion(cur.getInt(10));
    			bookmark.setIsReady(cur.getInt(11));
    			bookmark.setIsDownload(cur.getInt(12));
    			if(!"".equals(latest) && latest!=null){
    	    		if(bookmark.getReadTime()!=null && !"null".equals(bookmark.getReadTime())){
    	    			bookmarkList.add(bookmark);
    	    			System.out.println(bookmark.getTitle()+"这个不是null+++++");
    	    		}else{
    	    			System.out.println(bookmark.getTitle()+"这个是null------");
    	    		}
    	    	}else{
    	    		bookmarkList.add(bookmark);
    	    	}
    			bookmark = null;
    		}
    		while(cur.moveToNext());
    	}
    	System.out.println("getData中bookmarkList个数"+bookmarkList.size());
    	cur.close();
    	db.close();
    }
    
    public void longClickItem(int position){
    	final int pos = position;
    	
    	String[] items;
    	//区分未读与其他类别的长按事件选项
    	if("0".equals(type)){
    		//未读
    		items = new String[] { "设为已读", "编辑", "分享", "删除" };
    	}else{
    		items = new String[] { "编辑", "分享", "删除" };
    	}
    	new AlertDialog.Builder(this)
		.setTitle(bookmarkList.get(pos).getTitle())
		.setIcon(android.R.drawable.ic_dialog_info)
		.setItems(items, 
			new DialogInterface.OnClickListener() {
		      	public void onClick(DialogInterface dialog, int which) {
		      		if("0".equals(type)){
		      			switch(which){
	    		      		case 0:
	    		      		{
	    		      			Toast.makeText(ReadList.this, "操作成功", Toast.LENGTH_SHORT).show();
	    	        			//本地设已读
	    	        			DBHelper db = new DBHelper(ReadList.this);
	    	        			db.setBookmarkRead(bookmarkList.get(pos).getId());
	    	        			db.close();
	    	        			bookmarkList.remove(pos);
	    	        			myAdapter.notifyDataSetChanged();
	    		      			break; 
	    		      		}
	    		      		case 1:
	    		      		{
	    		      			editArticle(pos);
	    		      			break; 
	    		      		}
	    		      		case 2:
	    		      		{
	    		      			share();
	    		      			break; 
	    		      		}
	    		      		case 3:
	    		      		{
	    		      			deleteArticle(pos);
	    		      			break;
	    		      		}
			      		}
		        	}else{
			      		switch(which){
	    		      		case 0:
	    		      		{
	    		      			editArticle(pos);
	    		      			break; 
	    		      		}
	    		      		case 1:
	    		      		{
	    		      			share();
	    		      			break; 
	    		      		}
	    		      		case 2:
	    		      		{
	    		      			if(!"".equals(latest) && latest!=null){
	    		      				deleteFromLatest(pos);
	    		      			}else{
	    		      				deleteArticle(pos);
	    		      			}
	    		      			break; 
	    		      		}
			      		}
		        	}
		      		dialog.dismiss();
		      	}
		    })
		.setNegativeButton("取消", null).show();
    }
    
    public void editArticle(int pos){
    	Intent intent = new Intent(this,EditArticle.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		ArrayList<Bookmark> bookmark = new ArrayList<Bookmark>();
		bookmark.add(bookmarkList.get(pos));
		bundle.putSerializable("bookmark", bookmark);
		bundle.putString("type", type);
		bundle.putString("latest", latest);
		bundle.putString("folder_name", folder_name);
		intent.putExtras(bundle);
		startActivity(intent);
    }
    
    public void share(){
    	Intent intent=new Intent(Intent.ACTION_SEND);  
		intent.setType("text/plain");  
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
		intent.putExtra(Intent.EXTRA_TEXT, "搜狐随身看，与您分享！");  
		startActivity(Intent.createChooser(intent, getTitle())); 
    }
    
    public void deleteArticle(int position){
    	final int pos = position;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("确认删除?")
    	       .setCancelable(false)
    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
	                	//服务器删除
	                	rapi = new RAPI(ReadList.this,global.getAccessToken(),userid);
    	                rapi.asyncDeleteArticle(bookmarkList.get(pos).getId(), global.getImgFlag());
    	                bookmarkList.remove(pos);
    	                myAdapter.notifyDataSetChanged(); 
    	           }
    	       })
    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.create().show();
    }
    
    public void deleteFromLatest(int position){
    	final int pos = position;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("确认要将该文章从最近阅读中移除?")
    	       .setCancelable(false)
    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
	                	//本地和服务器清空阅读时间
	                	rapi = new RAPI(ReadList.this,global.getAccessToken(),userid);
    	                rapi.asyncDeleteArticleReadTime(bookmarkList.get(pos));
    	                bookmarkList.remove(pos);
    	                myAdapter.notifyDataSetChanged(); 
    	           }
    	       })
    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.create().show();
    }
    
    public void ensureUi(){
    	unreadlist_guide = (RelativeLayout)findViewById(R.id.unreadlist_guide);
    	if("1".equals(global.getUnreadlistGuide()))
    		unreadlist_guide.setVisibility(View.GONE);
    	unreadlist_guide.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			DBHelper db = new DBHelper(ReadList.this);
    			db.setGuideRead(global.getUserId(), "unreadlist_guide");
    			db.close();
    			global.setUnreadlistGuide("1");
    			unreadlist_guide.setVisibility(View.GONE);
    		}
    	});
    	
    	list_title = (TextView)findViewById(R.id.list_title);
    	list_title.setText("未读文章");
    	if("1".equals(type)){
    		list_title.setText("已读文章");
    	}else if("1".equals(latest)){
    		list_title.setText("最近阅读");
    	}else if(!"".equals(folder_name) && folder_name!=null){
    		list_title.setText(folder_name);
    	}
    	
    	refresh = (ImageView)findViewById(R.id.refresh);
    	refresh.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			new RefreshList().execute();
    		}
    	});
    	
    	filter = (EditText)findViewById(R.id.filter);
    	filter.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
           
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
            	bookmarkList = null;
            	bookmarkList = new ArrayList<Bookmark>();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // TODO Auto-generated method stub
                //关键是这里,监听输入的字符串,如果大于零,则可点击,enable.
            	if(!"".equals(s) && s!=null){
	            	for(int n=0;n<bookmarkListBak.size();n++){
		            	if(bookmarkListBak.get(n).getTitle().contains(s) || bookmarkListBak.get(n).getUrl().contains(s)){
		            		bookmarkList.add(bookmarkListBak.get(n));
		            	}
	            	}
            	}else{
            		bookmarkList = bookmarkListBak;
            	}
            	myAdapter.notifyDataSetChanged();
            }
        });
    	
//    	RelativeLayout list_bottom_tools = (RelativeLayout)findViewById(R.id.list_bottom_tools);
//    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_bottom_black);
//    	BitmapDrawable bd = new BitmapDrawable(bitmap);
//    	bd.setTileModeXY(TileMode.REPEAT , TileMode.CLAMP );
//    	bd.setDither(true);
//
//    	list_bottom_tools.setBackgroundDrawable(bd);
    	sort = (ImageView)findViewById(R.id.sort);
    	if(!"".equals(latest) && latest!=null){
    		sort.setVisibility(View.INVISIBLE);
    	}else{
    		sort.setOnClickListener(new OnClickListener(){
	    		public void onClick(View v){
	    			dialogSingle();
	    		}
	    	});
    	}
    	confirm = (RelativeLayout)findViewById(R.id.confirm);
    	
    	cancel = (Button)findViewById(R.id.cancel);
    	cancel.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			visflag = false;
    			confirm.setVisibility(View.GONE);
    			myAdapter.notifyDataSetChanged();
    		}
    	});
    	
    	delete = (Button)findViewById(R.id.delete);
    	delete.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			dialogConfirm();
    		}
    	});
    	
    	sign_num = (TextView)findViewById(R.id.sign_num);
    	
    	sign_num_read = (TextView)findViewById(R.id.sign_num_read);
    	sign_num_read.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			//批量为已读
    			Toast.makeText(ReadList.this, "操作成功", Toast.LENGTH_SHORT).show(); 
    			
    			ArrayList<Integer> strList = new ArrayList<Integer>();
                for(Integer in:idList)  
                {  
             	   strList.add(bookmarkList.get(in).getId());
             	   bookmarkList.get(in).setId(-1);
             	   bookmarkList.set(in, bookmarkList.get(in));  
                }  
                Iterator<Bookmark> it = bookmarkList.iterator();  
                while(it.hasNext())  
                {  
             	   Bookmark al = (Bookmark)it.next();  
                    if(al.getId()==-1)  
                    {  
                        it.remove();  
                    }  
                }  
                db = new DBHelper(ReadList.this);
                for(int d=0;d<strList.size();d++){
             	   //本地设为已读
             	   db.setBookmarkRead(strList.get(d));
             	   Bookmark bookmark = new Bookmark();
             	   bookmark.setId(strList.get(d));
             	   bookmark.setReadProgress(1+"");
             	   //服务器设为已读
             	   rapi = new RAPI(ReadList.this,global.getAccessToken(),userid);
	               rapi.asyncUpdateReadProgress(bookmark);
                }
                idList.clear();
                myAdapter.notifyDataSetChanged();  
                sign_num_read.setText("标记 0 条为已读");
    		}
    	});
    	
    	set_articles_read = (RelativeLayout)findViewById(R.id.set_articles_read);
    	cancel_set_read = (Button)findViewById(R.id.cancel_set_read);
    	cancel_set_read.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			visflag = false;
    			set_articles_read.setVisibility(View.GONE);
    			myAdapter.notifyDataSetChanged();
    		}
    	});
    	
    	emptylist = (TextView)findViewById(R.id.empty_list);
    	guide = (Button)findViewById(R.id.guide);
    	guide.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent(ReadList.this,Collection.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
    	});
    	if("0".equals(type)){
    		emptylist.setText("您还没有收藏任何网页到未读列表");
    		guide.setVisibility(View.VISIBLE);
    	}
    }
    
    //单选框
    protected void dialogSingle() {
    	new AlertDialog.Builder(this)
    		.setTitle("文章排序")
    		.setIcon(android.R.drawable.ic_dialog_info)
    		.setItems(new String[] { "最新收藏", "最早收藏", "网址排序" }, 
    			new DialogInterface.OnClickListener() {
    		      	public void onClick(DialogInterface dialog, int which) {
    		      		switch(which){
	    		      		case 0:
		    		      		{
		    		      			Toast.makeText(ReadList.this, "最新收藏", Toast.LENGTH_SHORT).show(); 
		    		      			bookmarkList = arrayListSort(false);
		    		      			myAdapter.notifyDataSetChanged();
		    		      			break; 
		    		      		}
	    		      		case 1:
		    		      		{
		    		      			Toast.makeText(ReadList.this, "最早收藏", Toast.LENGTH_SHORT).show();
		    		      			bookmarkList = arrayListSort(true);
		    		      			myAdapter.notifyDataSetChanged();
		    		      			break; 
		    		      		}
	    		      		case 2:
		    		      		{
		    		      			Toast.makeText(ReadList.this, "网址排序", Toast.LENGTH_SHORT).show();
		    		      			bookmarkList = arrayListUrlSort();
		    		      			myAdapter.notifyDataSetChanged();
		    		      			break; 
		    		      		}
    		      		}
    		      		dialog.dismiss();
    		      	}
    		    })
    		.setNegativeButton("取消", null).show();
    }
    
    //网址排序
    private ArrayList<Bookmark> arrayListUrlSort(){
    	ArrayList<Bookmark> resultList =  new ArrayList<Bookmark>();
    	ArrayList<Bookmark> tempList =  new ArrayList<Bookmark>();
    	for(int a=0;a<bookmarkList.size();a++){
    		tempList.add(bookmarkList.get(a));
    	}
    	Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv|us|uk)",Pattern.CASE_INSENSITIVE);
    	Matcher m1;
    	Matcher m2;
    	for(int i=0;i<tempList.size();i++) {
    		if(tempList.get(i)!=null){
	    		m1 = p.matcher(tempList.get(i).getUrl());
	    		m1.find();
	    		resultList.add(tempList.get(i));
			    for(int j=i+1;j<tempList.size();j++) {
				    	if(tempList.get(j)!=null){
				    	m2 = p.matcher(tempList.get(j).getUrl());
				    	m2.find();
					    if((tempList.get(i)!=null && tempList.get(j)!=null) && m1.group().equals(m2.group())){
					    		resultList.add(tempList.get(j));
						    	tempList.set(j, null);
				    	}
			    	}
			    }
    		}
		}
    	return resultList;
    }
    
    //文章排序 最近收藏 最早收藏
    private ArrayList<Bookmark> arrayListSort(boolean flag){
    	ArrayList<Bookmark> tempList =  new ArrayList<Bookmark>();
    	for(int a=0;a<bookmarkList.size();a++){
    		tempList.add(bookmarkList.get(a));
    	}
    	for(int i=0;i<tempList.size()-1;i++) {
		    for(int j=1;j<tempList.size()-i;j++) {
			    Bookmark bookmark;
			    if((Integer.parseInt(tempList.get(j-1).getCreateTime())>Integer.parseInt(tempList.get(j).getCreateTime()))==flag) {   //比较两个整数的大小
			    	bookmark=tempList.get(j-1);
			    	tempList.set((j-1),tempList.get(j));
			    	tempList.set(j,bookmark);
			    }
		    }
		}
    	return tempList;
    }
    
    //确认框
    protected void dialogConfirm() {
    	if(idList.size()>0)  
        {
    		String message;
    		if(!"".equals(latest) && latest!=null){
        		message = "确认要将选中的文章从最近阅读中移除?";
        	}else{
        		message = "确认删除?";
        	}
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(message)
	    	       .setCancelable(false)
	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   ArrayList<Integer> strList = new ArrayList<Integer>();
    	                   for(Integer in:idList)  
    	                   {  
    	                	   strList.add(bookmarkList.get(in).getId());
    	                	   bookmarkList.get(in).setId(-1);
    	                	   bookmarkList.set(in, bookmarkList.get(in));  
    	                   }  
    	                   Iterator<Bookmark> it = bookmarkList.iterator();  
    	                   while(it.hasNext())  
    	                   {  
    	                	   Bookmark al = (Bookmark)it.next();  
    	                       if(al.getId()==-1)  
    	                       {  
    	                           it.remove();  
    	                       }  
    	                   }  
    	                   
    	                   for(int d=0;d<strList.size();d++){
    	                	   //服务器删除
    	                	   rapi = new RAPI(ReadList.this,global.getAccessToken(),userid);
    	                	   if(!"".equals(latest) && latest!=null){
    	                		   for(int e=0;e<bookmarkList.size();e++){
    	                			   if(bookmarkList.get(e).getId()==strList.get(d))
    	                				   System.out.println(bookmarkList.get(e).getId()+"|"+strList.get(d));
    	                				   rapi.asyncDeleteArticleReadTime(bookmarkList.get(e));
    	                		   }
	    	                   }else{
	    	                	   rapi.asyncDeleteArticle(strList.get(d), global.getImgFlag());
	    	                   }
    	                   }
    	                   idList.clear();  
    	                   myAdapter.notifyDataSetChanged();  
	                	   sign_num.setText("标记 0 条");
    	                   //UnRead.this.finish();
	    	           }
	    	       })
	    	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	builder.create().show();
    	}else{
     	   Toast.makeText(ReadList.this, "请选择删除项!", Toast.LENGTH_SHORT).show();  
        }
    }
	
    class ListViewAdapter extends BaseAdapter  
    {  
          
        Context c;  
        LayoutInflater mInflater ;  
        ListViewAdapter(Context context)  
        {  
            c = context;  
            mInflater = getLayoutInflater();  
        }  
        @Override  
        public int getCount()  
        {  
            return bookmarkList.size();  
        }  
  
        @Override  
        public Object getItem(int position)  
        {  
            return bookmarkList.get(position);  
        }  
  
        @Override  
        public long getItemId(int position)  
        {  
            return position;  
        }  
          
  
        @Override  
        public View getView(int position, View convertView,  
                ViewGroup parent)  
        {  
            ViewHolder holder = new ViewHolder();  
            final int pos = position;  
//            if(convertView==null)  
//            {  
            convertView  = mInflater.inflate(R.layout.list_item, null);
//            }  
//                if("1".equals(articleList.get(pos).getIsRead().toString()))
//                	convertView.setClickable(true);//不可点击
//                else
//                	convertView.setClickable(false);//可点击
            holder.title = (TextView)convertView.findViewById(R.id.title);  
            holder.url = (TextView)convertView.findViewById(R.id.url);  
            holder.checkItem = (CheckBox)convertView.findViewById(R.id.checkItem);  
            holder.title.setText(bookmarkList.get(position).getTitle());
            if("".equals(type) || type==null){
        		if("1".equals(bookmarkList.get(position).getReadProgress())){
        			holder.title.setTextColor(R.color.has_read_color);
        		}
        	}
            
            Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv|us|uk)",Pattern.CASE_INSENSITIVE);
        	Matcher m;
        	m = p.matcher(bookmarkList.get(position).getUrl());
    		m.find();
    		m.group();
            
            holder.url.setText(m.group()); 
            holder.checkItem.setChecked(false);  
  
            holder.checkItem.setOnCheckedChangeListener(new OnCheckedChangeListener()  
            {  
                  
                @Override  
                public void onCheckedChanged(CompoundButton buttonView,  
                        boolean isChecked)  
                {  
                    if(isChecked&&(!idList.contains(pos)))  
                    {  
                        idList.add(pos);  
                    }  
                    else 
                    {  
                        if(!isChecked && idList.contains(pos))  
                        {  
                            idList.remove(Integer.valueOf(pos));  
                        }  
                    }       
                	sign_num.setText("标记  "+idList.size()+" 条");
                	sign_num_read.setText("标记"+idList.size()+"条为已读");
                }  
            });  
            if(idList.contains(position)){
            	holder.checkItem.setChecked(true);  
            }else{
            	holder.checkItem.setChecked(false);  
            }
            if(bookmarkList.get(position).getIsDownload()==1){
            	convertView.setClickable(false);
            	convertView.setBackgroundResource(R.drawable.sohu_main_item);
            }else{
            	convertView.setClickable(true);
            }
            if(visflag)  
            {  
                holder.checkItem.setVisibility(View.VISIBLE);  
            }  
            else  
            {  
                holder.checkItem.setVisibility(View.INVISIBLE);  
            }  
            return convertView;  
        }  
        class ViewHolder
        {  
            TextView title;
            TextView url;
            CheckBox checkItem;
        }  
          
    } 
    
    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
    	if("0".equals(type)){
        	menu.add(0, 1, 0, "已读").setIcon(R.drawable.ic_menu_already_read);
        }else{
        	menu.add(0, 1, 0, "未读").setIcon(R.drawable.ic_menu_has_not_read);
        }
    	if("1".equals(type) || "0".equals(type)){
    		menu.add(0, 2, 0, "最近阅读").setIcon(R.drawable.ic_menu_reading_history);  
        }else{
        	menu.add(0, 2, 0, "已读").setIcon(R.drawable.ic_menu_already_read);
        }
        menu.add(0, 3, 0, "标记").setIcon(R.drawable.ic_menu_mark);  
        menu.add(0, 4, 0, "刷新").setIcon(R.drawable.ic_menu_refresh);  
        menu.add(0, 5, 0, "设置").setIcon(R.drawable.ic_menu_settings);  
        menu.add(0, 6, 0, "意见反馈").setIcon(R.drawable.ic_menu_feedback);  
        return super.onCreateOptionsMenu(menu);  
//        MenuInflater inflater = getMenuInflater();
//        
//        inflater.inflate(R.menu.menu, menu);
//        return true;
    }  
  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item)  
    {  
        switch(item.getItemId())  
        {  
	        case 1: 
	        {  
	        	if("0".equals(type)){
	        		Intent intent = new Intent(this,ReadList.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			Bundle bundle = new Bundle();
	    			bundle.putString("type", "1");
	    			intent.putExtras(bundle);
	    			startActivity(intent);
	            }else{
	            	Intent intent = new Intent(this,ReadList.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			Bundle bundle = new Bundle();
	    			bundle.putString("type", "0");
	    			intent.putExtras(bundle);
	    			startActivity(intent);
	            }
	            break;  
	        }
	        case 2: 
	        {  
	        	if("1".equals(type) || "0".equals(type)){
	        		Intent intent = new Intent(ReadList.this,ReadList.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			Bundle bundle = new Bundle();
	    			bundle.putString("latest", "1");
	    			intent.putExtras(bundle);
	    			startActivity(intent);
	            }else{
	            	Intent intent = new Intent(this,ReadList.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			Bundle bundle = new Bundle();
	    			bundle.putString("type", "1");
	    			intent.putExtras(bundle);
	    			startActivity(intent);
	            }         
	            break;  
	        }
            case 3:  // 标记
            {  
                if(visflag==true)  
                {  
                    visflag = false;  
                    if("0".equals(type)){
                    	set_articles_read.setVisibility(View.INVISIBLE);
                    }else{
                    	confirm.setVisibility(View.INVISIBLE);
                    }
                    idList.clear();  
                }  
                else  
                {  
                    visflag = true;  
                    if("0".equals(type)){
                    	set_articles_read.setVisibility(View.VISIBLE);
                    }else{
                    	confirm.setVisibility(View.VISIBLE);
                    }
                }  
                this.myAdapter.notifyDataSetInvalidated();  
                break;  
            } 
            case 4: 
	        {  
	        	new RefreshList().execute();
	            break;  
	        }
            case 5: 
	        {    
	        	Intent intent = new Intent(this,Setting.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
	            break;  
	        }
            case 6: 
	        {  
	        	Toast.makeText(ReadList.this, "意见反馈", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
        }  
        return super.onOptionsItemSelected(item);  
    }
    
    private ProgressDialog showProgressDialogInfo(){
    	if(dialog == null){
    		ProgressDialog progressDialog = new ProgressDialog(this);
    		progressDialog.setTitle("请稍后");//
    		progressDialog.setMessage("处理中...");//设置title和message报错
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
          e.printStackTrace();
        }
    }
    
    class RefreshList extends AsyncTask<Void, Void, Boolean>{
    	
    	protected void onPreExecute(){
    		showProgressDialogInfo();
    	}
    	
    	protected Boolean doInBackground(Void... params){
    		//更新bookmark表和category数据
    		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(ReadList.this);
			rapi.refreshList(preferences,global);
			ReadList.this.getData();
    		return false;
    	}
    	
    	protected void onPostExecute(Boolean loggedIn){
			if(bookmarkList.size()>0){
				for(int t=0;t<bookmarkList.size();t++){
		    		System.out.println("bookmarkid:"+bookmarkList.get(t).getId());
		    		System.out.println(bookmarkList.get(t).getTitle());
		    	}
				empty_layout.setVisibility(View.GONE);
				relative_list.setVisibility(View.VISIBLE);
				myAdapter.notifyDataSetChanged();
				//重新下载文件
				rapi.downloadResource(global.getImgFlag(), bookmarkList, global.getUserId(), listView);
			}else{
				empty_layout.setVisibility(View.VISIBLE);
			}
    		dismissProgressDialog();
    	}
    	
    	protected void onCancelled(){
    		dismissProgressDialog();
    	}
    }
     
}

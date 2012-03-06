package com.sohu.kan;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.database.DBHelper;
import com.sohu.kan.Global;
import com.sohu.utils.FileUtils;
import com.sohu.utils.HttpDownloader;
import com.sohu.utils.RAPI;
import com.sohu.wuhan.Constant;
import com.sohu.wuhan.HttpRead;
import com.sohu.wuhan.IReadable;
import com.sohu.xml.model.ArticleList;
import com.sohu.xml.parser.ArticleListParser;
import com.sohu.xml.parser.PullArticleListParser;

public class ReadList extends Activity {
	
	private TextView list_title; 
    private ArrayList<Integer> idList = new ArrayList<Integer>();  
    private ListViewAdapter myAdapter; 
	private ListView listView;
	private boolean visflag = false; 
    private TextView refresh;
    private TextView emptylist;
    private EditText filter;
    private TextView sort;
    
    private RelativeLayout confirm;
    private Button cancel;
    private Button delete;
    private TextView sign_num; 
    
     
    private List<ArticleList> articleList; 
    private List<ArticleList> articleListBak;
	
	private String type;
	private String category_id;
	private String category_name;
	
	boolean fileExist;
	
	private DBHelper db;
	
	private RAPI rapi;
	
	ProgressDialog downloadFileDialog;
	
	private boolean wifi;
	String token = "649cfef6a94ee38f0c82a26dc8ad341292c7510e";
	
	private Global global;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        articleList = new ArrayList<ArticleList>(); 
        articleListBak = new ArrayList<ArticleList>();
        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getString("type");
        
        category_id = bundle.getString("category_id");
        category_name = bundle.getString("category_name");
        wifi = RAPI.checkNetworkConnection(this);
        rapi = new RAPI(ReadList.this);
        
        getData();
        
        //网址排序算法需要一个备份list
        articleListBak = articleList;
        
        if(articleList.size()>0){
        	setContentView(R.layout.readlist);
	        ensureUi();
	        listView = (ListView)findViewById(R.id.list);
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
	    			ArrayList<ArticleList> article = new ArrayList<ArticleList>();
	    			article.add(articleList.get(position));
	    			bundle.putSerializable("article", article);
	    			intent.putExtras(bundle);
//	    			startActivity(intent);
	    			isHtmlDonwload(intent, articleList.get(position).getKey()); 
	                  
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
        }else{
        	setContentView(R.layout.emptylist);
        	emptylist = new TextView(this);
	    	emptylist = (TextView)findViewById(R.id.emptylist);
	    	emptylist.setVisibility(View.VISIBLE);
        }
        
        //写入xml
//        String xml = parser.serialize(books);  //序列化
//        FileOutputStream fos = openFileOutput("books.xml", Context.MODE_PRIVATE);  
//        fos.write(xml.getBytes("UTF-8"));  
    }
    
    public void getData(){
    	db = new DBHelper(this);
    	Cursor cur = null;
    	if(!"".equals(type) && type!=null){
    		cur = db.loadAllArticles(type);
    	}else if(!"".equals(category_id) && category_id!=null){
    		cur = db.loadArticlesByCategory(category_id);
    	}
    	
    	ArticleList article;
    	if(cur.moveToFirst()){
    		do{
    			article = new ArticleList();
    			article.setKey(cur.getString(0));
    			article.setTitle(cur.getString(1));
    			article.setUrl(cur.getString(2));
    			article.setDownloadUrl(cur.getString(3));
    			article.setImageUrls(cur.getString(4));
    			article.setIsRead(cur.getString(5));
    			article.setCreateTime(cur.getString(6));
    			articleList.add(article);
    			article = null;
    		}
    		while(cur.moveToNext());
    	}
    	cur.close();
    	db.close();
    }
    
    public void isHtmlDonwload(Intent intent, String key){
    	downloadFileDialog = new ProgressDialog(this);
    	global = (Global)getApplication();
    	FileUtils file;
    	if(global.getSaveFlag()){
    		file = new FileUtils("sd");
    	}else{
    		file = new FileUtils();
    	}
    	
		fileExist = file.isFileExist(token+"/"+key+".html");
        if(fileExist){
        	downloadFileDialog.dismiss();
        	//跳转
        	startActivity(intent);
        }else{
        	downloadFileDialog.setTitle("请稍后");
        	downloadFileDialog.setMessage("正在下载文件...");//设置title和message报错
        	downloadFileDialog.setIndeterminate(true);
        	downloadFileDialog.setCancelable(true);
        	downloadFileDialog.show();
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        	isHtmlDonwload(intent, key);
        }
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
		.setTitle(articleList.get(pos).getTitle())
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
	    	        			db.setArticleRead(articleList.get(pos).getKey());
	    	        			db.close();
	    	        			articleList.remove(pos);
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
	    		      			deleteArticle(pos);
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
		ArrayList<ArticleList> article = new ArrayList<ArticleList>();
		article.add(articleList.get(pos));
		bundle.putSerializable("article", article);
		bundle.putString("type", type);
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
    	        	    db = new DBHelper(ReadList.this);
    	        	   	//本地删除
	                	db.deleteArticle(articleList.get(pos).getKey());
	                	//服务器删除
	                	rapi = new RAPI(ReadList.this);
    	                rapi.deleteArticle(articleList.get(pos).getKey());
    	                articleList.remove(pos);
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
    	list_title = new TextView(this);
    	list_title = (TextView)findViewById(R.id.list_title);
    	list_title.setText("未读文章");
    	if("1".equals(type)){
    		list_title.setText("已读文章");
    	}else if(!"".equals(category_id) && category_id!=null){
    		list_title.setText(category_name);
    	}
    	
    	refresh = new TextView(this);
    	refresh = (TextView)findViewById(R.id.refresh);
    	refresh.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			Toast.makeText(ReadList.this, "刷新", Toast.LENGTH_SHORT).show();
    			//同步服务器数据
//    			RAPI rapi = new RAPI(ReadList.this);
//    			if(rapi.dataSync()!=null){
//    				articleList = new ArrayList<ArticleList>();
//    				articleList = rapi.dataSync();
//    			}
//    	    	for(int t=0;t<articleList.size();t++){
//    	    		System.out.println(articleList.get(t).getTitle());
//    	    	}
    			if(wifi){
    	    		articleList = new ArrayList<ArticleList>();
    	        	//wifi情况
    	        	System.out.println("正在下载");
    	        	IReadable ir;
    	        	ir = HttpRead.instance();
    	    		if (!ir.init(token)) {
    	    			System.out.println("初始化失败");
    	    			// Log.Error(" 初始化失败!");
    	    		}else{
    	    			ir.asyncProbeArticle(new Handler(){
    	    				public void  handleMessage(Message msg) {
    	    					
    	    					Bundle data = msg.getData();
    	    					Constant.Error error = (Constant.Error)data.getSerializable("error");
    	    					if (error != Constant.Error.OK) {
    	    						System.out.println("-------------报错---------------");
    	    						System.out.println(error);
    	    						System.out.println("-------------报错结束---------------");
    	    						
    	    						//hint to users.
    	    					} else {
    	    						String rtns = data.getString("result");
    	    						System.out.println("-------------取list数据---------------");
    	    						if (null != rtns){
    	    							ByteArrayInputStream is;
    	    							try {  
    	    								is = new ByteArrayInputStream(rtns.getBytes("utf-8"));
    	    								ArticleListParser parser; 
    	    			                    parser = new PullArticleListParser(); 
    	    			                    db = new DBHelper(ReadList.this);
    	    			                    List<ArticleList> list = parser.parse(is);
    	    			                    if(list.size()>0)
    	    			                    db.truncateArticle();
    	    			                    for(int i=0;i<list.size();i++){
    	    			                    	//根据download_url下html和image_url下图片
    	    			                    	Thread download = new downloadHtmlAndImages(list.get(i).getKey(),list.get(i).getDownloadUrl(),list.get(i).getImageUrls());
    	    			                    	download.start();
    	    			                        //用户传递到下个activity的arraylist
    	    			                    	db.insertArticle(list.get(i));
    	    			                    	articleList.add(list.get(i));
    	    			                    }
    	    			                    for(int b=0;b<articleList.size();b++){
	    			                        	System.out.println("文章标题:"+articleList.get(b).getTitle());
	    			                        }
    	    			                } catch (UnsupportedEncodingException e1) {
    	    								// TODO Auto-generated catch block
    	    								e1.printStackTrace();
    	    								System.out.println(e1+"UnsupportedEncodingException e1有这样的错222222");
    	    							} catch (Exception e) {  
    	    			                	System.out.println(e+"Exception有这样的错22222222222");
    	    			                } 
    	    						}
    	    						db.close();
    	    						System.out.println("-------------取数据结束---------------");
    	    					}
    	    				}
    	    			});
    	    		}
    	        }else{
    	        	//离线情况  记录离线操作
    	        	System.out.println("暂不支持离线阅读,请先连接WIFI!");
    	        }
    			try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			myAdapter.notifyDataSetChanged();  
    		}
    	});
    	
    	filter = new EditText(this);
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
                articleList = null;
                articleList = new ArrayList<ArticleList>();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // TODO Auto-generated method stub
                //关键是这里,监听输入的字符串,如果大于零,则可点击,enable.
            	if(!"".equals(s) && s!=null){
	            	for(int n=0;n<articleListBak.size();n++){
		            	if(articleListBak.get(n).getTitle().contains(s) || articleListBak.get(n).getUrl().contains(s)){
		            		articleList.add(articleListBak.get(n));
		            	}
	            	}
            	}else{
            		articleList = articleListBak;
            	}
            	myAdapter.notifyDataSetChanged();
            }
        });
    	
    	sort = new TextView(this);
    	sort = (TextView)findViewById(R.id.sort);
    	sort.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			dialogSingle();
    		}
    	});
    	
    	confirm = new RelativeLayout(this);
    	confirm = (RelativeLayout)findViewById(R.id.confirm);
    	
    	cancel = new Button(this);
    	cancel = (Button)findViewById(R.id.cancel);
    	cancel.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			visflag = false;
    			confirm.setVisibility(View.GONE);
    		}
    	});
    	
    	delete = new Button(this);
    	delete = (Button)findViewById(R.id.delete);
    	delete.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			dialogConfirm();
    		}
    	});
    	
    	sign_num = new TextView(this);
    	sign_num = (TextView)findViewById(R.id.sign_num);
    }
    
    class downloadHtmlAndImages extends Thread
    {        
    	private String key;
    	private String download_url;
    	private String[] image_url;

    	public downloadHtmlAndImages(String key, String download_url, String image_urls) {
    		this.key = key;
    		this.download_url = download_url;
    		image_url = image_urls.trim().split("\\|");
    	}
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            synchronized(this){
//	            System.out.println("机身内存路径:"+getFilesDir());
	            HttpDownloader httpDownloader = new HttpDownloader();
	            httpDownloader.downFile(download_url, "/download/", key+".html");
	            
	//          String lrc = httpDownloader.download("http://192.168.1.101:8080/20111021/a.lrc");  
	            for(int m=0;m<image_url.length;m++){
	            	if(!"0".equals(image_url[m])){
	            		httpDownloader.downFile(image_url[m], "/download/", key+"_"+m);
	            	}
	            }
            }
//            db.save("lidan");//数据库操作
//            Cursor cur = db.loadAll();
//            StringBuffer sf = new StringBuffer();
//            cur.moveToFirst();
//            while (!cur.isAfterLast()) {
//    			sf.append(cur.getInt(0)).append(" : ").append(
//    					cur.getString(1)).append("\n");
//    			cur.moveToNext();
//    			System.out.println(sf);
//    		}
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
		    		      			articleList = arrayListSort(false);
		    		      			myAdapter.notifyDataSetChanged();
		    		      			break; 
		    		      		}
	    		      		case 1:
		    		      		{
		    		      			Toast.makeText(ReadList.this, "最早收藏", Toast.LENGTH_SHORT).show();
		    		      			articleList = arrayListSort(true);
		    		      			myAdapter.notifyDataSetChanged();
		    		      			break; 
		    		      		}
	    		      		case 2:
		    		      		{
		    		      			Toast.makeText(ReadList.this, "网址排序", Toast.LENGTH_SHORT).show();
		    		      			articleList = arrayListUrlSort();
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
    private ArrayList<ArticleList> arrayListUrlSort(){
    	ArrayList<ArticleList> resultList =  new ArrayList<ArticleList>();
    	ArrayList<ArticleList> tempList =  new ArrayList<ArticleList>();
    	for(int a=0;a<articleList.size();a++){
    		tempList.add(articleList.get(a));
    	}
    	Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv|us)",Pattern.CASE_INSENSITIVE);
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
    private ArrayList<ArticleList> arrayListSort(boolean flag){
    	ArrayList<ArticleList> tempList =  new ArrayList<ArticleList>();
    	for(int a=0;a<articleList.size();a++){
    		tempList.add(articleList.get(a));
    	}
    	for(int i=0;i<tempList.size()-1;i++) {
		    for(int j=1;j<tempList.size()-i;j++) {
			    ArticleList article;
			    if(compareTime(tempList.get(j-1).getCreateTime(),tempList.get(j).getCreateTime())==flag) {   //比较两个整数的大小
			    	article=tempList.get(j-1);
			    	tempList.set((j-1),tempList.get(j));
			    	tempList.set(j,article);
			    }
		    }
		}
    	return tempList;
    }
    
    //将字符串形式的日期时间做比较
    public boolean compareTime(String time1, String time2){
    	java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	java.util.Calendar c1=java.util.Calendar.getInstance();
    	java.util.Calendar c2=java.util.Calendar.getInstance();
    	try
    	{
    		c1.setTime(df.parse(time1));
    		c2.setTime(df.parse(time2));
    	}catch(java.text.ParseException e){
    	}
    	int result=c1.compareTo(c2);
    	if(result==0)
    	//c1相等c2
    	return false;
    	else if(result<0)
        //c1小于c2
    	return false;
    	else
    	//c1大于c2
    	return true;
    }
    
    //确认框
    protected void dialogConfirm() {
    	if(idList.size()>0)  
        {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("确认删除?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
    	        	   		//dialog.dismiss();
	    	        	   ArrayList<String> strList = new ArrayList<String>();
    	                   for(Integer in:idList)  
    	                   {  
    	                	   strList.add(articleList.get(in).getKey());
    	                	   articleList.get(in).setKey("-1");
    	                	   articleList.set(in, articleList.get(in));  
    	                   }  
    	                   Iterator<ArticleList> it = articleList.iterator();  
    	                   while(it.hasNext())  
    	                   {  
    	                	   ArticleList al = (ArticleList)it.next();  
    	                       if(al.getKey().equals("-1"))  
    	                       {  
    	                           it.remove();  
    	                       }  
    	                   }  
    	                   db = new DBHelper(ReadList.this);
    	                   for(int d=0;d<strList.size();d++){
    	                	   //本地删除
    	                	   db.deleteArticle(strList.get(d));
    	                	   //服务器删除
    	                	   rapi = new RAPI(ReadList.this);
        	                   rapi.deleteArticle(strList.get(d));
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
            return articleList.size();  
        }  
  
        @Override  
        public Object getItem(int position)  
        {  
            return articleList.get(position);  
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
            if(convertView==null)  
            {  
                System.out.println("convertView==null");  
                convertView  = mInflater.inflate(R.layout.list_item, null);  
            }  
            holder.title = (TextView)convertView.findViewById(R.id.title);  
            holder.url = (TextView)convertView.findViewById(R.id.url);  
            holder.checkItem = (CheckBox)convertView.findViewById(R.id.checkItem);  
            holder.title.setText(articleList.get(position).getTitle()); 
            holder.url.setText(articleList.get(position).getUrl()); 
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
                        if(idList.contains(pos))  
                        {  
                            idList.remove(Integer.valueOf(pos));  
                        }  
                    }  
                          
                    System.out.println(idList.toString());  
                    sign_num.setText("标记  "+idList.size()+" 条");
                }  
            });  
              
            if(visflag)  
            {  
                holder.checkItem.setVisibility(View.VISIBLE);  
            }  
            else  
            {  
                holder.checkItem.setVisibility(View.INVISIBLE);  
            }  
//            if (idList != null)
//
//                for (int i = 0; i < idList.size(); i++) {
//
//                        if (position == idList.get(i))
//
//                        	holder.checkItem.setChecked(true);
//
//                }
          
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
          
        menu.add(0, 1, 0, "已读");  
        menu.add(0, 2, 0, "最近阅读");  
        menu.add(0, 3, 0, "标记");  
        menu.add(0, 4, 0, "刷新");  
        menu.add(0, 5, 0, "设置");  
        menu.add(0, 6, 0, "关于");  
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
	        	Toast.makeText(ReadList.this, "已读", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
	        case 2: 
	        {  
	        	Toast.makeText(ReadList.this, "最近阅读", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
            case 3:  // 标记
            {  
                if(visflag==true)  
                {  
                    visflag = false;  
                    confirm.setVisibility(View.INVISIBLE);  
                    idList.clear();  
                }  
                else  
                {  
                    visflag = true;  
                    confirm.setVisibility(View.VISIBLE);  
                }  
                this.myAdapter.notifyDataSetInvalidated();  
                break;  
            } 
            case 4: 
	        {  
	        	Toast.makeText(ReadList.this, "刷新", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
            case 5: 
	        {  
	        	Toast.makeText(ReadList.this, "设置", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
            case 6: 
	        {  
	        	Toast.makeText(ReadList.this, "关于", Toast.LENGTH_SHORT).show();               
	            break;  
	        }
        }  
        return super.onOptionsItemSelected(item);  
    }  
     
}

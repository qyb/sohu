package com.sohu.database;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sohu.xml.model.Bookmark;
import com.sohu.xml.model.Resource;
  
/** 
 * 数据库操作工具类 
 *  
 * @author 2011zhouhang@gmail.com 
 *  
 */  
public class DBHelper {  
//    private static final String TAG = "sohu";// 调试标签  
  
    private static final String DATABASE_NAME = "sohukan.db";// 数据库名  
    SQLiteDatabase db;  
    Context context;//应用环境上下文   Activity 是其子类  
    
    long currentTime;
  
    public DBHelper(Context _context) {  
        context = _context;  
        //开启数据库  
        
//        Date utilDate = new java.util.Date();
//        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");//获取中国的时区
//	    myFmt.setTimeZone(timeZoneChina);//设置系统时区
//	    currentTime = myFmt.format(utilDate);
	    
	    Date date = new Date();
	    currentTime = Math.round(date.getTime()/1000);
        
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);  
        
        CreateTable();
    }  
  
    /** 
     * 建表 
     * 列名 区分大小写？ 
     * 都有什么数据类型？ 
     * SQLite 3  
     *  TEXT    文本 
        NUMERIC 数值 
        INTEGER 整型 
        REAL    小数 
        NONE    无类型 
     * 查询可否发送select ? 
     */  
    public void CreateTable() {  
        try {   
//          db.execSQL("drop table s_bookmark");
            //bookmark表
            db.execSQL("create table if not exists s_bookmark (" +  
                    "id varchar(100) PRIMARY KEY not null,"
                    + "url varchar(200) not null,"   
                    + "title text not null,"
                    + "description text not null,"
                    + "is_star int not null," 
                    + "create_time timestamp not null," 
                    + "read_time timestamp not null,"
                    + "folder_name varchar(100)," 
                    + "read_progress int not null," 
                    + "version  int not null," 
                    + "text_version int not null," 
                    + "is_ready int not null," 
                    + "is_download int default 0 not null,"
                    + "userid varchar(20) not null"
                    + ");");
            //resource表
//            db.execSQL("drop table s_resource");
            db.execSQL("create table if not exists s_resource (" +  
                     "bookmarkid varchar(100) not null,"
                    + "type varchar(10) not null,"
                    + "key varchar(200) not null," 
                    + "is_download int default 0 not null,"
                    + "userid varchar(20) not null"
                    + ");");
//            db.execSQL("drop table s_category");
            //分类表
            db.execSQL("create table if not exists s_category (" +  
                    "id INTEGER PRIMARY KEY autoincrement not null,"  
                    + "category_name TEXT not null,"   
                    + "create_time timestamp,"
                    + "userid varchar(20) not null"
                    + ");"); 
//            db.execSQL("drop table s_settings");
            //个人设置表
            db.execSQL("create table if not exists s_settings (" +  
                    "userid varchar(20) PRIMARY KEY not null,"
                    + "access_token varchar(200) not null,"
                    + "sync_status int default 0 not null,"
                    + "img_status int default 1 not null,"
                    + "file_status int default 0 not null,"
                    + "index_guide int default 0 not null,"
                    + "unreadlist_guide int default 0 not null,"
                    + "read_guide int default 0 not null,"
                    + "category_guide int default 0 not null,"
                    + "is_login int not null,"
                    + "login_time timestamp"
                    + ");");
            //离线操作表
            db.execSQL("create table if not exists s_operation (" +  
            		"id INTEGER PRIMARY KEY autoincrement not null,"
            		+ "userid varchar(20) not null,"
                    + "key varchar(50) not null,"
                    + "type varchar(10) not null,"
                    + "method varchar(20) not null,"
                    + "params varchar(100),"
                    + "time timestamp"
                    + ");");
//            Log.v(TAG, "Create Table t_user ok");  
        } catch (Exception e) {  
//            Log.v(TAG, "Create Table t_user err,table exists.");  
        }  
    }  
    
    /** 
     * 登录保存token
     * @param token
     * @return 
     */  
    public boolean saveAccessToken(String access_token, String userid){
    	Cursor cur = getSettings(userid);
    	String sql="";  
        try{  
            if(cur.getCount()==0){
            	sql="insert into s_settings (userid, access_token, is_login, login_time)values('"+userid+"','"+access_token+"',1,'"+currentTime+"')";  //0gprs也同步,0显示图片,0缓存到机身内存
            }else{
            	sql="update s_settings set access_token = '"+access_token+"',login_time = '"+currentTime+"',is_login = 1 where userid = '"+userid+"'";
            }
            System.out.println(sql);
            db.execSQL(sql);
            cur.close();
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;
        }
    }
    /** 
     * 登录保存token
     * @param token
     * @return 
     */  
    public boolean saveLastSyncRecord(String userid, String last_sync_record){
    	String sql="";  
        try{ 
            sql="update s_settings set last_sync_record = '"+last_sync_record+"' where userid = '"+userid+"'";
            System.out.println(sql);
            db.execSQL(sql);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /** 
     * 新增分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public boolean addCategory(String category_name, String userid){
    	Cursor cur = checkCategoryUnique(category_name, userid);
        String sql="";  
        try{  
        	if(cur.getCount()==0){
	            sql="insert into s_category (category_name, create_time, userid)values('"+category_name+"','"+currentTime+"','"+userid+"')";  
	            System.out.println(sql);
	            db.execSQL(sql);
        	}
        	cur.close();
            return true;
        }catch(Exception e){  
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 检测唯一分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public Cursor checkCategoryUnique(String category_name, String userid){  
    	Cursor cur;
        cur=db.query("s_category", new String[]{"id","category_name","create_time","userid"}, "userid = ? and category_name = ?",new String[]{userid,category_name},null, null, null);  
        return cur;  
    }
    /** 
     * 修改分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public boolean updateCategoryName(int category_id, String old_category_name, String category_name){  
        String sql="";  
        try{  
            sql="update s_category set category_name = '"+category_name+"' where id = "+category_id;//, create_time)values('"+category_name+"','"+currentTime+"')";  
            db.execSQL(sql);  
            sql="update s_articles set category_name = '"+category_name+"' where category_name = '"+old_category_name+"'";
            db.execSQL(sql);
//            Log.v(TAG,"insert Table t_user ok");  
            return true;  
              
        }catch(Exception e){  
//            Log.v(TAG,"insert Table t_user err ,sql: "+sql);  
            return false;  
        }  
    }  
    /** 
     * 修改分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public boolean deleteCategory(int category_id, String category_name){  
        String sql="";  
        try{  
            sql="delete from s_category where id = "+category_id;//, create_time)values('"+category_name+"','"+currentTime+"')";  
            db.execSQL(sql);  
            sql="update s_bookmark set folder_name = \"\" where folder_name = '"+category_name+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 同步bookmark
     * @param bookmark 
     * @return 
     */ 
    public boolean syncBookmark(Bookmark bookmark, String userid){  
        String sql="";  
        try{  
        	if(bookmark.getTitle()==null){
        		sql = "delete from s_bookmark where id = "+bookmark.getId();
        		db.execSQL(sql);
        		System.out.println(sql);
        		return true;
        	}
            Cursor cur = getBookmarkById(bookmark.getId()+"");
            cur.moveToFirst();
        	if(cur.getCount()==0){
        		sql="insert into s_bookmark(id,url,title,description,is_star,create_time,read_time,folder_name,read_progress,version,text_version,is_ready,userid)" +
            		"values("+bookmark.getId()+",'"+bookmark.getUrl()+"','"+bookmark.getTitle()+"','"+bookmark.getDescription()+"',"+bookmark.getIsStar()+",'"+bookmark.getCreateTime()+"','"+bookmark.getReadTime()+"'," +
            		"'"+bookmark.getFolderName()+"','"+bookmark.getReadProgress()+"',"+bookmark.getVersion()+","+bookmark.getTextVersion()+","+bookmark.getIsReady()+",'"+userid+"')";
        		db.execSQL(sql);
        	}else{
        		if(bookmark.getVersion()>cur.getInt(9)){
        			sql="update s_bookmark set url = '"+bookmark.getUrl()+"',title = '"+bookmark.getTitle()+"',description = '"+bookmark.getDescription()+"',is_star = "+bookmark.getIsStar()+"," +
        				"create_time = '"+bookmark.getCreateTime()+"',read_time = '"+bookmark.getReadTime()+"',folder_name = '"+bookmark.getFolderName()+"'," +
        				"read_progress = '"+bookmark.getReadProgress()+"',version = "+bookmark.getVersion()+",text_version = "+bookmark.getTextVersion()+",is_ready = "+bookmark.getIsReady()+" where id = "+bookmark.getId()+" and userid = '"+userid+"'";
        			db.execSQL(sql);
        		}
        	}
            System.out.println(sql);
            cur.close();
            return true;  
        }catch(Exception e){    
        	System.out.println(e);
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 通过bookmarkid取bookmark
     * @param bookmark 
     * @return 
     */ 
    public Cursor getBookmarkById(String id){  
    	Cursor cur;
        cur=db.query("s_bookmark", new String[]{"id","url","title","description","is_star","create_time","read_time","folder_name","read_progress","version","text_version","is_ready"}, "id = ?",new String[]{id}, null, null, null);  
        return cur;  
    }
    /** 
     * 同步resource
     * @param bookmark 
     * @return 
     */ 
    public boolean syncResource(Resource resource, String userid){  
        String sql="";  
        Cursor cur = getResourceByKey(resource.getKey());
        cur.moveToFirst();
        try{  
        	if(cur.getCount()==0){
        		sql="insert into s_resource(bookmarkid,type,key,userid)" +
            		"values("+resource.getBookmarkId()+",'"+resource.getType()+"','"+resource.getKey()+"','"+resource.getUserId()+"')";
        		db.execSQL(sql);
        	}
            System.out.println(sql);
            cur.close();
            return true;  
        }catch(Exception e){    
        	System.out.println(e);
            return false;  
        }  
    }
    /** 
     * 通过bookmarkid取resource
     * @param bookmark 
     * @return 
     */ 
    public Cursor getResourceById(String bookmarkid){  
    	Cursor cur;
        cur=db.query("s_resource", new String[]{"bookmarkid","type","key","is_download"}, "bookmarkid = ?",new String[]{bookmarkid}, null, null, null);  
        return cur;  
    }
    /** 
     * 通过bookmarkid删除resource
     * @param id 
     * @return 
     */ 
    public boolean deleteResourceById(String bookmarkid){  
        String sql="";  
        try{  
            sql="delete from s_resource where bookmarkid = '"+bookmarkid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 通过key取resource
     * @param bookmark 
     * @return 
     */ 
    public Cursor getResourceByKey(String key){  
    	Cursor cur;
        cur=db.query("s_resource", new String[]{"bookmarkid","type","key","is_download"}, "key = ?",new String[]{key}, null, null, null);  
        return cur;  
    }
    /** 
     * 设资源is_download
     * @param id 
     * @return 
     */ 
    public boolean setResourceIsDownload(String key){  
        String sql="";  
        try{  
            sql="update s_resource set is_download = 1 where key = '"+key+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 删除文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean deleteBookmark(int id){  
        String sql="";  
        try{  
            sql="delete from s_bookmark where id = "+id;
            System.out.println(sql);
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 修改文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean updateBookmark(String category_name,String title,String id){  
        String sql="";  
        try{  
            sql="update s_bookmark set title = '"+title+"',folder_name = '"+category_name+"' where id = '"+id+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            System.out.println(e);
            return false;  
        }  
    }
    /** 
     * 修改文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean updateReadTimeById(int id, String current_time){  
        String sql="";  
        try{  
            sql="update s_bookmark set read_time = '"+current_time+"' where id = '"+id+"'";
            System.out.println(sql+"最近阅读");
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            System.out.println(e);
            e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 通过key取此篇文章分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public Cursor getCategoryById(String id){  
    	Cursor cur;
        cur=db.query("s_bookmark", new String[]{"folder_name"}, "id = ?",new String[]{id}, null, null, null);  
        return cur;  
    } 
    /** 
     * 通过key取此文章是否已经设为已读
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public Cursor getProgressById(String id){  
    	Cursor cur;
        cur=db.query("s_bookmark", new String[]{"read_progress"}, "id = ?",new String[]{id}, null, null, null);  
        return cur;  
    } 
    /** 
     * 设文章为已读状态
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setBookmarkRead(int id){  
        String sql="";  
        try{  
            sql="update s_bookmark set read_progress = 1 where id = "+id;
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 设文章is_download
     * @param id 
     * @return 
     */ 
    public boolean setBookmarkIsDownload(int id){  
        String sql="";  
        try{  
            sql="update s_bookmark set is_download = 1 where id = "+id;
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 根据类型查询文章记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadBookmarkByFolderName(String folder_name, String userid){  
    	Cursor cur;
    	cur=db.query("s_bookmark", new String[]{"id","url","title","description","is_star","create_time","read_time","folder_name","read_progress","version","text_version","is_ready","is_download"}, "userid = ? and folder_name = ?",new String[]{userid,folder_name}, null, null, "create_time desc");  
        return cur;  
    }
    /** 
     * 根据read_time查询文章记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadBookmarkByTime(String userid){  
    	Cursor cur;
    	cur=db.query("s_bookmark", new String[]{"id","url","title","description","is_star","create_time","read_time","folder_name","read_progress","version","text_version","is_ready","is_download"}, "userid = ? and read_time is not null",new String[]{userid}, null, null, "read_time desc","20");  
        return cur;  
    } 
    /** 
     * 根据类型查询文章记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadAllBookmark(String type, String userid){  
    	Cursor cur;
    	if("0".equals(type)){
    		//未读
    		cur=db.query("s_bookmark", new String[]{"id","url","title","description","is_star","create_time","read_time","folder_name","read_progress","version","text_version","is_ready","is_download"}, "userid = ? and read_progress != ?",new String[]{userid,"1.0"}, null, null, "create_time desc");  
    	}else{
    		//已读
    		cur=db.query("s_bookmark", new String[]{"id","url","title","description","is_star","create_time","read_time","folder_name","read_progress","version","text_version","is_ready","is_download"}, "userid = ? and read_progress = ?",new String[]{userid,"1.0"}, null, null, "create_time desc");  
    	}
        return cur;  
    }
    /** 
     * 清空bookmark表
     *  
     * @return boolean
     */ 
    public boolean truncateBookmark(String userid){  
        String sql="";  
        try{  
            sql="delete from s_bookmark where userid = '"+userid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;  
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 清空资源表
     *  
     * @return boolean
     */ 
    public boolean truncateResource(String userid){  
        String sql="";  
        try{  
            sql="delete from s_resource where userid = '"+userid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 清空分类表
     *  
     * @return boolean
     */ 
    public boolean truncateCategory(String userid){  
        String sql="";  
        try{  
            sql="delete from s_category where userid = '"+userid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 清空分类表
     *  
     * @return boolean
     */ 
    public boolean truncateOperation(String userid){  
        String sql="";  
        try{  
            sql="delete from s_operation where userid = '"+userid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }  
    }
    /** 
     * 查询所有分类记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadAllCategory(String userid){  
        System.out.println("userid:"+userid);
        Cursor cur=db.query("s_category", new String[]{"id","category_name","create_time"}, "userid = ?",new String[]{userid}, null, null, null);  
          
        return cur;  
    }  
    public void close(){  
        db.close();  
    }  
    /** 
     * 取个人设置
     * @param token
     * @return 
     */ 
    public Cursor getSettings(String userid){

    	Cursor cur=db.query("s_settings", new String[]{"userid","access_token","sync_status","img_status","file_status","index_guide","unreadlist_guide","read_guide","category_guide","is_login","login_time"}, "userid = ?",new String[]{userid}, null, null, null);
    	return cur;
    }
    /** 
     * 标记引导已看
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setGuideRead(String userid, String guide_name){  
        String sql="";  
        try{  
            sql="update s_settings set "+guide_name+" = 1 where userid = '"+userid+"'";
            db.execSQL(sql);  
            System.out.println(sql);
            return true;  
        }catch(Exception e){
        	System.out.println(e); 
            return false;  
        }  
    }
    /** 
     * 重置引导状态
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean resetGuide(String userid){  
        String sql="";  
        try{  
            sql="update s_settings set index_guide = 0,unreadlist_guide = 0,read_guide = 0,category_guide = 0 where userid = '"+userid+"'";
            db.execSQL(sql);  
            System.out.println(sql);
            return true;  
        }catch(Exception e){
        	System.out.println(e); 
            return false;  
        }  
    }
    /** 
     * 个人设置 图片是否显示
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setSettingsImg(String userid, int status){  
        String sql="";  
        try{  
            sql="update s_settings set img_status = "+status+" where userid = '"+userid+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){
        	System.out.println(e); 
            return false;  
        }  
    }
    /** 
     * 个人设置 图片是否显示
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setSettingsWifi(String userid, int status){  
        String sql="";  
        try{  
            sql="update s_settings set sync_status = "+status+" where userid = '"+userid+"'";
            db.execSQL(sql);  
            System.out.println(sql);
            return true;  
        }catch(Exception e){
        	System.out.println(e);
            return false;  
        }  
    }
    /** 
     * 个人设置 是否保存到sd卡
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setSettingsSave(String userid, int status){  
        String sql="";  
        try{  
            sql="update s_settings set file_status = "+status+" where userid = '"+userid+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 新增离线操作
     * @param userid 
     * @param bookmarkid 
     * @return 
     */  
    public boolean addOperation(String userid, String key, String type, String method, String params){
    	Cursor cur = getOperationByIds(userid, key);
    	cur.moveToFirst();
        String sql="";  
        try{
        	if(cur.getCount()!=0){
	        	if(!"0".equals(key) && key!=null){//添加删除文章操作，并删除之前对此文章的更新操作
	        		if("delete".equals(type)){
	        			sql="delete from s_operation where key = '"+key+"' and userid = '"+userid+"'";
	        			db.execSQL(sql);
	        			System.out.println(sql);
	        			sql="insert into s_operation (userid, key, type, method, params, time)values('"+userid+"','"+key+"','"+type+"','"+method+"','"+params+"','"+currentTime+"')";
	        		}else if("update".equals(type)){
	        			do{
	        				if(method.equals(cur.getString(1))){
	        					sql="update s_operation set params = '"+params+"',time = '"+currentTime+"' where key = '"+key+"' and method = '"+method+"' and userid = '"+userid+"'";
	        				}
	        			}while(cur.moveToNext());
	        		}
	        	}else{//添加删除分类操作，并删除之前对此文章的更新操作
	        		if("delete".equals(type)){
	        			sql="delete from s_operation where key = '"+key+"' and userid = '"+userid+"'";
	        			db.execSQL(sql);
	        			do{
	        				if(!("insert".equals(cur.getString(0)))){
	        					sql="insert into s_operation (userid, key, type, method, params, time)values('"+userid+"','"+key+"','"+type+"','"+method+"','"+params+"','"+currentTime+"')";
	        				}
	        			}while(cur.moveToNext());
	        		}else if("update".equals(type)){
	        			cur.moveToFirst();
	        			do{
	        				if(method.equals(cur.getString(1))){
	        					sql="update s_operation set params = '"+params+"',time = '"+currentTime+"' where key = '"+key+"' and method = '"+method+"' and userid = '"+userid+"'";
	        				}
	        			}while(cur.moveToNext());
	        		}
	        	}
        	}else{
        		sql="insert into s_operation (userid, key, type, method, params, time)values('"+userid+"','"+key+"','"+type+"','"+method+"','"+params+"','"+currentTime+"')";
        	}
            System.out.println(sql);
            db.execSQL(sql);
            return true;
        }catch(Exception e){
        	e.printStackTrace();
            return false;
        }  
    }  
    /** 
     * 通过userid和bookmarkid取离线操作
     * @param userid 
     * @param bookmarkid 
     * @return 
     */
    public Cursor getOperationByIds(String userid, String key){

    	Cursor cur=db.query("s_operation", new String[]{"type","method","params"}, "userid = ? and key = ?",new String[]{userid,key}, null, null, null);
    	return cur;
    }
    /** 
     * 获取所有离线操作记录
     * @return 
     */
    public Cursor loadAllOperation(String userid){

    	Cursor cur=db.query("s_operation", new String[]{"key","type","method","params","time"}, "userid = ?",new String[]{userid}, null, null, null);
    	return cur;
    }
    /** 
     * 检查登录
     * @return 
     */
    public Cursor checkUserLogin(){
    	Cursor cur=db.query("s_settings", new String[]{"userid","access_token"}, "is_login = ?",new String[]{"1"}, null, null, "login_time desc","1");
    	return cur;
    }
    /** 
     * 取最近登录的用户
     * @return 
     */
    public Cursor getLastLoginUser(){
    	Cursor cur=db.query("s_settings", new String[]{"userid"}, null,null, null, null, "login_time desc","1");
    	return cur;
    }
    /** 
     * 注销登录
     * @return 
     */
    public boolean logout(String userid){

    	String sql="";  
        try{  
            sql="update s_settings set is_login = 0 where userid = '"+userid+"'";
            db.execSQL(sql);
            System.out.println(sql);
            return true;  
        }catch(Exception e){
        	e.printStackTrace();
            return false;  
        }
    }
}  

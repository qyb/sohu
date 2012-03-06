package com.sohu.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sohu.xml.model.ArticleList;
  
/** 
 * 数据库操作工具类 
 *  
 * @author 2011zhouhang@gmail.com 
 *  
 */  
public class DBHelper {  
//    private static final String TAG = "sohu";// 调试标签  
  
    private static final String DATABASE_NAME = "sohu.db";// 数据库名  
    SQLiteDatabase db;  
    Context context;//应用环境上下文   Activity 是其子类  
    
    String currentTime;
  
    public DBHelper(Context _context) {  
        context = _context;  
        //开启数据库  
        
        Date utilDate = new java.util.Date();
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");//获取中国的时区
	    myFmt.setTimeZone(timeZoneChina);//设置系统时区
	    currentTime = myFmt.format(utilDate);
        
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);  
        
        CreateTable();  
//        Log.v(TAG, "db path=" + db.getPath());  
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
            //文章表
            db.execSQL("create table if not exists s_articles (" +  
                    "key varchar(200) PRIMARY KEY not null,"  //autoincrement 
                    + "title text not null,"   
                    + "url varchar(200) not null," 
                    + "download_url varchar(200) not null," 
                    + "image_urls varchar(500)," 
                    + "progress float not null,"
                    + "category_id integer," 
                    + "create_time timestamp not null," 
                    + "update_time timestamp" 
                    + ");");  
            //分类表
            db.execSQL("create table if not exists s_category (" +  
                    "id INTEGER PRIMARY KEY autoincrement not null,"  
                    + "category_name TEXT not null,"   
                    + "create_time timestamp"   
                    + ");");  
            //个人设置表
            db.execSQL("create table if not exists s_settings (" +  
                    "token varchar(200) PRIMARY KEY not null,"  
                    + "img_status int not null,"   
                    + "file_status int not null," 
                    + "is_login int not null,"
                    + "login_time timestamp"   
                    + ");");
//            db.execSQL("insert into s_settings (token, img_status, file_status, is_login, login_time)values('649cfef6a94ee38f0c82a26dc8ad341292c7510e',0,0,1,'"+currentTime+"')");
//            Log.v(TAG, "Create Table t_user ok");  
        } catch (Exception e) {  
//            Log.v(TAG, "Create Table t_user err,table exists.");  
        }  
    }  
    /** 
     * 新增分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public boolean addCategory(String category_name){  
        String sql="";  
        try{  
            sql="insert into s_category (category_name, create_time)values('"+category_name+"','"+currentTime+"')";  
            System.out.println(sql);
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
    public boolean updateCategoryName(int category_id, String category_name){  
        String sql="";  
        try{  
            sql="update s_category set category_name = '"+category_name+"' where id = "+category_id;//, create_time)values('"+category_name+"','"+currentTime+"')";  
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
    public boolean deleteCategory(int category_id){  
        String sql="";  
        try{  
            sql="delete from s_category where id = "+category_id;//, create_time)values('"+category_name+"','"+currentTime+"')";  
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 添加文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean insertArticle(ArticleList article){  
        String sql="";  
        try{  
            sql="insert into s_articles(key,title,url,download_url,image_urls,progress,category_id,create_time)" +
            		"values('"+article.getKey()+"','"+article.getTitle()+"','"+article.getUrl()+"','"+article.getDownloadUrl()+"'" +
            				",'"+article.getImageUrls()+"','"+article.getIsRead()+"',0,'"+article.getCreateTime()+"')";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){    
        	System.out.println(e);
            return false;  
        }  
    }
    /** 
     * 删除文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean deleteArticle(String key){  
        String sql="";  
        try{  
            sql="delete from s_articles where key = '"+key+"'";
            System.out.println(sql);
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 修改文章
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean updateArticle(int category_id,String title,String key){  
        String sql="";  
        try{  
            sql="update s_articles set title = '"+title+"',category_id = "+category_id+" where key = '"+key+"'";
            db.execSQL(sql);  
            return true;  
              
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 通过key取此篇文章分类
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public Cursor getCategoryByKey(String key){  
    	Cursor cur;
        cur=db.query("s_articles", new String[]{"category_id"}, "key = ?",new String[]{key}, null, null, null);  
        return cur;  
    } 
    /** 
     * 通过key取此文章是否已经设为已读
     * @param category_name 
     * @param create_time 
     * @return 
     */  
    public Cursor getProgressByKey(String key){  
    	Cursor cur;
        cur=db.query("s_articles", new String[]{"progress"}, "key = ?",new String[]{key}, null, null, null);  
        return cur;  
    } 
    /** 
     * 设文章为已读状态
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setArticleRead(String key){  
        String sql="";  
        try{  
            sql="update s_articles set progress = '1' where key = '"+key+"'";
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
    public Cursor loadArticlesByCategory(String category){  
    	Cursor cur;
    	cur=db.query("s_articles", new String[]{"key","title","url","download_url","image_urls","progress","create_time"}, "category_id = ?",new String[]{category}, null, null, null);  
        return cur;  
    } 
    /** 
     * 根据类型查询文章记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadAllArticles(String type){  
    	Cursor cur;
    	if("0".equals(type)){
    		//未读
    		cur=db.query("s_articles", new String[]{"key","title","url","download_url","image_urls","progress","create_time"}, "progress = ?",new String[]{"0.0"}, null, null, null);  
    	}else{
    		//已读
    		cur=db.query("s_articles", new String[]{"key","title","url","download_url","image_urls","progress","create_time"}, "progress != ?",new String[]{"0.0"}, null, null, null);  
    	}
        return cur;  
    }  
    /** 
     * 清空文章表
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */ 
    public boolean truncateArticle(){  
        String sql="";  
        try{  
            sql="delete from s_articles";
            db.execSQL(sql);  
//            Log.v(TAG,"insert Table t_user ok");  
            return true;  
              
        }catch(Exception e){  
//            Log.v(TAG,"insert Table t_user err ,sql: "+sql);  
            return false;  
        }  
    }
    /** 
     * 查询所有分类记录 
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor loadAllCategory(){  
          
        Cursor cur=db.query("s_category", new String[]{"id","category_name","create_time"}, null,null, null, null, null);  
          
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
    public Cursor getSettings(String token){

    	Cursor cur=db.query("s_settings", new String[]{"token","img_status","file_status","is_login","login_time"}, "token = ?",new String[]{token}, null, null, null);  
        
    	return cur;
    }
    /** 
     * 个人设置 图片是否显示
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setSettingsImg(String key, int status){  
        String sql="";  
        try{  
            sql="update s_settings set img_status = "+status+" where token = '"+key+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 个人设置 是否保存到sd卡
     * @param category_name 
     * @param create_time 
     * @return 
     */ 
    public boolean setSettingsSave(String key, int status){  
        String sql="";  
        try{  
            sql="update s_settings set file_status = "+status+" where token = '"+key+"'";
            db.execSQL(sql);  
            return true;  
        }catch(Exception e){  
            return false;  
        }  
    }
    /** 
     * 自定义查询
     *  
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet 
     */  
    public Cursor query(){  
            
        Cursor cur=db.query("t_user", new String[]{"_ID","NAME"}, null,null, null, null, null);
        
//        Cursor c=db.rawQuery( 
//        	     "SELECT name FROM sqlite_master WHERE type='table' AND name='mytable'", null); 
//        c.moveToFirst(); 
//        while (!c.isAfterLast()) { 
//            int id=c.getInt(0); 
//            String name=c.getString(1); 
//            int inventory=c.getInt(2); 
//            // do something useful with these 
//            c.moveToNext(); 
//          } 
//          c.close();
        return cur;  
    }  
}  

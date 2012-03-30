package com.sohu.kan;

import android.app.Application;

public class Global extends Application {
	
	private String access_token;
	private String userid;
	private Boolean wifi_sync_flag;
	private Boolean img_flag;
	private Boolean save_flag;
	private String saveToSD;
	
	private String index_guide;
	private String unreadlist_guide;
	private String read_guide;
	private String category_guide;
	private Integer last_sync_record;
	
	public Integer getLastSyncRecord() {
		return last_sync_record;
	}
	public void setLastSyncRecord(Integer last_sync_record) {
		this.last_sync_record = last_sync_record;
	}
	public String getIndexGuide() {
		return index_guide;
	}
	public void setIndexGuide(String index_guide) {
		this.index_guide = index_guide;
	}
	public String getUnreadlistGuide() {
		return unreadlist_guide;
	}
	public void setUnreadlistGuide(String unreadlist_guide) {
		this.unreadlist_guide = unreadlist_guide;
	}
	public String getReadGuide() {
		return read_guide;
	}
	public void setReadGuide(String read_guide) {
		this.read_guide = read_guide;
	}
	public String getCategoryGuide() {
		return category_guide;
	}
	public void setCategoryGuide(String category_guide) {
		this.category_guide = category_guide;
	}
	
	
	public String getAccessToken(){
        return access_token;
    }   
    public void setAccessToken(String access_token){
        this.access_token = access_token;
    }
    
    public String getUserId(){
        return userid;
    }   
    public void setUserId(String userid){
        this.userid = userid;
    }
	
	public Boolean getWifiFlag(){
        return wifi_sync_flag;
    }   
    public void setWifiFlag(Boolean s){
        this.wifi_sync_flag = s;
    }
	
    public Boolean getImgFlag(){
        return img_flag;
    }   
    public void setImgFlag(Boolean s){
        this.img_flag = s;
    }
    
    public Boolean getSaveFlag(){
        return save_flag;
    }   
    public void setSaveFlag(Boolean s){
        this.save_flag = s;
    }
    
    public String getSavePath(){
        return saveToSD;
    }   
    public void setSavePath(String s){
        this.saveToSD = s;
    }
   
}

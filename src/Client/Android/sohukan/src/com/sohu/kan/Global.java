package com.sohu.kan;

import android.app.Application;

public class Global extends Application {
	
	private Boolean img_flag;
	private Boolean save_flag;
	private String saveToSD;
	
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

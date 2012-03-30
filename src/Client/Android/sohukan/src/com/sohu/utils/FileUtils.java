package com.sohu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
  
public class FileUtils {  
    private String PATH;  
      
    public String getSDPATH(){  
        return PATH;  
    }  
    
    public FileUtils(){
//    	PATH = Environment.getFilesDir()+"/";
    	PATH = "/data/data/com.sohu.kan/files/";
    	createDir("");
    }
    
    public FileUtils(String type){  //type类型是否存sd
    	if(!"sd".equals(type.toLowerCase())){
    		//机身内存路径
            PATH = "/data/data/com.sohu.kan/files/";
    	}else{
	        //sd存储路径
	        // /SDCARD  
	        PATH = Environment.getExternalStorageDirectory()+"/sohukan/";  
    	}
    	createDir(""); 
    }  
      
    /*  
     * 创建文件
     */  
    public File createFile(String fileName) throws IOException{  
        File file = new File(PATH+fileName);  
        file.createNewFile();  
        return file;  
    }  
      
    /*  
     * 创建目录
     */  
      
    public File createDir(String dirName){  
        File dir = new File(PATH+dirName);  
        dir.mkdir();  
        return dir;  
    }  
      
    /*  
     * 文件是否存在
     */  
    public boolean isFileExist(String fileName){  
        File file = new File(PATH+fileName);  
        return file.exists();  
    }  
      
    /*  
     * 将InputStream写入到sd卡  
     */  
      
    public File write2SDFromInput(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try {  
            //创建目录
            createDir(path);  
            //创建文件
            file = createFile(path+fileName);  
            //输出流
            output = new FileOutputStream(file);  
            int bytesRead = 0;
            //创建byte对象
            byte buffer[] =  new byte[8192];  
            //写入byte
            while((bytesRead = input.read(buffer,0,8192))!=-1){  
                output.write(buffer,0,bytesRead);  
            }  
            //调试
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input,  
//                    "utf-8"));  
//            String line = null;  
//                while ((line = reader.readLine()) != null) {  
//                    System.out.println(line+"-----------------------------------");
//                }  
            
            
            //刷新
            output.flush();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                //关闭流
                output.close();  
                input.close();
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file;  
    }  
    
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public void copyFile(String oldPath, String newPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //文件存在时 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   System.out.println(bytesum); 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   } 

   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public void copyFolder(String oldPath, String newPath) { 

       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ //File.separator windonws代表\ unix代表/
                   temp=new File(oldPath+file[i]); 
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制整个文件夹内容操作出错"); 
           e.printStackTrace(); 

       } 

   }
   
   public void del(String filepath) throws IOException{  
	    File f = new File(filepath);//定义文件路径
	    if(f.exists() && f.isDirectory()){//判断是文件还是目录  
	    	if(f.listFiles().length==0){//若目录下没有文件则直接删除  
	    		f.delete();
	        }else{//若有则把文件放进数组，并判断是否有下级目录  
	        	File delFile[]=f.listFiles();
	           	int i =f.listFiles().length;
	            for(int j=0;j<i;j++){
	            	if(delFile[j].isDirectory()){
	            		del(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径 
	            	}  
	            	delFile[j].delete();
	            }
	        }
	    }else{
	    	System.out.println("不存在此目录");
	    	if(f.isFile()){
	    		f.delete();
	    	}
	    	System.out.println("删除此文件");
	    }
   }
      
}  
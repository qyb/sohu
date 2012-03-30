package com.sohu.kan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Window;

import com.sohu.database.DBHelper;
import com.sohu.utils.RAPI;

public class Start extends Activity {
	
	Handler handler = null;
	private Global global; 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start);
        global = (Global)getApplication();
        
        handler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) {  
                Bundle b = msg.getData();
                String userid = b.getString("userid");
                String access_token = b.getString("access_token");
                if(!"".equals(userid) && userid !=null){
                	//做同步
                	RAPI rapi = new RAPI(Start.this,access_token,userid);
                	SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(Start.this);
        			rapi.refreshList(preferences,global);
        			
                	Intent intent = new Intent(Start.this,SohuKan.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("userid", userid);
					bundle.putBoolean("hasSync", true);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
                }else{
                	Intent intent = new Intent(Start.this,Login.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
                }
                switch (9) {
	        		case 0:
	        			/*注意：此方法只能在startActivity和finish方法之后调用。
	        			  第一个参数为第一个Activity离开时的动画，第二参数为所进入的Activity的动画效果*/
	        			overridePendingTransition(R.anim.fade, R.anim.hold);
	        			break;
	        		case 1:
	        			overridePendingTransition(R.anim.my_scale_action,
	        					R.anim.my_alpha_action);
	        			break;
	        		case 2:
	        			overridePendingTransition(R.anim.scale_rotate,
	        					R.anim.my_alpha_action);
	        			break;
	        		case 3:
	        			overridePendingTransition(R.anim.scale_translate_rotate,
	        					R.anim.my_alpha_action);
	        			break;
	        		case 4:
	        			overridePendingTransition(R.anim.scale_translate,
	        					R.anim.my_alpha_action);
	        			break;
	        		case 5:
	        			overridePendingTransition(R.anim.hyperspace_in,
	        					R.anim.hyperspace_out);
	        			break;
	        		case 6:
	        			overridePendingTransition(R.anim.push_left_in,
	        					R.anim.push_left_out);
	        			break;
	        		case 7:
	        			overridePendingTransition(R.anim.push_up_in,
	        					R.anim.push_up_out);
	        			break;
	        		case 8:
	        			overridePendingTransition(R.anim.slide_left,
	        					R.anim.slide_right);
	        			break;
	        		case 9:
	        			overridePendingTransition(R.anim.wave_scale,
	        					R.anim.my_alpha_action);
	        			break;
	        		case 10:
	        			overridePendingTransition(R.anim.zoom_enter,
	        					R.anim.zoom_exit);
	        			break;
	        		case 11:
	        			overridePendingTransition(R.anim.slide_up_in,
	        					R.anim.slide_down_out);
	        			break;
        		}
            }  
        }; 
        
        new Thread(){
		   public void run(){
			    Message msg = new Message();
//			    try {
//					sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			    DBHelper db = new DBHelper(Start.this);
				Cursor cur = db.checkUserLogin();
				if(cur.getCount()!=0){
					if(cur.moveToFirst()){
						Bundle bundle = new Bundle();
						bundle.putString("userid", cur.getString(0));
						bundle.putString("access_token", cur.getString(1));
						msg.setData(bundle);
			            handler.sendMessage(msg);
			        }
				}else{
					handler.sendMessage(msg);
				}
		        cur.close();
				db.close();
		   }
        }.start();
        
		
    }
    
}

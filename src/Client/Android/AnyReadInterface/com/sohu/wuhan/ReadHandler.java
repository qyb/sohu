/**
 * 
 */
package com.sohu.wuhan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * @author Leon
 *
 */
public class ReadHandler extends Handler {
	
	private TextView tv;
	
	public ReadHandler(TextView __tv) {
		tv = __tv;
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		if (null != tv) {
			
			Bundle data = msg.getData();
			Constant.Error error = (Constant.Error)data.getSerializable("error");
			if (error != Constant.Error.OK) {
				//hint to users.
				tv.setText("Server Error");
			} else {
				String rtns = data.getString("result");
				if (null != rtns)
					tv.setText(rtns);
			}
		}
	}
}

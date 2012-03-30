package com.sohu.kan;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class BrowserCollection extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.browser_collection);
	}
}

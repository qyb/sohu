/**
 *
 * @desc: IReadable means iterface to acquire Server Information
 * 
 */
package com.sohu.wuhan;

import java.util.Hashtable;

import android.os.Handler;

import com.sohu.wuhan.Constant.Error;

/**
 * @author Leon
 * 
 */
public interface IReadable {
	boolean init(String __token);
	Error getError();
	void cancel();
	
	/* ======================================================================= */
	
	Error createArticle(String __url); // C

	String probeArticle(); // P

	String readArticle(String key); // R

	Error updateArticle(String key, Hashtable<String, String> args); // U

	Error deleteArticle(String key); // D
	
	/* article ======================================================================= */

	boolean asyncCreateArticle(String __url, Handler __handler);

	boolean asyncProbeArticle(Handler __handler);
	
	boolean asyncReadArticle(String key, Handler __handler);
	
	boolean asyncUpdateArticle(String key, Hashtable<String, String> args, Handler __handler); 
		
	boolean asyncDeleteArticle(String key, Handler __handler); 
	
	// 非article接口一律不再提供阻塞调用
	/* category ======================================================================= */
	boolean asyncReadCategory(Handler __handler);
	boolean asyncDeleteCategory(String __category, Handler __handler);
	boolean asyncCreateCategory(String __category, Handler __handler);
	
	/* image ======================================================================= */
	
	boolean asyncReadImage(String key, Handler __handler);
	boolean asyncReadRawImage(String key, Handler __handler);
	
	
}

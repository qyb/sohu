/**
 * 
 */
package com.sohu.wuhan;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.net.Proxy;

import com.sohu.wuhan.Constant.Error;

/**
 * @author Leon
 *
 */
public class DirectUrl {
	
	private Proxy proxy = null;
	public Error error = Error.OK;
	
	public void setProxy(Proxy __proxy) {
		proxy = __proxy;
	}
	
	public void delProxy() { proxy = null; }
	
	public boolean isProxy() { return proxy != null; } 
	
	protected String call_url(String str, String method, String content) {
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		BufferedReader br = null;
		
		if (null == str) {
			error = Error.ParamNull;
			return null;
		}
		
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			error = Error.Url;
			return null;
		}
		
		if (null == method || "" == method) {
			method = "GET";
		}
		
		try {
			if (isProxy())
				conn = (HttpURLConnection) url.openConnection(proxy);
			else {
				conn = (HttpURLConnection) url.openConnection();
			}
			
			if (null == conn) {
				error = Error.Fail;
				return null;
			}
			
			{
				conn.setRequestMethod(method);
				if (method.compareToIgnoreCase("POST") == 0)
					conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setInstanceFollowRedirects(false);
				conn.setConnectTimeout(Constant.HTTP_TIMEOUT);
			}
			
			{
				conn.connect();
				if (null != content) {
					dos = new DataOutputStream(conn.getOutputStream()); dos = new DataOutputStream(conn.getOutputStream());
					dos.writeBytes(content);
					dos.flush();
					dos.close();
					dos = null;
				}
				
				if (-1 != conn.getResponseCode()) {
					String tmp = null;
					String output = "";
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while (null != (tmp = br.readLine())) {
						output += tmp;
					}
					error = Error.OK;
					return output;
				} else 
					error = Error.Fail;
			}
		} catch (SocketTimeoutException ste)  {
			error = Error.Timeout;
			return null;
		} catch (IOException e) {
			error = Error.NetIO;
			return null;
		} catch (Exception e) {
			error = Error.Fail;
			return null;
		} finally {
			if (null != br) 
				try {
					br.close();
				} catch (Exception ee) {}
			
			if (null != dos)
				try {
					dos.flush();
					dos.close();
				} catch (Exception ee) {}
			
			if (null != conn)
				conn.disconnect();
			
			dos		= null;
			conn	= null;
		}
		
		error = Error.Fail;
		return null;
	}	
}

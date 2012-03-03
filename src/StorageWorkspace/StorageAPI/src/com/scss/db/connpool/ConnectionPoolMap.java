package com.scss.db.connpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * @Title:连接池Map:适用于多数据库的情况。跨数据库的事务交给第三方框架去处理。<br>
 * @Description: 根据驱动、URL、用户名、密码，取出不同的数据库连接<br>
 * @Deprecated ：
 * 
 * @author Jack.wu.xu 2011-10-10
 * @version 1.0
 * 
 */
public class ConnectionPoolMap {
	private static ConcurrentHashMap<String, ConnectionPool> queue = new ConcurrentHashMap<String, ConnectionPool>();
//	private static final Logger logger = Logger.getLogger("scss/POOLMAP",
//			Logger.ALL, true);
	private static final Logger logger = Logger.getLogger(ConnectionPoolMap.class);

	public static Connection getConnection(String driver, String url,
			String username, String password, int min, int max) {
		String key = driver + url + username + password;
		ConnectionPool connectionPool = queue.get(key);
		try {
			if (connectionPool != null) {
				return connectionPool.getConnection();
			} else {
				ConnectionPool cp = new ConnectionPool(url, username, password,
						driver, min, max);
				queue.put(key, cp);
				return cp.getConnection();
			}
		} catch (SQLException e) {
			logger.debug(e);
			//e.printStackTrace();
		}
		return null;
	}

}

package com.scss.db.connpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.bfsapi.utility.Logger;

public class ConnectionPoolMap {
	private static ConcurrentHashMap<String, ConnectionPool> queue = new ConcurrentHashMap<String, ConnectionPool>();
	private static final Logger logger = Logger.getLogger("ITMANAGER2/POOLMAP",
			Logger.ALL, true);

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
			logger.exception(e);
			e.printStackTrace();
		}
		return null;
	}

}

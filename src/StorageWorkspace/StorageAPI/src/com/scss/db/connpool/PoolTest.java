package com.scss.db.connpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.scss.utility.Logger;

public class PoolTest implements Runnable {
	private long time = 1000;
	private ConnectionPool connectionPool;
	private static final Logger logger = Logger.getLogger("ConnPool/TEST",
			Logger.ALL, true);
	private String name;

	public void run() {
		try {
			while (true) {
				logger.debugT("线程" + name + "开始去数据库连接");
				Connection connection = connectionPool.getConnection();
				logger.debugT("线程" + name + "已经取到链接" + connection.toString());
				String updateSql = "insert into test values('"
						+ UUID.randomUUID().toString().substring(10) + "')";
				Statement stmt = connection.createStatement();
				stmt.execute(updateSql);
				stmt.close();
				connection.close();
				Thread.sleep(time);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public PoolTest(ConnectionPool connectionPool, long time, String name) {
		super();
		this.time = time;
		this.name = name;
		this.connectionPool = connectionPool;
	}
}

package com.scss.db.connpool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.scss.utility.Logger;


/**
 * @Title:数据库连接池:<br>
 * @Description: <br>
 * @Deprecated ：<br>
 *             依赖于ConnectionProxy extends Connection<br>
 *             这个连接池有3个辅助线程去维护数据库链接队列，辅助线程的数量是不可修改的。<br>
 *             辅助线程完成：<br>
 *             去掉队列中超时的链接和已关毕的链接，<br>
 *             并且当队列的Connection的数量小于设置的最小值时就开始整理队列<br>
 *             辅助线程何时被唤醒：<br>
 *             当队列中可用连接少于最小连接数时<br>
 *             当线程等待时间超出设定的的超时时间的时<br>
 *             取链接同步：<br>
 *             做了优化，仅对被取出的链接做同步<br>
 *             当空闲链接不够使用时才唤醒等待线程<br>
 *             连接池配置：<br>
 *             max 最大连接数<br>
 *             min 最小链接数<br>
 *             url 数据库链接URL<br>
 *             username用户名<br>
 *             password密码<br>
 *             driverClassName驱动名称<br>
 * 
 * @author Jack.wu.xu 2011-10-10
 * @version 1.0
 * 
 */
public class ConnectionPool implements Runnable, DataSource {

	public ConnectionPool() {
	}

	public ConnectionPool(String configFileName) {

		String path = ConnectionPool.class.getResource("/").toString();
		if (path != null && path.indexOf("/bin/") != -1) {
			path = path.substring(6, path.indexOf("/bin") + 4)
					+ configFileName;
		} else {
			path = path.substring(6, path.indexOf("/classes") + 8)
					+ configFileName;
		}
		InputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Properties config = new Properties();
		try {
			config.load(fis);
			this.max = Integer.parseInt(config.getProperty("poolMaxSize"));
			this.min = Integer.parseInt(config.getProperty("poolMinSize"));
			this.url = config.getProperty("url");
			this.username = config.getProperty("username");
			this.password = config.getProperty("password");
			this.driverClassName = config.getProperty("driverClassName");
			init();
			workThread();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final ReentrantLock rlock = new ReentrantLock();
	private boolean isInit = false;
	private long timeout = 5 * 60 * 1000;
	// private int MAX_CONNECTION_SIZE = 50;
	private List<ConnectionProxy> queue = new LinkedList<ConnectionProxy>();
	private int max = 0;
	private int min = 0;
	private static final int defultMin = 10;
	private static final int defultMax = 20;
	private String url;
	private String username;
	private String password;
	private String driverClassName;
	private final int countThread = 3;
	private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1,
			countThread, 60 * 2, TimeUnit.SECONDS,
			new ArrayBlockingQueue<Runnable>(countThread + 1),
			new ThreadPoolExecutor.DiscardOldestPolicy());;
	private static final Logger logger = Logger.getLogger("scss/pool",
			Logger.ALL, true);
	private boolean lock = false;
	private PrintWriter logWriter;

	public Connection getConnection() throws SQLException {
		if (!isInit) {
			init();
			workThread();
		}
		for (ConnectionProxy conn : queue) {
			if (conn.isUseing()) {
				// synchronized (this) {
				// rlock.lock();
				// try {
				synchronized (conn) {
					if (conn.isUseing()) {
						conn.setLive(-1);
						// logger.debugT("取到 链接 ： " + conn.toString());
						// this.notify();
						return conn;
					}
				}
				// } finally {
				// rlock.unlock();
				// }
			}
		}
		Connection connection = DriverManager.getConnection(url, username,
				password);
		ConnectionProxy result = new ConnectionProxy(connection);
		result.setReadlyCloseFlag();
		// logger.debugT("新建链接 ： " + result.toString());
		synchronized (this) {
			lock = true;
			this.notify();
			return result;
		}

	}

	public ConnectionPool(String url, String username, String password,
			String driverClassName, int poolMinSize, int poolMaxSize) {
		this.max = poolMaxSize;
		this.min = poolMinSize;
		this.url = url;
		this.username = username;
		this.password = password;
		this.driverClassName = driverClassName;
		init();
		workThread();

	}

	private void workThread() {
		for (int i = 0; i < countThread; i++) {
			Thread thread = new Thread(this);
			threadPool.execute(thread);
		}
	}

	public ConnectionPool(String url, String username, String password,
			String diver) {
		this(url, username, password, diver, defultMin, defultMax);
	}

	private void init() {
		try {
			Class.forName(driverClassName);
			for (int i = 0; i < max; i++) {
				long start = System.currentTimeMillis();
				Connection connection = DriverManager.getConnection(url,
						username, password);
				long end = System.currentTimeMillis();
				System.out.println("初始化 ,第" + (i + 1) + "个链接用时："
						+ (end - start) + "毫秒。");
				ConnectionProxy cp = new ConnectionProxy(connection);
				queue.add(cp);
			}
			isInit = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void timeOutClose() {
		// Iterator<ConnectionProxy> iterator = queue.iterator();
		for (int i = 0; i < queue.size(); i++) {
			ConnectionProxy conn = queue.get(i);
			if (conn.isUseing()) {
				// 如果超时
				synchronized (conn) {
					if (System.currentTimeMillis() - conn.getLastUseTime() >= this.timeout)
						if (conn.isUseing()) {
							// logger.debugT("关闭掉超时的链接： " + conn.toString());
							conn.readlyClose();
							queue.remove(conn);
							i--;
						} else {
							// logger.debugT("将超时的链接设置为真实关闭： "
							// +conn.toString());
							conn.setReadlyCloseFlag();
							queue.remove(conn);
							i--;
						}
				}
			}
		}
	}

	public void run() {
		while (true) {
			try {
				// logger.debugT("Fetch kpi from Queue: ");
				if (!lock) {
					synchronized (this) {
						this.wait(timeout);
						lock = true;
					}
				} else {
					// long st = System.currentTimeMillis();
					clearPool();
					// long et = System.currentTimeMillis();
				}
			} catch (Exception e) {
				logger.exception(e);
			}
		}

	}

	private void clearPool() {
		this.lock = false;
		timeOutClose();
		resetQueue();
	}

	private void resetQueue() {
		int size = queue.size();
		if (size < min) {
			try {
				// Class.forName(diver);
				for (int i = 0; i < ((max + min) / 2) - size; i++) {

					// long start = System.currentTimeMillis();
					Connection connection = DriverManager.getConnection(url,
							username, password);
					// long end = System.currentTimeMillis();
					// System.out.println("初始化 ,第" + (i + 1) + "个链接用时："
					// + (end - start) + "毫秒。");
					ConnectionProxy cp = new ConnectionProxy(connection);
					queue.add(cp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ConnectionPool connectionPool = new ConnectionPool(
				"jdbc:oracle:thin:@127.0.0.1:1521:ITMANAGER", "xuyongping",
				"xuyongping", "oracle.jdbc.driver.OracleDriver", 20, 50);
		// Thread t = new Thread(new PoolTest(connectionPool, 1000, "Thread
		// 0-1"));
		// t.start();
		long start = System.currentTimeMillis();
		try {
			Connection connection = connectionPool.getConnection();
			for (int i = 0; i < 1000000; i++) {

				String updateSql = "insert into test values('"
						+ UUID.randomUUID().toString().substring(10) + "')";
				Statement stmt = connection.createStatement();
				stmt.execute(updateSql);
				stmt.close();

			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("kaishi  :" + (end - start) + "ms");
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout * 1000;
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		throw new UnsupportedOperationException(
				"getConnectionString username, String password) is not supported");
	}

	public PrintWriter getLogWriter() throws SQLException {
		return this.logWriter;
	}

	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException(
				"getLoginTimeout is unsupported.");
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException(
				"setLoginTimeout is unsupported.");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		return null;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public long getTimeout() {
		return timeout;
	}

}

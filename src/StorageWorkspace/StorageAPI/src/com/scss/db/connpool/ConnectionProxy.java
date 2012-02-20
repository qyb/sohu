package com.scss.db.connpool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class ConnectionProxy implements Connection {
	private Connection conn = null;
	private Object lock = new Object();
	//
	private int live = 1;// 0是真实关闭，1是空闲，-1是正在被使用。
	private long lastUsedTime = 0;
	private boolean readlyClose = false;

	
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.isWrapperFor(iface);
	}

	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.unwrap(iface);
	}

	
	public void clearWarnings() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.clearWarnings();
	}

	
	public void close() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		if (this.readlyClose) {
			this.conn.close();
		} else if (conn != null && !conn.isClosed()) {
			this.live = 1;
		} else {
			this.live = 0;
		}

	}

	
	public void commit() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.commit();
	}

	
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createArrayOf(typeName, elements);
	}

	
	public Blob createBlob() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createBlob();
	}

	
	public Clob createClob() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createClob();
	}

	
	public NClob createNClob() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createNClob();
	}

	
	public SQLXML createSQLXML() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createSQLXML();
	}

	
	public Statement createStatement() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createStatement();
	}

	
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createStatement(resultSetType, resultSetConcurrency);
	}

	
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.createStruct(typeName, attributes);
	}

	
	public boolean getAutoCommit() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getAutoCommit();
	}

	
	public String getCatalog() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getCatalog();
	}

	
	public Properties getClientInfo() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getClientInfo();
	}

	
	public String getClientInfo(String name) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getClientInfo(name);
	}

	
	public int getHoldability() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getHoldability();
	}

	
	public DatabaseMetaData getMetaData() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getMetaData();
	}

	
	public int getTransactionIsolation() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getTransactionIsolation();
	}

	
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getTypeMap();
	}

	
	public SQLWarning getWarnings() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.getWarnings();
	}

	
	public boolean isClosed() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
//		if (readlyClose && conn != null) {
			return conn.isClosed();
//		} else if (live == -1) {
//			return false;
//		} else if (live == 1 || live == 0) {
//			return true;
//		}
//		return false;
	}

	
	public boolean isReadOnly() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.isReadOnly();
	}

	
	public boolean isValid(int timeout) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.isValid(timeout);
	}

	
	public String nativeSQL(String sql) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.nativeSQL(sql);
	}

	
	public CallableStatement prepareCall(String sql) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareCall(sql);
	}

	
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql);
	}

	
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql, autoGeneratedKeys);
	}

	
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql, columnIndexes);
	}

	
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql, columnNames);
	}

	
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.releaseSavepoint(savepoint);
	}

	
	public void rollback() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.rollback();
	}

	
	public void rollback(Savepoint savepoint) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.rollback(savepoint);
	}

	
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.setAutoCommit(autoCommit);

	}

	
	public void setCatalog(String catalog) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.setCatalog(catalog);
	}

	
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		lastUsedTime = System.currentTimeMillis();
		conn.setClientInfo(properties);
	}

	
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		lastUsedTime = System.currentTimeMillis();
		conn.setClientInfo(name, value);

	}

	
	public void setHoldability(int holdability) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.setHoldability(holdability);
	}

	
	public void setReadOnly(boolean readOnly) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.setReadOnly(readOnly);
	}

	
	public Savepoint setSavepoint() throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.setSavepoint();
	}

	
	public Savepoint setSavepoint(String name) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		return conn.setSavepoint(name);
	}

	
	public void setTransactionIsolation(int level) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		this.conn.setTransactionIsolation(level);
	}

	
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		lastUsedTime = System.currentTimeMillis();
		conn.setTypeMap(map);
	}

	public int getLive() {
		return live;
	}

	protected void setLive(int live) {
		synchronized (lock) {
			this.live = live;
		}

	}

	public ConnectionProxy(Connection conn) {
		super();
		lastUsedTime = System.currentTimeMillis();
		this.conn = conn;
	}

	protected long getLastUseTime() {
		return lastUsedTime;
	}

	protected boolean isUseing() {
		try {
			if (live == 1 && conn != null && !conn.isClosed())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void readlyClose() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
				this.live = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void setReadlyCloseFlag() {
		this.readlyClose = true;

	}

	protected boolean isClosedOrNull() {
		try {
			if (conn != null && !conn.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}

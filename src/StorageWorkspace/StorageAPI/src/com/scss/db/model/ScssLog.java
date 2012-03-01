package com.scss.db.model;

import java.util.Date;

/**
 * ScssLog entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssLog extends Resource implements java.io.Serializable {

	// Fields

	private Long id;
	private Integer level = Log.INFO;
	private String action = "R";
	private Long userId;
	private String resourceType = "O";
	private String resourceId;
	private String server = "";
	private String clientName = "Andriod";
	private String clientAddr;
	private Date timestamp = new Date();
	private String message;

	// Constructors

	/** default constructor */
	public ScssLog() {
	}
	
	public ScssLog(Resource r,Accessor a) {
	}

	/** minimal constructor */
	public ScssLog(Long id, Integer level, String action, Long userId,
			String resourceType, String resourceId, String server,
			Date timestamp, String message) {
		this.id = id;
		this.level = level;
		this.action = action;
		this.userId = userId;
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.server = server;
		this.timestamp = timestamp;
		this.message = message;
	}

	/** full constructor */
	public ScssLog(Long id, Integer level, String action, Long userId,
			String resourceType, String resourceId, String server,
			String clientName, String clientAddr, Date timestamp, String message) {
		this.id = id;
		this.level = level;
		this.action = action;
		this.userId = userId;
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.server = server;
		this.clientName = clientName;
		this.clientAddr = clientAddr;
		this.timestamp = timestamp;
		this.message = message;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientAddr() {
		return this.clientAddr;
	}

	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
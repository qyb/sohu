package com.scss.db.model;

/**
 * ScssUser entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssUser implements java.io.Serializable {

	// Fields

	private Long id;
	private String sohuId;
	private String accessId;
	private String accessKey;
	private String status="B";

	// Constructors
	public ScssUser(Long id, String sohuId, String accessId, String accessKey,
			String status) {
		super();
		this.id = id;
		this.sohuId = sohuId;
		this.accessId = accessId;
		this.accessKey = accessKey;
		this.status = status;
	}

	/** default constructor */
	public ScssUser() {
	}


	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSohuId() {
		return this.sohuId;
	}

	public void setSohuId(String sohuId) {
		this.sohuId = sohuId;
	}

	public String getAccessKey() {
		return this.accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

}
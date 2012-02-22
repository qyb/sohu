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
	private String accessKey;
	private String status;

	// Constructors

	/** default constructor */
	public ScssUser() {
	}

	/** full constructor */
	public ScssUser(String sohuId, String accessKey, String status) {
		this.sohuId = sohuId;
		this.accessKey = accessKey;
		this.status = status;
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

}
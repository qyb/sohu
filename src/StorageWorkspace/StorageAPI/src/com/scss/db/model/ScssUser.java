package com.scss.db.model;
<<<<<<< HEAD

=======
>>>>>>> b4691dab6552d06f9f56b08a2e3e3320a69696cd

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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSohuId() {
		return sohuId;
	}

	public void setSohuId(String sohuId) {
		this.sohuId = sohuId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

}
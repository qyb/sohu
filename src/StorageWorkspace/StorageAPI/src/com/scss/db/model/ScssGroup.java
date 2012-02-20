package com.scss.db.model;


/**
 * ScssGroup entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssGroup implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;
	private String userIds;

	// Constructors

	/** default constructor */
	public ScssGroup() {
	}

	/** full constructor */
	public ScssGroup(String name, String userIds) {
		this.name = name;
		this.userIds = userIds;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserIds() {
		return this.userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

}
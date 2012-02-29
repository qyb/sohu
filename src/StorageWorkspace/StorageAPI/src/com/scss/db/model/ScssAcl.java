package com.scss.db.model;

/**
 * ScssAcl entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssAcl implements java.io.Serializable {

	// Fields

	private Long id;
	private Long resourceId;
	private String resourceType = "O";
	private Long accessorId;
	private String accessorType = "U";
	private String permission = "R";

	// Constructors

	/** default constructor */
	public ScssAcl() {
	}

	/** full constructor */
	public ScssAcl(Long id, Long resourceId, String resourceType,
			Long accessorId, String accessorType, String permission) {
		this.id = id;
		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.accessorId = accessorId;
		this.accessorType = accessorType;
		this.permission = permission;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public Long getAccessorId() {
		return this.accessorId;
	}

	public void setAccessorId(Long accessorId) {
		this.accessorId = accessorId;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getAccessorType() {
		return accessorType;
	}

	public void setAccessorType(String accessorType) {
		this.accessorType = accessorType;
	}

}
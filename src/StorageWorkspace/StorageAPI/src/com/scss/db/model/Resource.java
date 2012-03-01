package com.scss.db.model;

public class Resource {
	private Long id;
	private String type;

	public Resource(Long id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Resource() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

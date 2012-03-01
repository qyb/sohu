package com.scss.db.model;

public class Accessor {
	private Long id;
	private String type;

	public Accessor(Long id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Accessor() {
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

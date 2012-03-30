package com.sohu.xml.model;

import java.io.Serializable;

public class CategoryList implements Serializable {
	
	private int id;
	private String folder;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}

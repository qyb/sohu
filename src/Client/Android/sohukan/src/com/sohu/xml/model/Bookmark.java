package com.sohu.xml.model;

import java.io.Serializable;

public class Bookmark implements Serializable {
	private int id;
	private String url;
	private String title;
	private String description;
	private int is_star;
	private String create_time;
	private String read_time;
	private String folder_name;
	private String read_progress;
	private int version;//更新本地数据
	private int text_version;//重新下载本地缓存文件
	private int is_ready;//0未下好图片,1下好图片了
	private int is_download;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIsStar() {
		return is_star;
	}
	public void setIsStar(int is_star) {
		this.is_star = is_star;
	}
	public String getCreateTime() {
		return create_time;
	}
	public void setCreateTime(String create_time) {
		this.create_time = create_time;
	}
	public String getReadTime() {
		return read_time;
	}
	public void setReadTime(String read_time) {
		this.read_time = read_time;
	}
	public String getFolderName() {
		return folder_name;
	}
	public void setFolderName(String folder_name) {
		this.folder_name = folder_name;
	}
	public String getReadProgress() {
		return read_progress;
	}
	public void setReadProgress(String read_progress) {
		this.read_progress = read_progress;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getTextVersion() {
		return text_version;
	}
	public void setTextVersion(int text_version) {
		this.text_version = text_version;
	}
	public int getIsReady() {
		return is_ready;
	}
	public void setIsReady(int is_ready) {
		this.is_ready = is_ready;
	}
	public int getIsDownload() {
		return is_download;
	}
	public void setIsDownload(int is_download) {
		this.is_download = is_download;
	}
}

package com.bfsapi.db.model;

import java.util.Date;

/**
 * ScssObject entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class ScssObject implements java.io.Serializable {

	// Fields

	private Long id;
	private String key;
	private Long bfsFile;
	private Long ownerId;
	private Long bucketId;
	private String meta;
	private Long size;
	private String mediaType;
	private Boolean versionEnabled;
	private String version;
	private Boolean deleted;
	private Date expirationTime;
	private Date createTime;
	private Date modifyTime;

	// Constructors

	/** default constructor */
	public ScssObject() {
	}

	/** minimal constructor */
	public ScssObject(String key, Long bfsFile, Long ownerId, Long bucketId) {
		this.key = key;
		this.bfsFile = bfsFile;
		this.ownerId = ownerId;
		this.bucketId = bucketId;
	}

	/** full constructor */
	public ScssObject(String key, Long bfsFile, Long ownerId, Long bucketId,
			String meta, Long size, String mediaType, Boolean versionEnabled,
			String version, Boolean deleted, Date expirationTime, Date createTime,
			Date modifyTime) {
		this.key = key;
		this.bfsFile = bfsFile;
		this.ownerId = ownerId;
		this.bucketId = bucketId;
		this.meta = meta;
		this.size = size;
		this.mediaType = mediaType;
		this.versionEnabled = versionEnabled;
		this.version = version;
		this.deleted = deleted;
		this.expirationTime = expirationTime;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getBfsFile() {
		return this.bfsFile;
	}

	public void setBfsFile(Long bfsFile) {
		this.bfsFile = bfsFile;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getBucketId() {
		return this.bucketId;
	}

	public void setBucketId(Long bucketId) {
		this.bucketId = bucketId;
	}

	public String getMeta() {
		return this.meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMediaType() {
		return this.mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public Boolean getVersionEnabled() {
		return this.versionEnabled;
	}

	public void setVersionEnabled(Boolean versionEnabled) {
		this.versionEnabled = versionEnabled;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Date getExpirationTime() {
		return this.expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}
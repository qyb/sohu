package com.scss.db.model;
<<<<<<< HEAD

=======
>>>>>>> b4691dab6552d06f9f56b08a2e3e3320a69696cd

import java.util.Date;

/**
 * ScssBucket entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssBucket implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;
	private Long ownerId;
	private Byte exprirationEnabled;
	private Byte loggingEnabled;
	private String meta;
	private Byte deleted;
	private Date createTime;
	private Date modifyTime;

	// Constructors

	/** default constructor */
	public ScssBucket() {
	}

	/** minimal constructor */
	public ScssBucket(String name, Long ownerId, Byte exprirationEnabled,
			Byte deleted, Date createTime, Date modifyTime) {
		this.name = name;
		this.ownerId = ownerId;
		this.exprirationEnabled = exprirationEnabled;
		this.deleted = deleted;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	/** full constructor */
	public ScssBucket(String name, Long ownerId, Byte exprirationEnabled,
			Byte loggingEnabled, String meta, Byte deleted, Date createTime,
			Date modifyTime) {
		this.name = name;
		this.ownerId = ownerId;
		this.exprirationEnabled = exprirationEnabled;
		this.loggingEnabled = loggingEnabled;
		this.meta = meta;
		this.deleted = deleted;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Byte getExprirationEnabled() {
		return this.exprirationEnabled;
	}

	public void setExprirationEnabled(Byte exprirationEnabled) {
		this.exprirationEnabled = exprirationEnabled;
	}

	public Byte getLoggingEnabled() {
		return this.loggingEnabled;
	}

	public void setLoggingEnabled(Byte loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	public String getMeta() {
		return this.meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public Byte getDeleted() {
		return this.deleted;
	}

	public void setDeleted(Byte deleted) {
		this.deleted = deleted;
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
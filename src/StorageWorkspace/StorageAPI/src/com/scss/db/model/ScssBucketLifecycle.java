package com.scss.db.model;

/**
 * ScssBucketLifecycle entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssBucketLifecycle implements java.io.Serializable {

	// Fields

	private Long id;
	private Long bucketId;
	private String expirationRule;

	// Constructors

	/** default constructor */
	public ScssBucketLifecycle() {
	}

	/** minimal constructor */
	public ScssBucketLifecycle(Long id, Long bucketId) {
		this.id = id;
		this.bucketId = bucketId;
	}

	/** full constructor */
	public ScssBucketLifecycle(Long id, Long bucketId, String expirationRule) {
		this.id = id;
		this.bucketId = bucketId;
		this.expirationRule = expirationRule;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBucketId() {
		return this.bucketId;
	}

	public void setBucketId(Long bucketId) {
		this.bucketId = bucketId;
	}

	public String getExpirationRule() {
		return this.expirationRule;
	}

	public void setExpirationRule(String expirationRule) {
		this.expirationRule = expirationRule;
	}

}
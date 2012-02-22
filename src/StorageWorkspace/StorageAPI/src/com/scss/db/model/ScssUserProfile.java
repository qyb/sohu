package com.scss.db.model;

import java.util.Date;

/**
 * ScssUserProfile entity.
 * 
 * @author Jack.wu.xu
 */

public class ScssUserProfile implements java.io.Serializable {

	// Fields

	private Long id;
	private String nickName;
	private String realName;
	private String phoneNum;
	private String country;
	private String state;
	private String city;
	private String address;
	private String avartar;
	private String gendar;
	private Date createdTime;
	private Date lastLogon;

	// Constructors

	/** default constructor */
	public ScssUserProfile() {
	}

	/** minimal constructor */
	public ScssUserProfile(Long id) {
		this.id = id;
	}

	/** full constructor */
	public ScssUserProfile(Long id, String nickName, String realName,
			String phoneNum, String country, String state, String city,
			String address, String avartar, String gendar, Date createdTime,
			Date lastLogon) {
		this.id = id;
		this.nickName = nickName;
		this.realName = realName;
		this.phoneNum = phoneNum;
		this.country = country;
		this.state = state;
		this.city = city;
		this.address = address;
		this.avartar = avartar;
		this.gendar = gendar;
		this.createdTime = createdTime;
		this.lastLogon = lastLogon;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvartar() {
		return this.avartar;
	}

	public void setAvartar(String avartar) {
		this.avartar = avartar;
	}

	public String getGendar() {
		return this.gendar;
	}

	public void setGendar(String gendar) {
		this.gendar = gendar;
	}

	public Date getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLastLogon() {
		return this.lastLogon;
	}

	public void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}

}
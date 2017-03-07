package com.jay.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	private Integer userId;
	
	private String userName;
	
	private City city;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
}

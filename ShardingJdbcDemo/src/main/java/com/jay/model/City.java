package com.jay.model;

import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class City implements Serializable {
	
	public Integer cityId;
	
	public String cityName;

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}

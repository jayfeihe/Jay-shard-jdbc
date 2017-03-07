package com.jay.mapper;

import com.jay.model.City;

public interface CityMapper {
	
	public void createCity(City city);
	
	public City getCityByCityId(Integer cityId);

}

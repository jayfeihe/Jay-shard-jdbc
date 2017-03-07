package com.jay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jay.mapper.CityMapper;
import com.jay.model.City;

@Service
public class CityService {
	
	@Autowired
	private CityMapper cityMapper;
	
	public void createCity(City city) {
		cityMapper.createCity(city);
	}

}

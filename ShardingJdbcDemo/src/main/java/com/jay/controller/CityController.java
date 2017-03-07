package com.jay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jay.id.CityIdGenerator;
import com.jay.model.City;
import com.jay.service.CityService;

@RestController
@RequestMapping("/city")
public class CityController {
	
	@Autowired
	private CityService cityService;
	
	@Autowired
	private CityIdGenerator cityIdGenerator;
	
	@RequestMapping(path="/{cityName}", method={RequestMethod.POST})
	public String createCity(@PathVariable("cityName") String cityName) {
		City city = new City();
		city.setCityId(cityIdGenerator.generateId().intValue());
		city.setCityName(cityName);
		cityService.createCity(city);
		return "success";
	}

}

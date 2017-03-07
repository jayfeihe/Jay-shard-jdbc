package com.jay.controller;

import com.jay.id.UserIdGenerator;
import com.jay.model.City;
import com.jay.model.User;
import com.jay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserIdGenerator userIdGenerator;
	
	@RequestMapping(path="/{userId}", method={RequestMethod.GET})
	public User getUserByUserId(@PathVariable("userId") Integer userId) {
		return userService.getUserByUserId(userId);
	}
	
	@RequestMapping(path="/master/{userId}", method={RequestMethod.GET})
	public User getUserByUserIdFromMaster(@PathVariable("userId") Integer userId) {
		return userService.getUserByUserIdFromMaster(userId);
	}
	
	@RequestMapping(method={RequestMethod.POST})
	public String createUser(@RequestBody User user) {
		user.setUserId(userIdGenerator.generateId().intValue());
		userService.createUser(user);
		return "success";
	}

	@GetMapping("/batch/{count}")
	public Integer batchCreate(@PathVariable("count") Integer count){

		Long start = System.currentTimeMillis();

		List<User> list = new ArrayList<>(count);

		City city = new City();
		city.setCityId(100);
		city.setCityName("BeiJing");
		User user = null;

		for (int i=0;i<count;i++){
			user = new User();
			user.setCity(city);
			user.setUserId(userIdGenerator.generateId().intValue());
			user.setUserName("UserName-"+i);
			list.add(user);
		}
		//使用了2个分库，每次最大批量插入数目是1000(多于1000则分多次批量插入)
		int result = userService.batchCreateUser(list, 2, 1000);

		Long end = System.currentTimeMillis();

		System.out.println("batch批量 "+count+" 条数据，耗时="+(end-start)+" 毫秒");

		return result;
	}

	@GetMapping("/batch1/{count}")
	public Integer batchCreate1(@PathVariable("count") Integer count){
		Long start = System.currentTimeMillis();

		City city = new City();
		city.setCityId(100);
		city.setCityName("BeiJing");
		User user = null;

		for (int i=0;i<count;i++){
			user = new User();
			user.setCity(city);
			user.setUserId(userIdGenerator.generateId().intValue());
			user.setUserName("UserName-"+i);
			userService.createUser(user);
		}

		Long end = System.currentTimeMillis();

		System.out.println("batch1批量 "+count+" 条数据，耗时="+(end-start)+" 毫秒");

		return count;
	}


}

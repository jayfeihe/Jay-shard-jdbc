package com.jay.service;

import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.jay.mapper.UserMapper;
import com.jay.model.User;
import com.jay.util.JayCommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	public User getUserByUserId(Integer userId) {
		User user = userMapper.getUserByUserId(userId);
		return user;
	}

	public User getUserByUserIdFromMaster(Integer userId) {
		try (HintManager hintManager = HintManager.getInstance();) {
			hintManager.setMasterRouteOnly();
			return userMapper.getUserByUserId(userId);
		} catch (Exception e) {
			throw e;
		}
	}

	public void createUser(User user) {
		userMapper.createUser(user);
	}

	/**
	 * 一次最多批量insert1000条，多于1000，将分多次插入
	 *
	 * @param list       ---  要插入的数据
	 * @param dbNum      ---  分库数
	 * @param count      ---  每次批量插入条数
     * @return
     */
	public Integer batchCreateUser(List<User> list, int dbNum, int count){
		List<List<User>> lists = choseDb(list, dbNum);
		for (List<User> l: lists) {

		//如果一次批量操作数据过多，则拆分为每次操作1000条，分多次操作
		List<List<?>> lts = JayCommonUtils.splitList(l, count);
		for (List<?> lt: lts) {
			userMapper.batchCreateUser((List<User>) lt);
		}
		}
		return list.size();
	}

	/**
	 * 根据数据库的分库数目， 返回每个分库应该操作的数据
	 * @param list
	 * @param dbNum
     * @return
     */
	public List<List<User>> choseDb(List<User> list, int dbNum){
		if (list == null || list.size() == 0 || dbNum < 1) {
			return null;
		}

		List<List<User>> result = new ArrayList<>(dbNum);
		for (int i=0;i<dbNum;i++){
			result.add(new ArrayList<User>());
		}
		for (User user:list){
			int i= user.getUserId()%dbNum;
			List<User> list1 = result.get(i);
			list1.add(user);
		}

		return result;
	}

}

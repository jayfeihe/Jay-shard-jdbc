package com.jay.mapper;

import com.jay.model.User;
import org.apache.ibatis.annotations.InsertProvider;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public interface UserMapper {
	
	User getUserByUserId(Integer userId);
	
	void createUser(User user);

	/**
	 * 注解版批量新增
	 * @param list
     */
	@InsertProvider(type = UserMapperProvider.class, method = "batchInsert")
	void batchCreateUser(List<User> list);

	public static class UserMapperProvider{
		public String batchInsert(Map<String, List<User>> map) {
			List<User> list = map.get("list");
			StringBuilder stringBuilder = new StringBuilder(1000);
			stringBuilder.append("insert into t_user(user_id,   user_name,   city_id) values ");
			MessageFormat messageFormat = new MessageFormat("(#'{'list[{0}].userId},#'{'list[{0}].userName},#'{'list[{0}].city.cityId})");
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(messageFormat.format(new Integer[]{i}));
				stringBuilder.append(",");
			}
			stringBuilder.setLength(stringBuilder.length() - 1);
			return stringBuilder.toString();
		}
	}

}

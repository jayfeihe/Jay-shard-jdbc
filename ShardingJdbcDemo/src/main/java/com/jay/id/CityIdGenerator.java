package com.jay.id;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.jay.util.JayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CityIdGenerator implements IdGenerator {

	@Autowired
	private StringRedisTemplate template;
	
	@Override
	public Number generateId() {
		return template.boundValueOps(JayConstants.RedisConst.ID_GENERATOR_CITY).increment(1);
	}

}

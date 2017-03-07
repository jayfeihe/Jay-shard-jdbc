package com.jay.id;

import com.dangdang.ddframe.rdb.sharding.id.generator.IdGenerator;
import com.jay.util.JayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * t_product表的主键生成策略
 * Created by hetiewei on 2017/3/3.
 */
@Component
public class ProductIdGenerator implements IdGenerator {

    @Autowired
    private StringRedisTemplate template;

    @Override
    public Number generateId() {
        return template.boundValueOps(JayConstants.RedisConst.ID_GENERATOR_PRODUCT).increment(1);
    }
}

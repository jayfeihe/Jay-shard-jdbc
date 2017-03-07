package com.jay.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.api.MasterSlaveDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.NoneDatabaseShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.jay.config.sharding.JayDataBaseShardingAlgorithm;
import com.jay.config.sharding.JayTableShardingAlgorithm;
import com.jay.id.OrderIdGenerator;
import com.jay.id.OrderItemIdGenerator;
import com.jay.id.ProductIdGenerator;
import com.jay.id.UserIdGenerator;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetiewei on 2017/3/3.
 */
@Configuration
@EnableConfigurationProperties(ShardingDataSourceProperties.class)
@EnableTransactionManagement
@MapperScan("com.jay.mapper")  //指定MyBatis的Mapper接口所在包
public class MyBatisConfig implements TransactionManagementConfigurer {

    //自定义配置文件解析类
    @Autowired
    private ShardingDataSourceProperties shardDataSourceProperties;

    //Driuid数据库连接池
    private DruidDataSource parentDs() throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(shardDataSourceProperties.getDriverClassName());
        ds.setFilters(shardDataSourceProperties.getFilters());
        ds.setMaxActive(shardDataSourceProperties.getMaxActive());
        ds.setInitialSize(shardDataSourceProperties.getInitialSize());
        ds.setMaxWait(shardDataSourceProperties.getMaxWait());
        ds.setMinIdle(shardDataSourceProperties.getMinIdle());
        ds.setTimeBetweenEvictionRunsMillis(shardDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(shardDataSourceProperties.getMinEvictableIdleTimeMillis());
        ds.setValidationQuery(shardDataSourceProperties.getValidationQuery());
        ds.setTestWhileIdle(shardDataSourceProperties.isTestWhileIdle());
        ds.setTestOnBorrow(shardDataSourceProperties.isTestOnBorrow());
        ds.setTestOnReturn(shardDataSourceProperties.isTestOnReturn());
        ds.setPoolPreparedStatements(shardDataSourceProperties.isPoolPreparedStatements());
        ds.setMaxPoolPreparedStatementPerConnectionSize(
                shardDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        ds.setRemoveAbandoned(shardDataSourceProperties.isRemoveAbandoned());
        ds.setRemoveAbandonedTimeout(shardDataSourceProperties.getRemoveAbandonedTimeout());
        ds.setLogAbandoned(shardDataSourceProperties.isLogAbandoned());
        ds.setConnectionInitSqls(shardDataSourceProperties.getConnectionInitSqls());
        return ds;
    }

    /**
     * 定义2个主库数据源
     */
    private DataSource mds0() throws SQLException {
        DruidDataSource ds = parentDs();
        ds.setUsername(shardDataSourceProperties.getMasterUsername0());
        ds.setUrl(shardDataSourceProperties.getMasterUrl0());
        ds.setPassword(shardDataSourceProperties.getMasterPassword0());
        return ds;
    }

    private DataSource mds1() throws SQLException {
        DruidDataSource ds = parentDs();
        ds.setUsername(shardDataSourceProperties.getMasterUsername1());
        ds.setUrl(shardDataSourceProperties.getMasterUrl1());
        ds.setPassword(shardDataSourceProperties.getMasterPassword1());
        return ds;
    }

    /**
     * 2个从库的数据源
     */
    private DataSource sds0() throws SQLException {
        DruidDataSource ds = parentDs();
        ds.setUsername(shardDataSourceProperties.getSlaveUsername0());
        ds.setUrl(shardDataSourceProperties.getSlaveUrl0());
        ds.setPassword(shardDataSourceProperties.getSlavePassword0());
        return ds;
    }

    private DataSource sds1() throws SQLException {
        DruidDataSource ds = parentDs();
        ds.setUsername(shardDataSourceProperties.getSlaveUsername1());
        ds.setUrl(shardDataSourceProperties.getSlaveUrl1());
        ds.setPassword(shardDataSourceProperties.getSlavePassword1());
        return ds;
    }

    /**
     * 定义分库数据源，及其每个数据源的主从配置
     * @return
     * @throws SQLException
     */
    private DataSourceRule dataSourceRule() throws SQLException {
        //1.主数据库mds0，对应从数据库为sds0
        DataSource msDs0 = MasterSlaveDataSourceFactory.createDataSource("ms_0", mds0(), sds0());
        //2.主数据库mds1，对应从数据库为sds1
        DataSource msDs1 = MasterSlaveDataSourceFactory.createDataSource("ms_1", mds1(), sds1());

        //3.将2个分库msDs0和msDs1加入到数据源分库规则中
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds_0", msDs0);
        dataSourceMap.put("ds_1", msDs1);
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);

        return  dataSourceRule;
    }

    /**
     * 这里配置分库分表策略
     *
     */

    /**
     * 1.分库不分表       ---  user
     */
    private TableRule  userTableRule() throws SQLException {
        //指定对 t_user 表：只进行分库，并指定主键生成策略
        TableRule tableRule = TableRule.builder("t_user")
                .autoIncrementColumns("user_id", UserIdGenerator.class)
                .databaseShardingStrategy(
                        new DatabaseShardingStrategy("user_id", new JayDataBaseShardingAlgorithm())
                )
                .dataSourceRule(dataSourceRule())
                .build();

        return tableRule;
    }

    /**
     * 2.分表不分库       ---  product
     */
    private TableRule productTableRule() throws SQLException {
        //指定对 t_product 表：只分表，不分库，并指定主键生成策略
        TableRule tableRule = TableRule.builder("t_product")
                .autoIncrementColumns("product_id", ProductIdGenerator.class)
                .actualTables(Arrays.asList("t_product0", "t_product_1"))
                .dataSourceRule(dataSourceRule())
                .databaseShardingStrategy(
                        new DatabaseShardingStrategy("product_id", new NoneDatabaseShardingAlgorithm())
                )
                .tableShardingStrategy(
                        new TableShardingStrategy("product_id", new JayTableShardingAlgorithm())
                )
                .build();

        return tableRule;
    }

    /**
     * 3.分库分表
     */
    private TableRule orderTableRule() throws SQLException {
        /*
          指定对 t_order 进行分库分表，分库分表的依据是： user_id, order_id， 指定表主键生成策略
          数据选库依据：
                    user_id
          数据选表：
                    order_id
         */
        TableRule tableRule = TableRule.builder("t_order")
                .autoIncrementColumns("order_id", OrderIdGenerator.class)
                .actualTables(Arrays.asList("t_order_0", "t_order_1"))
                .dataSourceRule(dataSourceRule())
                .databaseShardingStrategy(
                        new DatabaseShardingStrategy("user_id", new JayDataBaseShardingAlgorithm())
                )
                .tableShardingStrategy(
                        new TableShardingStrategy("order_id", new JayTableShardingAlgorithm())
                )
                .build();

        return tableRule;
    }

    /**
     * 4.分库分表
     * @return
     * @throws SQLException
     */
    private TableRule orderItemTableRule() throws SQLException {
        /*
          指定对 t_order_item 进行分库分表
          选库：order_id
          选表：item_id
         */
        TableRule tableRule = TableRule.builder("t_order_item")
                .autoIncrementColumns("item_id", OrderItemIdGenerator.class)
                .actualTables(Arrays.asList("t_order_item_0", "t_order_item_1"))
                .dataSourceRule(dataSourceRule())
                .databaseShardingStrategy(
                        new DatabaseShardingStrategy("order_id", new JayDataBaseShardingAlgorithm())
                )
                .tableShardingStrategy(
                        new TableShardingStrategy("item_id", new JayTableShardingAlgorithm())
                )
                .build();

       return tableRule;
    }


    private ShardingRule shardingRule() throws SQLException {
        ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule())
                .tableRules(Arrays.asList( productTableRule(), userTableRule(), orderTableRule(), orderItemTableRule()))
                .build();
        return shardingRule;
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        return ShardingDataSourceFactory.createDataSource(shardingRule());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }


    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        try {
            return new DataSourceTransactionManager(dataSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

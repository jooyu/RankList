//package com.dsky.baas.ranklist.config;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import redis.clients.jedis.JedisPoolConfig;
//
//@Configuration
//public class BoardRedisConfig {
//
//	@Bean(name = "redis123Template")
//	public StringRedisTemplate redisTemplate(
//			@Value("${redis123.hostName}") String hostName,
//			@Value("${redis123.port}") int port,
//			@Value("${redis123.password}") String password,
//			@Value("${redis123.maxIdle}") int maxIdle,
//			@Value("${redis123.maxTotal}") int maxTotal,
//			@Value("${redis123.index}") int index,
//			@Value("${redis123.maxWaitMillis}") long maxWaitMillis,
//			@Value("${redis123.testOnBorrow}") boolean testOnBorrow) {
//		StringRedisTemplate temple = new StringRedisTemplate();
//		temple.setConnectionFactory(connectionFactory(hostName, port, password,
//				maxIdle, maxTotal, index, maxWaitMillis, testOnBorrow));
//
//		return temple;
//	}
//
//	public RedisConnectionFactory connectionFactory(String hostName, int port,
//			String password, int maxIdle, int maxTotal, int index,
//			long maxWaitMillis, boolean testOnBorrow) {
//		JedisConnectionFactory jedis = new JedisConnectionFactory();
//		jedis.setHostName(hostName);
//		jedis.setPort(port);
//		if (!StringUtils.isEmpty(password)) {
//			jedis.setPassword(password);
//		}
//		if (index != 0) {
//			jedis.setDatabase(index);
//		}
//		jedis.setPoolConfig(poolCofig(maxIdle, maxTotal, maxWaitMillis,
//				testOnBorrow));
//		// 初始化连接pool
//		jedis.afterPropertiesSet();
//		RedisConnectionFactory factory = jedis;
//
//		return factory;
//	}
//
//	public JedisPoolConfig poolCofig(int maxIdle, int maxTotal,
//			long maxWaitMillis, boolean testOnBorrow) {
//		JedisPoolConfig poolCofig = new JedisPoolConfig();
//		poolCofig.setMaxIdle(maxIdle);
//		poolCofig.setMaxTotal(maxTotal);
//		poolCofig.setMaxWaitMillis(maxWaitMillis);
//		poolCofig.setTestOnBorrow(testOnBorrow);
//		return poolCofig;
//	}
//}
//  
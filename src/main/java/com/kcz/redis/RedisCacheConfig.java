package com.kcz.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by kcz on 2017/7/19.
 */
@Configuration
@EnableCaching
/**
 * 启动Redis
 */
public class RedisCacheConfig extends CachingConfigurerSupport {
	  @Value("${spring.redis.hostName}")
		private String host;
		@Value("${spring.redis.database}")
		private int dataIndex;
		@Value("${spring.redis.password}")
		private String passWord;
		@Bean
		public JedisConnectionFactory redisConnectionFactory() {
				JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();

				// Defaults
				redisConnectionFactory.setHostName(host);
				redisConnectionFactory.setDatabase(dataIndex);
				redisConnectionFactory.setPassword(passWord);
				redisConnectionFactory.setPort(6379);
				return redisConnectionFactory;
		}

		@Bean
		public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
				RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
				redisTemplate.setConnectionFactory(cf);
				return redisTemplate;
		}

		@Bean
		public CacheManager cacheManager(RedisTemplate redisTemplate) {
				RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
			// Sets the default expire time (in seconds)
				cacheManager.setDefaultExpiration(3000);
				return cacheManager;
		}

}

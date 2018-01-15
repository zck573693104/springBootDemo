package com.kcz.util;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Created by kcz on 2017/7/17.
 */
@Component
public class RedisUtil {
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	/**
	 * 保存数据并设置超时
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void saveEx(String key, Object value, int expire) {
		RedisConnection connection = redisConnectionFactory.getConnection();
		try {
			connection.setEx(key.getBytes(), expire, serialize(value));
		} catch (Exception e) {
			logger.error("redis save expire data error" + "key is:" + key + "value is:" + value + "error info:" + e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * 删除数据
	 * @param key
	 */
	public void del(String key) {
		RedisConnection connection = redisConnectionFactory.getConnection();
		try {
			connection.del(key.getBytes());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * 查询数据
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		RedisConnection connection = redisConnectionFactory.getConnection();
		byte[] bytes = null;
		try {
			bytes = connection.get(key.getBytes());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return deserialize(bytes);
	}

	/**
	 * 保存数据
	 * @param key
	 * @param value
	 */
	public void save(String key, Object value) {
		RedisConnection connection = redisConnectionFactory.getConnection();
		try {
			connection.set(key.getBytes(), serialize(value));
		} catch (Exception e) {
			logger.error("redis保存数据错误" + "key为:" + key + "值为:" + value + "异常信息是:" + e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * 功能简述: 对实体Bean进行序列化操作.
	 *
	 * @param source
	 *          待转换的实体
	 * @return 转换之后的字节数组
	 * @throws Exception
	 */
	public static byte[] serialize(Object source) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		if (source != null) {
			ObjectOutputStream ObjOut = null;
			try {
				ObjOut = new ObjectOutputStream(byteOut);
				ObjOut.writeObject(source);
				ObjOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != ObjOut) {
						ObjOut.close();
					}
				} catch (IOException e) {
					ObjOut = null;
				}
			}
		}
		return byteOut.toByteArray();
	}

	/**
	 * 功能简述: 将字节数组反序列化为实体Bean.
	 *
	 * @param source
	 *          需要进行反序列化的字节数组
	 * @return 反序列化后的实体Bean
	 * @throws Exception
	 */
	public static Object deserialize(byte[] source) {
		Object retVal = null;
		if (source != null) {
			ObjectInputStream ObjIn = null;
			try {
				ByteArrayInputStream byteIn = new ByteArrayInputStream(source);
				ObjIn = new ObjectInputStream(byteIn);
				retVal = ObjIn.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != ObjIn) {
						ObjIn.close();
					}
				} catch (IOException e) {
					ObjIn = null;
				}
			}

		}
		return retVal;
	}

}

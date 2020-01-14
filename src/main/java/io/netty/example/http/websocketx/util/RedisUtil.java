package io.netty.example.http.websocketx.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;


@Slf4j
public class RedisUtil {

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            Properties prop = PropertyUtil.load("redis.properties");
            String hostName = prop.getProperty("hostName");
            Integer port = Integer.parseInt(prop.getProperty("port"));
            String password = prop.getProperty("password");
            Integer timeout = Integer.parseInt(prop.getProperty("timeout"));
            Integer defaultDatabase = Integer.parseInt(prop.getProperty("defaultDatabase"));

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(prop.getProperty("maxActive")));
            config.setMaxIdle(Integer.parseInt(prop.getProperty("maxIdle")));
            config.setMaxWaitMillis(Long.parseLong (prop.getProperty("maxWaitMillis")));
            config.setTestOnBorrow(Boolean.parseBoolean(prop.getProperty("testOnBorrow")));
            config.setTestWhileIdle(Boolean.parseBoolean(prop.getProperty("testWhileIdle")));
//            jedisPool = new JedisPool(config, hostName, port, timeout,password==""?null:password,defaultDatabase);
            jedisPool = new JedisPool(config, hostName, port, timeout,null,defaultDatabase);
            log.info("redis连接池初始化成功");
        } catch (Exception e) {
            log.error("redis连接池初始化失败");
            e.printStackTrace();
        }

    }

    /**
     * 获取Jedis实例
     */
    public synchronized static Jedis getJedis() {

        try {

            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                log.info("redis--服务正在运行: "+resource.ping());
                return resource;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /***
     *
     * 释放资源
     */
    public static void releaseResource(final Jedis jedis) {
        if(jedis != null) {
            jedis.close();
            log.info("资源已释放");
        }

    }

    /***
     *
     * 释放连接池
     */
    public static void releasePool() {
        if(jedisPool != null) {
            jedisPool.close();
            log.info("连接池已释放");
        }

    }
}

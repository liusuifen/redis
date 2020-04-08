package com.example.redisjedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 访问redis的lua脚本
 *
 * @author
 */
public class JedisLuaTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(5);

        // timeout，这里既是连接超时又是读写超时，从Jedis 2.8开始有区分connectionTimeout和soTimeout的构造函数
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "195.55.0.84", 6379, 3000, null);

        Jedis jedis = null;
        try{
            //从redis连接池里拿出一个连接执行命令
            jedis = jedisPool.getResource();
            jedis.set("product_stock_10016", "15");
            //初始化商品10016的库存
            String script = " local count = redis.call('get', KEYS[1]) "
                    + " local a = tonumber(count) "
                    + " local b = tonumber(ARGV[1]) "
                    + " if a >= b then "
                    + " redis.call('set', KEYS[1], count‐b) "
                    + " return 1 "
                    + " end "
                    + " return 0 ";
            Object obj = jedis.eval(script, Arrays.asList("product_stock_10016"), Arrays.asList("10"));
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
            if (jedis != null)
                jedis.close();
        }
    }
}

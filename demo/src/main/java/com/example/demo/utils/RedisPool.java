package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by zhang on 2017/11/11.
 */

@Slf4j
@Component
public class RedisPool {

    @Autowired
    private RedisConfig redisConfig;

    private JedisPool jedisPool;

    @Bean
    public JedisPool initRedisPool() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMinIdle(10);
        config.setMaxWaitMillis(300);
//        config.setTestOnBorrow(redisConfig.getTestOnBorrow());
//        config.setTestOnReturn(redisConfig.getTestOnReturn());
        /*if (redisConfig.getCyberArkKey() != null && !redisConfig.getCyberArkKey().equals("")) {
            String pwd = CyberArkUtil.getPassword(redisConfig.getCyberArkKey(), redisConfig.getPassword());
            log.info("THE REAL REIDS PASSWORD:{}", pwd);
            jedisPool = new JedisPool(config,
                    redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout(),
                    pwd, redisConfig.getDatabase());
        } else {
            jedisPool = new JedisPool(config,
                    redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout(),
                    redisConfig.getPassword(), redisConfig.getDatabase());

        }*/

        jedisPool = new JedisPool(config,
                redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout(),
                redisConfig.getPassword(), redisConfig.getDatabase());
        log.info("init redis pool finished");

        return jedisPool;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1");
        System.out.println("testdev");
        System.out.println("链接成功");

        System.out.println("test local1115");

        System.out.println("dev 合并代码");
        System.out.println("服务正在运行:"+jedis.ping());

        String key = "test20171111";

        if(jedis.get(key) == null){
            jedis.set(key,"1");
            System.out.println(jedis.get(key));
        }

        if("2".equals(jedis.get(key).toString())){
            jedis.incr(key);
            System.out.println(jedis.get(key));
        }

    }
}

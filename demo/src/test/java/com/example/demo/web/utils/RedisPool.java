package com.example.demo.web.utils;

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
        config.setMaxTotal(redisConfig.getMaxActive());
        config.setMinIdle(redisConfig.getMinIdle());
        config.setMaxWaitMillis(redisConfig.getMaxWait());
        config.setTestOnBorrow(redisConfig.getTestOnBorrow());
        config.setTestOnReturn(redisConfig.getTestOnReturn());
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
}

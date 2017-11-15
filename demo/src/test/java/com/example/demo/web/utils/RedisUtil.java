package com.example.demo.web.utils;

import redis.clients.jedis.Jedis;

import javax.swing.plaf.synth.SynthOptionPaneUI;

/**
 * Created by zhang on 2017/11/11.
 */
public class RedisUtil {


    public Jedis getConnection() {
        Jedis jedis = null;
        try {
            jedis = null;
        } catch (Exception e) {
            System.out.println(e);
        }
        return jedis;
    }


}

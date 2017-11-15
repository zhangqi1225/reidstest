package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Created by zhang on 2017/11/11.
 */

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    RedisPool redisPool;

    /**
     * 获取连接
     *
     */
    public Jedis getConnection() {
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
        } catch (Exception e) {
            log.error("getConnection errors, ", e);
        }
        return jedis;
    }

    public  void sadd(String key, String value) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.sadd(key, value);

        } catch (Exception e) {
            log.error("redis putToMap kv binary failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 关闭数据库连接
     *
     */
    public void returnConnection(Jedis jedis) {
        if (null != jedis) {
            try {
                jedis.close();
            } catch (Exception e) {
                log.info("jedis.close() failed", e);
            }
        }
    }

    /**
     * 设置key-value失效时间，序列化类型key
     *
     */
    public long expireObjectKey(Object key, int seconds) {
        return expire(serializable(key), seconds);
    }

    /**
     *
     * @param key
     * @param value
     * @return  1 if the key was set 0 if the key was not set
     */
    public long setnx(String key, String value) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.setnx(key, value);
        } catch (Exception e) {
            log.error("redis setnx failed", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    public String getSet(String key, String value) {
        String result = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            result = conn.getSet(key, value);
        } catch (Exception e) {
            log.error("redis getSet failed", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 设置key-value失效时间，字符串类型key
     *
     */
    public long expire(String key, int seconds) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }

        try {
            conn.expire(key, seconds);
        } catch (Exception e) {
            log.error("conn.expire failed", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 设置key-value失效时间，字节类型key
     *
     */
    public  long expire(byte[] key, int seconds) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.expire(key, seconds);
        } catch (Exception e) {
            log.error("redis expire failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 检查key是否存在缓存
     *
     */
    public boolean checkKeyExisted(Object key) {
        Jedis conn = getConnection();
        if (conn == null) {
            return false;
        }
        boolean result;
        try {
            if (key instanceof String) {
                if (conn.exists((String) key)) {// 字符串key存在，直接返回
                    returnConnection(conn);
                    return true;
                }
            }
            result = conn.exists(serializable(key));
        } catch (Exception e) {
            log.error("checkKeyExisted failed, ", e);
            result = false;
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 检查key是否存在
     *
     */
    public  boolean checkKeyExisted(byte[] key) {
        boolean result = false;
        Jedis conn = getConnection();
        if (conn == null) {
            return false;
        }
        try {
            result = conn.exists(key);
        } catch (Exception e) {
            log.error("checkKeyExisted failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 加1操作
     *
     */
    public long increase(String key) {
        return increase(key, 1);
    }

    /**
     * 加操作，指定加的量
     *
     */
    public  long increase(String key, int num) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }

        try {
            result = conn.incrBy(key, num);
        } catch (Exception e) {
            log.error("redis increase failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 加1操作
     *
     */
    public  long increase(byte[] key) {
        return increase(key, 1);
    }

    /**
     * 加操作，指定加的量
     *
     */
    public  long increase(byte[] key, int num) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.incrBy(key, num);
        } catch (Exception e) {
            log.error("redis increase by num failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 减1操作
     *
     */
    public  long decrease(String key) {
        return decrease(key, 1);
    }

    /**
     * 减操作，指定减的值
     *
     */
    public  long decrease(String key, int num) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.decrBy(key, num);
        } catch (Exception e) {
            log.error("redis decrease by num failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 减1操作
     *
     */
    public  long decrease(byte[] key) {
        return decrease(key, 1);
    }

    /**
     * 减操作，指定减的值
     *
     */
    public  long decrease(byte[] key, int num) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.decrBy(key, num);
            returnConnection(conn);
            return result;
        } catch (Exception e) {
            log.error("redis decrease by num failed, ", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 删除缓存记录，先做字符串判断，不存在再对key做序列化处理
     *
     */
    public  long delete(String key) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.del(key);
            if (result == 0) {
                result = conn.del(serializable(key));
            }
        } catch (Exception e) {
            log.error("redis decrease by num failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 删除缓存记录，直接对key做序列化处理
     *
     */
    public  long deleteObjectKey(Object key) {
        return delete(serializable(key));
    }

    /**
     * 删除记录
     *
     */
    public  long delete(byte[] key) {
        long result = -1;
        Jedis conn = getConnection();
        if (conn == null) {
            return result;
        }
        try {
            result = conn.del(key);
        } catch (Exception e) {
            log.error("redis delete failed,", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 设置对象类型缓存项，无失效时间
     *
     */
    public  void set(Object key, Object value) {
        set(serializable(key), serializable(value), -1);
    }

    public  void set(String key, Object value) {
        set(key.getBytes(), serializable(value), -1);
    }

    /**
     * 设置对象类型缓存项，加入失效时间，单位为秒
     *
     */
    public  void set(Object key, Object value, int exp) {
        set(serializable(key), serializable(value), exp);
    }

    public  void set(String key, Object value, int exp) {
        set(key.getBytes(), serializable(value), exp);
    }

    /**
     * 设置key-value项，字节类型
     *
     */
    public  void set(byte[] key, byte[] value, int exp) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            if (exp > 0) {
                conn.setex(key, exp, value);
            } else {
                conn.set(key, value);
            }
        } catch (Exception e) {
            log.error("redis set binary failed,", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 获取对象类型
     *
     */
    public  Object get(Object key) {
        byte[] data = get(serializable(key));
        if (data != null) {
            return unserialize(data);
        }
        return null;
    }

    public  Object get(String key) {
        byte[] data = get(key.getBytes());
        if (data != null) {
            return unserialize(data);
        }
        return null;
    }

    /**
     * 获取key value
     *
     *
     */
    public  byte[] get(byte[] key) {
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            byte[] data = conn.get(key);
            return data;
        } catch (Exception e) {
            log.error("redis setString failed", e);
        }finally {
            returnConnection(conn);
        }
        return null;
    }

    /**
     * 设置字符串类型缓存项
     *
     */
    public  void setString(String key, String value) {
        setString(key, value, -1);
    }

    /**
     * 存储字符串类型缓存项，加入失效时间，单位为秒
     *
     */
    public  void setString(String key, String value, int exp) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            if (exp > 0) {
                conn.setex(key, exp, value);
            } else {
                conn.set(key, value);
            }
        } catch (Exception e) {
            log.error("redis setString failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 获取字符串类型
     *
     */
    public  String getString(String key) {
        String value = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            value = conn.get(key);
        } catch (Exception e) {
            log.error("redis getString failed", e);
        }finally {
            returnConnection(conn);
        }
        return value;
    }

    public Map<String, Object> getObjectMapAll(Object mapKey) {
        Map<byte[], byte[]> data = getMapAll(serializable(mapKey));
        if (data != null && data.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            Set<byte[]> keys = data.keySet();
            for (byte[] key : keys) {
                result.put((String)unserialize(key), unserialize(data.get(key)));
            }
            return result;
        }
        return null;
    }

    /**
     * 获取Map结构所有数据
     *
     */
    public  Map<Object, Object> getMapAll(Object mapKey) {
        Map<byte[], byte[]> data = getMapAll(serializable(mapKey));
        if (data != null && data.size() > 0) {
            Map<Object, Object> result = new HashMap<>();
            Set<byte[]> keys = data.keySet();
            for (byte[] key : keys) {
                result.put(unserialize(key), unserialize(data.get(key)));
            }
            return result;
        }
        return null;
    }


    /**
     * 获取Map的长度
     *
     */
    public  long getMapLenght(String mapKey) {
        Jedis conn = getConnection();
        if (conn == null) {
            return 0;
        }
        try {
            long len = conn.hlen(mapKey);
            return len;
        } catch (Exception e) {
            log.error("redis getMapLenght failed, ", e);
            returnConnection(conn);
            return 0;
        }
    }

    /**
     * 获取Map结构所有数据
     *
     */
    public  Map<byte[], byte[]> getMapAll(byte[] mapKey) {
        Map<byte[], byte[]> data = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            data = conn.hgetAll(mapKey);
        } catch (Exception e) {
            log.error("redis getMapAll binray failed, ", e);
        }finally {
            returnConnection(conn);
        }

        return data;
    }

    /**
     * 获取Map结构所有数据(key为String)
     *
     */
    public  Map<String, String> getStringMapAll(String mapKey) {

        Map<String, String> result = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            result = conn.hgetAll(mapKey);
        } catch (Exception e) {
            log.error("redis getStringMapAll failed, ", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 获取Map所有数据，直接返回未序列化的结果，对一些特殊的应用场景更高效
     *
     */
    public Map<byte[], byte[]> getMapAllByte(Object mapKey) {
        Map<byte[], byte[]> result = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            result = conn.hgetAll(serializable(mapKey));
        } catch (Exception e) {
            log.error("redis getMapAllByte failed", e);
        }finally {
            returnConnection(conn);
        }

        return result;
    }

    /**
     * 添加到Map结构
     *
     */
    public  void putToMap(Object mapKey, Object field, Object value) {
        putToMap(serializable(mapKey), serializable(field), serializable(value));
    }

    /**
     * 添加到Map结构
     *
     */
    public  void putToMapString(String mapKey, String field, String value) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hset(mapKey, field, value);
        } catch (Exception e) {
            log.error("redis putToMapString kv failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 添加到Map结构
     *
     */
    public  void putToMap(byte[] mapKey, byte[] field, byte[] value) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hset(mapKey, field, value);
        } catch (Exception e) {
            log.error("redis putToMap kv binary failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 添加到Map结构(key为String)
     *
     */
    public  void putStringToMap(String mapKey, String field, String value) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hset(mapKey, field, value);
        } catch (Exception e) {
            log.error("redis putStringToMap kv failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 批量设置到hash数据结果，采用byte类型存储，
     * 取的时候得注意数据类型转换（例如：Map<key,value>中put数据时key的类型为String
     * ，那么get的时候需严格用String类型,否则get时会得不到你想要的）
     *
     */
    public  void putToMap(Object mapKey, Map<Object, Object> data) {
        putToMap(serializable(mapKey), serializeMap(data));
    }

    /**
     * 批量设置到hash数据结果，采用byte类型存储
     *
     */
    public  void putToMap(byte[] mapKey, Map<byte[], byte[]> data) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hmset(mapKey, data);
        } catch (Exception e) {
            log.error("redis putStringToMap kv failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 添加到Map结构（key为String）
     *
     */
    public  void putStringToMap(String mapKey, Map<String, String> data) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hmset(mapKey, data);
        } catch (Exception e) {
            log.error("redis putStringToMap kv failed", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 从Map结构中获取数据
     *
     */
    public  Object getFromMap(Object mapKey, Object field) {
        return unserialize(getFromMap(serializable(mapKey), serializable(field)));
    }

    /**
     * 从Map结构中获取数据
     *
     */
    public  String getFromMapString(String mapKey, String field) {
        String data = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            data = conn.hget(mapKey, field);
        } catch (Exception e) {
            log.error("redis putStringToMap kv failed", e);
        }finally {
            returnConnection(conn);
        }
        return data;
    }

    /**
     * 从Map结构中获取数据
     *
     */

    public  byte[] getFromMap(byte[] mapKey, byte[] field) {
        byte[] data = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            data = conn.hget(mapKey, field);
        } catch (Exception e) {
            log.error("redis getFromMap", e );
        }finally {
            returnConnection(conn);
        }
        return data;
    }


    public  String getStringFromMap(String mapKey, String field) {
        String data = null;
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            data = conn.hget(mapKey, field);
        } catch (Exception e) {
            log.error("redis getStringFromMap", e );
        }finally {
            returnConnection(conn);
        }
        return data;
    }

    public  Boolean isContainFromMap(String mapKey, String field) {
        Boolean result = false;
        Jedis conn = getConnection();
        if (conn == null) {
            return false;
        }
        try {
            result = conn.hexists(mapKey, field);
        } catch (Exception e) {
            log.error("redis isContainFromMap", e );
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 从map中移除记录
     *
     */
    public  void removeFromMap(Object mapKey, Object field) {
        removeFromMap(serializable(mapKey), serializable(field));
    }

    /**
     * 从map中移除记录
     *
     */
    public  void removeFromMap(byte[] mapKey, byte[] field) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.hdel(mapKey, field);
        } catch (Exception e) {
            log.error("redis removeFromMap", e );
        }finally {
            returnConnection(conn);
        }
    }

    /*
     * 添加到sorted set队列，字符串类型
     *
     */

    public void addToSortedSet(String setKey, String value, double score) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.zadd(setKey, score, value);
        } catch (Exception e) {
            log.error("redis addToSortedSet failed:", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 添加到sorted set队列，java序列化对象类型
     *
     */
    public  void addToSortedSet(Object setKey, Object value, double score) {
        addToSortedSet(serializable(setKey), serializable(value), score);
    }

    /**
     * 添加到sorted set队列，字节类型
     *
     */
    public void addToSortedSet(byte[] setKey, byte[] value, double score) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.zadd(setKey, score, value);
        } catch (Exception e) {
            log.error("redis addToSortedSet failed:", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 批量添加到sorted set队列，字符串类型
     *
     */
    public void addToSortedSet(String setKey, Map<String, Double> valueMap) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.zadd(setKey, valueMap);
        } catch (Exception e) {
            log.error("redis addToSortedSet failed:", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 批量添加到sorted set队列，java序列化类型
     *
     */
    public void addToSortedSet(Object setKey, Map<Object, Double> valueMap) {
        Set<Object> keys = valueMap.keySet();
        Map<byte[], Double> buf = new HashMap<>();
        if (keys.size() > 0) {
            for (Object key : keys) {
                buf.put(serializable(key), valueMap.get(key));
            }
            addToSortedSet(serializable(setKey), buf);
        }
    }

    public List<String> keys(String keys){
        Jedis conn = getConnection();
        if (conn == null) {
            return null;
        }
        try {
            Set<String> set = conn.keys(keys + "*");
            Iterator<String> it = set.iterator();
            List<String> lst = new ArrayList<String>();
            while (it.hasNext()) {
                String str = it.next();
                lst.add(str);
            }
            return lst;
        } catch (Exception e) {
            log.error("redis addToSortedSet failed:", e);
        }finally {
            returnConnection(conn);
        }
        return null;
    }

    /**
     * 批量添加到sorted set队列，字节类型
     *
     */
    public  void addToSortedSet(byte[] setKey, Map<byte[], Double> valueMap) {
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        try {
            conn.zadd(setKey, valueMap);
        } catch (Exception e) {
            log.error("redis addToSortedSet failed:", e);
        }finally {
            returnConnection(conn);
        }
    }

    /**
     * 从sorted set中取某个元素的排名，正序排列
     * 返回从1开始
     */
    public long getSortedSetRank(String setKey, String memeber) {
        Jedis conn = getConnection();
        long rank = -1;
        if (conn == null) {
            return -1;
        }
        try {
            rank = conn.zrank(setKey,memeber) + 1;
        } catch (Exception e) {
            log.error("redis getSortedSetRank failed:", e);
        }finally {
            returnConnection(conn);
        }
        return rank;
    }

    /**
     * 从sorted set中取某个元素的排名，倒叙排列
     * 返回从1开始
     */
    public long getSortedSetRevRank(String setKey, String member) {
        Jedis conn = getConnection();
        long rank = -1;
        if (conn == null) {
            return -1;
        }
        try {
            Long r = conn.zrevrank(setKey,member);
            rank = r == null ? 0 : r + 1;
        } catch (Exception e) {
            log.error("redis getSortedSetRevRank failed:", e);
        }finally {
            returnConnection(conn);
        }
        return rank;
    }

    /**
     * 从sorted set中获取一定范围的段，按score从低到高
     *
     */
    public  Set<String> getSortedSetRange(String sortKey, long start, long size) {
        Jedis conn = getConnection();
        Set<String> result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.zrange(sortKey, start, start + size - 1);
        } catch (Exception e) {
            log.error("redis getSortedSetRange failed:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 从sorted set中获取一定范围的段，按score从高到低
     *
     */
    public  Set<String> getSortedSetRangeReverse(String sortKey, long start, long size) {
        Jedis conn = getConnection();
        Set<String> result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.zrevrange(sortKey, start, start + size - 1);
        } catch (Exception e) {
            log.error("redis getSortedSetRangeReverse failed:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    public Set<Tuple> getSortedSetRangeReverseWithScore(String key, long start, long end) {
        Jedis conn = getConnection();
        Set<Tuple> result = null;

        if (conn == null) {
            return null;
        }
        try {
            result = conn.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error("getSortedSetRangeReverseWithScore failed:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 从sorted set中获取一定范围的段，字节类型，按score从低到高
     *
     */
    public  Set<byte[]> getSortedSetRange(byte[] sortKey, long start, long size) {
        Jedis conn = getConnection();
        Set<byte[]> result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.zrange(sortKey, start, start + size - 1);
        } catch (Exception e) {
            log.error("getSortedSetRange failed:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 从sorted set中获取一定范围的段，字节类型，按score从高到低
     *
     */
    public  Set<byte[]> getSortedSetRangeReverse(byte[] sortKey, long start, long size) {
        Jedis conn = getConnection();
        Set<byte[]> result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.zrevrange(sortKey, start, start + size - 1);
        } catch (Exception e) {
            log.error("getSortedSetRangeReverse faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 获取有序集合member数量
     * @param key
     * @return
     */
    public long getSortedSetMemberCount(String key) {
        Jedis conn = getConnection();
        long result = 0;
        if (conn == null) {
            return -1;
        }
        try {
            result = conn.zcard(key);
        } catch (Exception e) {
            log.error("getSortedSetMemberCount faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * sorted set中移除元素
     *
     */
    public long removeFromSortedSet(String key, String... members) {
        Jedis conn = getConnection();
        long cnt = 0L;
        if (conn == null) {
            return 0;
        }
        try {
            cnt = conn.zrem(key, members);
        } catch (Exception e) {
            log.error("removeFromSortedSet faled:", e);
        }finally {
            returnConnection(conn);
        }
        return cnt;
    }

    /**
     * 根据score从sorted set中移除记录
     *
     */
    public long removeFromSortedSetByScore(String keySet, double score) {
        Jedis conn = getConnection();
        long cnt = 0L;
        if (conn == null) {
            return 0;
        }
        try {
            cnt = conn.zremrangeByScore(keySet, score, score);
        } catch (Exception e) {
            log.error("removeFromSortedSetByScore faled:", e);
        }finally {
            returnConnection(conn);
        }
        return cnt;
    }

    /**
     * 根据score从sorted set中移除记录
     *
     */
    public long removeFromSortedSetByScore(byte[] keySet, double score) {
        Jedis conn = getConnection();
        long cnt = 0L;
        if (conn == null) {
            return 0;
        }
        try {
            cnt = conn.zremrangeByScore(keySet, score, score);
        } catch (Exception e) {
            log.error("removeFromSortedSetByScore faled:", e);
        }finally {
            returnConnection(conn);
        }
        return cnt;
    }

    /**
     * map数据序列化转换
     *
     */
    public  Map<byte[], byte[]> serializeMap(Map<Object, Object> data) {
        Map<byte[], byte[]> result = new HashMap<>();
        try {
            Set<Object> keys = data.keySet();
            if (keys.size() > 0) {
                for (Object key : keys) {
                    result.put(serializable(key), serializable(data.get(key)));
                }
            }
        } catch (Exception e) {
            log.error("redis serializeMap", e );
        }
        return result;
    }

    /**
     * 序列化处理
     *
     */
    public  byte[] serializable(Object obj) {
        if (obj == null) {
            return null;
        }
        ObjectOutputStream oos;
        ByteArrayOutputStream baos;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("redis serializable", e );
            return null;
        }
    }

    /**
     * 反序列化处理
     *
     */
    public  Object unserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bais;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            log.error("redis unserialize", e );
            return null;
        }
    }

    public  void flashDb(){
        Jedis conn = getConnection();
        if (conn == null) {
            return;
        }
        conn.flushDB();
    }

    public  long size() {
        Jedis conn = getConnection();
        if (conn == null) {
            return 0;
        }
        return conn.dbSize();
    }

    public Long lpush(String key, String... values){
        Jedis conn = getConnection();
        long result = 0;
        if (conn == null) {
            return -1L;
        }
        try {
            result = conn.lpush(key, values);
        } catch (Exception e) {
            log.error("lpush faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 返回并移除列表第一个元素
     * @param key
     * @return 如果key不存在或者列表已经为空，返回nil
     */
    public String lpop(String key){
        Jedis conn = getConnection();
        String result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.lpop(key);
        } catch (Exception e) {
            log.error("lpop faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 返回并移除最后的元素
     * @param key
     * @return 如果key不存在或者列表已经为空，返回nil
     */
    public String rpop(String key){
        Jedis conn = getConnection();
        String result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.rpop(key);
        } catch (Exception e) {
            log.error("rpop faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 根据下标返回列表元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end){
        Jedis conn = getConnection();
        List<String> result = null;
        if (conn == null) {
            return null;
        }
        try {
            result = conn.lrange(key, start, end);
        } catch (Exception e) {
            log.error("lrange faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }

    /**
     * 返回列表长度
     * @param key
     * @return
     */
    public Long llen(String key){
        Jedis conn = getConnection();
        Long result = null;
        if (conn == null) {
            return -1L;
        }
        try {
            result = conn.llen(key);
        } catch (Exception e) {
            log.error("llen faled:", e);
        }finally {
            returnConnection(conn);
        }
        return result;
    }


    public static void main(String[] args) {

        String key = "test20171111";

        RedisUtil redisUtil = new RedisUtil();
        redisUtil.set(key,"1");
        if(redisUtil.get(key) == null){
            redisUtil.set(key,1);
            log.info("redis",redisUtil.get(key));
        }

        if(redisUtil.get(key).toString().equals("1")){
            redisUtil.increase(key);
            log.info("increase redis",redisUtil.get(key));
        }

        if("".equals(redisUtil.get("hcz_app_versoin"))){
            redisUtil.set("hcz+app+2017","");
        }

    }

}

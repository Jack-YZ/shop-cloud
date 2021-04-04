package com.jz.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    //往redis中设置key
    public <T> boolean set(KeyPrefix prefix,String token,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + token;
            String realVal = beanToString(value);
            if(prefix.getExpireSeconds()>0){
                jedis.setex(realKey,prefix.getExpireSeconds(),realVal);
            }else{
                jedis.set(realKey, realVal);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }

    }

    //归还至连接池
    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

    //将对象转化为jsonString字符串
    private <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clz = value.getClass();
        if(clz == int.class ||clz == Integer.class){
            return ""+value;
        }else if(clz == double.class ||clz == Double.class){
            return ""+value;
        }else if(clz == long.class ||clz == Long.class){
            return ""+value;
        }else if(clz == float.class ||clz == Float.class){
            return ""+value;
        }else if(clz == boolean.class ||clz == Boolean.class){
            return ""+value;
        }else if(clz == String.class){
            return (String) value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    public <T> T get(String token,KeyPrefix prefix, Class<T> clazz) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + token;
            String realVal = jedis.get(realKey);

            return stringToBean(realVal,clazz);
        }finally {
            returnToPool(jedis);
        }

    }

    private <T> T stringToBean(String value, Class<T> clazz) {
        if(clazz==int.class || clazz == Integer.class){
            return (T) Integer.valueOf(value);
        }else if(clazz == float.class || clazz == Float.class){
            return (T) Float.valueOf(value);
        }else if(clazz==long.class || clazz==Long.class){
            return (T) Long.valueOf(value);
        }else if(clazz==boolean.class || clazz==Boolean.class){
            return (T) Boolean.valueOf(value);
        }else if(clazz == String.class){
            return (T) value;
        }else{
            return JSON.parseObject(value,clazz);
        }
    }

    public void expire(KeyPrefix prefix,String token,int times) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + token;
            jedis.expire(realKey,times);
        }finally {
            returnToPool(jedis);
        }

    }

    public Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    //--------------------------------------------------------------------

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public  boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        }finally {
            returnToPool(jedis);
        }
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock( String lockKey, String requestId) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        }finally {
            returnToPool(jedis);
        }

    }



    //往hash结构中设置
    public <T> boolean hset(KeyPrefix prefix,String key,String field,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + key;
            String realVal = beanToString(value);
            jedis.hset(realKey,field,realVal);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    //获取hash结构中所有的键值对
    public <T> Map<String,T> hgetAll(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + key;
            Map<String,String>  tmap = jedis.hgetAll(realKey);
            Map<String,T> map = new HashMap<>();
            for(Map.Entry<String,String> entry :tmap.entrySet()){
                map.put(entry.getKey(),stringToBean(entry.getValue(),clazz));
            }
            return map;
        }finally {
            returnToPool(jedis);
        }
    }
    //获取hash结构中指定field的键值对
    public <T> T hget(KeyPrefix prefix,String key,String field,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String prefix1 = prefix.getPrefix();
            String realKey = prefix1 + key;
            String hget = jedis.hget(realKey, field);
            return stringToBean(hget,clazz);
        }finally {
            returnToPool(jedis);
        }
    }


    public boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
}

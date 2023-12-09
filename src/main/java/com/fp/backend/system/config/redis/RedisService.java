package com.fp.backend.system.config.redis;

import com.fp.backend.system.config.websocket.SocketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    public void setValues(String key, String data){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }
    public void setValues(String key, String data, Duration duration){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }
    @Transactional(readOnly = true)
    public String getValues(String key){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if(values.get(key) == null){
            return"false";
        }
        return (String) values.get(key);
    }
    public void setValuesPush(String key, String data){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(key, data);
    }
    public String getValuesLastIndex(String key){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return (String) list.index(key, 0);
    }
    @Transactional(readOnly = true)
    public List<SocketVO> getValuesListAll(String key){
        ListOperations<String,Object> list = redisTemplate.opsForList();
        List<SocketVO> stringList = new ArrayList<>();
        List<Object> bidList = list.range(key, 0, -1);
        for(int i = bidList.size()-1; i >= 0; i--){
            Object bid = list.index(key, i);
            SocketVO socketVO = new SocketVO(key, bid);
            stringList.add(socketVO);
        }

        return stringList;
    }


    public void deleteValues(String key){
        redisTemplate.delete(key);
    }
    public void expireValues(String key, int timeout){
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }


    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    @Transactional(readOnly = true)
    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }
}
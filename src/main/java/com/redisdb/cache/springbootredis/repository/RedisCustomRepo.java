package com.redisdb.cache.springbootredis.repository;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redisdb.cache.springbootredis.model.User;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCustomRepo {

    private final HashOperations hashOperations;

    private final RedisTemplate redisTemplate;

    public RedisCustomRepo(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
        this.redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.afterPropertiesSet();
    }

    public void save(User user){
        ObjectMapper objectMapper = new ObjectMapper();
        String userString=null;
        try {
            userString = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        hashOperations.put("USER", user.getId(), userString);
    }
    public List<User> findAll(){
        return hashOperations.values("USER");
    }

    public User findById(String id){
        String userJson = (String) hashOperations.get("USER", id);
        ObjectMapper objectMapper = new ObjectMapper();
        User user;
        try {
            user = objectMapper.readValue(userJson, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void update(User user){
        save(user);
    }

    public void delete(String id){
        hashOperations.delete("USER", id);
    }

    public List<User> multiGetUsers(List<String> userIds){
        return hashOperations.multiGet("USER", userIds);
    }
}

package com.rahul.cricketgame.matchmaking;

import com.rahul.cricketgame.config.Constants;
import com.rahul.cricketgame.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PlayerProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public PlayerProducer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addPlayerToQueue(Player player) {
        System.out.println("Sending player to queue: {}" + player);
        redisTemplate.convertAndSend(Constants.PLAYER_QUEUE, player);
    }
}

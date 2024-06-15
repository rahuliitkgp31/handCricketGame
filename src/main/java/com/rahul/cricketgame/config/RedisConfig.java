package com.rahul.cricketgame.config;

import com.rahul.cricketgame.matchmaking.GameService;
import com.rahul.cricketgame.matchmaking.MatchMakingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter playerQueueListenerAdapter,
                                                   MessageListenerAdapter automaticRoundEndEventsQueueListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(playerQueueListenerAdapter, ChannelTopic.of(Constants.PLAYER_QUEUE));
        container.addMessageListener(automaticRoundEndEventsQueueListener, ChannelTopic.of(Constants.automaticRoundEndEventsQueue));
        return container;
    }

    @Bean
    public MessageListenerAdapter playerQueueListenerAdapter(MatchMakingService matchmakingService) {
        MessageListenerAdapter playerQueueListenerAdapter = new MessageListenerAdapter(matchmakingService,
                "addPlayerToQueueListener");
        playerQueueListenerAdapter.setSerializer(new JdkSerializationRedisSerializer());
        return playerQueueListenerAdapter;
    }

    @Bean
    public MessageListenerAdapter automaticRoundEndEventsQueueListener(GameService gameService) {
        MessageListenerAdapter automaticRoundEndEventsQueueListener = new MessageListenerAdapter(gameService,
                "endRoundEventListener");
        automaticRoundEndEventsQueueListener.setSerializer(new JdkSerializationRedisSerializer());
        return automaticRoundEndEventsQueueListener;
    }
}

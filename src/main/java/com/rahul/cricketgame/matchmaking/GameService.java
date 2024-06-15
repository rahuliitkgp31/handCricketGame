package com.rahul.cricketgame.matchmaking;

import com.rahul.cricketgame.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@Service
public class GameService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TaskScheduler taskScheduler;

    public void startGame(GameSession session) {
        String sessionId = session.getSessionId();
        redisTemplate.opsForHash().put(Constants.GAME_SESSION_MAP_NAME, sessionId, session);
        startRound(sessionId);
    }

    public void startRound(String sessionId) {
        //starts the first round
        GameSession session = (GameSession) redisTemplate.opsForHash().get(Constants.GAME_SESSION_MAP_NAME, sessionId);
        if (session != null) {
            ScheduledFuture<?> future = scheduleFuture(sessionId, session.getStartTime());
            session.setScheduledFuture(future);
            redisTemplate.opsForHash().put("game_sessions", sessionId, session);
        }
    }

    public ScheduledFuture<?> scheduleFuture(String sessionId, LocalDateTime roundStartTime) {
        long delay = LocalDateTime.now().until(roundStartTime.plusSeconds(5), ChronoUnit.MILLIS);

        return taskScheduler.schedule(() -> {
            redisTemplate.convertAndSend(Constants.automaticRoundEndEventsQueue, sessionId);
        }, new Date(System.currentTimeMillis()+delay));
    }

    public void endRoundEventListener(String sessionId) {
        GameSession session = (GameSession) redisTemplate.opsForHash().get(Constants.GAME_SESSION_MAP_NAME, sessionId);
        if(!Objects.isNull(session)) {
            Integer player1Input = getRandomNumberBetweenOneToSix();
            Integer player2Input = getRandomNumberBetweenOneToSix();
            if(session.getPlayer1Input() != null) {
                player1Input = session.getPlayer1Input();
            }
            if(session.getPlayer2Input() != null) {
                player2Input = session.getPlayer2Input();
            }
            session.setPlayer1Input(player1Input);
            session.setPlayer2Input(player2Input);
            proceedToNextRoundOrEnd(session);
        }
    }

    public void submitNumber(String sessionId, int playerId, int number) {
        GameSession session = (GameSession) redisTemplate.opsForHash().get(Constants.GAME_SESSION_MAP_NAME, sessionId);
        if(session!=null) {
            session.submitNumber(playerId, number);
            if(session.bothPlayerSubmitted()) {
                session.getScheduledFuture().cancel(true);
                proceedToNextRoundOrEnd(session);
            }
        }
    }

    public void proceedToNextRoundOrEnd(GameSession session) {
        session.calculateThisRoundScore();
        if(session.isGameOver()) {
            //calculate winner
            session.calculateWinner();

            //save game info to sql table
            System.out.println("saving game to sql table");

            redisTemplate.opsForHash().delete(Constants.GAME_SESSION_MAP_NAME, session.getSessionId());
        } else{
            session.prepareForNextRound();
            ScheduledFuture<?> future = scheduleFuture(session.getSessionId(), session.getStartTime());
            session.setScheduledFuture(future);
        }
    }

    int getRandomNumberBetweenOneToSix() {
        Random random = new Random();
        return random.nextInt(5)+1;
    }
}

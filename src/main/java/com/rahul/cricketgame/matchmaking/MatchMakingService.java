package com.rahul.cricketgame.matchmaking;

import com.rahul.cricketgame.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MatchMakingService {

    @Autowired
    GameService gameService;

    private final ConcurrentLinkedQueue<Player> playerQueue = new ConcurrentLinkedQueue<>();

    public void addPlayerToQueueListener(Player player) {
        addPlayerToQueue(player);
    }

    private void addPlayerToQueue(Player player) {
        playerQueue.add(player);
        synchronized(this) {
            if (playerQueue.size() >= 2) {
                Player player1 = playerQueue.poll();
                Player player2 = playerQueue.poll();
                if (player1 != null && player2 != null) {
                    String sessionId = UUID.randomUUID().toString();
                    GameSession gameSession = new GameSession(sessionId, player1, player2);
                    gameService.startGame(gameSession);
                    System.out.println("game started between "+ player1.getName() + " " + player2.getName());
                }
            }
        }
    }
}

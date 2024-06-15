package com.rahul.cricketgame.matchmaking;

import com.rahul.cricketgame.entity.Player;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

public class GameSession {

    private final String sessionId;
    private final Player player1;
    private final Player player2;
    private Integer currRoundNumber;
    private Player winner;
    private Integer player1Input;
    private Integer player2Input;
    private LocalDateTime startTime;
    private ScheduledFuture<?> scheduledFuture;
    private Integer totalPlayer1Score;
    private Integer totalPlayer2Score;
    private boolean isPlayer1BatsMan;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public GameSession(String sessionId, Player player1, Player player2) {
        this.sessionId = sessionId;
        this.player1 = player1;
        this.player2 = player2;
        this.currRoundNumber = 1;
        this.player1Input = null;
        this.player2Input = null;
        this.totalPlayer1Score = 0;
        this.totalPlayer2Score=0;
        this.winner = null;
        startTime = LocalDateTime.now();
        scheduledFuture = null;
        isPlayer1BatsMan = true;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isGameOver() {
        return currRoundNumber>5;
    }

    public void calculateThisRoundScore() {
        if(Objects.equals(player1Input, player2Input)) {
            System.out.println("out");
            isPlayer1BatsMan = false;
        } else{
            if(isPlayer1BatsMan) {
                totalPlayer1Score+=player1Input;
            } else{
                totalPlayer2Score+=player2Input;
            }
        }
        if(currRoundNumber==5 && isPlayer1BatsMan) {
            isPlayer1BatsMan=false;
            System.out.println("player roles swapped as balls for innings finished");
        }
    }

    public void submitNumber(int playerId, int inputNumber) {
        if(player1.getId()==playerId) {
            player1Input=inputNumber;
        } else {
            player2Input=inputNumber;
        }
    }

    public void calculateWinner() {
        if(totalPlayer1Score>totalPlayer2Score) {
            winner = player1;
        } else if(totalPlayer1Score.equals(totalPlayer2Score)) {
            winner = null;
        } else {
            winner = player2;
        }
        if(Objects.nonNull(winner)) {
            System.out.println(winner.getName() + "won the match");
        } else{
            System.out.println("match is draw no winner");
        }
    }

    public void prepareForNextRound() {
        this.currRoundNumber++;
        this.player1Input = null;
        this.player2Input = null;
        startTime = LocalDateTime.now();
        scheduledFuture = null;
    }

    public boolean bothPlayerSubmitted() {
        return Objects.nonNull(player1Input) && Objects.nonNull(player2Input);
    }

    public Integer getPlayer1Input() {
        return player1Input;
    }

    public void setPlayer1Input(Integer player1Input) {
        this.player1Input = player1Input;
    }

    public Integer getPlayer2Input() {
        return player2Input;
    }

    public void setPlayer2Input(Integer player2Input) {
        this.player2Input = player2Input;
    }
}

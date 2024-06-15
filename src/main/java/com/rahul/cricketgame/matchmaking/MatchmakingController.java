package com.rahul.cricketgame.matchmaking;

import com.rahul.cricketgame.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MatchmakingController {
    private final PlayerProducer playerProducer;

    @Autowired
    public MatchmakingController(PlayerProducer playerProducer) {
        this.playerProducer = playerProducer;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGame(@RequestBody Player player) {
        playerProducer.addPlayerToQueue(player);
        String body = "player added successfully";
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}

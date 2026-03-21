package com.realtimeleaderboard.controller;

import com.realtimeleaderboard.service.GameService;
import com.realtimeleaderboard.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
}

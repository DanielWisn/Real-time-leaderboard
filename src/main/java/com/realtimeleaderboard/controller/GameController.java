package com.realtimeleaderboard.controller;

import com.realtimeleaderboard.DTO.CreateGameRequest;
import com.realtimeleaderboard.model.Game;
import com.realtimeleaderboard.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames(){
        return ResponseEntity.ok(this.gameService.getAllGames());
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Integer id){
        return ResponseEntity.ok(this.gameService.getGameById(id));
    }

    @PostMapping()
    public ResponseEntity<Game> createGame(@RequestBody CreateGameRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.gameService.createGame(request.title()));
    }
}

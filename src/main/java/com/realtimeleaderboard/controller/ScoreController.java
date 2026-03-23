package com.realtimeleaderboard.controller;

import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.service.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Score>> findAllByOrderByScoreDesc(){
        return ResponseEntity.ok(this.scoreService.getAllOrderByScoreDesc());
    }

//    @PostMapping("/save")
//    public ResponseEntity<Score> save(@RequestParam Integer score, @RequestParam Integer gameId){
//
//    }
}

package com.realtimeleaderboard.controller;

import com.realtimeleaderboard.DTO.GlobalLeaderboardResponse;
import com.realtimeleaderboard.DTO.LeaderboardEntry;
import com.realtimeleaderboard.DTO.LeaderboardEntryResponse;
import com.realtimeleaderboard.DTO.MaxScoreResponse;
import com.realtimeleaderboard.model.Game;
import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.security.CustomUserDetails;
import com.realtimeleaderboard.service.GameService;
import com.realtimeleaderboard.service.LeaderboardService;
import com.realtimeleaderboard.service.ScoreService;
import com.realtimeleaderboard.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    private final ScoreService scoreService;
    private final LeaderboardService leaderboardService;
    private final GameService gameService;
    private final UserService userService;

    public ScoreController(ScoreService scoreService, LeaderboardService leaderboardService, GameService gameService, UserService userService) {
        this.scoreService = scoreService;
        this.leaderboardService = leaderboardService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<GlobalLeaderboardResponse>> getLeaderboard(@RequestParam(defaultValue = "10") int top){
        List<LeaderboardEntry> leaderboardEntries = this.leaderboardService.getGlobalTopN(top);

        List<GlobalLeaderboardResponse> response = leaderboardEntries.stream()
                .map(entry -> {
                    String username = userService.getUserById(entry.userId()).getUsername();
                    return new GlobalLeaderboardResponse(
                            entry.rank(),
                            username,
                            entry.score().intValue()
                    );
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/gameLeaderboard/{gameId}")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboardForGame(@PathVariable Long gameId, @RequestParam(defaultValue = "10") int top) {
        List<LeaderboardEntry> leaderboardEntries = this.leaderboardService.getTopN(gameId, top);

        try {
            Game game = gameService.getGameById(gameId);
            List<LeaderboardEntryResponse> response = leaderboardEntries.stream()
                    .map(entry -> {
                        String username = userService.getUserById(entry.userId()).getUsername();
                        return new LeaderboardEntryResponse(
                                entry.rank(),
                                username,
                                game.getTitle(),
                                entry.score().intValue()
                        );
                    })
                    .toList();

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/userRank/{gameId}")
    public ResponseEntity<Long> findUserGameRanking(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long gameId){
        Long rank = this.leaderboardService.getUserRank(gameId, currentUser.getId());
        if (rank == -1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/userGlobalRank")
    public ResponseEntity<Long> findUserGlobalRanking(@AuthenticationPrincipal CustomUserDetails currentUser){
        Long rank = this.leaderboardService.getUserGlobalRank(currentUser.getId());
        if (rank == -1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/userScore")
    public ResponseEntity<List<Score>> findUserScores(@AuthenticationPrincipal CustomUserDetails currentUser){
        return ResponseEntity.ok(this.scoreService.getScoresByUserId(currentUser.getId()));
    }

    @PostMapping("/save")
    public ResponseEntity<Score> save(@RequestParam Integer score, @RequestParam Long gameId, @AuthenticationPrincipal CustomUserDetails currentUser){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.scoreService.save(score,gameId,currentUser.getUser()));
    }

    @GetMapping("/userMaxScores")
    public ResponseEntity<List<MaxScoreResponse>> findUserMaxScores(@AuthenticationPrincipal CustomUserDetails current){
        return ResponseEntity.ok(this.scoreService.findUserMaxScores(current.getUser()));
    }
}

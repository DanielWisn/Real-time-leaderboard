package com.realtimeleaderboard.service;

import com.realtimeleaderboard.DTO.LeaderboardEntry;
import com.realtimeleaderboard.DTO.MaxScoreResponse;
import com.realtimeleaderboard.DTO.TopPlayerResponse;
import com.realtimeleaderboard.model.Game;
import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.model.User;
import com.realtimeleaderboard.repository.ScoreRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final GameService gameService;
    private final LeaderboardService leaderboardService;

    public ScoreService(ScoreRepository scoreRepository, GameService gameService, LeaderboardService leaderboardService) {
        this.scoreRepository = scoreRepository;
        this.gameService = gameService;
        this.leaderboardService = leaderboardService;
    }

    public List<Score> getScoresByUserId(Long userId) {
        return this.scoreRepository.findScoresByUserIdOrderByScoreDesc(userId);
    }

    public Score save(Integer score, Long gameId, User user) {
        System.out.println(user.getId() + " " + score + " " + gameId);
        LocalDateTime now = LocalDateTime.now();
        Game game = this.gameService.getGameById(gameId);
        Score scoreEntity = new Score(user,game,score,now);
        leaderboardService.submitScore(game.getId(), user.getId(), score);
        return this.scoreRepository.save(scoreEntity);
    }

    public List<MaxScoreResponse> findUserMaxScores(User user){
        return this.scoreRepository.findUserMaxScores(user);
    }

    public List<TopPlayerResponse> getTopPlayers(Long gameId, LocalDateTime start, LocalDateTime end){
        return this.scoreRepository.findTopPlayersByGameAndPeriod(gameId,start,end);
    }
 }

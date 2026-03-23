package com.realtimeleaderboard.service;

import com.realtimeleaderboard.model.Game;
import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.model.User;
import com.realtimeleaderboard.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final GameService gameService;
    public ScoreService(ScoreRepository scoreRepository, GameService gameService) {
        this.scoreRepository = scoreRepository;
        this.gameService = gameService;
    }

    public List<Score> getAllOrderByScoreDesc() {
        return scoreRepository.findAllByOrderByScoreDesc();
    }

    public Score save(Integer score, Integer gameId, User user) {

        LocalDateTime now = LocalDateTime.now();
        Game game = this.gameService.getGameById(gameId);
        Score scoreEntity = new Score(user,game,score,now);
        return this.scoreRepository.save(scoreEntity);
    }
}

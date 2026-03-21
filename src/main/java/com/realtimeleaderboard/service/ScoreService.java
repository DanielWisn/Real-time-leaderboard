package com.realtimeleaderboard.service;

import com.realtimeleaderboard.repository.ScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }
}

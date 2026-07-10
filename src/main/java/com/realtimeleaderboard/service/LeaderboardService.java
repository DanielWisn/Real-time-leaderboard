package com.realtimeleaderboard.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import com.realtimeleaderboard.DTO.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String KEY_PREFIX = "leaderboard:game:";

    private final RedisTemplate<String, String> redisTemplate;

    public LeaderboardService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long gameId) {
        return KEY_PREFIX + gameId;
    }

    /**
     * Submits a score to Redis. Only keeps the best (highest) score per user per game.
     * Uses ZADD GT — Redis only updates the member if the new score is greater.
     */
    public void submitScore(Long gameId, Long userId, double score) {
        Double current = redisTemplate.opsForZSet()
                .score(key(gameId), String.valueOf(userId));
        if (current == null || score > current) {
            redisTemplate.opsForZSet().add(key(gameId), String.valueOf(userId), score);
        }
    }

    /**
     * Returns the top N entries for a game, highest score first.
     * Each entry contains the userId (as String) and their best score.
     */
    public List<LeaderboardEntry> getTopN(Long gameId, int n) {
        Set<TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key(gameId), 0, n - 1);

        List<LeaderboardEntry> entries = new ArrayList<>();
        if (results == null) return entries;

        int rank = 1;
        for (TypedTuple<String> tuple : results) {
            entries.add(new LeaderboardEntry(
                    rank++,
                    Long.parseLong(tuple.getValue()),
                    tuple.getScore()
            ));
        }
        return entries;
    }

    public long getUserRank(Long gameId, Long userId) {
        Long rank = redisTemplate.opsForZSet()
                .reverseRank(key(gameId), String.valueOf(userId));
        return rank == null ? -1 : rank + 1; // Redis rank is 0-based
    }
}
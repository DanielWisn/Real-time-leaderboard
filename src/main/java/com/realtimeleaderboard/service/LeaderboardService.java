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
    private static final String GLOBAL_KEY = "leaderboard:global";

    private final RedisTemplate<String, String> redisTemplate;

    public LeaderboardService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long gameId) {
        return KEY_PREFIX + gameId;
    }

    public void submitScore(Long gameId, Long userId, double score) {
        String member = String.valueOf(userId);
        Double current = redisTemplate.opsForZSet()
                .score(key(gameId), member);
        if (current == null || score > current) {
            redisTemplate.opsForZSet().add(key(gameId), member, score);

            double delta = score - (current != null ? current : 0.0);
            redisTemplate.opsForZSet().incrementScore(GLOBAL_KEY, member, delta);
        }
    }

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

    public List<LeaderboardEntry> getGlobalTopN(int n) {
        Set<TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(GLOBAL_KEY, 0, n - 1);

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
        return rank == null ? -1 : rank + 1;
    }

    public long getUserGlobalRank(Long userId) {
        Long rank = redisTemplate.opsForZSet()
                .reverseRank(GLOBAL_KEY, String.valueOf(userId));
        return rank == null ? -1 : rank + 1;
    }
}
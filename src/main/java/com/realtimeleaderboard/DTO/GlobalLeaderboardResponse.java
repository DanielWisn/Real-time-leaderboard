package com.realtimeleaderboard.DTO;

public record GlobalLeaderboardResponse(
        int rank,
        String username,
        int totalScore
) {}
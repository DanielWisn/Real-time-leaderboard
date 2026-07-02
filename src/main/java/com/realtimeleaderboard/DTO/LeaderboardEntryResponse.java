package com.realtimeleaderboard.DTO;

public record LeaderboardEntryResponse(
        int rank,
        String username,
        String gameTitle,
        int score
) {}
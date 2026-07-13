package com.realtimeleaderboard.repository;

import com.realtimeleaderboard.DTO.MaxScoreResponse;
import com.realtimeleaderboard.DTO.TopPlayerResponse;
import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findScoresByGameIdOrderByScoreDesc(Long gameId);

    List<Score> findScoresByUserIdOrderByScoreDesc(Long userId);

    @Query("SELECT s.game.title,Max(s.score) FROM Score s where s.user=:user group by s.game, s.game.title")
    List<MaxScoreResponse> findUserMaxScores(@Param("user") User user);

    @Query("SELECT new com.realtimeleaderboard.DTO.TopPlayerResponse(s.user.username, s.game.title, MAX(s.score))" +
            "FROM Score s " +
            "WHERE s.game.id = :gameId AND s.date BETWEEN :from AND :to " +
            "GROUP BY s.user.id, s.user.username, s.game.title " +
            "ORDER BY MAX(s.score) DESC")
    List<TopPlayerResponse> findTopPlayersByGameAndPeriod(
            @Param("gameId") Long gameId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

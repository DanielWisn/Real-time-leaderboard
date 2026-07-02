package com.realtimeleaderboard.repository;

import com.realtimeleaderboard.model.Score;
import com.realtimeleaderboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findScoresByGameIdOrderByScoreDesc(Long gameId);

    List<Score> findScoresByUserIdOrderByScoreDesc(Long userId);

    @Query("SELECT s.game,Max(s.score), s.date FROM Score s where s.user=:user group by s.game")
    List<Object[]> findUserMaxScores(@Param("user") User user);
}

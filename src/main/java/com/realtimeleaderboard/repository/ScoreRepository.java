package com.realtimeleaderboard.repository;

import com.realtimeleaderboard.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score,Long> {

}

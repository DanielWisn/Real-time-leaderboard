package com.realtimeleaderboard.repository;

import com.realtimeleaderboard.model.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {
    @Override
    List<Game> findAll();


}

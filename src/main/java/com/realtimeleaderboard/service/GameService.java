package com.realtimeleaderboard.service;

import com.realtimeleaderboard.model.Game;
import com.realtimeleaderboard.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames(){
        return this.gameRepository.findAll();
    }

    public Game getGameById(Long id){

        return this.gameRepository.findById(id).orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public Game createGame(String title){
        Game game = new Game(title);
        return this.gameRepository.save(game);
    }

    public void deleteGame(Long id){
        this.gameRepository.deleteById(id);
    }
}

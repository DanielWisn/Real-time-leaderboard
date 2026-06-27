package com.realtimeleaderboard.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name="games")
public class Game {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    public Game(){

    }

    public Game(String title){
        this.title = title;
    }
}

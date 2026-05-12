package com.realtimeleaderboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="scores")
public class Score {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime date;

    public Score() {
    }

    public Score(User user, Game game, Integer score, LocalDateTime date) {
        this.user = user;
        this.game = game;
        this.score = score;
        this.date = date;
    }
}

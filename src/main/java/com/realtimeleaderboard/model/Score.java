package com.realtimeleaderboard.model;

import jakarta.persistence.*;

import java.util.Date;

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
    private Date date;
}

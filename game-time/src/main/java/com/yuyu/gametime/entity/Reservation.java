package com.yuyu.gametime.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private Long userId;
    private String username;
    private LocalDate date;
    private String timeslot;
    private Long gameId;
    private String gameName;
    private String note;
    private LocalDateTime createdAt;

    public Reservation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTimeslot() { return timeslot; }
    public void setTimeslot(String timeslot) { this.timeslot = timeslot; }
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

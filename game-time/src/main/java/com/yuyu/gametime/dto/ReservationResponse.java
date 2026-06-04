package com.yuyu.gametime.dto;

import java.time.format.DateTimeFormatter;

import com.yuyu.gametime.entity.Reservation;

public class ReservationResponse {
    private Long id;
    private Long userId;
    private String username;
    private String date;
    private String timeslot;
    private Long gameId;
    private String gameName;
    private String note;
    private String createdAt;

    public ReservationResponse() {}

    public static ReservationResponse from(Reservation r) {
        ReservationResponse resp = new ReservationResponse();
        resp.id = r.getId();
        resp.userId = r.getUserId();
        resp.username = r.getUsername();
        resp.date = r.getDate() == null ? null : r.getDate().toString();
        resp.timeslot = r.getTimeslot();
        resp.gameId = r.getGameId();
        resp.gameName = r.getGameName();
        resp.note = r.getNote();
        resp.createdAt = r.getCreatedAt() == null ? null : r.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        return resp;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDate() { return date; }
    public String getTimeslot() { return timeslot; }
    public Long getGameId() { return gameId; }
    public String getGameName() { return gameName; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
}

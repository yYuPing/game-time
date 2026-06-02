package com.yuyu.game_time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ReservationRequest {

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "时段不能为空")
    @Pattern(regexp = "^(08-09|09-10|10-11|11-12|12-13|13-14|14-15|15-16|16-17|17-18|18-19|19-20|20-21|21-22)$", message = "时段不在允许范围内")
    private String timeslot;

    @NotNull(message = "gameId 不能为空")
    private Long gameId;

    @Size(max = 500, message = "备注长度不能超过 500 字符")
    private String note;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTimeslot() { return timeslot; }
    public void setTimeslot(String timeslot) { this.timeslot = timeslot; }
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

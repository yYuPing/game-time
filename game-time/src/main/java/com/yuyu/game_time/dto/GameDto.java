package com.yuyu.game_time.dto;

public class GameDto {
    private Long id;
    private String name;
    private String color;
    private String meta;

    public GameDto() {}

    public GameDto(Long id, String name, String color, String meta) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.meta = meta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getMeta() { return meta; }
    public void setMeta(String meta) { this.meta = meta; }
}

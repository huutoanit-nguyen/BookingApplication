package com.example.movieticket.model;

public class Showtime {
    private int id;
    private int movieId;
    private String room;
    private String startTime;
    private int price;

    public Showtime() {}

    public Showtime(int id, int movieId, String room, String startTime, int price) {
        this.id = id;
        this.movieId = movieId;
        this.room = room;
        this.startTime = startTime;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}

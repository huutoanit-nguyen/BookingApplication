package com.example.movieticket.model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private int duration;
    private String director;
    private String description;
    private int poster;

    public Movie() {}

    public Movie(int id, String title, String genre, int duration, String director, String description, int poster) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.description = description;
        this.poster = poster;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPoster() { return poster; }
    public void setPoster(int poster) { this.poster = poster; }
}

package com.example.movieticket.model;

import java.util.List;
import java.util.ArrayList;

public class Booking {
    private int id;
    private int userId;
    private int showtimeId;
    private String movieTitle;
    private String startTime;
    private List<String> seats;
    private int totalPrice;
    private String createdAt;
    private String paymentMethod;
    private String paymentStatus;

    public Booking() {}

    public Booking(int id, int userId, int showtimeId, String movieTitle, String startTime, List<String> seats, int totalPrice, String createdAt) {
        this(id, userId, showtimeId, movieTitle, startTime, seats, totalPrice, createdAt, "", "");
    }

    public Booking(int id, int userId, int showtimeId, String movieTitle, String startTime, List<String> seats,
                   int totalPrice, String createdAt, String paymentMethod, String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.startTime = startTime;
        this.seats = new ArrayList<>(seats);
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public List<String> getSeats() { return seats == null ? new ArrayList<>() : new ArrayList<>(seats); }
    public void setSeats(List<String> seats) { this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats); }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}

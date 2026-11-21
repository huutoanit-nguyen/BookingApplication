package com.example.movieticket.model;

public final class AdminStats {
    private final int customers;
    private final int loggedInCustomers;
    private final int loginCount;
    private final int movieViewCount;
    private final int uniqueViewers;
    private final int ticketCount;
    private final long revenue;

    public AdminStats(int customers, int loggedInCustomers, int loginCount, int movieViewCount,
                      int uniqueViewers, int ticketCount, long revenue) {
        this.customers = customers;
        this.loggedInCustomers = loggedInCustomers;
        this.loginCount = loginCount;
        this.movieViewCount = movieViewCount;
        this.uniqueViewers = uniqueViewers;
        this.ticketCount = ticketCount;
        this.revenue = revenue;
    }

    public int getCustomers() { return customers; }
    public int getLoggedInCustomers() { return loggedInCustomers; }
    public int getLoginCount() { return loginCount; }
    public int getMovieViewCount() { return movieViewCount; }
    public int getUniqueViewers() { return uniqueViewers; }
    public int getTicketCount() { return ticketCount; }
    public long getRevenue() { return revenue; }
}

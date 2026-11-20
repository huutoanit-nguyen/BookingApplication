package com.example.movieticket.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import com.example.movieticket.R;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;

public class TicketDetailActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        Booking booking = ownBooking();
        if (booking == null) { missingData(); return; }
        Showtime showtime = repository.getShowtimeById(booking.getShowtimeId());
        Movie movie = repository.getMovieById(showtime.getMovieId());
        setContentView(R.layout.activity_ticket_detail);
        backButton();
        ((ImageView) findViewById(R.id.poster)).setImageResource(movie.getPoster());
        text(R.id.code, getString(R.string.booking_code_format, getString(R.string.ticket_code, booking.getId())));
        text(R.id.title, booking.getMovieTitle());
        text(R.id.genre, movie.getGenre() + " · " + getString(R.string.duration_format, movie.getDuration()));
        text(R.id.date, getString(R.string.booking_day_format, booking.getStartTime().split(" · ")[0]));
        text(R.id.time, getString(R.string.ticket_time_format, booking.getStartTime()));
        text(R.id.room, getString(R.string.ticket_room_format, showtime.getRoom()));
        text(R.id.seats, getString(R.string.ticket_seats_format, TextUtils.join(", ", booking.getSeats())));
        text(R.id.user, getString(R.string.ticket_user_format, booking.getUserId()));
        text(R.id.created_at, getString(R.string.ticket_created_format, booking.getCreatedAt()));
        text(R.id.total, getString(R.string.total_format, price(booking.getTotalPrice())));
        findViewById(R.id.home).setOnClickListener(v -> resetTo(MovieListActivity.class));
    }
}

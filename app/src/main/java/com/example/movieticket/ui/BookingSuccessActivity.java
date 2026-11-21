package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.example.movieticket.R;
import com.example.movieticket.model.Booking;

public class BookingSuccessActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        Booking booking = ownBooking();
        if (booking == null) { missingData(); return; }
        setContentView(R.layout.activity_booking_success);
        text(R.id.code, getString(R.string.booking_code_format, getString(R.string.ticket_code, booking.getId())));
        text(R.id.movie_title, booking.getMovieTitle());
        text(R.id.time, getString(R.string.ticket_time_format, booking.getStartTime()));
        text(R.id.seats, getString(R.string.ticket_seats_format, TextUtils.join(", ", booking.getSeats())));
        text(R.id.total, getString(R.string.total_format, price(booking.getTotalPrice())));
        text(R.id.payment_method, getString(R.string.payment_method_format, booking.getPaymentMethod()));
        text(R.id.payment_status, getString(R.string.payment_status_success));
        findViewById(R.id.home).setOnClickListener(v -> resetTo(MovieListActivity.class));
        findViewById(R.id.tickets).setOnClickListener(v -> {
            Intent home = new Intent(this, MovieListActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(home);
            startActivity(new Intent(this, MyTicketsActivity.class));
            finish();
        });
    }
}

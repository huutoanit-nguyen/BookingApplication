package com.example.movieticket.ui;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.adapter.BookingAdapter;
import com.example.movieticket.model.Booking;
import java.util.List;

public class MyTicketsActivity extends BaseActivity {
    private RecyclerView list;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        setContentView(R.layout.activity_my_tickets);
        backButton();
        list = findViewById(R.id.bookings);
        list.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.discover).setOnClickListener(v -> resetTo(MovieListActivity.class));
    }
    @Override protected void onResume() {
        super.onResume();
        if (list == null || !session.isLoggedIn()) return;
        List<Booking> bookings = repository.getBookingsByUser(session.getUserId());
        text(R.id.count, getString(R.string.ticket_count_format, bookings.size()));
        list.setVisibility(bookings.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.empty).setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
        list.setAdapter(new BookingAdapter(bookings, b -> open(TicketDetailActivity.class, BOOKING_ID, b.getId())));
    }
}

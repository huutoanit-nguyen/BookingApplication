package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.adapter.SeatAdapter;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends BaseActivity {
    private static final String SELECTED = "selected_seats";
    private Showtime showtime;
    private SeatAdapter adapter;
    private boolean bookingInProgress;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        showtime = repository.getShowtimeById(getIntent().getIntExtra(SHOWTIME_ID, -1));
        if (showtime == null) { missingData(); return; }
        Movie movie = repository.getMovieById(showtime.getMovieId());
        setContentView(R.layout.activity_seat_selection);
        backButton();
        text(R.id.movie_info, movie.getTitle());
        text(R.id.showtime_info, showtime.getStartTime() + " · " + showtime.getRoom());
        ArrayList<String> restored = state == null ? null : state.getStringArrayList(SELECTED);
        adapter = new SeatAdapter(repository.getBookedSeats(showtime.getId()),
                restored == null ? new ArrayList<>() : restored, new SeatAdapter.Listener() {
            @Override public void onSelectionChanged(List<String> selected) { updateSummary(); }
            @Override public void onLimitReached() { message(R.string.seat_limit_error); }
        });
        RecyclerView seats = findViewById(R.id.seats);
        seats.setLayoutManager(new GridLayoutManager(this, 6));
        seats.setAdapter(adapter);
        findViewById(R.id.book).setOnClickListener(this::book);
        updateSummary();
    }
    @Override protected void onResume() {
        super.onResume();
        if (adapter != null && showtime != null) {
            bookingInProgress = false;
            adapter.updateBooked(repository.getBookedSeats(showtime.getId()));
            updateSummary();
        }
    }
    private void updateSummary() {
        List<String> selected = adapter.getSelected();
        text(R.id.selected_seats, getString(R.string.chosen_seats_format,
                selected.isEmpty() ? getString(R.string.no_seats) : TextUtils.join(", ", selected)));
        text(R.id.total, getString(R.string.total_format, price(selected.size() * showtime.getPrice())));
        findViewById(R.id.book).setEnabled(!selected.isEmpty() && !bookingInProgress);
    }
    private void book(View button) {
        if (bookingInProgress) return;
        bookingInProgress = true; button.setEnabled(false);
        ArrayList<String> seats = new ArrayList<>(adapter.getSelected());
        startActivity(new Intent(this, PaymentActivity.class)
                .putExtra(SHOWTIME_ID, showtime.getId())
                .putStringArrayListExtra(PaymentActivity.SELECTED_SEATS, seats));
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle out) {
        if (adapter != null) out.putStringArrayList(SELECTED, new ArrayList<>(adapter.getSelected()));
        super.onSaveInstanceState(out);
    }
}

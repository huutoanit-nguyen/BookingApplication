package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.movieticket.R;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import java.util.ArrayList;

public class PaymentActivity extends BaseActivity {
    public static final String SELECTED_SEATS = "selected_seats";

    private Showtime showtime;
    private ArrayList<String> seats;
    private RadioGroup methods;
    private boolean paymentInProgress;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;

        showtime = repository.getShowtimeById(getIntent().getIntExtra(SHOWTIME_ID, -1));
        seats = getIntent().getStringArrayListExtra(SELECTED_SEATS);
        if (showtime == null || seats == null || seats.isEmpty()) { missingData(); return; }
        Movie movie = repository.getMovieById(showtime.getMovieId());
        if (movie == null) { missingData(); return; }

        setContentView(R.layout.activity_payment);
        backButton();
        methods = findViewById(R.id.payment_methods);
        text(R.id.payment_showtime, getString(R.string.payment_showtime_format,
                movie.getTitle(), showtime.getStartTime(), showtime.getRoom()));
        text(R.id.payment_seats, getString(R.string.chosen_seats_format, TextUtils.join(", ", seats)));
        text(R.id.payment_total, getString(R.string.total_format, price(seats.size() * showtime.getPrice())));
        findViewById(R.id.confirm_payment).setOnClickListener(v -> confirmPayment());
        findViewById(R.id.simulate_failure).setOnClickListener(v -> simulateFailure());
    }

    private String selectedMethod() {
        int selectedId = methods.getCheckedRadioButtonId();
        if (selectedId == -1) return null;
        return ((RadioButton) findViewById(selectedId)).getText().toString();
    }

    private void confirmPayment() {
        if (paymentInProgress) return;
        String method = selectedMethod();
        if (method == null) { message(R.string.payment_method_required); return; }

        paymentInProgress = true;
        findViewById(R.id.confirm_payment).setEnabled(false);
        int bookingId = repository.createBookingAndPayment(session.getUserId(), showtime.getId(),
                seats, seats.size() * showtime.getPrice(), method);
        if (bookingId <= 0) {
            paymentInProgress = false;
            findViewById(R.id.confirm_payment).setEnabled(true);
            message(R.string.booking_failed);
            return;
        }
        startActivity(new Intent(this, BookingSuccessActivity.class).putExtra(BOOKING_ID, bookingId));
        finish();
    }

    private void simulateFailure() {
        String method = selectedMethod();
        if (method == null) { message(R.string.payment_method_required); return; }
        boolean recorded = repository.recordFailedPayment(session.getUserId(), showtime.getId(),
                seats, seats.size() * showtime.getPrice(), method);
        if (recorded) message(R.string.payment_failed_demo);
    }
}

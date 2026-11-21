package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.movieticket.R;
import com.example.movieticket.data.Repository;
import com.example.movieticket.data.SessionManager;
import com.example.movieticket.model.Booking;
import com.google.android.material.snackbar.Snackbar;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String MOVIE_ID = "movie_id", SHOWTIME_ID = "showtime_id", BOOKING_ID = "booking_id";
    protected Repository repository;
    protected SessionManager session;

    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        repository = Repository.getInstance(this);
        session = new SessionManager(this);
    }
    protected boolean requireLogin() {
        if (session.isLoggedIn()) return true;
        resetTo(LoginActivity.class);
        return false;
    }
    protected boolean requireAdmin() {
        if (!requireLogin()) return false;
        if (repository.isAdmin(session.getUserId())) return true;
        Toast.makeText(this, R.string.admin_required, Toast.LENGTH_SHORT).show();
        resetTo(MovieListActivity.class);
        return false;
    }
    @Override protected void onResume() {
        super.onResume();
        if (!(this instanceof LoginActivity) && !(this instanceof RegisterActivity)
                && !(this instanceof SplashActivity) && session != null && !session.isLoggedIn()) resetTo(LoginActivity.class);
    }
    protected void resetTo(Class<?> activity) {
        startActivity(new Intent(this, activity).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
    protected void open(Class<?> activity, String extra, int id) {
        startActivity(new Intent(this, activity).putExtra(extra, id));
    }
    protected void backButton() { findViewById(R.id.back).setOnClickListener(v -> finish()); }
    protected void text(int id, CharSequence value) { ((TextView) findViewById(id)).setText(value); }
    protected String price(int amount) {
        return getString(R.string.price_format, NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(amount));
    }
    protected String price(long amount) {
        return getString(R.string.price_format, NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(amount));
    }
    protected void message(int stringId) { Snackbar.make(findViewById(android.R.id.content), stringId, Snackbar.LENGTH_LONG).show(); }
    protected void missingData() {
        Toast.makeText(this, R.string.missing_data, Toast.LENGTH_SHORT).show();
        finish();
    }
    protected Booking ownBooking() {
        Booking booking = repository.getBookingById(getIntent().getIntExtra(BOOKING_ID, -1));
        return booking != null && booking.getUserId() == session.getUserId() ? booking : null;
    }
}

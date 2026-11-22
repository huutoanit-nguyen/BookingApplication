package com.example.movieticket.ui;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.adapter.ShowtimeAdapter;
import com.example.movieticket.model.Movie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        Movie movie = repository.getMovieById(getIntent().getIntExtra(MOVIE_ID, -1));
        if (movie == null) { missingData(); return; }
        if (state == null) repository.recordMovieView(session.getUserId(), movie.getId());
        setContentView(R.layout.activity_movie_detail);
        backButton();
        loadPoster(findViewById(R.id.poster), movie.getPoster());
        text(R.id.title, movie.getTitle()); text(R.id.genre, movie.getGenre());
        text(R.id.duration, getString(R.string.duration_format, movie.getDuration()));
        text(R.id.director, getString(R.string.director_format, movie.getDirector()));
        text(R.id.description, movie.getDescription());
        text(R.id.date, getString(R.string.booking_day_format, new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN")).format(new Date())));
        RecyclerView list = findViewById(R.id.showtimes);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new ShowtimeAdapter(repository.getShowtimes(movie.getId()), showtime -> open(SeatSelectionActivity.class, SHOWTIME_ID, showtime.getId())));
    }
}

package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.adapter.MovieAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MovieListActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireLogin()) return;
        setContentView(R.layout.activity_movie_list);
        RecyclerView list = findViewById(R.id.movies);
        list.setLayoutManager(new GridLayoutManager(this, 2));
        list.setAdapter(new MovieAdapter(repository.getMovies(), movie -> open(MovieDetailActivity.class, MOVIE_ID, movie.getId())));
        findViewById(R.id.tickets).setOnClickListener(v -> startActivity(new Intent(this, MyTicketsActivity.class)));
        findViewById(R.id.logout).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout_title).setMessage(R.string.logout_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout, (dialog, which) -> { session.logout(); resetTo(LoginActivity.class); }).show());
    }
}

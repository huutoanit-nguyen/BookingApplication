package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.adapter.AdminMovieAdapter;
import com.example.movieticket.model.Movie;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;

public class AdminDashboardActivity extends BaseActivity {
    private RecyclerView movies;
    private AdminMovieAdapter adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireAdmin()) return;
        setContentView(R.layout.activity_admin_dashboard);
        backButton();
        movies = findViewById(R.id.admin_movies);
        movies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminMovieAdapter(new ArrayList<>(), new AdminMovieAdapter.Listener() {
            @Override public void onEdit(Movie movie) {
                open(AdminMovieFormActivity.class, MOVIE_ID, movie.getId());
            }

            @Override public void onDelete(Movie movie) {
                new MaterialAlertDialogBuilder(AdminDashboardActivity.this)
                        .setTitle(R.string.delete_movie_title)
                        .setMessage(getString(R.string.delete_movie_message, movie.getTitle()))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, (dialog, which) -> deleteMovie(movie))
                        .show();
            }
        });
        movies.setAdapter(adapter);
        findViewById(R.id.add_movie).setOnClickListener(v ->
                startActivity(new Intent(this, AdminMovieFormActivity.class)));
        findViewById(R.id.logout).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout_title).setMessage(R.string.logout_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout, (dialog, which) -> {
                    session.logout();
                    resetTo(LoginActivity.class);
                }).show());
        refreshMovies();
    }

    @Override protected void onResume() {
        super.onResume();
        if (adapter != null) refreshMovies();
    }

    private void refreshMovies() {
        adapter.replaceMovies(repository.getMovies());
        text(R.id.movie_count, getString(R.string.admin_movie_count_format, adapter.getItemCount()));
    }

    private void deleteMovie(Movie movie) {
        if (!repository.deleteMovie(movie.getId())) {
            message(R.string.movie_delete_blocked);
            return;
        }
        message(R.string.movie_deleted);
        refreshMovies();
    }
}

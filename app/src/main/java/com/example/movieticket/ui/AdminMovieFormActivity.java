package com.example.movieticket.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.movieticket.R;
import com.example.movieticket.model.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminMovieFormActivity extends BaseActivity {
    private static final int[] POSTERS = {
            R.drawable.poster_1, R.drawable.poster_2, R.drawable.poster_3,
            R.drawable.poster_4, R.drawable.poster_5, R.drawable.poster_6
    };

    private TextInputEditText title, genre, duration, director, description;
    private TextInputLayout titleLayout, genreLayout, durationLayout, directorLayout, descriptionLayout;
    private Spinner poster;
    private Movie editingMovie;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!requireAdmin()) return;

        int movieId = getIntent().getIntExtra(MOVIE_ID, -1);
        editingMovie = movieId > 0 ? repository.getMovieById(movieId) : null;
        if (movieId > 0 && editingMovie == null) { missingData(); return; }

        setContentView(R.layout.activity_admin_movie_form);
        backButton();
        titleLayout = findViewById(R.id.title_layout);
        genreLayout = findViewById(R.id.genre_layout);
        durationLayout = findViewById(R.id.duration_layout);
        directorLayout = findViewById(R.id.director_layout);
        descriptionLayout = findViewById(R.id.description_layout);
        title = findViewById(R.id.title);
        genre = findViewById(R.id.genre);
        duration = findViewById(R.id.duration);
        director = findViewById(R.id.director);
        description = findViewById(R.id.description);
        poster = findViewById(R.id.poster);

        ArrayAdapter<CharSequence> posters = ArrayAdapter.createFromResource(this,
                R.array.movie_posters, android.R.layout.simple_spinner_item);
        posters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poster.setAdapter(posters);
        text(R.id.form_heading, getString(editingMovie == null
                ? R.string.add_movie : R.string.edit_movie));
        findViewById(R.id.save_movie).setOnClickListener(v -> saveMovie());

        if (editingMovie != null) populateForm();
    }

    private void populateForm() {
        title.setText(editingMovie.getTitle());
        genre.setText(editingMovie.getGenre());
        duration.setText(String.valueOf(editingMovie.getDuration()));
        director.setText(editingMovie.getDirector());
        description.setText(editingMovie.getDescription());
        for (int i = 0; i < POSTERS.length; i++) {
            if (POSTERS[i] == editingMovie.getPoster()) {
                poster.setSelection(i);
                break;
            }
        }
    }

    private String value(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    private void saveMovie() {
        clearErrors();
        String movieTitle = value(title);
        String movieGenre = value(genre);
        String movieDirector = value(director);
        String movieDescription = value(description);
        String durationText = value(duration);
        boolean valid = true;

        if (movieTitle.isEmpty()) { titleLayout.setError(getString(R.string.required)); valid = false; }
        if (movieGenre.isEmpty()) { genreLayout.setError(getString(R.string.required)); valid = false; }
        if (movieDirector.isEmpty()) { directorLayout.setError(getString(R.string.required)); valid = false; }
        if (movieDescription.isEmpty()) { descriptionLayout.setError(getString(R.string.required)); valid = false; }

        int minutes = 0;
        try {
            minutes = Integer.parseInt(durationText);
            if (minutes <= 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            durationLayout.setError(getString(R.string.invalid_duration));
            valid = false;
        }
        if (!valid) return;

        int id = editingMovie == null ? 0 : editingMovie.getId();
        Movie movie = new Movie(id, movieTitle, movieGenre, minutes, movieDirector,
                movieDescription, POSTERS[poster.getSelectedItemPosition()]);
        if (editingMovie == null) {
            if (repository.createMovie(movie) <= 0) {
                message(R.string.movie_save_failed);
                return;
            }
            Toast.makeText(this, R.string.movie_created, Toast.LENGTH_SHORT).show();
        } else {
            if (!repository.updateMovie(movie)) {
                message(R.string.movie_save_failed);
                return;
            }
            Toast.makeText(this, R.string.movie_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void clearErrors() {
        titleLayout.setError(null);
        genreLayout.setError(null);
        durationLayout.setError(null);
        directorLayout.setError(null);
        descriptionLayout.setError(null);
    }
}

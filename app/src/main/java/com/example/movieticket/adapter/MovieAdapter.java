package com.example.movieticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.model.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Holder> {
    public interface Listener { void onMovieClick(Movie movie); }
    private final List<Movie> movies;
    private final Listener listener;
    public MovieAdapter(List<Movie> movies, Listener listener) {
        this.movies = movies; this.listener = listener;
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Movie movie = movies.get(position);
        h.poster.setImageResource(movie.getPoster());
        h.poster.setContentDescription(movie.getTitle());
        h.title.setText(movie.getTitle());
        h.genre.setText(movie.getGenre());
        h.duration.setText(h.itemView.getContext().getString(R.string.duration_format, movie.getDuration()));
        h.itemView.setOnClickListener(v -> listener.onMovieClick(movie));
    }
    @Override public int getItemCount() { return movies.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final ImageView poster;
        final TextView title, genre, duration;
        Holder(View view) {
            super(view);
            poster = view.findViewById(R.id.poster);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            duration = view.findViewById(R.id.duration);
        }
    }
}

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
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class AdminMovieAdapter extends RecyclerView.Adapter<AdminMovieAdapter.Holder> {
    public interface Listener {
        void onEdit(Movie movie);
        void onDelete(Movie movie);
    }

    private final List<Movie> movies;
    private final Listener listener;

    public AdminMovieAdapter(List<Movie> movies, Listener listener) {
        this.movies = new ArrayList<>(movies);
        this.listener = listener;
    }

    public void replaceMovies(List<Movie> updatedMovies) {
        movies.clear();
        movies.addAll(updatedMovies);
        notifyDataSetChanged();
    }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_movie, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Movie movie = movies.get(position);
        holder.poster.setImageResource(movie.getPoster());
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.duration.setText(holder.itemView.getContext()
                .getString(R.string.duration_format, movie.getDuration()));
        holder.edit.setOnClickListener(v -> listener.onEdit(movie));
        holder.delete.setOnClickListener(v -> listener.onDelete(movie));
    }

    @Override public int getItemCount() { return movies.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final ImageView poster;
        final TextView title, genre, duration;
        final MaterialButton edit, delete;

        Holder(View view) {
            super(view);
            poster = view.findViewById(R.id.poster);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            duration = view.findViewById(R.id.duration);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }
    }
}

package com.example.movieticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.model.Showtime;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.Holder> {
    public interface Listener { void onShowtimeClick(Showtime showtime); }
    private final List<Showtime> showtimes;
    private final Listener listener;
    public ShowtimeAdapter(List<Showtime> showtimes, Listener listener) { this.showtimes = showtimes; this.listener = listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Showtime showtime = showtimes.get(position);
        h.time.setText(showtime.getStartTime());
        h.room.setText(h.itemView.getContext().getString(R.string.showtime_room_format, showtime.getRoom()));
        h.price.setText(h.itemView.getContext().getString(R.string.price_format,
                NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(showtime.getPrice())));
        h.itemView.setOnClickListener(v -> listener.onShowtimeClick(showtime));
    }
    @Override public int getItemCount() { return showtimes.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView time, room, price;
        Holder(View view) {
            super(view);
            time = view.findViewById(R.id.time); room = view.findViewById(R.id.room); price = view.findViewById(R.id.price);
        }
    }
}

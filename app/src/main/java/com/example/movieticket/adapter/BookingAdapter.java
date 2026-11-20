package com.example.movieticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.example.movieticket.model.Booking;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.Holder> {
    public interface Listener { void onBookingClick(Booking booking); }
    private final List<Booking> bookings;
    private final Listener listener;
    public BookingAdapter(List<Booking> bookings, Listener listener) { this.bookings = bookings; this.listener = listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Booking b = bookings.get(position);
        android.content.Context c = h.itemView.getContext();
        h.code.setText(c.getString(R.string.ticket_code, b.getId()));
        h.title.setText(b.getMovieTitle());
        h.time.setText(c.getString(R.string.ticket_time_format, b.getStartTime()));
        h.seats.setText(c.getString(R.string.ticket_seats_format, TextUtils.join(", ", b.getSeats())));
        h.total.setText(c.getString(R.string.total_format, c.getString(R.string.price_format,
                NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(b.getTotalPrice()))));
        h.itemView.setOnClickListener(v -> listener.onBookingClick(b));
    }
    @Override public int getItemCount() { return bookings.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView code, title, time, seats, total;
        Holder(View view) {
            super(view);
            code = view.findViewById(R.id.code); title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.time); seats = view.findViewById(R.id.seats); total = view.findViewById(R.id.total);
        }
    }
}

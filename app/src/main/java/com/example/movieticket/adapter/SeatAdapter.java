package com.example.movieticket.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Transient selection is UI state, never a second booking store. */
public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.Holder> {
    public interface Listener {
        void onSelectionChanged(List<String> selected);
        void onLimitReached();
    }
    private final Set<String> occupied = new HashSet<>();
    private final List<String> selected = new ArrayList<>();
    private final Listener listener;

    public SeatAdapter(List<String> booked, List<String> restored, Listener listener) {
        occupied.addAll(booked);
        for (String seat : restored) if (seat.matches("[A-E][1-6]") && !occupied.contains(seat)
                && !selected.contains(seat) && selected.size() < 6) selected.add(seat);
        this.listener = listener;
    }
    public List<String> getSelected() {
        List<String> result = new ArrayList<>(selected);
        Collections.sort(result);
        return result;
    }
    public void updateBooked(List<String> booked) {
        occupied.clear(); occupied.addAll(booked);
        selected.removeAll(occupied);
        notifyDataSetChanged();
        listener.onSelectionChanged(getSelected());
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        String seat = String.valueOf((char) ('A' + position / 6)) + (position % 6 + 1);
        boolean booked = occupied.contains(seat), chosen = selected.contains(seat);
        int color = booked ? R.color.seat_booked : chosen ? R.color.seat_selected : R.color.seat_available;
        h.button.setText(seat);
        h.button.setEnabled(!booked);
        h.button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(h.itemView.getContext(), color)));
        h.button.setTextColor(ContextCompat.getColor(h.itemView.getContext(), chosen ? R.color.on_lime : R.color.text_primary));
        h.button.setContentDescription(h.itemView.getContext().getString(
                booked ? R.string.seat_booked_description : chosen ? R.string.seat_chosen_description : R.string.seat_available_description, seat));
        h.button.setOnClickListener(v -> {
            if (occupied.contains(seat)) return;
            if (selected.contains(seat)) selected.remove(seat);
            else if (selected.size() == 6) { listener.onLimitReached(); return; }
            else selected.add(seat);
            notifyItemChanged(h.getBindingAdapterPosition());
            listener.onSelectionChanged(getSelected());
        });
    }
    @Override public int getItemCount() { return 30; }
    static class Holder extends RecyclerView.ViewHolder {
        final MaterialButton button;
        Holder(View view) { super(view); button = view.findViewById(R.id.seat); }
    }
}

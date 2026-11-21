package com.example.movieticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import com.example.movieticket.model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/** Provides persistent access to the app's local demo data. */
public final class Repository {
    private static final Repository INSTANCE = new Repository();
    private DatabaseHelper databaseHelper;

    private Repository() {}

    public static synchronized Repository getInstance(Context context) {
        if (INSTANCE.databaseHelper == null) {
            INSTANCE.databaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private SQLiteDatabase database() {
        if (databaseHelper == null) throw new IllegalStateException("Repository must be initialized with a Context");
        return databaseHelper.getWritableDatabase();
    }

    public synchronized User login(String email, String password) {
        if (email == null || password == null) return null;
        try (Cursor cursor = database().query("users", new String[]{"id", "name", "email", "phone", "password", "role"},
                "email = ? AND password = ?", new String[]{email.trim(), password}, null, null, null)) {
            return cursor.moveToFirst() ? userFrom(cursor) : null;
        }
    }

    public synchronized boolean isAdmin(int userId) {
        try (Cursor cursor = database().query("users", new String[]{"role"}, "id = ?",
                new String[]{String.valueOf(userId)}, null, null, null)) {
            return cursor.moveToFirst() && "ADMIN".equals(cursor.getString(0));
        }
    }

    public synchronized boolean register(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail().trim().toLowerCase(Locale.ROOT));
        values.put("phone", user.getPhone());
        values.put("password", user.getPassword());
        try {
            return database().insertOrThrow("users", null, values) != -1;
        } catch (android.database.sqlite.SQLiteConstraintException exception) {
            return false;
        }
    }

    public synchronized List<Movie> getMovies() {
        List<Movie> result = new ArrayList<>();
        try (Cursor cursor = database().query("movies",
                new String[]{"id", "title", "genre", "duration", "director", "description", "poster"},
                null, null, null, null, "id ASC")) {
            while (cursor.moveToNext()) result.add(movieFrom(cursor));
        }
        return result;
    }

    public synchronized Movie getMovieById(int id) {
        try (Cursor cursor = database().query("movies",
                new String[]{"id", "title", "genre", "duration", "director", "description", "poster"},
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? movieFrom(cursor) : null;
        }
    }

    public synchronized int createMovie(Movie movie) {
        if (!isValidMovie(movie)) return -1;
        SQLiteDatabase db = database();
        db.beginTransaction();
        try {
            long movieId = db.insertOrThrow("movies", null, movieValues(movie));
            createDefaultShowtimes(db, (int) movieId);
            db.setTransactionSuccessful();
            return (int) movieId;
        } finally {
            db.endTransaction();
        }
    }

    public synchronized boolean updateMovie(Movie movie) {
        if (movie == null || movie.getId() <= 0 || !isValidMovie(movie)) return false;
        return database().update("movies", movieValues(movie), "id = ?",
                new String[]{String.valueOf(movie.getId())}) == 1;
    }

    public synchronized boolean deleteMovie(int movieId) {
        SQLiteDatabase db = database();
        db.beginTransaction();
        try {
            if (getMovieById(movieId) == null || hasBookingsOrPayments(db, movieId)) return false;
            String showtimes = "SELECT id FROM showtimes WHERE movie_id = ?";
            db.delete("booked_seats", "showtime_id IN (" + showtimes + ")",
                    new String[]{String.valueOf(movieId)});
            db.delete("showtimes", "movie_id = ?", new String[]{String.valueOf(movieId)});
            if (db.delete("movies", "id = ?", new String[]{String.valueOf(movieId)}) != 1) return false;
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    private boolean hasBookingsOrPayments(SQLiteDatabase db, int movieId) {
        String sql = "SELECT COUNT(*) FROM showtimes s "
                + "LEFT JOIN bookings b ON b.showtime_id = s.id "
                + "LEFT JOIN payments p ON p.showtime_id = s.id "
                + "WHERE s.movie_id = ? AND (b.id IS NOT NULL OR p.id IS NOT NULL)";
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(movieId)})) {
            return cursor.moveToFirst() && cursor.getInt(0) > 0;
        }
    }

    private boolean isValidMovie(Movie movie) {
        return movie != null && movie.getTitle() != null && !movie.getTitle().trim().isEmpty()
                && movie.getGenre() != null && !movie.getGenre().trim().isEmpty()
                && movie.getDirector() != null && !movie.getDirector().trim().isEmpty()
                && movie.getDescription() != null && !movie.getDescription().trim().isEmpty()
                && movie.getDuration() > 0 && movie.getPoster() > 0;
    }

    private ContentValues movieValues(Movie movie) {
        ContentValues values = new ContentValues();
        values.put("title", movie.getTitle().trim());
        values.put("genre", movie.getGenre().trim());
        values.put("duration", movie.getDuration());
        values.put("director", movie.getDirector().trim());
        values.put("description", movie.getDescription().trim());
        values.put("poster", movie.getPoster());
        return values;
    }

    private void createDefaultShowtimes(SQLiteDatabase db, int movieId) {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN")).format(new Date());
        String[] times = {"14:30", "18:00", "20:30"};
        int[] prices = {85000, 95000, 110000};
        for (int i = 0; i < times.length; i++) {
            ContentValues values = new ContentValues();
            values.put("movie_id", movieId);
            values.put("room", String.format(Locale.ROOT, "Phòng 0%d", i + 1));
            values.put("start_time", date + " · " + times[i]);
            values.put("price", prices[i]);
            db.insertOrThrow("showtimes", null, values);
        }
    }

    public synchronized List<Showtime> getShowtimes(int movieId) {
        List<Showtime> result = new ArrayList<>();
        try (Cursor cursor = database().query("showtimes",
                new String[]{"id", "movie_id", "room", "start_time", "price"}, "movie_id = ?",
                new String[]{String.valueOf(movieId)}, null, null, "id ASC")) {
            while (cursor.moveToNext()) result.add(showtimeFrom(cursor));
        }
        return result;
    }

    public synchronized Showtime getShowtimeById(int id) {
        try (Cursor cursor = database().query("showtimes",
                new String[]{"id", "movie_id", "room", "start_time", "price"}, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? showtimeFrom(cursor) : null;
        }
    }

    public synchronized List<String> getBookedSeats(int showtimeId) {
        List<String> result = new ArrayList<>();
        try (Cursor cursor = database().query("booked_seats", new String[]{"seat_number"},
                "showtime_id = ?", new String[]{String.valueOf(showtimeId)}, null, null, "seat_number ASC")) {
            while (cursor.moveToNext()) result.add(cursor.getString(0));
        }
        return result;
    }

    public synchronized boolean createBooking(int userId, int showtimeId, List<String> seats, int totalPrice) {
        return createBookingAndPayment(userId, showtimeId, seats, totalPrice, "Thanh toán demo") > 0;
    }

    public synchronized int createBookingAndPayment(int userId, int showtimeId, List<String> seats,
                                                     int totalPrice, String paymentMethod) {
        SQLiteDatabase db = database();
        Showtime showtime = getShowtimeById(showtimeId);
        if (!isValidPayment(userId, showtime, seats, totalPrice, paymentMethod)) return -1;

        List<String> sortedSeats = new ArrayList<>(seats);
        Collections.sort(sortedSeats);
        db.beginTransaction();
        try {
            List<String> occupied = getBookedSeats(showtimeId);
            for (String seat : sortedSeats) if (occupied.contains(seat)) return -1;

            Movie movie = getMovieById(showtime.getMovieId());
            if (movie == null) return -1;
            String createdAt = now();
            ContentValues bookingValues = new ContentValues();
            bookingValues.put("user_id", userId);
            bookingValues.put("showtime_id", showtimeId);
            bookingValues.put("movie_title", movie.getTitle());
            bookingValues.put("start_time", showtime.getStartTime());
            bookingValues.put("total_price", totalPrice);
            bookingValues.put("created_at", createdAt);
            long bookingId = db.insertOrThrow("bookings", null, bookingValues);

            ContentValues paymentValues = new ContentValues();
            paymentValues.put("booking_id", bookingId);
            paymentValues.put("user_id", userId);
            paymentValues.put("showtime_id", showtimeId);
            paymentValues.put("amount", totalPrice);
            paymentValues.put("method", paymentMethod);
            paymentValues.put("status", "SUCCESS");
            paymentValues.put("created_at", createdAt);
            db.insertOrThrow("payments", null, paymentValues);

            for (String seat : sortedSeats) {
                ContentValues seatValues = new ContentValues();
                seatValues.put("showtime_id", showtimeId);
                seatValues.put("seat_number", seat);
                seatValues.put("booking_id", bookingId);
                db.insertOrThrow("booked_seats", null, seatValues);
            }
            db.setTransactionSuccessful();
            return (int) bookingId;
        } finally {
            db.endTransaction();
        }
    }

    public synchronized boolean recordFailedPayment(int userId, int showtimeId, List<String> seats,
                                                     int totalPrice, String paymentMethod) {
        Showtime showtime = getShowtimeById(showtimeId);
        if (!isValidPayment(userId, showtime, seats, totalPrice, paymentMethod)) return false;
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("showtime_id", showtimeId);
        values.put("amount", totalPrice);
        values.put("method", paymentMethod);
        values.put("status", "FAILED");
        values.put("created_at", now());
        return database().insertOrThrow("payments", null, values) != -1;
    }

    private boolean isValidPayment(int userId, Showtime showtime, List<String> seats,
                                   int totalPrice, String paymentMethod) {
        if (userId <= 0 || showtime == null || seats == null || seats.isEmpty() || seats.size() > 6
                || paymentMethod == null || paymentMethod.trim().isEmpty()
                || new HashSet<>(seats).size() != seats.size()
                || totalPrice != seats.size() * showtime.getPrice()) return false;
        for (String seat : seats) if (seat == null || !seat.matches("[A-E][1-6]")) return false;
        try (Cursor cursor = database().query("users", new String[]{"id"}, "id = ?",
                new String[]{String.valueOf(userId)}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public synchronized List<Booking> getBookingsByUser(int userId) {
        List<Integer> ids = new ArrayList<>();
        try (Cursor cursor = database().query("bookings", new String[]{"id"}, "user_id = ?",
                new String[]{String.valueOf(userId)}, null, null, "id DESC")) {
            while (cursor.moveToNext()) ids.add(cursor.getInt(0));
        }
        List<Booking> result = new ArrayList<>();
        for (int id : ids) {
            Booking booking = getBookingById(id);
            if (booking != null) result.add(booking);
        }
        return result;
    }

    public synchronized Booking getBookingById(int id) {
        String sql = "SELECT b.id, b.user_id, b.showtime_id, b.movie_title, b.start_time, "
                + "b.total_price, b.created_at, p.method, p.status FROM bookings b "
                + "JOIN payments p ON p.booking_id = b.id WHERE b.id = ?";
        try (Cursor cursor = database().rawQuery(sql, new String[]{String.valueOf(id)})) {
            if (!cursor.moveToFirst()) return null;
            List<String> seats = new ArrayList<>();
            try (Cursor seatCursor = database().query("booked_seats", new String[]{"seat_number"},
                    "booking_id = ?", new String[]{String.valueOf(id)}, null, null, "seat_number ASC")) {
                while (seatCursor.moveToNext()) seats.add(seatCursor.getString(0));
            }
            return new Booking(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4), seats, cursor.getInt(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8));
        }
    }

    private static User userFrom(Cursor cursor) {
        return new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));
    }

    private static Movie movieFrom(Cursor cursor) {
        return new Movie(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
    }

    private static Showtime showtimeFrom(Cursor cursor) {
        return new Showtime(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                cursor.getString(3), cursor.getInt(4));
    }

    private static String now() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("vi-VN")).format(new Date());
    }
}

package com.example.movieticket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.movieticket.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "cine.db";
    private static final int DATABASE_VERSION = 4;
    private final Context context;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, "
                + "email TEXT NOT NULL COLLATE NOCASE UNIQUE, phone TEXT NOT NULL, password TEXT NOT NULL, "
                + "role TEXT NOT NULL DEFAULT 'USER')");
        db.execSQL("CREATE TABLE movies (id INTEGER PRIMARY KEY, title TEXT NOT NULL, genre TEXT NOT NULL, "
                + "duration INTEGER NOT NULL, director TEXT NOT NULL, description TEXT NOT NULL, poster TEXT NOT NULL)");
        db.execSQL("CREATE TABLE showtimes (id INTEGER PRIMARY KEY, movie_id INTEGER NOT NULL, room TEXT NOT NULL, "
                + "start_time TEXT NOT NULL, price INTEGER NOT NULL, "
                + "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, "
                + "showtime_id INTEGER NOT NULL, movie_title TEXT NOT NULL, start_time TEXT NOT NULL, "
                + "total_price INTEGER NOT NULL, created_at TEXT NOT NULL, "
                + "FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(showtime_id) REFERENCES showtimes(id))");
        db.execSQL("CREATE TABLE payments (id INTEGER PRIMARY KEY AUTOINCREMENT, booking_id INTEGER UNIQUE, "
                + "user_id INTEGER NOT NULL, showtime_id INTEGER NOT NULL, amount INTEGER NOT NULL, "
                + "method TEXT NOT NULL, status TEXT NOT NULL, created_at TEXT NOT NULL, "
                + "FOREIGN KEY(booking_id) REFERENCES bookings(id) ON DELETE CASCADE, "
                + "FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(showtime_id) REFERENCES showtimes(id))");
        db.execSQL("CREATE TABLE booked_seats (showtime_id INTEGER NOT NULL, seat_number TEXT NOT NULL, "
                + "booking_id INTEGER, PRIMARY KEY(showtime_id, seat_number), "
                + "FOREIGN KEY(showtime_id) REFERENCES showtimes(id), "
                + "FOREIGN KEY(booking_id) REFERENCES bookings(id) ON DELETE CASCADE)");
        createAnalyticsTables(db);
        seed(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movie_views");
        db.execSQL("DROP TABLE IF EXISTS login_events");
        db.execSQL("DROP TABLE IF EXISTS booked_seats");
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS showtimes");
        db.execSQL("DROP TABLE IF EXISTS movies");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    private void createAnalyticsTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE login_events (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, logged_at TEXT NOT NULL, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))");
        db.execSQL("CREATE TABLE movie_views (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "movie_id INTEGER NOT NULL, user_id INTEGER NOT NULL, viewed_at TEXT NOT NULL, "
                + "FOREIGN KEY(movie_id) REFERENCES movies(id), FOREIGN KEY(user_id) REFERENCES users(id))");
        db.execSQL("CREATE INDEX login_events_user_idx ON login_events(user_id)");
        db.execSQL("CREATE INDEX movie_views_movie_idx ON movie_views(movie_id)");
        db.execSQL("CREATE INDEX movie_views_user_idx ON movie_views(user_id)");
    }

    private void seed(SQLiteDatabase db) {
        insert(db, "users", values("name", "Minh Anh", "email", "test@gmail.com",
                "phone", "0901234567", "password", "123456", "role", "USER", "id", 1));
        seedAdmin(db);

        String[] posters = {
                String.valueOf(R.drawable.poster_1), String.valueOf(R.drawable.poster_2),
                String.valueOf(R.drawable.poster_3), String.valueOf(R.drawable.poster_4),
                String.valueOf(R.drawable.poster_5), String.valueOf(R.drawable.poster_6)
        };
        String[][] movies = {
                {context.getString(R.string.movie_1_title), context.getString(R.string.movie_1_genre), "166",
                        context.getString(R.string.movie_1_director), context.getString(R.string.movie_1_description)},
                {context.getString(R.string.movie_2_title), context.getString(R.string.movie_2_genre), "128",
                        context.getString(R.string.movie_2_director), context.getString(R.string.movie_2_description)},
                {context.getString(R.string.movie_3_title), context.getString(R.string.movie_3_genre), "142",
                        context.getString(R.string.movie_3_director), context.getString(R.string.movie_3_description)},
                {context.getString(R.string.movie_4_title), context.getString(R.string.movie_4_genre), "135",
                        context.getString(R.string.movie_4_director), context.getString(R.string.movie_4_description)},
                {context.getString(R.string.movie_5_title), context.getString(R.string.movie_5_genre), "115",
                        context.getString(R.string.movie_5_director), context.getString(R.string.movie_5_description)},
                {context.getString(R.string.movie_6_title), context.getString(R.string.movie_6_genre), "122",
                        context.getString(R.string.movie_6_director), context.getString(R.string.movie_6_description)}
        };
        for (int i = 0; i < movies.length; i++) {
            insert(db, "movies", values("id", i + 1, "title", movies[i][0], "genre", movies[i][1],
                    "duration", Integer.parseInt(movies[i][2]), "director", movies[i][3],
                    "description", movies[i][4], "poster", posters[i]));
        }

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN")).format(new Date());
        String[] times = {"14:30", "18:00", "20:30"};
        int[] prices = {85000, 95000, 110000};
        String[] seats = {"A1", "A2", "B3", "C4", "D5"};
        for (int movieId = 1; movieId <= movies.length; movieId++) {
            for (int i = 0; i < times.length; i++) {
                int showtimeId = movieId * 10 + i;
                insert(db, "showtimes", values("id", showtimeId, "movie_id", movieId,
                        "room", context.getString(R.string.room_format, i + 1),
                        "start_time", date + " · " + times[i], "price", prices[i]));
                for (String seat : seats) {
                    insert(db, "booked_seats", values("showtime_id", showtimeId, "seat_number", seat));
                }
            }
        }
    }

    private static void seedAdmin(SQLiteDatabase db) {
        insert(db, "users", values("name", "Quản trị viên", "email", "admin@example.com",
                "phone", "0900000000", "password", "123456", "role", "ADMIN"));
    }

    private static void insert(SQLiteDatabase db, String table, ContentValues values) {
        long id = db.insertOrThrow(table, null, values);
        if (id == -1) throw new IllegalStateException("Failed to seed " + table);
    }

    private static ContentValues values(Object... pairs) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < pairs.length; i += 2) {
            String key = (String) pairs[i];
            Object value = pairs[i + 1];
            if (value instanceof Integer) values.put(key, (Integer) value);
            else values.put(key, String.valueOf(value));
        }
        return values;
    }
}

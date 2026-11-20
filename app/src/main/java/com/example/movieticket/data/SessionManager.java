package com.example.movieticket.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Only the signed-in ID is persisted; demo content remains in Repository RAM. */
public final class SessionManager {
    private static final String FILE = "cine_session";
    private static final String USER_ID = "user_id";
    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public int getUserId() { return preferences.getInt(USER_ID, 0); }
    public boolean isLoggedIn() { return getUserId() > 0; }
    public void login(int userId) { preferences.edit().putInt(USER_ID, userId).apply(); }
    public void logout() { preferences.edit().remove(USER_ID).apply(); }
}

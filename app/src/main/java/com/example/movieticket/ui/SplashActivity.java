package com.example.movieticket.ui;

import android.os.Bundle;
import com.example.movieticket.R;

public class SplashActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_splash);
        Class<?> destination = LoginActivity.class;
        if (session.isLoggedIn()) {
            destination = repository.isAdmin(session.getUserId())
                    ? AdminDashboardActivity.class : MovieListActivity.class;
        }
        resetTo(destination);
    }
}

package com.example.movieticket.ui;

import android.os.Bundle;
import com.example.movieticket.R;

public class SplashActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_splash);
        resetTo(session.isLoggedIn() ? MovieListActivity.class : LoginActivity.class);
    }
}

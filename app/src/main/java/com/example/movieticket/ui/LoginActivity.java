package com.example.movieticket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.example.movieticket.R;
import com.example.movieticket.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity {
    private TextInputEditText email, password;
    private TextInputLayout emailLayout, passwordLayout;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (session.isLoggedIn()) {
            resetTo(repository.isAdmin(session.getUserId())
                    ? AdminDashboardActivity.class : MovieListActivity.class);
            return;
        }
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email); password = findViewById(R.id.password);
        emailLayout = findViewById(R.id.email_layout); passwordLayout = findViewById(R.id.password_layout);
        findViewById(R.id.login).setOnClickListener(v -> login());
        findViewById(R.id.register).setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.demo).setOnClickListener(v -> {
            email.setText("test@gmail.com"); password.setText("123456");
            emailLayout.setError(null); passwordLayout.setError(null);
        });
        findViewById(R.id.demo_admin).setOnClickListener(v -> {
            email.setText("admin@example.com"); password.setText("123456");
            emailLayout.setError(null); passwordLayout.setError(null);
        });
    }
    private void login() {
        emailLayout.setError(null); passwordLayout.setError(null);
        String e = String.valueOf(email.getText()).trim(), p = String.valueOf(password.getText());
        boolean valid = true;
        if (e.isEmpty()) { emailLayout.setError(getString(R.string.required)); valid = false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { emailLayout.setError(getString(R.string.invalid_email)); valid = false; }
        if (p.isEmpty()) { passwordLayout.setError(getString(R.string.required)); valid = false; }
        if (!valid) return;
        User user = repository.login(e, p);
        if (user == null) { passwordLayout.setError(getString(R.string.invalid_login)); return; }
        repository.recordLogin(user.getId());
        session.login(user.getId());
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        resetTo(user.isAdmin() ? AdminDashboardActivity.class : MovieListActivity.class);
    }
}

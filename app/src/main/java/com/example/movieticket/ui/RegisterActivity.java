package com.example.movieticket.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.example.movieticket.R;
import com.example.movieticket.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity {
    private TextInputEditText name, email, phone, password, confirm;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (session.isLoggedIn()) { resetTo(MovieListActivity.class); return; }
        setContentView(R.layout.activity_register);
        backButton();
        name = findViewById(R.id.name); email = findViewById(R.id.email); phone = findViewById(R.id.phone);
        password = findViewById(R.id.password); confirm = findViewById(R.id.confirm);
        findViewById(R.id.login).setOnClickListener(v -> finish());
        findViewById(R.id.register).setOnClickListener(v -> register());
    }
    private String value(TextInputEditText field) { return String.valueOf(field.getText()); }
    private void error(int id, int stringId) { ((TextInputLayout) findViewById(id)).setError(getString(stringId)); }
    private void register() {
        int[] layouts = {R.id.name_layout, R.id.email_layout, R.id.phone_layout, R.id.password_layout, R.id.confirm_layout};
        for (int id : layouts) ((TextInputLayout) findViewById(id)).setError(null);
        boolean valid = true;
        String n = value(name).trim(), e = value(email).trim(), ph = value(phone).trim(), p = value(password), c = value(confirm);
        if (n.isEmpty()) { error(R.id.name_layout, R.string.required); valid = false; }
        if (e.isEmpty()) { error(R.id.email_layout, R.string.required); valid = false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { error(R.id.email_layout, R.string.invalid_email); valid = false; }
        if (ph.isEmpty()) { error(R.id.phone_layout, R.string.required); valid = false; }
        else if (!ph.matches("(0|\\+84)[0-9]{9}")) { error(R.id.phone_layout, R.string.invalid_phone); valid = false; }
        if (p.isEmpty()) { error(R.id.password_layout, R.string.required); valid = false; }
        else if (p.length() < 6) { error(R.id.password_layout, R.string.short_password); valid = false; }
        if (c.isEmpty()) { error(R.id.confirm_layout, R.string.required); valid = false; }
        else if (!p.equals(c)) { error(R.id.confirm_layout, R.string.password_mismatch); valid = false; }
        if (!valid) return;
        if (!repository.register(new User(0, n, e, ph, p))) { error(R.id.email_layout, R.string.email_exists); return; }
        Toast.makeText(this, R.string.register_success, Toast.LENGTH_LONG).show();
        finish();
    }
}

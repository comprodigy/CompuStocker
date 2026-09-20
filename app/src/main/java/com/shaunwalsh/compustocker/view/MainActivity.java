package com.shaunwalsh.compustocker.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.shaunwalsh.compustocker.R;
import com.shaunwalsh.compustocker.model.User;
import com.shaunwalsh.compustocker.services.DatabaseManager;
import com.shaunwalsh.compustocker.services.PasswordHasher;
import com.shaunwalsh.compustocker.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager.initialize(this);
        DatabaseManager.seedInitialData();

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = DatabaseManager.getDbHelper().getUserByUsername(username);
        if (user == null || !PasswordHasher.matches(password, user.getPasswordSalt(), user.getPassword())) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        new SessionManager(this).startSession(user);
        Toast.makeText(this, "Login Successful, " + user.getUsername(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, InventoryActivity.class));
        finish();
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password to register", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (DatabaseManager.getDbHelper().getUserByUsername(username) != null) {
            Toast.makeText(this, "User already exists. Please login instead.", Toast.LENGTH_SHORT).show();
            return;
        }

        String salt = PasswordHasher.newSalt();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordSalt(salt);
        newUser.setPassword(PasswordHasher.hash(password, salt));
        newUser.setFullName(username);
        newUser.setRole("employee");

        long result = DatabaseManager.getDbHelper().addUser(newUser);
        if (result > 0) {
            Toast.makeText(this, "Registration Successful! You can now login.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
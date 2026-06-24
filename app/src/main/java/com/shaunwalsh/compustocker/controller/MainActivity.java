package com.shaunwalsh.compustocker.controller;

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

        User user = DatabaseManager.getDbHelper().getUser(username, password);

        if (user != null) {
            Toast.makeText(this, "Login Successful, " + user.getUsername(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password to register", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check to make sure there are no duplicates
        if (DatabaseManager.getDbHelper().getUser(username, password) != null) {
            Toast.makeText(this, "User already exists. Please login instead.", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
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
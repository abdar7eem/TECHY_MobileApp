package com.example.assignment1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class signup extends AppCompatActivity {
    private EditText etEmail;
    private EditText etUserName;
    private EditText etPassword;
    private Button signUpBtn;
    private List<User> users = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private static final String USERS_KEY = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etUserName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        signUpBtn = findViewById(R.id.btnSignup);

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadUsersFromPrefs();

        signUpBtn.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String username = etUserName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUserExist(username, password)) {
                Toast.makeText(this, "Username or password already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            users.add(new User(username, password, email));
            saveUsersToPrefs();

            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadUsersFromPrefs() {
        String json = sharedPreferences.getString(USERS_KEY, null);
        if (json != null) {
            User[] userArray = gson.fromJson(json, User[].class);
            Collections.addAll(users, userArray);
        }
    }

    private void saveUsersToPrefs() {
        String updatedJson = gson.toJson(users.toArray(new User[0]));
        editor.putString(USERS_KEY, updatedJson);
        editor.commit();
    }

    private boolean isUserExist(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) || user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

}
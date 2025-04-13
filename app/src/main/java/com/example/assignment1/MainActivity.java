package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etUserName;
    private EditText etPassword;
    private CheckBox chkbox;
    private Button signupBtn;
    private Button loginBtn;
    private User currentUser;
    final String ADMIN_USERNAME = "admin";
    final String ADMIN_PASSWORD = "admin123";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();

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
        etUserName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        chkbox = findViewById(R.id.checkBox);
        signupBtn = findViewById(R.id.signupBtn);
        loginBtn = findViewById(R.id.btnLogin);

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        checkPrefrences();

        loginBtn.setOnClickListener(v -> handleLogin());

        signupBtn.setOnClickListener(view->{
            Intent intent=new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        });
    }

    private void checkPrefrences() {
        String savedUserName = sharedPreferences.getString("userName", "");
        String savedPassword = sharedPreferences.getString("password", "");

        if (!savedUserName.equals("") || !savedPassword.equals("")) {
            etUserName.setText(savedUserName);
            etPassword.setText(savedPassword);
            chkbox.setChecked(true);
        }
    }

    private void handleLogin() {
        String username = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }


        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            if (chkbox.isChecked()) {
                saveUserData();
            }
            Intent intent= new Intent(MainActivity.this, admin_homepage.class);
            intent.putExtra("userName", "admin");
            startActivity(intent);
            finish();
        } else if (isUserValid(etUserName.getText().toString(), etPassword.getText().toString())) {
            if (chkbox.isChecked()) {
                saveUserData();
            }
            Intent intent=new Intent(MainActivity.this, customer_homePage.class);
            intent.putExtra("userName", currentUser.getUsername());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Account does not exist or wrong password", Toast.LENGTH_SHORT).show();
        }
    }



private void saveUserData() {
    editor.putString("userName", etUserName.getText().toString());
    editor.putString("password", etPassword.getText().toString());
    editor.commit();
}
    private boolean isUserValid(String username, String password) {
        String usersJson = sharedPreferences.getString("users", null);
        if (usersJson == null) return false;


        User[] usersArray = gson.fromJson(usersJson, User[].class);

        for (User user : usersArray) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser=user;
                return true;
            }
        }
        return false;
    }


}


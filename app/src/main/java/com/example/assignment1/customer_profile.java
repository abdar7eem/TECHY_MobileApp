package com.example.assignment1;

import android.content.Intent; import android.content.SharedPreferences; import android.os.Bundle; import android.widget.Button; import android.widget.SeekBar; import android.widget.TextView; import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView; import com.google.gson.Gson;

import java.util.ArrayList; import java.util.Arrays; import java.util.List;

public class customer_profile extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPassword, tvBalance, tvSliderAmount;
    private SeekBar amountSeekBar;
    private Button btnAddAmount;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String currentUser;
    private double currentBalance = 0.0;
    private List<User> userList = new ArrayList<>();
    private Gson gson = new Gson();
    private Intent intent;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvBalance = findViewById(R.id.tvBalance);
        tvSliderAmount = findViewById(R.id.tvSliderAmount);
        amountSeekBar = findViewById(R.id.amountSeekBar);
        btnAddAmount = findViewById(R.id.btnAddAmount);
        bottomNav = findViewById(R.id.bottomNavigationView);

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        intent=getIntent();
        currentUser = intent.getStringExtra("currentUser");

        loadUserData(currentUser);

        amountSeekBar.setMax(1000);
        amountSeekBar.setProgress(0);

        amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                tvSliderAmount.setText("Amount to Add: $" + value);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnAddAmount.setOnClickListener(v -> {
            int addedAmount = amountSeekBar.getProgress();
            if (addedAmount > 0) {
                updateBalance(currentUser, addedAmount);
                tvBalance.setText("Available Balance: $" + String.format("%.2f", currentBalance));
                Toast.makeText(this, "$" + addedAmount + " added successfully!", Toast.LENGTH_SHORT).show();
                amountSeekBar.setProgress(0);
                tvSliderAmount.setText("Amount to Add: $0");
            } else {
                Toast.makeText(this, "Please select an amount greater than 0", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent=new Intent(customer_profile.this, customer_homePage.class);
                intent.putExtra("userName", currentUser);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                Intent i = new Intent(this, customer_cart.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, customer_profile.class));
                return true;
            }
            return false;
        });
    }

    private void loadUserData(String username) {
        String usersJson = sharedPreferences.getString("users", "");
        if (!usersJson.isEmpty()) {
            User[] usersArray = gson.fromJson(usersJson, User[].class);
            userList = new ArrayList<>(Arrays.asList(usersArray));

            for (User user : userList) {
                if (user.getUsername().equals(username)) {
                    tvUsername.setText("Username: " + user.getUsername());
                    tvEmail.setText("Email: " + user.getEmail());
                    tvPassword.setText("Password: " + user.getPassword());
                    currentBalance = user.getAmount();
                    tvBalance.setText("Available Balance: $" + String.format("%.2f", currentBalance));
                    break;
                }
            }
        }
    }

    private void updateBalance(String username, double amountToAdd) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                currentBalance = user.getAmount() + amountToAdd;
                user.setAmount(currentBalance);
                break;
            }
        }

        String updatedJson = gson.toJson(userList);
        editor.putString("users", updatedJson);
        editor.apply();
    }
}
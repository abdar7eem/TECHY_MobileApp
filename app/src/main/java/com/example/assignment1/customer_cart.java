package com.example.assignment1;

import android.content.Intent; import android.content.SharedPreferences; import android.os.Bundle; import android.view.MenuItem; import android.widget.Button; import android.widget.ListView; import android.widget.TextView; import android.widget.Toast;

import androidx.annotation.NonNull; import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView; import com.google.android.material.navigation.NavigationBarView; import com.google.gson.Gson;

import java.util.ArrayList; import java.util.Arrays; import java.util.List;

public class customer_cart extends AppCompatActivity {

    private ListView cartListView;
    private List<Product> cartList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private BottomNavigationView bottomNavigationView;
    private CartAdapter adapter;
    private Intent intent;
    private TextView tvTotalPrice;
    private TextView tvMyBalance;
    private Button confirmPurchaseBtn;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        intent = getIntent();
        currentUser = intent.getStringExtra("currentUser");

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        cartListView = findViewById(R.id.cartListView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        confirmPurchaseBtn = findViewById(R.id.confirmPurchaseBtn);
        tvMyBalance = findViewById(R.id.tvMyBalance);

        loadCartItems();
        setCurrentBalance(currentUser);
        tvTotalPrice.setText(String.format("Total: $%.2f", getTotalPrice()));

        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(customer_cart.this, customer_homePage.class);
                    intent.putExtra("userName", currentUser);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.nav_cart) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent cartIntent = new Intent(customer_cart.this, customer_profile.class);
                    cartIntent.putExtra("currentUser", currentUser);
                    startActivity(cartIntent);
                    return true;
                }
                return false;
            }
        });

        confirmPurchaseBtn.setOnClickListener(view -> {
            String usersJson = sharedPreferences.getString("users", "");
            double totalPrice = getTotalPrice();

            if (!usersJson.isEmpty()) {
                Gson gson = new Gson();
                User[] usersArray = gson.fromJson(usersJson, User[].class);
                List<User> userList = new ArrayList<>(Arrays.asList(usersArray));
                boolean userFound = false;

                for (User user : userList) {
                    if (user.getUsername().equals(currentUser)) {
                        userFound = true;
                        double balance = user.getAmount();

                        if (totalPrice > balance) {
                            Toast.makeText(this, "Insufficient balance! Please add funds.", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            // Deduct amount
                            user.setAmount(balance - totalPrice);

                            // Update product quantities
                            String productsJson = sharedPreferences.getString("products", "");
                            if (!productsJson.isEmpty()) {
                                Product[] originalArray = gson.fromJson(productsJson, Product[].class);
                                List<Product> originalList = new ArrayList<>(Arrays.asList(originalArray));

                                for (Product cartItem : cartList) {
                                    for (Product product : originalList) {
                                        if (product.getName().equals(cartItem.getName())) {
                                            int updatedQty = product.getQuantity() - cartItem.getQuantity();
                                            product.setQuantity(Math.max(updatedQty, 0));
                                        }
                                    }
                                }

                                String updatedProductsJson = gson.toJson(originalList);
                                editor.putString("products", updatedProductsJson);
                            }

                            // Save updated user list
                            String updatedUsersJson = gson.toJson(userList);
                            editor.putString("users", updatedUsersJson);

                            // Clear cart
                            String cartKey = "cart_" + currentUser;
                            editor.remove(cartKey);
                            editor.apply();

                            // Clear and refresh UI
                            cartList.clear();
                            adapter.notifyDataSetChanged();
                            tvTotalPrice.setText("Total: $0.00");
                            setCurrentBalance(currentUser); // 🔁 refresh displayed balance

                            Toast.makeText(this, "Purchase confirmed!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!userFound) {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCurrentBalance(String currentUser) {
        String usersJson = sharedPreferences.getString("users", "");
        if (!usersJson.isEmpty()) {
            Gson gson = new Gson();
            User[] usersArray = gson.fromJson(usersJson, User[].class);
            List<User> userList = new ArrayList<>(Arrays.asList(usersArray));
            for (User user : userList) {
                if (user.getUsername().equals(currentUser)) {
                    tvMyBalance.setText(String.format("Balance: $%.2f", user.getAmount()));
                    break;
                }
            }
        }
    }

    private void loadCartItems() {
        String cartKey = "cart_" + currentUser;
        String cartJson = sharedPreferences.getString(cartKey, "");

        if (!cartJson.isEmpty()) {
            Gson gson = new Gson();
            Product[] cartArray = gson.fromJson(cartJson, Product[].class);
            cartList = new ArrayList<>(Arrays.asList(cartArray));
        } else {
            cartList = new ArrayList<>();
        }

        adapter = new CartAdapter(this, cartList);
        cartListView.setAdapter(adapter);
    }

    private double getTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartList) {
            totalPrice += product.getQuantity() * product.getPrice();
        }
        return totalPrice;
    }
}
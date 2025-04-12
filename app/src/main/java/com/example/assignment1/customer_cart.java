package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class customer_cart extends AppCompatActivity {

    private ListView cartListView;
    private List<Product> cartList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private BottomNavigationView bottomNavigationView;
    private CartAdapter adapter;
    private Intent intent;
    private TextView tvTotalPrice;
    private Button confirmPurchaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        intent = getIntent();
        final String currentUser = intent.getStringExtra("currentUser");

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        cartListView = findViewById(R.id.cartListView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        confirmPurchaseBtn = findViewById(R.id.confirmPurchaseBtn);

        loadCartItems();
        tvTotalPrice.setText(String.valueOf(getTotalPrice()));

        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent=new Intent(customer_cart.this, customer_homePage.class);
                    intent.putExtra("userName", currentUser);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.nav_cart) {
                    return true;
                }
                return false;
            }
        });

        confirmPurchaseBtn.setOnClickListener(view -> {

            String productsJson = sharedPreferences.getString("products", "");
            if (!productsJson.isEmpty()) {
                Gson gson = new Gson();
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

                // Save updated products
                String updatedProductsJson = gson.toJson(originalList);
                editor.putString("products", updatedProductsJson);
                editor.commit();

                // Clear cart
                String cartKey = "cart_" + currentUser;
                editor.remove(cartKey);
                editor.commit();

                cartList.clear();
                adapter.notifyDataSetChanged();
                tvTotalPrice.setText("Total: $0.00");

                Toast.makeText(this, "Purchase confirmed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadCartItems() {
        String currentUser = intent.getStringExtra("currentUser");
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

    private double getTotalPrice(){
        double totalPrice=0;
        for(Product product:cartList){
            totalPrice += product.getQuantity() * product.getPrice();
        }
        return totalPrice;
    }
}

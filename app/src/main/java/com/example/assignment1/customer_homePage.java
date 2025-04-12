package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class customer_homePage extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private ListView listView;
    private List<Product> productList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Intent intent;
    private TextView tvUserWelcome;
    private Button logoutBtn;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intent
        intent = getIntent();
        final String currentUser = intent.getStringExtra("userName");

        // SharedPreferences
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Views
        listView = findViewById(R.id.productListView);
        tvUserWelcome = findViewById(R.id.etWelcome);
        logoutBtn = findViewById(R.id.logoutBtn);
        searchView = findViewById(R.id.search_bar);
        bottomNav = findViewById(R.id.bottomNavigationView);

        searchView.setIconifiedByDefault(false);
        searchView.setFocusableInTouchMode(true);


        // Welcome message
        tvUserWelcome.setText("Welcome, " + currentUser);

        // Load product list
        loadItemDynamic();

        // Search logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        // Logout action (optional)
        logoutBtn.setOnClickListener(v -> {
            editor.remove("current_user");
            editor.apply();
            startActivity(new Intent(customer_homePage.this, MainActivity.class));
            finish();
        });

        // Navigation listener
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    return true;
                }
                if (item.getItemId() == R.id.nav_cart) {
                    Intent cartIntent = new Intent(customer_homePage.this, customer_cart.class);
                    cartIntent.putExtra("currentUser", currentUser);
                    startActivity(cartIntent);
                    return true;
                }
                return false;
            }
        });

        // Item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = productList.get(position);

            Intent intent = new Intent(customer_homePage.this, itemDetail.class);
            intent.putExtra("name", clickedProduct.getName());
            intent.putExtra("price", clickedProduct.getPrice());
            intent.putExtra("quantity", clickedProduct.getQuantity());
            intent.putExtra("type", clickedProduct.getType());
            intent.putExtra("imageName", clickedProduct.getImgName());
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(view->{
            startActivity(new Intent(customer_homePage.this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadItemDynamic();
    }

    private void loadItemsHardCode() {
        productList = new ArrayList<>();
        productList.add(new Product("Blue Yeti Microphone", 30, 129.99, "Microphone", "cart"));
        productList.add(new Product("LG UltraWide 29", 35, 320.00, "Monitor", "lg_ultrawide"));
        productList.add(new Product("Logitech G502 Mouse", 12, 69.99, "Mouse", "g502_hero"));
        productList.add(new Product("Razer BlackWidow Keyboard", 15, 119.99, "Keyboard", "keyboard"));
        productList.add(new Product("Corsair RAM 16GB", 40, 89.99, "RAM", "ram"));
        productList.add(new Product("GTX 1660 GPU", 10, 279.00, "GPU", "gpu"));

        allProducts = new ArrayList<>(productList); // backup full list
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        Gson gson = new Gson();
        String products = gson.toJson(productList);
        editor.putString("products", products);
        editor.apply();
    }

    private void loadItemDynamic() {
        String productsJson = sharedPreferences.getString("products", "");

        if (!productsJson.isEmpty()) {
            Gson gson = new Gson();
            Product[] productsArray = gson.fromJson(productsJson, Product[].class);

            allProducts = new ArrayList<>(Arrays.asList(productsArray));
            productList = new ArrayList<>(allProducts);

            adapter = new ProductAdapter(this, productList);
            listView.setAdapter(adapter);
        } else {
            loadItemsHardCode();
        }
    }

    private void filterProducts(String query) {
        productList.clear();

        if (query.isEmpty()) {
            productList.addAll(allProducts);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Product p : allProducts) {
                if (p.getName().toLowerCase().contains(lowerQuery)) {
                    productList.add(p);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}

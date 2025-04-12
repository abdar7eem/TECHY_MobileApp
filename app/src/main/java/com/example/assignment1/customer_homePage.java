package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
    private Spinner typeSpinner;

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

        intent = getIntent();
        final String currentUser = intent.getStringExtra("userName");

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.productListView);
        tvUserWelcome = findViewById(R.id.etWelcome);
        logoutBtn = findViewById(R.id.logoutBtn);
        searchView = findViewById(R.id.search_bar);
        typeSpinner = findViewById(R.id.search_spinner);
        bottomNav = findViewById(R.id.bottomNavigationView);

        tvUserWelcome.setText("Welcome, " + currentUser);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusableInTouchMode(true);

        loadItemDynamic();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductsByName(newText);
                return true;
            }
        });

        logoutBtn.setOnClickListener(view -> {
            startActivity(new Intent(customer_homePage.this, MainActivity.class));
            finish();
        });

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
                if (item.getItemId() == R.id.nav_profile) {
                    Intent profileIntent = new Intent(customer_homePage.this, customer_profile.class);
                    profileIntent.putExtra("currentUser", currentUser);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = productList.get(position);

            Intent detailIntent = new Intent(customer_homePage.this, itemDetail.class);
            detailIntent.putExtra("name", clickedProduct.getName());
            detailIntent.putExtra("price", clickedProduct.getPrice());
            detailIntent.putExtra("quantity", clickedProduct.getQuantity());
            detailIntent.putExtra("type", clickedProduct.getType());
            detailIntent.putExtra("imageName", clickedProduct.getImgName());
            detailIntent.putExtra("currentUser", currentUser);
            startActivity(detailIntent);
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

        allProducts = new ArrayList<>(productList);
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        Gson gson = new Gson();
        String products = gson.toJson(productList);
        editor.putString("products", products);
        editor.commit();

        populateTypeSpinner();
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

            populateTypeSpinner();
        } else {
            loadItemsHardCode();
        }
    }

    private void filterProductsByName(String query) {
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

    private void populateTypeSpinner() {
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("All");

        for (Product product : allProducts) {
            String type = product.getType();
            if (!typeList.contains(type)) {
                typeList.add(type);
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                filterProductsByType(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterProductsByType(String selectedType) {
        productList.clear();

        if (selectedType.equals("All")) {
            productList.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.getType().equalsIgnoreCase(selectedType)) {
                    productList.add(p);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}
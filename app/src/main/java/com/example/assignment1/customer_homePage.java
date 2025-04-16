package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private ArrayList<String> typeList;

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

        int id_search = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id_search);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);

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
        List<Product> productList = new ArrayList<>();

// Monitors
        productList.add(new Product("Samsung Curved Monitor 27", 20, 249.99, "Monitor", "samsung_monitor"));
        productList.add(new Product("ASUS ProArt Display", 18, 349.99, "Monitor", "asus_monitor"));
        productList.add(new Product("Dell Ultrasharp U2723QE", 15, 429.99, "Monitor", "dell_monitor"));
        productList.add(new Product("Acer Nitro XV272U", 25, 289.99, "Monitor", "acer_monitor"));
        productList.add(new Product("BenQ PD2700U", 12, 379.99, "Monitor", "benq_monitor"));

// Keyboards
        productList.add(new Product("Corsair K70 RGB", 30, 129.99, "Keyboard", "corsair_keyboard"));
        productList.add(new Product("SteelSeries Apex Pro", 20, 199.99, "Keyboard", "steelseries_keyboard"));
        productList.add(new Product("Logitech MX Keys", 25, 99.99, "Keyboard", "logitech_keyboard"));
        productList.add(new Product("Razer Cynosa V2", 35, 59.99, "Keyboard", "rayzer_keyboard"));
        productList.add(new Product("HyperX Alloy Origins", 28, 89.99, "Keyboard", "hyperx_keyboard"));

// Mouses
        productList.add(new Product("Razer DeathAdder V2", 40, 69.99, "Mouse", "mouse1"));
        productList.add(new Product("Logitech G Pro Wireless", 30, 129.99, "Mouse", "mouse2"));
        productList.add(new Product("SteelSeries Rival 3", 22, 39.99, "Mouse", "mouse3"));
        productList.add(new Product("Glorious Model O", 35, 49.99, "Mouse", "mouse4"));
        productList.add(new Product("Corsair Harpoon RGB", 27, 29.99, "Mouse", "mouse5"));

// Microphones
        productList.add(new Product("Blue Yeti Microphone", 30, 129.99, "Microphone", "mic1"));
        productList.add(new Product("Elgato Wave:3", 20, 149.99, "Microphone", "mic2"));
        productList.add(new Product("HyperX QuadCast", 25, 119.99, "Microphone", "mic3"));
        productList.add(new Product("Rode NT-USB Mini", 18, 99.99, "Microphone", "mic4"));
        productList.add(new Product("Samson Meteor Mic", 22, 69.99, "Microphone", "mic5"));

// Headphones
        productList.add(new Product("Sony WH-1000XM4", 15, 349.99, "Headphones", "headphone1"));
        productList.add(new Product("Bose QuietComfort 45", 17, 329.99, "Headphones", "headphone2"));
        productList.add(new Product("HyperX Cloud II", 30, 99.99, "Headphones", "headphone3"));
        productList.add(new Product("SteelSeries Arctis 7", 25, 149.99, "Headphones", "headphone4"));
        productList.add(new Product("Razer BlackShark V2", 28, 119.99, "Headphones", "headphone5"));

// Mousepads
        productList.add(new Product("SteelSeries QcK Large", 50, 19.99, "Mousepad", "mousepad1"));
        productList.add(new Product("Razer Goliathus", 35, 29.99, "Mousepad", "mousepad2"));
        productList.add(new Product("Corsair MM300", 40, 34.99, "Mousepad", "mousepad3"));
        productList.add(new Product("Logitech G640", 32, 39.99, "Mousepad", "mousepad4"));
        productList.add(new Product("HyperX Fury S", 38, 24.99, "Mousepad", "mousepad5"));

// Cables
        productList.add(new Product("UGREEN USB-C Cable 2m", 60, 12.99, "Cable", "cable1"));
        productList.add(new Product("Anker Powerline III", 50, 16.99, "Cable", "cable2"));
        productList.add(new Product("AmazonBasics HDMI", 55, 9.99, "Cable", "cable3"));
        productList.add(new Product("Cable Matters DisplayPort", 48, 13.99, "Cable", "cable4"));
        productList.add(new Product("Monoprice Cat6 Ethernet", 70, 8.99, "Cable", "cable5"));

// CPU
        productList.add(new Product("Intel Core i9-13900K", 10, 599.99, "CPU", "cpu1"));
        productList.add(new Product("AMD Ryzen 9 7900X", 12, 469.99, "CPU", "cpu2"));
        productList.add(new Product("Intel Core i5-13600K", 15, 319.99, "CPU", "cpu3"));
        productList.add(new Product("AMD Ryzen 5 7600X", 18, 259.99, "CPU", "cpu4"));
        productList.add(new Product("Intel Core i7-12700F", 14, 349.99, "CPU", "cpu5"));

// RAM
        productList.add(new Product("Corsair Vengeance 16GB DDR4", 40, 89.99, "RAM", "ram1"));
        productList.add(new Product("G.SKILL Trident Z 32GB", 25, 139.99, "RAM", "ram2"));
        productList.add(new Product("Kingston Fury 16GB", 38, 79.99, "RAM", "ram3"));
        productList.add(new Product("TeamGroup T-Force Delta RGB", 30, 99.99, "RAM", "ram4"));
        productList.add(new Product("Patriot Viper Steel 16GB", 32, 84.99, "RAM", "ram5"));

// GPU
        productList.add(new Product("NVIDIA RTX 4080", 8, 1199.99, "GPU", "gpu1"));
        productList.add(new Product("AMD RX 7900 XT", 10, 899.99, "GPU", "gpu2"));
        productList.add(new Product("NVIDIA RTX 4070 Ti", 12, 799.99, "GPU", "gpu3"));
        productList.add(new Product("Intel Arc A770", 15, 349.99, "GPU", "gpu4"));
        productList.add(new Product("NVIDIA GTX 1660 Super", 20, 229.99, "GPU", "gpu5"));

// Motherboards
        productList.add(new Product("ASUS ROG STRIX Z790-E", 18, 389.99, "Motherboard", "mb1"));
        productList.add(new Product("MSI MAG B650 TOMAHAWK", 20, 249.99, "Motherboard", "mb2"));
        productList.add(new Product("Gigabyte Z690 AORUS Elite", 22, 269.99, "Motherboard", "mb3"));
        productList.add(new Product("ASRock B550M Steel Legend", 25, 149.99, "Motherboard", "mb4"));
        productList.add(new Product("NZXT N7 Z790", 15, 319.99, "Motherboard", "mb5"));

// Cases
        productList.add(new Product("NZXT H510 Mid Tower", 30, 79.99, "Case", "case1"));
        productList.add(new Product("Corsair iCUE 4000X RGB", 25, 129.99, "Case", "case2"));
        productList.add(new Product("Cooler Master NR600", 28, 74.99, "Case", "case3"));
        productList.add(new Product("Fractal Design Meshify C", 22, 109.99, "Case", "case4"));
        productList.add(new Product("Lian Li PC-O11 Dynamic", 20, 139.99, "Case", "case5"));

// Storage Devices
        productList.add(new Product("Samsung 980 Pro 1TB NVMe", 40, 119.99, "Storage Device", "storage1"));
        productList.add(new Product("WD Black SN850X 1TB", 35, 109.99, "Storage Device", "storage2"));
        productList.add(new Product("Crucial MX500 1TB SSD", 38, 89.99, "Storage Device", "storage3"));
        productList.add(new Product("Seagate Barracuda 2TB HDD", 50, 54.99, "Storage Device", "storage4"));
        productList.add(new Product("Kingston NV2 1TB NVMe", 42, 84.99, "Storage Device", "storage5"));

// Consoles
        productList.add(new Product("PlayStation 5", 10, 499.99, "Console", "console1"));
        productList.add(new Product("Xbox Series X", 12, 499.99, "Console", "console2"));
        productList.add(new Product("Nintendo Switch OLED", 20, 349.99, "Console", "console3"));
        productList.add(new Product("Steam Deck 512GB", 15, 649.99, "Console", "console4"));
        productList.add(new Product("Xbox Series S", 18, 299.99, "Console", "console5"));

// Gaming Chairs
        productList.add(new Product("Secretlab TITAN Evo", 12, 429.99, "Gaming Chair", "chair1"));
        productList.add(new Product("Razer Iskur", 14, 399.99, "Gaming Chair", "chair2"));
        productList.add(new Product("AndaSeat Kaiser 3", 15, 449.99, "Gaming Chair", "chair3"));
        productList.add(new Product("DXRacer Formula Series", 18, 289.99, "Gaming Chair", "chair4"));
        productList.add(new Product("GTRacing Pro Series", 20, 179.99, "Gaming Chair", "chair5"));

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
        typeList = new ArrayList<>();
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
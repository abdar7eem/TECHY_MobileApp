package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class admin_homepage extends AppCompatActivity {

    private ListView listView;
    private List<Product> productList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tvUserWelcome;
    private Button logoutBtn;
    private SearchView searchView;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.productListView);
        tvUserWelcome = findViewById(R.id.etWelcome);
        logoutBtn = findViewById(R.id.logoutBtn);
        searchView = findViewById(R.id.search_bar);
        typeSpinner = findViewById(R.id.search_spinner);

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
            startActivity(new Intent(admin_homepage.this, MainActivity.class));
            finish();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product clickedProduct = productList.get(position);

            Intent detailIntent = new Intent(admin_homepage.this, admin_item_detail.class);
            detailIntent.putExtra("name", clickedProduct.getName());
            detailIntent.putExtra("price", clickedProduct.getPrice());
            detailIntent.putExtra("quantity", clickedProduct.getQuantity());
            detailIntent.putExtra("type", clickedProduct.getType());
            detailIntent.putExtra("imageName", clickedProduct.getImgName());
            startActivity(detailIntent);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadItemDynamic();
    }

    private void loadItemsHardCode() {
        productList = new ArrayList<>();

        productList.add(new Product("Samsung Curved Monitor 27", 20, 249.99, "Monitor", "samsung_monitor"));
        productList.add(new Product("ASUS ProArt Display", 18, 349.99, "Monitor", "asus_monitor"));
        productList.add(new Product("Dell Ultrasharp U2723QE", 15, 429.99, "Monitor", "dell_monitor"));
        productList.add(new Product("Acer Nitro XV272U", 25, 289.99, "Monitor", "acer_monitor"));
        productList.add(new Product("BenQ PD2700U", 12, 379.99, "Monitor", "benq_monitor"));

        productList.add(new Product("Corsair K70 RGB", 30, 129.99, "Keyboard", "corsair_keyboard"));
        productList.add(new Product("SteelSeries Apex Pro", 20, 199.99, "Keyboard", "steelseries_keyboard"));
        productList.add(new Product("Logitech MX Keys", 25, 99.99, "Keyboard", "logitech_keyboard"));
        productList.add(new Product("Razer Cynosa V2", 35, 59.99, "Keyboard", "rayzer_keyboard"));
        productList.add(new Product("HyperX Alloy Origins", 28, 89.99, "Keyboard", "hyperx_keyboard"));

        productList.add(new Product("Razer DeathAdder V2", 40, 69.99, "Mouse", "razer_mouse"));
        productList.add(new Product("Logitech G Pro Wireless", 30, 129.99, "Mouse", "logitech_mouse"));
        productList.add(new Product("SteelSeries Rival 3", 22, 39.99, "Mouse", "steelseries_mouse"));
        productList.add(new Product("Glorious Model O", 35, 49.99, "Mouse", "glorious_mouse"));
        productList.add(new Product("Corsair Harpoon RGB", 27, 29.99, "Mouse", "corsair_mouse"));

        productList.add(new Product("Blue Yeti Microphone", 30, 129.99, "Microphone", "blue_mic"));
        productList.add(new Product("Elgato Wave:3", 20, 149.99, "Microphone", "elgato_mic"));
        productList.add(new Product("HyperX QuadCast", 25, 119.99, "Microphone", "hyperx_mic"));
        productList.add(new Product("Rode NT-USB Mini", 18, 99.99, "Microphone", "rode_mic"));
        productList.add(new Product("Samson Meteor Mic", 22, 69.99, "Microphone", "samson_mic"));

        productList.add(new Product("Sony WH-1000XM4", 15, 349.99, "Headphones", "sony_head"));
        productList.add(new Product("Bose QuietComfort 45", 17, 329.99, "Headphones", "bose_head"));
        productList.add(new Product("HyperX Cloud II", 30, 99.99, "Headphones", "hyperx_head"));
        productList.add(new Product("SteelSeries Arctis 7", 25, 149.99, "Headphones", "steelseries_head"));
        productList.add(new Product("Razer BlackShark V2", 28, 119.99, "Headphones", "razer_head"));

        productList.add(new Product("Intel Core i9-13900K", 10, 599.99, "CPU", "i9_cpu"));
        productList.add(new Product("AMD Ryzen 9 7900X", 12, 469.99, "CPU", "ryzen9_cpu"));
        productList.add(new Product("Intel Core i5-13600K", 15, 319.99, "CPU", "i5_cpu"));
        productList.add(new Product("AMD Ryzen 5 7600X", 18, 259.99, "CPU", "ryzen5_cpu"));
        productList.add(new Product("Intel Core i7-12700F", 14, 349.99, "CPU", "i7_cpu"));

        productList.add(new Product("Corsair Vengeance 16GB DDR4", 40, 89.99, "RAM", "corsair_ram"));
        productList.add(new Product("G.SKILL Trident Z 32GB", 25, 139.99, "RAM", "g_ram"));
        productList.add(new Product("Kingston Fury 16GB", 38, 79.99, "RAM", "kingston_ram"));
        productList.add(new Product("TeamGroup T-Force Delta RGB", 30, 99.99, "RAM", "team_ram"));
        productList.add(new Product("Patriot Viper Steel 16GB", 32, 84.99, "RAM", "patriot_ram"));

        productList.add(new Product("NVIDIA RTX 4080", 8, 1199.99, "GPU", "rtx_gpu"));
        productList.add(new Product("AMD RX 7900 XT", 10, 899.99, "GPU", "amd_gpu"));
        productList.add(new Product("Intel Arc A770", 15, 349.99, "GPU", "intel_gpu"));
        productList.add(new Product("NVIDIA GTX 1660 Super", 20, 229.99, "GPU", "gtx_gpu"));

        productList.add(new Product("Samsung 980 Pro 1TB NVMe", 40, 119.99, "Storage Device", "samsung_storage"));
        productList.add(new Product("WD Black SN850X 1TB", 35, 109.99, "Storage Device", "wd_storage"));
        productList.add(new Product("Crucial MX500 1TB SSD", 38, 89.99, "Storage Device", "crucial_storage"));
        productList.add(new Product("Seagate Barracuda 2TB HDD", 50, 54.99, "Storage Device", "seagate_storage"));
        productList.add(new Product("Kingston NV2 1TB NVMe", 42, 84.99, "Storage Device", "kingston_storage"));

        productList.add(new Product("PlayStation 5", 10, 499.99, "Console", "ps5"));
        productList.add(new Product("Xbox Series X", 12, 499.99, "Console", "xboxx"));
        productList.add(new Product("Nintendo Switch OLED", 20, 349.99, "Console", "switch_counsle"));
        productList.add(new Product("Steam Deck 512GB", 15, 649.99, "Console", "steam"));
        productList.add(new Product("Xbox Series S", 18, 299.99, "Console", "xboxs"));
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
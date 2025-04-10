package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class customer_homePage extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private ListView listView;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
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

        bottomNav = findViewById(R.id.bottomNavigationView);

        sharedPreferences=getSharedPreferences("MyApp", MODE_PRIVATE);
        editor=sharedPreferences.edit();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    return true;
                }
                if (item.getItemId() == R.id.nav_cart) {
                    startActivity(new Intent(customer_homePage.this, customer_cart.class));
                    return true;
                }
                if (item.getItemId() == R.id.nav_history) {
                    startActivity(new Intent(customer_homePage.this, customer_history.class));
                    return true;
                }
                return false;
            }
        });

        listView = findViewById(R.id.productListView);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product clickedProduct = productList.get(position);

                Intent intent = new Intent(customer_homePage.this, itemDetail.class);
                intent.putExtra("name", clickedProduct.getName());
                intent.putExtra("price", clickedProduct.getPrice());
                intent.putExtra("quantity", clickedProduct.getQuantity());
                intent.putExtra("type", clickedProduct.getType());
                intent.putExtra("imageName", clickedProduct.getImgName());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void loadItemsHardCode(){
        listView = findViewById(R.id.productListView);

        productList = new ArrayList<>();
        productList.add(new Product("Blue Yeti Microphone",30, 129.99, "Microphone", "cart"));
        productList.add(new Product("LG UltraWide 29", 35, 320.00, "Monitor", "lg_ultrawide"));
        productList.add(new Product("Logitech G502 Mouse", 12,69.99, "Mouse", "g502_hero"));
        productList.add(new Product("Logitech G502 Mouse", 12,69.99, "Mouse", "g502_hero"));
        productList.add(new Product("Logitech G502 Mouse", 12,69.99, "Mouse", "g502_hero"));
        productList.add(new Product("Logitech G502 Mouse", 12,69.99, "Mouse", "g502_hero"));
        productList.add(new Product("Logitech G502 Mouse", 12,69.99, "Mouse", "g502_hero"));

        ProductAdapter adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);
    }

    private void loadItemDynamic(){
        
    }
}
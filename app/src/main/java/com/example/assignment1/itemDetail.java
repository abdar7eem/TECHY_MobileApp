package com.example.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class itemDetail extends AppCompatActivity {
    private Intent intent;
    private ImageView img;
    private TextView itemName;
    private TextView itemPrice;
    private TextView itemType;
    private TextView itemQuantitiy;
    private NumberPicker picker;
    private Button buyBtn;
    private Button backBtn;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img = findViewById(R.id.productImageView);
        itemName = findViewById(R.id.productNameText);
        itemPrice = findViewById(R.id.productPriceText);
        itemType = findViewById(R.id.productTypeText);
        itemQuantitiy = findViewById(R.id.productQtyText);
        picker = findViewById(R.id.quantityPicker);
        buyBtn = findViewById(R.id.buyButton);

        intent = getIntent();
        String imgName = intent.getStringExtra("imageName");
        String productName = intent.getStringExtra("name");
        int productQuantity = intent.getIntExtra("quantity", 0);
        String productType = intent.getStringExtra("type");
        double productPrice = intent.getDoubleExtra("price", 0);
        final String currentUser = intent.getStringExtra("currentUser");

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        itemName.setText("Name: " + productName);
        itemPrice.setText("Price: " + productPrice);
        itemQuantitiy.setText("Available: " + productQuantity);
        itemType.setText("Type: " + productType);
        int imageResId = getResources().getIdentifier(imgName, "drawable", getPackageName());
        img.setImageResource(imageResId);

        picker.setMinValue(1);
        picker.setMaxValue(productQuantity);

        if (productQuantity == 1) {
            picker.setWrapSelectorWheel(false);
            picker.setEnabled(false);
        }
        if (productQuantity <=0){
            buyBtn.setEnabled(false);
        }



        getProducts();

        buyBtn.setOnClickListener(event -> {
            int selectedQuantity = picker.getValue();

            Product cartProduct = new Product(
                    productName,
                    selectedQuantity,
                    productPrice,
                    productType,
                    imgName
            );

            String cartKey = "cart_" + currentUser;
            String cartJson = sharedPreferences.getString(cartKey, "");
            Gson gson = new Gson();
            List<Product> cartList;

            if (!cartJson.isEmpty()) {
                Product[] cartArray = gson.fromJson(cartJson, Product[].class);
                cartList = new ArrayList<>(Arrays.asList(cartArray));
            } else {
                cartList = new ArrayList<>();
            }

            cartList.add(cartProduct);

            String updatedCartJson = gson.toJson(cartList);
            editor.putString(cartKey, updatedCartJson);
            editor.commit();

            Toast.makeText(this, "Added to cart: " + selectedQuantity + " items", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    private void saveProductData() {
        Gson gson = new Gson();
        String products = gson.toJson(productList);
        editor.putString("products", products);
        editor.commit();
    }


    private void getProducts() {
        String productsJson = sharedPreferences.getString("products", "");

        if (!productsJson.isEmpty()) {
            Gson gson = new Gson();
            Product[] productsArray = gson.fromJson(productsJson, Product[].class);
            productList = new ArrayList<>();

            productList.addAll(Arrays.asList(productsArray));

        }
    }

    private Product findProduct(String name) {
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

}
package com.example.assignment1;

import android.content.SharedPreferences; import android.os.Bundle; import android.widget.Button; import android.widget.EditText; import android.widget.ImageView; import android.widget.NumberPicker; import android.widget.TextView; import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList; import java.util.Arrays; import java.util.List;

public class admin_item_detail extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameText, productTypeText, productPriceText, productQtyText;
    private EditText editProductPrice;
    private NumberPicker quantityPicker;
    private Button quantityBtn, priceBtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private List<Product> productList = new ArrayList<>();
    private String selectedProductName;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_detail);

        // Bind Views
        productImageView = findViewById(R.id.productImageView);
        productNameText = findViewById(R.id.productNameText);
        productTypeText = findViewById(R.id.productTypeText);
        productPriceText = findViewById(R.id.productPriceText);
        productQtyText = findViewById(R.id.productQtyText);
        editProductPrice = findViewById(R.id.editProductPrice);
        quantityPicker = findViewById(R.id.quantityPicker);
        quantityBtn = findViewById(R.id.quantityBtn);
        priceBtn = findViewById(R.id.priceBtn);

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String imgName = getIntent().getStringExtra("imageName");
        selectedProductName = getIntent().getStringExtra("name");
        int quantity = getIntent().getIntExtra("quantity", 0);
        String type = getIntent().getStringExtra("type");
        double price = getIntent().getDoubleExtra("price", 0);

        loadProducts();

        for (Product p : productList) {
            if (p.getName().equals(selectedProductName)) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productNameText.setText(selectedProduct.getName());
        productTypeText.setText("Type: " + selectedProduct.getType());
        productPriceText.setText("Price: $" + selectedProduct.getPrice());
        productQtyText.setText("Available: " + selectedProduct.getQuantity());
        int imageResId = getResources().getIdentifier(imgName, "drawable", getPackageName());
        productImageView.setImageResource(imageResId);

        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100);

        quantityBtn.setOnClickListener(v -> {
            int addedQty = quantityPicker.getValue();
            selectedProduct.setQuantity(selectedProduct.getQuantity() + addedQty);
            saveProducts();
            productQtyText.setText("Available: " + selectedProduct.getQuantity());
            Toast.makeText(this, "Quantity updated!", Toast.LENGTH_SHORT).show();
        });

        priceBtn.setOnClickListener(v -> {
            String newPriceStr = editProductPrice.getText().toString().trim();
            if (newPriceStr.isEmpty()) {
                Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double newPrice = Double.parseDouble(newPriceStr);
                if (newPrice < 0) throw new NumberFormatException();
                selectedProduct.setPrice(newPrice);
                saveProducts();
                productPriceText.setText("Price: $" + newPrice);
                Toast.makeText(this, "Price updated!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price input", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        String productsJson = sharedPreferences.getString("products", "");
        if (!productsJson.isEmpty()) {
            Product[] productArray = new Gson().fromJson(productsJson, Product[].class);
            productList = new ArrayList<>(Arrays.asList(productArray));
        }
    }

    private void saveProducts() {
        String updatedJson = new Gson().toJson(productList);
        editor.putString("products", updatedJson);
        editor.apply();
    }
}
package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        backBtn = findViewById(R.id.backButton);

        intent = getIntent();
        String imgName = intent.getStringExtra("imageName");
        String productName = intent.getStringExtra("name");
        int productQuantity = intent.getIntExtra("quantity", 0);
        String productType = intent.getStringExtra("type");
        double porductPrice = intent.getDoubleExtra("price", 0);

        itemName.setText("Name: " + productName);
        itemPrice.setText("Price: " + porductPrice);
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

        int selectedQuantity = picker.getValue();
    }
}
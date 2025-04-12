package com.example.assignment1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CartAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> products;

    public CartAdapter(@NonNull Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = products.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_product_item_cart, parent, false);
        }

        TextView name = convertView.findViewById(R.id.tvProductName);
        TextView price = convertView.findViewById(R.id.tvProductPrice);
        TextView type = convertView.findViewById(R.id.tvProductType);
        ImageView image = convertView.findViewById(R.id.productImage);
        TextView quantity = convertView.findViewById(R.id.tvProductQuantity);

        name.setText(product.getName());
        price.setText("Price: $" + product.getPrice());
        type.setText("Type: " + product.getType());
        quantity.setText("Quantity: " + product.getQuantity());

        int imageResId = context.getResources().getIdentifier(product.getImgName(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            image.setImageResource(imageResId);
        }

        return convertView;
    }
}

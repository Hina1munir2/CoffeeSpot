package com.example.coffee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.coffee.R;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private ProductClickListener listener;
    private LocalDatabaseHelper localDatabaseHelper;

    public interface ProductClickListener {
        void onAddToCartClick(Product product);
        void onLikeClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, ProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.localDatabaseHelper = new LocalDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.productDescription.setText(product.getDescription());
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.coffee_default)
                .into(holder.productImage);
        }
        
        boolean isLiked = localDatabaseHelper.isLiked(product.getId());
        holder.likeButton.setImageResource(isLiked ? R.drawable.ic_favouritefilled : R.drawable.ic_favourite_border);
        
        holder.addToCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClick(product);
            }
        });
        
        holder.likeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        ImageButton likeButton;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            likeButton = itemView.findViewById(R.id.likeButton);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}
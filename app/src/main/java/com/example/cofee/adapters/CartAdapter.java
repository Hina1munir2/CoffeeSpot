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
import com.example.coffee.models.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, int newQuantity);
        void onRemoveItem(int position);
        void onItemClick(int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.cartItemName.setText(item.getProductName());
        holder.cartItemPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        
        double itemTotal = item.getPrice() * item.getQuantity();
        holder.itemTotalText.setText(String.format("$%.2f", itemTotal));
        
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.coffee_default)
                .into(holder.cartItemImage);
        }
        
        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity >= 1) {
                if (listener != null) {
                    listener.onQuantityChanged(position, newQuantity);
                }
            }
        });
        
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (newQuantity <= 50) {
                if (listener != null) {
                    listener.onQuantityChanged(position, newQuantity);
                }
            }
        });
        
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(position);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemName;
        TextView cartItemPrice;
        TextView quantityText;
        TextView itemTotalText;
        Button decreaseButton;
        Button increaseButton;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cartItemImage);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            itemTotalText = itemView.findViewById(R.id.itemTotalText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
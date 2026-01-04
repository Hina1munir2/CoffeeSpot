package com.example.coffee.managers;

import android.content.Context;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.models.CartItem;
import com.example.coffee.models.Product;
import java.util.List;
import java.util.UUID;

public class CartManager {
    private Context context;
    private LocalDatabaseHelper localDatabaseHelper;

    public CartManager(Context context) {
        this.context = context.getApplicationContext();
        this.localDatabaseHelper = new LocalDatabaseHelper(this.context);
    }

    public void addToCart(Product product) {
        List<CartItem> cartItems = localDatabaseHelper.getCartItems();
        
        boolean itemExists = false;
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(product.getId())) {
                updateCartItemQuantity(item.getId(), item.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }
        
        if (!itemExists) {
            CartItem cartItem = new CartItem();
            cartItem.setId(UUID.randomUUID().toString());
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(1);
            cartItem.setImageUrl(product.getImageUrl());
            
            localDatabaseHelper.addToCart(cartItem);
        }
    }

    public List<CartItem> getCartItems() {
        return localDatabaseHelper.getCartItems();
    }

    public void updateCartItemQuantity(String itemId, int quantity) {
        localDatabaseHelper.updateCartItemQuantity(itemId, quantity);
    }

    public void removeFromCart(String itemId) {
        localDatabaseHelper.removeFromCart(itemId);
    }

    public void clearCart() {
        localDatabaseHelper.clearCart();
    }

    public int getCartItemCount() {
        return localDatabaseHelper.getCartItemCount();
    }

    public double getCartTotal() {
        List<CartItem> cartItems = getCartItems();
        double total = 0;
        
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        
        return total;
    }
}
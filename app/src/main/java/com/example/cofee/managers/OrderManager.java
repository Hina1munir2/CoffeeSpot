package com.example.coffee.managers;

import android.content.Context;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.models.CartItem;
import com.example.coffee.models.Order;
import java.util.List;

public class OrderManager {
    private Context context;
    private LocalDatabaseHelper localDatabaseHelper;

    public OrderManager(Context context) {
        this.context = context.getApplicationContext();
        this.localDatabaseHelper = new LocalDatabaseHelper(this.context);
    }

    public void saveOrder(Order order) {
        localDatabaseHelper.saveOrder(order);
    }

    public List<Order> getOrders() {
        return localDatabaseHelper.getOrders();
    }

    public int getOrdersCount() {
        return localDatabaseHelper.getOrdersCount();
    }

    public void updateOrder(Order order) {
        localDatabaseHelper.updateOrderStatus(order.getId(), order.getStatus());
    }

    public void reorderItems(List<CartItem> items) {
        CartManager cartManager = new CartManager(context);
        
        for (CartItem item : items) {
            com.example.coffee.models.Product product = new com.example.coffee.models.Product();
            product.setId(item.getProductId());
            product.setName(item.getProductName());
            product.setPrice(item.getPrice());
            product.setImageUrl(item.getImageUrl());
            
            for (int i = 0; i < item.getQuantity(); i++) {
                cartManager.addToCart(product);
            }
        }
    }

    public void syncOrders(List<Order> orders) {
        localDatabaseHelper.syncOrders(orders);
    }
}
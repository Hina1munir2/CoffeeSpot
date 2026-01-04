package com.example.coffee.managers;

import android.content.Context;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.models.Product;
import java.util.List;

public class LikedItemsManager {
    private Context context;
    private LocalDatabaseHelper localDatabaseHelper;

    public LikedItemsManager(Context context) {
        this.context = context.getApplicationContext();
        this.localDatabaseHelper = new LocalDatabaseHelper(this.context);
    }
    public void addToLiked(Product product) {
        localDatabaseHelper.addToLiked(product);
    }

    public List<Product> getLikedItems() {
        return localDatabaseHelper.getLikedItems();
    }

    public boolean isLiked(String productId) {
        return localDatabaseHelper.isLiked(productId);
    }

    public void removeFromLiked(String productId) {
        localDatabaseHelper.removeFromLiked(productId);
    }

    public void clearAllLiked() {
        localDatabaseHelper.clearAllLiked();
    }

    public void syncLikedItems(List<Product> products) {
        localDatabaseHelper.syncLikedItems(products);
    }

    public int getLikedItemsCount() {
        return localDatabaseHelper.getLikedItems().size();
    }
}
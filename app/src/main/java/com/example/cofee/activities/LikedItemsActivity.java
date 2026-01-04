package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffee.R;
import com.example.coffee.adapters.ProductAdapter;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.CartManager;
import com.example.coffee.managers.LikedItemsManager;
import com.example.coffee.models.Product;
import java.util.ArrayList;
import java.util.List;

public class LikedItemsActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private RecyclerView likedRecyclerView;
    private ProductAdapter productAdapter;
    private TextView emptyLikedText;
    private List<Product> likedProducts;
    private LikedItemsManager likedItemsManager;
    private CartManager cartManager;
    private FirebaseRepository firebaseRepository;
    private LocalDatabaseHelper localDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_items);

        likedItemsManager = new LikedItemsManager(this);
        cartManager = new CartManager(this);
        firebaseRepository = new FirebaseRepository();
        localDatabaseHelper = new LocalDatabaseHelper(this);

        likedRecyclerView = findViewById(R.id.likedRecyclerView);
        emptyLikedText = findViewById(R.id.emptyLikedText);

        likedRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        likedProducts = new ArrayList<>();
        loadLikedItems();
    }

    private void loadLikedItems() {
        likedProducts = likedItemsManager.getLikedItems();
        
        if (likedProducts.isEmpty()) {
            emptyLikedText.setVisibility(View.VISIBLE);
            likedRecyclerView.setVisibility(View.GONE);
            
            firebaseRepository.getLikedItems(localDatabaseHelper.getCurrentUserId(), new FirebaseRepository.LikedItemsCallback() {
                @Override
                public void onSuccess(List<Product> products) {
                    if (!products.isEmpty()) {
                        likedProducts.addAll(products);
                        likedItemsManager.syncLikedItems(products);
                        setupAdapter();
                    }
                }

                @Override
                public void onFailure(String error) {
                }
            });
        } else {
            emptyLikedText.setVisibility(View.GONE);
            likedRecyclerView.setVisibility(View.VISIBLE);
            setupAdapter();
        }
    }

    private void setupAdapter() {
        productAdapter = new ProductAdapter(this, likedProducts, this);
        likedRecyclerView.setAdapter(productAdapter);
        
        if (likedProducts.isEmpty()) {
            emptyLikedText.setVisibility(View.VISIBLE);
            likedRecyclerView.setVisibility(View.GONE);
        } else {
            emptyLikedText.setVisibility(View.GONE);
            likedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartManager.addToCart(product);
        Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeClick(Product product) {
        likedItemsManager.removeFromLiked(product.getId());
        likedProducts.remove(product);
        productAdapter.notifyDataSetChanged();
        
        firebaseRepository.removeFromLiked(localDatabaseHelper.getCurrentUserId(), product.getId());
        
        if (likedProducts.isEmpty()) {
            emptyLikedText.setVisibility(View.VISIBLE);
            likedRecyclerView.setVisibility(View.GONE);
        }
        
        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.liked_items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_add_all_to_cart) {
            addAllToCart();
            return true;
        } else if (id == R.id.menu_clear_liked) {
            clearAllLiked();
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_explore) {
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void addAllToCart() {
        if (likedProducts.isEmpty()) {
            Toast.makeText(this, "No items to add", Toast.LENGTH_SHORT).show();
            return;
        }
        
        for (Product product : likedProducts) {
            cartManager.addToCart(product);
        }
        
        Toast.makeText(this, "All items added to cart", Toast.LENGTH_SHORT).show();
    }

    private void clearAllLiked() {
        if (likedProducts.isEmpty()) {
            Toast.makeText(this, "Already empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        likedItemsManager.clearAllLiked();
        firebaseRepository.clearAllLiked(localDatabaseHelper.getCurrentUserId());
        likedProducts.clear();
        productAdapter.notifyDataSetChanged();
        emptyLikedText.setVisibility(View.VISIBLE);
        likedRecyclerView.setVisibility(View.GONE);
        
        Toast.makeText(this, "All favorites cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLikedItems();
    }
}
package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ExploreActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseRepository firebaseRepository;
    private LocalDatabaseHelper localDatabaseHelper;
    private CartManager cartManager;
    private LikedItemsManager likedItemsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Initialize with application context
        localDatabaseHelper = new LocalDatabaseHelper(getApplicationContext());
        cartManager = new CartManager(getApplicationContext());
        likedItemsManager = new LikedItemsManager(getApplicationContext());
        firebaseRepository = new FirebaseRepository();

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productList = new ArrayList<>();

        // Load products immediately with sample data
        loadSampleProducts();

        productAdapter = new ProductAdapter(this, productList, this);
        productsRecyclerView.setAdapter(productAdapter);

        // Try to load from Firebase in background (optional)
        tryLoadFromFirebase();
    }

    private void loadSampleProducts() {
        productList.clear();

        // Sample product 1
        Product p1 = new Product();
        p1.setId("1");
        p1.setName("Cappuccino");
        p1.setDescription("Rich espresso with steamed milk foam");
        p1.setPrice(4.99);
        p1.setImageUrl("https://images.unsplash.com/photo-1534687941688-651ccaafbff8?w=400");
        p1.setCategory("Espresso");
        productList.add(p1);

        // Sample product 2
        Product p2 = new Product();
        p2.setId("2");
        p2.setName("Latte");
        p2.setDescription("Smooth espresso with steamed milk");
        p2.setPrice(5.49);
        p2.setImageUrl("https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400");
        p2.setCategory("Espresso");
        productList.add(p2);

        // Sample product 3
        Product p3 = new Product();
        p3.setId("3");
        p3.setName("Americano");
        p3.setDescription("Espresso with hot water");
        p3.setPrice(3.99);
        p3.setImageUrl("https://images.unsplash.com/photo-1498804103079-a6351b050096?w=400");
        p3.setCategory("Espresso");
        productList.add(p3);

        // Sample product 4
        Product p4 = new Product();
        p4.setId("4");
        p4.setName("Mocha");
        p4.setDescription("Chocolate flavored latte");
        p4.setPrice(5.99);
        p4.setImageUrl("https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400");
        p4.setCategory("Specialty");
        productList.add(p4);

        // Sample product 5
        Product p5 = new Product();
        p5.setId("5");
        p5.setName("Cold Brew");
        p5.setDescription("Slow-steeped cold coffee");
        p5.setPrice(4.49);
        p5.setImageUrl("https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=400");
        p5.setCategory("Cold Brew");
        productList.add(p5);

        // Sample product 6
        Product p6 = new Product();
        p6.setId("6");
        p6.setName("Iced Coffee");
        p6.setDescription("Chilled coffee with ice");
        p6.setPrice(3.99);
        p6.setImageUrl("https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400");
        p6.setCategory("Iced Coffee");
        productList.add(p6);

        // Sample product 7
        Product p7 = new Product();
        p7.setId("7");
        p7.setName("Macchiato");
        p7.setDescription("Espresso with dollop of foamed milk");
        p7.setPrice(4.29);
        p7.setImageUrl("https://images.unsplash.com/photo-1534778101976-62847782c213?w=400");
        p7.setCategory("Espresso");
        productList.add(p7);

        // Sample product 8
        Product p8 = new Product();
        p8.setId("8");
        p8.setName("Flat White");
        p8.setDescription("Velvet microfoam over espresso");
        p8.setPrice(5.29);
        p8.setImageUrl("https://images.unsplash.com/photo-1568649929103-28ffbefaca1e?w=400");
        p8.setCategory("Espresso");
        productList.add(p8);

        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }

        Toast.makeText(this, "Browse our coffee selection", Toast.LENGTH_SHORT).show();
    }

    private void tryLoadFromFirebase() {
        // Optional: Try to load from Firebase in background
        new Thread(() -> {
            try {
                firebaseRepository.getProducts(new FirebaseRepository.ProductsCallback() {
                    @Override
                    public void onSuccess(List<Product> products) {
                        runOnUiThread(() -> {
                            if (products != null && !products.isEmpty()) {
                                productList.clear();
                                productList.addAll(products);
                                productAdapter.notifyDataSetChanged();
                                Toast.makeText(ExploreActivity.this,
                                        "Products loaded from cloud", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        // Already have sample products, so no error needed
                    }
                });
            } catch (Exception e) {
                // Ignore errors - we have sample products
            }
        }).start();
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartManager.addToCart(product);
        Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();

        // Update cart badge in menu
        invalidateOptionsMenu();
    }

    @Override
    public void onLikeClick(Product product) {
        if (likedItemsManager.isLiked(product.getId())) {
            likedItemsManager.removeFromLiked(product.getId());
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            likedItemsManager.addToLiked(product);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore_menu, menu);

        MenuItem cartMenuItem = menu.findItem(R.id.menu_cart);
        int cartCount = cartManager.getCartItemCount();
        if (cartCount > 0) {
            cartMenuItem.setTitle("Cart (" + cartCount + ")");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_favorites) {
            Intent intent = new Intent(this, LikedItemsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            loadSampleProducts();
            Toast.makeText(this, "Products refreshed", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}
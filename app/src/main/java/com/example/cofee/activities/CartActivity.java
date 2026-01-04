package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffee.R;
import com.example.coffee.adapters.CartAdapter;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.CartManager;
import com.example.coffee.models.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceText, emptyCartText;
    private Button checkoutButton, continueShoppingButton;
    private CartManager cartManager;
    private LocalDatabaseHelper localDatabaseHelper;
    private FirebaseRepository firebaseRepository;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = new CartManager(this);
        localDatabaseHelper = new LocalDatabaseHelper(this);
        firebaseRepository = new FirebaseRepository();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        emptyCartText = findViewById(R.id.emptyCartText);
        checkoutButton = findViewById(R.id.checkoutButton);
        continueShoppingButton = findViewById(R.id.continueShoppingButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCartItems();

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

        continueShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ExploreActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadCartItems() {
        cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        updateUI();
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
            totalPriceText.setText("Total: $0.00");
        } else {
            emptyCartText.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(true);
            
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getPrice() * item.getQuantity();
            }
            totalPriceText.setText(String.format("Total: $%.2f", total));
        }
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        CartItem item = cartItems.get(position);
        cartManager.updateCartItemQuantity(item.getId(), newQuantity);
        item.setQuantity(newQuantity);
        cartAdapter.notifyItemChanged(position);
        updateUI();
    }

    @Override
    public void onRemoveItem(int position) {
        CartItem item = cartItems.get(position);
        cartManager.removeFromCart(item.getId());
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);
        updateUI();
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        CartItem item = cartItems.get(position);
        Toast.makeText(this, item.getProductName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_clear_cart) {
            cartManager.clearCart();
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            updateUI();
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_save_for_later) {
            for (CartItem cartItem : cartItems) {
                firebaseRepository.saveForLater(cartItem);
            }
            Toast.makeText(this, "Items saved for later", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
}
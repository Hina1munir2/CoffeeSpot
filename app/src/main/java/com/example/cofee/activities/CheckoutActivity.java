package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee.R;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.CartManager;
import com.example.coffee.managers.OrderManager;
import com.example.coffee.models.CartItem;
import com.example.coffee.models.Order;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private EditText fullNameEditText, addressEditText, cityEditText, zipCodeEditText, phoneEditText;
    private RadioGroup paymentMethodGroup;
    private RadioButton cashOnDeliveryRadio, cardPaymentRadio;
    private TextView subtotalText, taxText, deliveryText, totalText;
    private Button placeOrderButton;
    private CartManager cartManager;
    private OrderManager orderManager;
    private LocalDatabaseHelper localDatabaseHelper;
    private FirebaseRepository firebaseRepository;
    private List<CartItem> cartItems;
    private double subtotal = 0;
    private double tax = 0;
    private double deliveryFee = 2.99;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartManager = new CartManager(this);
        orderManager = new OrderManager(this);
        localDatabaseHelper = new LocalDatabaseHelper(this);
        firebaseRepository = new FirebaseRepository();

        fullNameEditText = findViewById(R.id.fullNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        cityEditText = findViewById(R.id.cityEditText);
        zipCodeEditText = findViewById(R.id.zipCodeEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        cashOnDeliveryRadio = findViewById(R.id.cashOnDeliveryRadio);
        cardPaymentRadio = findViewById(R.id.cardPaymentRadio);
        subtotalText = findViewById(R.id.subtotalText);
        taxText = findViewById(R.id.taxText);
        deliveryText = findViewById(R.id.deliveryText);
        totalText = findViewById(R.id.totalText);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        cartItems = cartManager.getCartItems();
        calculateTotals();
        loadUserInfo();

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    placeOrder();
                }
            }
        });
    }

    private void loadUserInfo() {
        String userName = localDatabaseHelper.getUserName();
        if (userName != null && !userName.isEmpty()) {
            fullNameEditText.setText(userName);
        }
    }

    private void calculateTotals() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        tax = subtotal * 0.08;
        total = subtotal + tax + deliveryFee;

        subtotalText.setText(String.format("$%.2f", subtotal));
        taxText.setText(String.format("$%.2f", tax));
        deliveryText.setText(String.format("$%.2f", deliveryFee));
        totalText.setText(String.format("$%.2f", total));
    }

    private boolean validateInput() {
        if (fullNameEditText.getText().toString().trim().isEmpty()) {
            fullNameEditText.setError("Full name required");
            return false;
        }

        if (addressEditText.getText().toString().trim().isEmpty()) {
            addressEditText.setError("Address required");
            return false;
        }

        if (cityEditText.getText().toString().trim().isEmpty()) {
            cityEditText.setError("City required");
            return false;
        }

        if (zipCodeEditText.getText().toString().trim().isEmpty()) {
            zipCodeEditText.setError("ZIP code required");
            return false;
        }

        if (phoneEditText.getText().toString().trim().isEmpty()) {
            phoneEditText.setError("Phone number required");
            return false;
        }

        if (paymentMethodGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        placeOrderButton.setEnabled(false);
        placeOrderButton.setText("Processing...");

        String orderId = UUID.randomUUID().toString();
        String paymentMethod = cashOnDeliveryRadio.isChecked() ? "Cash on Delivery" : "Card Payment";

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(localDatabaseHelper.getCurrentUserId());
        order.setFullName(fullNameEditText.getText().toString().trim());
        order.setAddress(addressEditText.getText().toString().trim());
        order.setCity(cityEditText.getText().toString().trim());
        order.setZipCode(zipCodeEditText.getText().toString().trim());
        order.setPhone(phoneEditText.getText().toString().trim());
        order.setPaymentMethod(paymentMethod);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setDeliveryFee(deliveryFee);
        order.setTotal(total);
        order.setItems(cartItems);
        order.setOrderDate(new Date());
        order.setStatus("Processing");

        orderManager.saveOrder(order);
        firebaseRepository.saveOrder(order, new FirebaseRepository.OrderCallback() {
            @Override
            public void onSuccess(String orderId) {
                cartManager.clearCart();
                Intent intent = new Intent(CheckoutActivity.this, PaymentDoneActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("total_amount", total);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CheckoutActivity.this, "Order placed locally: " + error, Toast.LENGTH_SHORT).show();
                cartManager.clearCart();
                Intent intent = new Intent(CheckoutActivity.this, PaymentDoneActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("total_amount", total);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
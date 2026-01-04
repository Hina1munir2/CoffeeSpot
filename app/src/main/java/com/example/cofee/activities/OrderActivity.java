package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffee.R;
import com.example.coffee.adapters.OrderAdapter;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.OrderManager;
import com.example.coffee.models.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.OrderClickListener {
    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private TextView emptyOrdersText;
    private List<Order> orderList;
    private OrderManager orderManager;
    private FirebaseRepository firebaseRepository;
    private LocalDatabaseHelper localDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderManager = new OrderManager(this);
        firebaseRepository = new FirebaseRepository();
        localDatabaseHelper = new LocalDatabaseHelper(this);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        emptyOrdersText = findViewById(R.id.emptyOrdersText);

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        loadOrders();
    }

    private void loadOrders() {
        orderList = orderManager.getOrders();
        
        if (orderList.isEmpty()) {
            emptyOrdersText.setVisibility(View.VISIBLE);
            ordersRecyclerView.setVisibility(View.GONE);
            
            firebaseRepository.getOrders(localDatabaseHelper.getCurrentUserId(), new FirebaseRepository.OrdersCallback() {
                @Override
                public void onSuccess(List<Order> orders) {
                    if (!orders.isEmpty()) {
                        orderList.addAll(orders);
                        orderManager.syncOrders(orders);
                        setupAdapter();
                    }
                }

                @Override
                public void onFailure(String error) {
                }
            });
        } else {
            emptyOrdersText.setVisibility(View.GONE);
            ordersRecyclerView.setVisibility(View.VISIBLE);
            setupAdapter();
        }
    }

    private void setupAdapter() {
        orderAdapter = new OrderAdapter(this, orderList, this);
        ordersRecyclerView.setAdapter(orderAdapter);
        
        if (orderList.isEmpty()) {
            emptyOrdersText.setVisibility(View.VISIBLE);
            ordersRecyclerView.setVisibility(View.GONE);
        } else {
            emptyOrdersText.setVisibility(View.GONE);
            ordersRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOrderClick(int position) {
        Order order = orderList.get(position);
        Toast.makeText(this, "Order #" + order.getId().substring(0, 8), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReorderClick(int position) {
        Order order = orderList.get(position);
        orderManager.reorderItems(order.getItems());
        Toast.makeText(this, "Items added to cart for reorder", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTrackClick(int position) {
        Order order = orderList.get(position);
        Toast.makeText(this, "Tracking order #" + order.getId().substring(0, 8), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelClick(int position) {
        Order order = orderList.get(position);
        if (order.getStatus().equals("Processing")) {
            order.setStatus("Cancelled");
            orderManager.updateOrder(order);
            orderAdapter.notifyItemChanged(position);
            firebaseRepository.updateOrderStatus(order.getId(), "Cancelled");
            Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cannot cancel this order", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_refresh) {
            loadOrders();
            Toast.makeText(this, "Refreshing orders", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_filter) {
            showFilterDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        Toast.makeText(this, "Filter options", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
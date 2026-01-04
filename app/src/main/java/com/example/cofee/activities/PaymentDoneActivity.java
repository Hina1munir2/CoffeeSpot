package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee.R;
import com.example.coffee.network.maps.MapUtils;

public class PaymentDoneActivity extends AppCompatActivity {
    private TextView orderIdText, amountText, statusText, thankYouText;
    private Button trackOrderButton, backToHomeButton, shareButton;
    private String orderId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentdone);

        orderIdText = findViewById(R.id.orderIdText);
        amountText = findViewById(R.id.amountText);
        statusText = findViewById(R.id.statusText);
        thankYouText = findViewById(R.id.thankYouText);
        trackOrderButton = findViewById(R.id.trackOrderButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        shareButton = findViewById(R.id.shareButton);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
        totalAmount = intent.getDoubleExtra("total_amount", 0.00);

        if (orderId != null) {
            String shortOrderId = orderId.substring(0, 8).toUpperCase();
            orderIdText.setText("Order #" + shortOrderId);
            amountText.setText(String.format("$%.2f", totalAmount));
            statusText.setText("Order Confirmed");
            thankYouText.setText("Thank you for your order!");
        } else {
            orderIdText.setText("Order #UNKNOWN");
            amountText.setText("$0.00");
            statusText.setText("Processing");
            thankYouText.setText("Thank you!");
        }

        trackOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtils.openMapForTracking(PaymentDoneActivity.this);
                Toast.makeText(PaymentDoneActivity.this, "Opening map for tracking", Toast.LENGTH_SHORT).show();
            }
        });

        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentDoneActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOrderDetails();
            }
        });
    }

    private void shareOrderDetails() {
        String shareText = "I just ordered coffee from Coffee Shop!\n" +
                "Order ID: " + orderId + "\n" +
                "Amount: $" + String.format("%.2f", totalAmount) + "\n" +
                "Download the app: [App Link Here]";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Order Details"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
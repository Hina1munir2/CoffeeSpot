package com.example.coffee.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee.R;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.CartManager;
import com.example.coffee.managers.OrderManager;
import com.example.coffee.network.facts.CoffeeFactsApiService;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView userNameText, userEmailText, ordersCountText, favoritesCountText, memberSinceText;
    private Button editProfileButton, changePasswordButton, contactSupportButton, rateAppButton, logoutButton;
    private LocalDatabaseHelper localDatabaseHelper;
    private FirebaseRepository firebaseRepository;
    private OrderManager orderManager;
    private CartManager cartManager;
    private CoffeeFactsApiService coffeeFactsApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        localDatabaseHelper = new LocalDatabaseHelper(this);
        firebaseRepository = new FirebaseRepository();
        orderManager = new OrderManager(this);
        cartManager = new CartManager(this);
        coffeeFactsApiService = new CoffeeFactsApiService();

        profileImage = findViewById(R.id.profileImage);
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        ordersCountText = findViewById(R.id.ordersCountText);
        favoritesCountText = findViewById(R.id.favoritesCountText);
        memberSinceText = findViewById(R.id.memberSinceText);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        contactSupportButton = findViewById(R.id.contactSupportButton);
        rateAppButton = findViewById(R.id.rateAppButton);
        logoutButton = findViewById(R.id.logoutButton);

        loadProfileData();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Edit Profile", Toast.LENGTH_SHORT).show();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        contactSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSupportEmail();
            }
        });

        rateAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayStore();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        fetchCoffeeFact();
    }

    private void loadProfileData() {
        String name = localDatabaseHelper.getUserName();
        String email = localDatabaseHelper.getUserEmail();
        
        if (name != null && !name.isEmpty()) {
            userNameText.setText(name);
        } else {
            userNameText.setText("Coffee Lover");
        }
        
        if (email != null && !email.isEmpty()) {
            userEmailText.setText(email);
        } else {
            userEmailText.setText("user@coffee.com");
        }
        
        int ordersCount = orderManager.getOrdersCount();
        ordersCountText.setText(String.valueOf(ordersCount));
        
        memberSinceText.setText("Member since 2024");
        
        firebaseRepository.getUserProfile(localDatabaseHelper.getCurrentUserId(), new FirebaseRepository.ProfileCallback() {
            @Override
            public void onSuccess(String name, String email, int favoritesCount) {
                if (name != null) userNameText.setText(name);
                if (email != null) userEmailText.setText(email);
                favoritesCountText.setText(String.valueOf(favoritesCount));
            }

            @Override
            public void onFailure(String error) {
                favoritesCountText.setText("0");
            }
        });
    }

    private void sendSupportEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@coffeeshop.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Coffee Shop App Support");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello Support Team,\n\n");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void logoutUser() {
        localDatabaseHelper.clearUserSession();
        cartManager.clearCart();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void fetchCoffeeFact() {
        coffeeFactsApiService.getRandomFact(new CoffeeFactsApiService.FactCallback() {
            @Override
            public void onSuccess(String fact) {
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_share) {
            shareApp();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String shareText = "Check out Coffee Shop app! Download now: [App Link Here]";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share App"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }
}
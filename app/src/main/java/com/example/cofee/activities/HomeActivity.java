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
import com.example.coffee.R;
import com.example.coffee.local.LocalDatabaseHelper;
import com.example.coffee.managers.CartManager;
import com.example.coffee.network.weather.WeatherApiClient;
import com.example.coffee.network.facts.CoffeeFactsApiService;

public class HomeActivity extends AppCompatActivity {
    private TextView welcomeText, weatherText, coffeeFactText, developerText;
    private Button browseCoffeeButton, viewCartButton, viewOrdersButton,
            viewFavoritesButton, viewProfileButton, refreshFactButton;
    private LocalDatabaseHelper localDatabaseHelper;
    private CartManager cartManager;
    private WeatherApiClient weatherApiClient;
    private CoffeeFactsApiService coffeeFactsApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        localDatabaseHelper = new LocalDatabaseHelper(this);
        cartManager = new CartManager(this);
        weatherApiClient = new WeatherApiClient();
        coffeeFactsApiService = new CoffeeFactsApiService();

        welcomeText = findViewById(R.id.welcomeText);
        weatherText = findViewById(R.id.weatherText);
        coffeeFactText = findViewById(R.id.coffeeFactText);
        developerText = findViewById(R.id.developerText);
        browseCoffeeButton = findViewById(R.id.browseCoffeeButton);
        viewCartButton = findViewById(R.id.viewCartButton);
        viewOrdersButton = findViewById(R.id.viewOrdersButton);
        viewFavoritesButton = findViewById(R.id.viewFavoritesButton);
        viewProfileButton = findViewById(R.id.viewProfileButton);
        refreshFactButton = findViewById(R.id.refreshFactButton);

        developerText.setText("Hina Munir");

        String userName = localDatabaseHelper.getUserName();
        if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Welcome, " + userName);
        } else {
            welcomeText.setText("Welcome to Coffee Shop");
        }

        updateCartBadge();
        fetchWeather();
        fetchCoffeeFact();

        browseCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ExploreActivity.class);
                startActivity(intent);
            }
        });

        viewCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        viewFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LikedItemsActivity.class);
                startActivity(intent);
            }
        });

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        refreshFactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoffeeFact();
            }
        });

        coffeeFactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoffeeFact();
            }
        });
    }

    private void updateCartBadge() {
        int cartItemCount = cartManager.getCartItemCount();
        if (cartItemCount > 0) {
            viewCartButton.setText("View Cart (" + cartItemCount + ")");
        } else {
            viewCartButton.setText("View Cart");
        }
    }

    private void fetchWeather() {
        weatherApiClient.getCurrentWeather(new WeatherApiClient.WeatherCallback() {
            @Override
            public void onSuccess(double temperature, String condition) {
                String tempCelsius = String.format("%.1f°C", temperature);
                String recommendation = getCoffeeRecommendation(temperature);
                weatherText.setText("Weather: " + tempCelsius + " - " + condition + "\n" + recommendation);
            }

            @Override
            public void onFailure(String error) {
                weatherText.setText("Weather: Unable to fetch\nTry: Hot Coffee Today");
            }
        });
    }

    private void fetchCoffeeFact() {
        coffeeFactText.setText("Loading coffee fact...");
        coffeeFactsApiService.getRandomFact(new CoffeeFactsApiService.FactCallback() {
            @Override
            public void onSuccess(String fact) {
                coffeeFactText.setText("☕ " + fact);
            }

            @Override
            public void onFailure(String error) {
                coffeeFactText.setText("☕ Coffee: The best part of waking up!");
            }
        });
    }

    private String getCoffeeRecommendation(double temperature) {
        if (temperature < 15) {
            return "Recommendation: Hot Mocha";
        } else if (temperature < 25) {
            return "Recommendation: Cappuccino";
        } else {
            return "Recommendation: Iced Coffee";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            localDatabaseHelper.clearUserSession();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_help) {
            Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}
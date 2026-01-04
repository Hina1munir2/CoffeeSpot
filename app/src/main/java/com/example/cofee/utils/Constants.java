package com.example.coffee.utils;

import java.util.Random;

public class Constants {
    public static final String SHARED_PREFS_NAME = "CoffeeShopPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_LOGIN_TYPE = "login_type";
    public static final String KEY_REMEMBER_ME = "remember_me";

    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_PRODUCTS_COLLECTION = "products";
    public static final String FIREBASE_ORDERS_COLLECTION = "orders";
    public static final String FIREBASE_LIKED_ITEMS_COLLECTION = "likedItems";
    public static final String FIREBASE_CART_ITEMS_COLLECTION = "cartItems";

    // Dummy API keys for demo
    public static final String WEATHER_API_KEY = "demo_key_12345";
    public static final String GOOGLE_MAPS_API_KEY = "demo_map_key_67890";

    public static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String COFFEE_FACTS_URL = "https://coffee.alexflipnote.dev/random.json";

    public static final String DEFAULT_CITY = "London";
    public static final double DEFAULT_LATITUDE = 51.5074;
    public static final double DEFAULT_LONGITUDE = -0.1278;

    public static final int CACHE_DURATION_MINUTES = 30;
    public static final int MAX_CART_ITEMS = 50;
    public static final double DELIVERY_FEE = 2.99;
    public static final double TAX_RATE = 0.08;

    public static final int REQUEST_CODE_LOCATION = 1001;
    public static final int REQUEST_CODE_CAMERA = 1002;
    public static final int REQUEST_CODE_GALLERY = 1003;
    public static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1004;

    public static final String[] COFFEE_CATEGORIES = {
            "Espresso",
            "Cappuccino",
            "Latte",
            "Americano",
            "Macchiato",
            "Mocha",
            "Cold Brew",
            "Iced Coffee",
            "Specialty"
    };

    public static final String[] PAYMENT_METHODS = {
            "Cash on Delivery",
            "Credit Card",
            "Debit Card",
            "Google Pay",
            "Apple Pay"
    };

    public static final String[] ORDER_STATUS = {
            "Processing",
            "Confirmed",
            "Preparing",
            "On the Way",
            "Delivered",
            "Cancelled"
    };

    // Sample weather conditions for demo
    public static final String[] WEATHER_CONDITIONS = {
            "Sunny", "Cloudy", "Rainy", "Snowy", "Windy", "Clear"
    };

    // Sample coffee facts for demo
    public static final String[] COFFEE_FACTS = {
            "Coffee is the second most traded commodity in the world, after oil.",
            "Brazil is the largest coffee producer in the world.",
            "Coffee beans are actually the pit of a berry.",
            "The word 'coffee' comes from the Arabic word 'qahwa'.",
            "Espresso means 'pressed out' in Italian.",
            "Coffee was discovered by goats in Ethiopia.",
            "The most expensive coffee in the world is made from elephant dung.",
            "Coffee can help you burn fat and improve physical performance.",
            "Drinking coffee may lower your risk of Alzheimer's disease.",
            "Coffee stays warmer when you add cream."
    };

    // Helper method to get random weather condition
    public static String getRandomWeatherCondition() {
        Random random = new Random();
        return WEATHER_CONDITIONS[random.nextInt(WEATHER_CONDITIONS.length)];
    }

    // Helper method to get random coffee fact
    public static String getRandomCoffeeFact() {
        Random random = new Random();
        return COFFEE_FACTS[random.nextInt(COFFEE_FACTS.length)];
    }
}
package com.example.coffee.network.facts;

import android.util.Log;
import com.example.coffee.utils.Constants;
import java.util.Random;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class CoffeeFactsApiService {
    private static final String TAG = "CoffeeFactsApiService";
    private Random random;
    private OkHttpClient client;

    public interface FactCallback {
        void onSuccess(String fact);
        void onFailure(String error);
    }

    public CoffeeFactsApiService() {
        this.random = new Random();
        this.client = new OkHttpClient();
    }

    public void getRandomFact(FactCallback callback) {
        // Try to get fact from API
        new Thread(() -> {
            try {
                String fact = fetchFactFromApi();
                if (fact != null && !fact.isEmpty()) {
                    callback.onSuccess(fact);
                } else {
                    // Fallback to local facts
                    callback.onSuccess(getLocalFact());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching coffee fact: " + e.getMessage());
                // Fallback to local facts
                callback.onSuccess(getLocalFact());
            }
        }).start();
    }

    private String fetchFactFromApi() {
        try {
            // Using a free coffee facts API
            String url = "https://coffee.alexflipnote.dev/random.json";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                // Simple JSON parsing (for demo - in real app use Gson)
                if (json.contains("\"text\":")) {
                    int start = json.indexOf("\"text\":\"") + 8;
                    int end = json.indexOf("\"", start);
                    if (start > 7 && end > start) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "API error: " + e.getMessage());
        }
        return null;
    }

    private String getLocalFact() {
        // Get random fact from local array
        String[] localFacts = {
                "Coffee is the second most traded commodity in the world, after oil.",
                "Brazil is the largest coffee producer in the world.",
                "Coffee beans are actually the pit of a berry.",
                "The word 'coffee' comes from the Arabic word 'qahwa'.",
                "Espresso means 'pressed out' in Italian.",
                "Coffee was discovered by goats in Ethiopia.",
                "The most expensive coffee in the world is made from elephant dung.",
                "Coffee can help you burn fat and improve physical performance.",
                "Drinking coffee may lower your risk of Alzheimer's disease.",
                "Coffee stays warmer when you add cream.",
                "Finland consumes the most coffee per capita in the world.",
                "Coffee was once banned in Mecca for being a 'intoxicating beverage'.",
                "The first webcam was invented to monitor a coffee pot.",
                "Coffee can be used as fuel for cars.",
                "There are two main types of coffee beans: Arabica and Robusta."
        };

        return localFacts[random.nextInt(localFacts.length)];
    }

    // Alternative simpler version that always uses local facts
    public void getRandomFactSimple(FactCallback callback) {
        String fact = getLocalFact();
        callback.onSuccess(fact);
    }
}
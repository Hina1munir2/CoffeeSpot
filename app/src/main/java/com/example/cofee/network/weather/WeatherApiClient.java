package com.example.coffee.network.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.example.coffee.utils.Constants;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApiClient {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = Constants.WEATHER_API_KEY;
    private static final String DEFAULT_CITY = "London";
    private Random random;

    private WeatherApiService weatherApiService;

    public interface WeatherCallback {
        void onSuccess(double temperature, String condition);
        void onFailure(String error);
    }

    public WeatherApiClient() {
        this.random = new Random();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApiService = retrofit.create(WeatherApiService.class);
    }

    public void getCurrentWeather(WeatherCallback callback) {
        // Try real API first
        if (!API_KEY.equals("demo_key_12345")) {
            Call<WeatherResponse> call = weatherApiService.getCurrentWeather(
                    DEFAULT_CITY,
                    API_KEY,
                    "metric"
            );

            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse weatherResponse = response.body();
                        double temperature = weatherResponse.getMain().getTemp();
                        String condition = "";

                        if (weatherResponse.getWeather() != null && weatherResponse.getWeather().length > 0) {
                            condition = weatherResponse.getWeather()[0].getMain();
                        }

                        callback.onSuccess(temperature, condition);
                    } else {
                        // Fallback to dummy data
                        provideDummyWeather(callback);
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    // Fallback to dummy data
                    provideDummyWeather(callback);
                }
            });
        } else {
            // Use dummy data for demo
            provideDummyWeather(callback);
        }
    }

    private void provideDummyWeather(WeatherCallback callback) {
        // Generate random temperature between 10°C and 30°C
        double temperature = 10 + random.nextDouble() * 20;

        // Pick random weather condition
        String condition = Constants.WEATHER_CONDITIONS[random.nextInt(Constants.WEATHER_CONDITIONS.length)];

        callback.onSuccess(temperature, condition);
    }

    public String getCoffeeRecommendation(double temperature) {
        if (temperature < 15) {
            return "Recommendation: Hot Mocha or Cappuccino";
        } else if (temperature < 25) {
            return "Recommendation: Iced Latte or Americano";
        } else {
            return "Recommendation: Cold Brew or Iced Coffee";
        }
    }
}
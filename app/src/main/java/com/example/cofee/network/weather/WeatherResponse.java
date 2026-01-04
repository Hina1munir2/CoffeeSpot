package com.example.coffee.network.weather;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;
    
    @SerializedName("weather")
    private Weather[] weather;
    
    @SerializedName("name")
    private String cityName;

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public String getCityName() {
        return cityName;
    }

    public class Main {
        @SerializedName("temp")
        private double temp;
        
        @SerializedName("feels_like")
        private double feelsLike;
        
        @SerializedName("temp_min")
        private double tempMin;
        
        @SerializedName("temp_max")
        private double tempMax;
        
        @SerializedName("pressure")
        private int pressure;
        
        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }
        
        public double getTempCelsius() {
            return temp - 273.15;
        }
    }

    public class Weather {
        @SerializedName("id")
        private int id;
        
        @SerializedName("main")
        private String main;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("icon")
        private String icon;

        public String getMain() {
            return main;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
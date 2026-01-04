package com.example.coffee.network.facts;

import com.google.gson.annotations.SerializedName;

public class CoffeeFact {
    @SerializedName("text")
    private String text;
    
    @SerializedName("source")
    private String source;
    
    public String getText() {
        return text;
    }
    
    public String getSource() {
        return source;
    }
}
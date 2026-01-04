package com.example.coffee.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CoffeeShopSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_LOGIN_TYPE = "loginType";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void createLoginSession(String userId, String name, String email, String loginType) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_LOGIN_TYPE, loginType);
        editor.commit();
    }
    
    public void setRememberMe(boolean remember) {
        editor.putBoolean(KEY_REMEMBER_ME, remember);
        editor.commit();
    }
    
    public boolean isRememberMe() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }
    
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }
    
    public String getLoginType() {
        return pref.getString(KEY_LOGIN_TYPE, "email");
    }
    
    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.commit();
    }
    
    public void updateUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit();
    }
    
    public void clearSession() {
        editor.clear();
        editor.commit();
    }
    
    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_LOGIN_TYPE);
        editor.commit();
    }
    
    public void setLoginType(String type) {
        editor.putString(KEY_LOGIN_TYPE, type);
        editor.commit();
    }
    
    public void setLastSyncTime(String key, long time) {
        editor.putLong(key, time);
        editor.commit();
    }
    
    public long getLastSyncTime(String key) {
        return pref.getLong(key, 0);
    }
    
    public void setCartSyncRequired(boolean required) {
        editor.putBoolean("cart_sync_required", required);
        editor.commit();
    }
    
    public boolean isCartSyncRequired() {
        return pref.getBoolean("cart_sync_required", false);
    }
    
    public void setOrdersSyncRequired(boolean required) {
        editor.putBoolean("orders_sync_required", required);
        editor.commit();
    }
    
    public boolean isOrdersSyncRequired() {
        return pref.getBoolean("orders_sync_required", false);
    }
    
    public void setLikedSyncRequired(boolean required) {
        editor.putBoolean("liked_sync_required", required);
        editor.commit();
    }
    
    public boolean isLikedSyncRequired() {
        return pref.getBoolean("liked_sync_required", false);
    }
    
    public void setAppFirstLaunch(boolean firstLaunch) {
        editor.putBoolean("first_launch", firstLaunch);
        editor.commit();
    }
    
    public boolean isAppFirstLaunch() {
        return pref.getBoolean("first_launch", true);
    }
}
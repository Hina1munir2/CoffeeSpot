package com.example.coffee.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.coffee.models.Product;
import com.example.coffee.models.CartItem;
import com.example.coffee.models.Order;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "coffee_shop.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_LIKED_ITEMS = "liked_items";
    private static final String TABLE_SESSION = "session";

    private SQLiteDatabase database;

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id TEXT,"
                + "name TEXT,"
                + "email TEXT,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "description TEXT,"
                + "price REAL,"
                + "image_url TEXT,"
                + "category TEXT,"
                + "last_updated DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + "id TEXT PRIMARY KEY,"
                + "product_id TEXT,"
                + "product_name TEXT,"
                + "price REAL,"
                + "quantity INTEGER,"
                + "image_url TEXT,"
                + "added_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + "id TEXT PRIMARY KEY,"
                + "user_id TEXT,"
                + "full_name TEXT,"
                + "address TEXT,"
                + "city TEXT,"
                + "zip_code TEXT,"
                + "phone TEXT,"
                + "payment_method TEXT,"
                + "subtotal REAL,"
                + "tax REAL,"
                + "delivery_fee REAL,"
                + "total REAL,"
                + "status TEXT,"
                + "order_date DATETIME,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_LIKED_ITEMS_TABLE = "CREATE TABLE " + TABLE_LIKED_ITEMS + "("
                + "id TEXT PRIMARY KEY,"
                + "product_id TEXT,"
                + "product_name TEXT,"
                + "description TEXT,"
                + "price REAL,"
                + "image_url TEXT,"
                + "category TEXT,"
                + "liked_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String CREATE_SESSION_TABLE = "CREATE TABLE " + TABLE_SESSION + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id TEXT,"
                + "email TEXT,"
                + "is_logged_in INTEGER DEFAULT 0,"
                + "login_type TEXT,"
                + "last_login DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
        db.execSQL(CREATE_LIKED_ITEMS_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKED_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        onCreate(db);
    }

    private synchronized SQLiteDatabase getReadableDatabaseInstance() {
        if (database == null || !database.isOpen()) {
            database = getReadableDatabase();
        }
        return database;
    }

    private synchronized SQLiteDatabase getWritableDatabaseInstance() {
        if (database == null || !database.isOpen()) {
            database = getWritableDatabase();
        }
        return database;
    }

    public void saveUserSession(String email, String userId) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        db.execSQL("DELETE FROM " + TABLE_SESSION);

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("email", email);
        values.put("is_logged_in", 1);
        values.put("login_type", "email");

        db.insert(TABLE_SESSION, null, values);
    }

    public void saveUserProfile(String name, String email) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);

        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            values.put("user_id", currentUserId);

            String query = "SELECT * FROM " + TABLE_USERS + " WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});

            if (cursor.getCount() > 0) {
                db.update(TABLE_USERS, values, "email = ?", new String[]{email});
            } else {
                db.insert(TABLE_USERS, null, values);
            }
            cursor.close();
        }
    }

    public String getCurrentUserId() {
        SQLiteDatabase db = getReadableDatabaseInstance();
        String userId = null;

        String query = "SELECT user_id FROM " + TABLE_SESSION + " WHERE is_logged_in = 1 LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            userId = cursor.getString(0);
        }

        cursor.close();
        return userId;
    }

    public String getUserName() {
        SQLiteDatabase db = getReadableDatabaseInstance();
        String name = null;

        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            String query = "SELECT name FROM " + TABLE_USERS + " WHERE user_id = ? LIMIT 1";
            Cursor cursor = db.rawQuery(query, new String[]{currentUserId});

            if (cursor.moveToFirst()) {
                name = cursor.getString(0);
            }

            cursor.close();
        }

        return name;
    }

    public String getUserEmail() {
        SQLiteDatabase db = getReadableDatabaseInstance();
        String email = null;

        String query = "SELECT email FROM " + TABLE_SESSION + " WHERE is_logged_in = 1 LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }

        cursor.close();
        return email;
    }

    public void saveProducts(List<Product> products) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        for (Product product : products) {
            ContentValues values = new ContentValues();
            values.put("id", product.getId());
            values.put("name", product.getName());
            values.put("description", product.getDescription());
            values.put("price", product.getPrice());
            values.put("image_url", product.getImageUrl());
            values.put("category", product.getCategory());

            db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public List<Product> getCachedProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();

        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY name";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getString(0));
            product.setName(cursor.getString(1));
            product.setDescription(cursor.getString(2));
            product.setPrice(cursor.getDouble(3));
            product.setImageUrl(cursor.getString(4));
            product.setCategory(cursor.getString(5));
            products.add(product);
        }

        cursor.close();
        return products;
    }

    public void addToCart(CartItem cartItem) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("id", cartItem.getId());
        values.put("product_id", cartItem.getProductId());
        values.put("product_name", cartItem.getProductName());
        values.put("price", cartItem.getPrice());
        values.put("quantity", cartItem.getQuantity());
        values.put("image_url", cartItem.getImageUrl());

        db.insertWithOnConflict(TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();

        String query = "SELECT * FROM " + TABLE_CART + " ORDER BY added_at DESC";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            CartItem item = new CartItem();
            item.setId(cursor.getString(0));
            item.setProductId(cursor.getString(1));
            item.setProductName(cursor.getString(2));
            item.setPrice(cursor.getDouble(3));
            item.setQuantity(cursor.getInt(4));
            item.setImageUrl(cursor.getString(5));
            cartItems.add(item);
        }

        cursor.close();
        return cartItems;
    }

    public void updateCartItemQuantity(String itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        db.update(TABLE_CART, values, "id = ?", new String[]{itemId});
    }

    public void removeFromCart(String itemId) {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_CART, "id = ?", new String[]{itemId});
    }

    public void clearCart() {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_CART, null, null);
    }

    public int getCartItemCount() {
        SQLiteDatabase db = getReadableDatabaseInstance();

        String query = "SELECT SUM(quantity) FROM " + TABLE_CART;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public void saveOrder(Order order) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("id", order.getId());
        values.put("user_id", order.getUserId());
        values.put("full_name", order.getFullName());
        values.put("address", order.getAddress());
        values.put("city", order.getCity());
        values.put("zip_code", order.getZipCode());
        values.put("phone", order.getPhone());
        values.put("payment_method", order.getPaymentMethod());
        values.put("subtotal", order.getSubtotal());
        values.put("tax", order.getTax());
        values.put("delivery_fee", order.getDeliveryFee());
        values.put("total", order.getTotal());
        values.put("status", order.getStatus());
        values.put("order_date", order.getOrderDate().getTime());

        db.insert(TABLE_ORDERS, null, values);
    }

    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();

        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return orders;
        }

        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE user_id = ? ORDER BY order_date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{currentUserId});

        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setId(cursor.getString(0));
            order.setUserId(cursor.getString(1));
            order.setFullName(cursor.getString(2));
            order.setAddress(cursor.getString(3));
            order.setCity(cursor.getString(4));
            order.setZipCode(cursor.getString(5));
            order.setPhone(cursor.getString(6));
            order.setPaymentMethod(cursor.getString(7));
            order.setSubtotal(cursor.getDouble(8));
            order.setTax(cursor.getDouble(9));
            order.setDeliveryFee(cursor.getDouble(10));
            order.setTotal(cursor.getDouble(11));
            order.setStatus(cursor.getString(12));
            order.setOrderDate(new Date(cursor.getLong(13)));
            orders.add(order);
        }

        cursor.close();
        return orders;
    }

    public int getOrdersCount() {
        SQLiteDatabase db = getReadableDatabaseInstance();

        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM " + TABLE_ORDERS + " WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUserId});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public void updateOrderStatus(String orderId, String status) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("status", status);

        db.update(TABLE_ORDERS, values, "id = ?", new String[]{orderId});
    }

    public void addToLiked(Product product) {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("id", product.getId());
        values.put("product_id", product.getId());
        values.put("product_name", product.getName());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("category", product.getCategory());

        db.insertWithOnConflict(TABLE_LIKED_ITEMS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Product> getLikedItems() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();

        String query = "SELECT * FROM " + TABLE_LIKED_ITEMS + " ORDER BY liked_at DESC";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getString(0));
            product.setName(cursor.getString(2));
            product.setDescription(cursor.getString(3));
            product.setPrice(cursor.getDouble(4));
            product.setImageUrl(cursor.getString(5));
            product.setCategory(cursor.getString(6));
            products.add(product);
        }

        cursor.close();
        return products;
    }

    public boolean isLiked(String productId) {
        SQLiteDatabase db = getReadableDatabaseInstance();

        String query = "SELECT COUNT(*) FROM " + TABLE_LIKED_ITEMS + " WHERE product_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{productId});

        boolean isLiked = false;
        if (cursor.moveToFirst()) {
            isLiked = cursor.getInt(0) > 0;
        }

        cursor.close();
        return isLiked;
    }

    public void removeFromLiked(String productId) {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_LIKED_ITEMS, "product_id = ?", new String[]{productId});
    }

    public void clearAllLiked() {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_LIKED_ITEMS, null, null);
    }

    public void syncLikedItems(List<Product> products) {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_LIKED_ITEMS, null, null);

        for (Product product : products) {
            addToLiked(product);
        }
    }

    public void syncOrders(List<Order> orders) {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_ORDERS, null, null);

        for (Order order : orders) {
            saveOrder(order);
        }
    }

    public void clearUserSession() {
        SQLiteDatabase db = getWritableDatabaseInstance();

        ContentValues values = new ContentValues();
        values.put("is_logged_in", 0);

        db.update(TABLE_SESSION, values, null, null);
    }

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabaseInstance();
        db.delete(TABLE_CART, null, null);
        db.delete(TABLE_LIKED_ITEMS, null, null);
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
            database = null;
        }
    }
}
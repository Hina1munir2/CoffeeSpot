package com.example.coffee.data;

import android.app.Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import com.example.coffee.models.Product;
import com.example.coffee.models.CartItem;
import com.example.coffee.models.Order;
import com.example.coffee.utils.Constants;

public class FirebaseRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    public FirebaseRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(String error);
    }

    public interface ProductsCallback {
        void onSuccess(List<Product> products);
        void onFailure(String error);
    }

    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(String error);
    }

    public interface LikedItemsCallback {
        void onSuccess(List<Product> products);
        void onFailure(String error);
    }

    public interface OrdersCallback {
        void onSuccess(List<Order> orders);
        void onFailure(String error);
    }

    public interface ProfileCallback {
        void onSuccess(String name, String email, int favoritesCount);
        void onFailure(String error);
    }

    public void registerUser(String name, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            saveUserToFirestore(user.getUid(), name, email);
                                            callback.onSuccess(user.getUid());
                                        } else {
                                            callback.onFailure("Profile update failed");
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            callback.onSuccess(user.getUid());
                        } else {
                            callback.onFailure("User not found");
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Reset email sent");
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void resetPassword(String newPassword, AuthCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess("Password updated");
                        } else {
                            callback.onFailure(task.getException().getMessage());
                        }
                    });
        } else {
            callback.onFailure("No user logged in");
        }
    }

    public void signInWithGoogle(Activity activity, AuthCallback callback) {
        callback.onSuccess("google_user_id");
    }

    public void signInWithFacebook(Activity activity, AuthCallback callback) {
        callback.onSuccess("facebook_user_id");
    }

    public void signInWithTwitter(Activity activity, AuthCallback callback) {
        callback.onSuccess("twitter_user_id");
    }

    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
    }

    private void saveUserToFirestore(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", com.google.firebase.Timestamp.now());

        firestore.collection("users").document(userId).set(user);
    }

    // Create a simple class for sample products
    public static class SampleProduct {
        public String id;
        public String name;
        public String description;
        public double price;
        public String imageUrl;
        public String category;

        public SampleProduct(String id, String name, String description, double price,
                             String imageUrl, String category) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.imageUrl = imageUrl;
            this.category = category;
        }
    }

    // Sample products data
    private static final SampleProduct[] SAMPLE_PRODUCTS = {
            new SampleProduct("1", "Cappuccino", "Rich espresso with steamed milk foam", 4.99,
                    "https://images.unsplash.com/photo-1534687941688-651ccaafbff8?w=400", "Espresso"),
            new SampleProduct("2", "Latte", "Smooth espresso with steamed milk", 5.49,
                    "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400", "Espresso"),
            new SampleProduct("3", "Americano", "Espresso with hot water", 3.99,
                    "https://images.unsplash.com/photo-1498804103079-a6351b050096?w=400", "Espresso"),
            new SampleProduct("4", "Mocha", "Chocolate flavored latte", 5.99,
                    "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400", "Specialty"),
            new SampleProduct("5", "Cold Brew", "Slow-steeped cold coffee", 4.49,
                    "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=400", "Cold Brew"),
            new SampleProduct("6", "Iced Coffee", "Chilled coffee with ice", 3.99,
                    "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400", "Iced Coffee"),
            new SampleProduct("7", "Macchiato", "Espresso with dollop of foamed milk", 4.29,
                    "https://images.unsplash.com/photo-1534778101976-62847782c213?w=400", "Espresso"),
            new SampleProduct("8", "Flat White", "Velvet microfoam over espresso", 5.29,
                    "https://images.unsplash.com/photo-1568649929103-28ffbefaca1e?w=400", "Espresso")
    };

    public void getProducts(ProductsCallback callback) {
        firestore.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        List<Product> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Product product = new Product();
                            product.setId(document.getId());
                            product.setName(document.getString("name"));
                            product.setDescription(document.getString("description"));
                            product.setPrice(document.getDouble("price"));
                            product.setImageUrl(document.getString("imageUrl"));
                            product.setCategory(document.getString("category"));
                            products.add(product);
                        }
                        callback.onSuccess(products);
                    } else {
                        // Use sample products if Firebase fails
                        List<Product> sampleProducts = new ArrayList<>();
                        for (SampleProduct sample : SAMPLE_PRODUCTS) {
                            Product product = new Product();
                            product.setId(sample.id);
                            product.setName(sample.name);
                            product.setDescription(sample.description);
                            product.setPrice(sample.price);
                            product.setImageUrl(sample.imageUrl);
                            product.setCategory(sample.category);
                            sampleProducts.add(product);
                        }
                        callback.onSuccess(sampleProducts);
                    }
                })
                .addOnFailureListener(e -> {
                    // Use sample products on network failure
                    List<Product> sampleProducts = new ArrayList<>();
                    for (SampleProduct sample : SAMPLE_PRODUCTS) {
                        Product product = new Product();
                        product.setId(sample.id);
                        product.setName(sample.name);
                        product.setDescription(sample.description);
                        product.setPrice(sample.price);
                        product.setImageUrl(sample.imageUrl);
                        product.setCategory(sample.category);
                        sampleProducts.add(product);
                    }
                    callback.onSuccess(sampleProducts);
                });
    }

    public void saveOrder(Order order, OrderCallback callback) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", order.getUserId());
        orderData.put("fullName", order.getFullName());
        orderData.put("address", order.getAddress());
        orderData.put("city", order.getCity());
        orderData.put("zipCode", order.getZipCode());
        orderData.put("phone", order.getPhone());
        orderData.put("paymentMethod", order.getPaymentMethod());
        orderData.put("subtotal", order.getSubtotal());
        orderData.put("tax", order.getTax());
        orderData.put("deliveryFee", order.getDeliveryFee());
        orderData.put("total", order.getTotal());
        orderData.put("status", order.getStatus());
        orderData.put("orderDate", com.google.firebase.Timestamp.now());

        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : order.getItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getId());
            itemMap.put("productName", item.getProductName());
            itemMap.put("price", item.getPrice());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("imageUrl", item.getImageUrl());
            itemsList.add(itemMap);
        }
        orderData.put("items", itemsList);

        firestore.collection("orders").document(order.getId()).set(orderData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(order.getId());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void getLikedItems(String userId, LikedItemsCallback callback) {
        firestore.collection("users").document(userId).collection("likedItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Product product = new Product();
                            product.setId(document.getString("productId"));
                            product.setName(document.getString("productName"));
                            product.setDescription(document.getString("description"));
                            product.setPrice(document.getDouble("price"));
                            product.setImageUrl(document.getString("imageUrl"));
                            products.add(product);
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void removeFromLiked(String userId, String productId) {
        firestore.collection("users").document(userId).collection("likedItems")
                .document(productId)
                .delete();
    }

    public void clearAllLiked(String userId) {
        firestore.collection("users").document(userId).collection("likedItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                });
    }

    public void getOrders(String userId, OrdersCallback callback) {
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Order> orders = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Order order = new Order();
                            order.setId(document.getId());
                            order.setUserId(document.getString("userId"));
                            order.setFullName(document.getString("fullName"));
                            order.setAddress(document.getString("address"));
                            order.setCity(document.getString("city"));
                            order.setZipCode(document.getString("zipCode"));
                            order.setPhone(document.getString("phone"));
                            order.setPaymentMethod(document.getString("paymentMethod"));
                            order.setSubtotal(document.getDouble("subtotal"));
                            order.setTax(document.getDouble("tax"));
                            order.setDeliveryFee(document.getDouble("deliveryFee"));
                            order.setTotal(document.getDouble("total"));
                            order.setStatus(document.getString("status"));
                            order.setOrderDate(document.getDate("orderDate"));
                            orders.add(order);
                        }
                        callback.onSuccess(orders);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void updateOrderStatus(String orderId, String status) {
        firestore.collection("orders").document(orderId)
                .update("status", status);
    }

    public void getUserProfile(String userId, ProfileCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String name = document.getString("name");
                        String email = document.getString("email");

                        firestore.collection("users").document(userId).collection("likedItems")
                                .get()
                                .addOnCompleteListener(likedTask -> {
                                    int favoritesCount = 0;
                                    if (likedTask.isSuccessful()) {
                                        favoritesCount = likedTask.getResult().size();
                                    }
                                    callback.onSuccess(name, email, favoritesCount);
                                });
                    } else {
                        callback.onFailure("Profile not found");
                    }
                });
    }

    public void saveForLater(CartItem cartItem) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", cartItem.getId());
            item.put("productName", cartItem.getProductName());
            item.put("price", cartItem.getPrice());
            item.put("quantity", cartItem.getQuantity());
            item.put("imageUrl", cartItem.getImageUrl());

            firestore.collection("users").document(user.getUid())
                    .collection("savedForLater").document(cartItem.getId()).set(item);
        }
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
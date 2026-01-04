package com.example.coffee.network.maps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

public class MapUtils {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public static void openMapForNavigation(Context context, String address) {
        String uri = "geo:0,0?q=" + Uri.encode(address + " coffee shop");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No map app available", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openMapForTracking(Activity activity) {
        // Open generic map for coffee shops
        String uri = "geo:0,0?q=coffee+shops+near+me";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "No map app available", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openMapForBranch(Context context, double lat, double lon, String branchName) {
        String uri = "geo:" + lat + "," + lon + "?q=" + Uri.encode(branchName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // Fallback to browser
            String url = "https://maps.google.com/maps?q=" + lat + "," + lon;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    public static String getStaticMapUrl(double lat, double lon, int width, int height) {
        // Return a placeholder image URL for demo
        return "https://via.placeholder.com/" + width + "x" + height + "/5D4037/D7CCC8?text=Coffee+Shop+Location";
    }
}
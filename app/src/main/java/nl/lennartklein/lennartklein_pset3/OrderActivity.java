package nl.lennartklein.lennartklein_pset3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    Context mContext;
    TextView totalPriceTv;
    BottomNavigationView navigation;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Set globals
        mContext = this;
        totalPriceTv = findViewById(R.id.total_price);

        // Set bottom menu
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mainNavigationListener);
        navigation.getMenu().findItem(R.id.navigation_order).setChecked(true);

        // Fill the list with ordered dishes
        lv = findViewById(R.id.list_dishes);
        getOrder(mContext, API.dishes);

        // Initiate CLEAR-button
        Button buttonClear = findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new ClearOrder());

        // Initiate send-button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new SendOrder());
    }

    // Fetch dishes from an API and adapt them to a list
    public static void getOrder(Context mContext, String uri) {
        // Get global views
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        ListView lv = rootView.findViewById(R.id.list_dishes);

        // Fetch order from local storage
        SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);

        // Fetch available dishes from server
        StringRequest request = new StringRequest(uri,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        // Get globals
                        float totalPrice = 0;
                        int totalAmount = prefs.getInt("totalAmount", 0);

                        // Get JSON object and array of data
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("items");
                        ArrayList<HashMap<String, String>> dishes = new ArrayList<>();

                        // Loop through every item in the ArrayList
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject dishObject = responseArray.getJSONObject(i);

                            // Get every value from JSON object
                            String dID = dishObject.getString("id");
                            String dName = dishObject.getString("name");
                            String dImage = dishObject.getString("image_url");
                            String dPrice = dishObject.getString("price");

                            // Print the dish if it is in My Order
                            int amount = prefs.getInt(dID, 0);

                            if (amount > 0) {
                                // Build an HashMap object for this item (key-value pairs)
                                HashMap<String, String> dish = new HashMap<>();
                                dish.put("id", dID);
                                dish.put("name", dName);
                                dish.put("image_url", dImage);
                                dish.put("price", dPrice);
                                dish.put("amount", Integer.toString(amount));

                                // Add this dish to the ArrayList
                                dishes.add(dish);
                            }

                            // Show the totals to the user
                            totalAmount += amount;
                            totalPrice += Float.valueOf(dPrice) * amount;
                            SetTotalPrice(mContext, totalPrice);
                        }
                        // Use an adapter and the ArrayList to feed the list
                        OrderAdapter adapter = new OrderAdapter(mContext, R.layout.list_item_order, dishes);
                        lv.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }

    // Bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mainNavigationListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(mContext, MainActivity.class));
                    break;
                case R.id.navigation_order:
                    startActivity(new Intent(mContext, OrderActivity.class));
                    break;
                case R.id.navigation_about:
                    startActivity(new Intent(mContext, AboutActivity.class));
                    break;
            }
            finish();
            return false;
        }
    };

    public static void SetTotalPrice(Context mContext, float totalPrice) {
        // Get global & views
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView totalPriceTv = rootView.findViewById(R.id.total_price);

        // Get current total price
        SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("totalPrice", totalPrice);
        editor.apply();

        // Set value of total price
        String sTotalPrice = "$ " + String.valueOf(totalPrice) + "0";
        totalPriceTv.setText(sTotalPrice);
    }

    // Click listener for clearing the order
    private class ClearOrder implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Reload the My Order page
            getOrder(mContext, API.dishes);
        }
    }

    // Click listener for sending the order
    private class SendOrder implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);

            // Create JSONobject to post
            JSONObject postData = null;
            JSONArray dishes = new JSONArray();

            // Loop through all preferences
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue().toString();

                // Get preferences with only a numeric key
                if (key.matches("[0-9]+") && key.length() <= 2) {
                    for (int i = 0; i < Integer.valueOf(value); i++) {
                        dishes.put(key);
                    }
                }
            }

            try {
                if (dishes.length() > 0) {
                    postData = new JSONObject("{\"menuIds\":" + dishes.toString() + "}");
                    // Post the data
                    JsonObjectRequest request = new JsonObjectRequest(API.order, postData,
                            response -> {
                                // Get response
                                String preparationTime = null;
                                try {
                                    preparationTime = response.getString("preparation_time");

                                    if (preparationTime != null) {

                                        // Show dialog
                                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                                        alert.setTitle("We placed your order");
                                        alert.setMessage("Thank you for placing your order. You can pick it up in about " + preparationTime + " minutes.");
                                        alert.setPositiveButton("Ok", (dialog, i) -> {
                                            // Clear the order
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.clear();
                                            editor.apply();

                                            // Reload the My Order page
                                            getOrder(mContext, API.dishes);
                                        });
                                        alert.show();
                                    } else {
                                        Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show();
                                }

                                //Toast.makeText(mContext, String.valueOf(response), Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show();
                            }
                    );
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    queue.add(request);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Back button on device
    @Override
    public void onBackPressed() {
        // Go back to main activity by finishing this
        finish();
    }

}

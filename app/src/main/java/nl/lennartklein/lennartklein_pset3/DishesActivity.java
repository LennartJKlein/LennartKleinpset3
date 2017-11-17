package nl.lennartklein.lennartklein_pset3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DishesActivity extends AppCompatActivity {

    Context mContext;
    BottomNavigationView navigation;
    ListView lv;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes);

        // Set globals & menu's
        mContext = this;

        // Set bottom menu
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mainNavigationListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

        // Fetch chosen category from intent
        Intent i = getIntent();
        category = i.getStringExtra("CATEGORY");

        // Set category in title
        TextView heading = findViewById(R.id.previous_title);
        String niceCategory = Character.toUpperCase(category.charAt(0)) + category.substring(1);
        heading.setText(niceCategory);
        heading.setOnClickListener(new GoToMain());

        // Fill the list with dishes
        lv = findViewById(R.id.list_dishes);
        getDishes(API.dishes);

        // Initiate action bar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Fetch dishes from an API and adapt them to a list
    public void getDishes(String uri) {
        StringRequest request = new StringRequest(uri,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        // Get JSON object and array of data
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("items");
                        ArrayList<HashMap<String, String>> dishes = new ArrayList<>();

                        // Loop through every item in the ArrayList
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject dishObject = responseArray.getJSONObject(i);

                            // Only fetch the dishes from this category
                            if (Objects.equals(dishObject.getString("category"), category)) {

                                // Get every value from JSON object
                                String dID = dishObject.getString("id");
                                String dName = dishObject.getString("name");
                                String dDesc = dishObject.getString("description");
                                String dImage = dishObject.getString("image_url");
                                String dPrice = dishObject.getString("price");

                                // Build an HashMap object for this item (key-value pairs)
                                HashMap<String, String> dish = new HashMap<>();
                                dish.put("id", dID);
                                dish.put("name", dName);
                                dish.put("description", dDesc);
                                dish.put("image_url", dImage);
                                dish.put("price", dPrice);

                                // Add this dish to the ArrayList
                                dishes.add(dish);
                            }
                        }
                        // Use an adapter and the ArrayList to feed the list
                        DishAdapter adapter = new DishAdapter(mContext, R.layout.list_item_dish, dishes);
                        lv.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(this);
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

    // Click listener for extra back buttons
    private class GoToMain implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Go back to main activity by finishing this
            finish();
        }
    }

    // Back button in top menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Go back to main activity by finishing this
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Back button on device
    @Override
    public void onBackPressed() {
        // Go back to main activity by finishing this
        finish();
    }

}

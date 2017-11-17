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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    Context mContext;
    BottomNavigationView navigation;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set globals
        mContext = this;

        // Set bottom menu
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mainNavigationListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

        // Fill the list with categories and make it clickable
        lv = findViewById(R.id.list_categories);
        getCategories(API.categories);
        lv.setOnItemClickListener(new CategoryClickListener());
    }

    // Fetch categories from an API and adapt them to a list
    public void getCategories(String uri) {
        StringRequest request = new StringRequest(uri,
                response -> {
                    JSONObject responseObject;
                    JSONArray responseArray;

                    try {
                        // Get JSON object and array of data
                        responseObject = new JSONObject(response);
                        responseArray = responseObject.getJSONArray("categories");
                        ArrayList<String> categories = new ArrayList<>();

                        // Add every found string to the ArrayList
                        for (int i = 0; i < responseArray.length(); i++) {
                            String category = responseArray.getString(i);
                            categories.add(category);
                        }
                        // Use an adapter and the ArrayList to feed the list
                        CategoryAdapter adapter = new CategoryAdapter(mContext, R.layout.list_item_category, categories);
                        lv.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mContext, R.string.error_network, Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Click listener for list of categories
    private class CategoryClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String category = ((TextView) view.findViewById(R.id.category_name)).getText().toString();
            startDishes(category);
        }
    }

    // Navigator for dishesActivity
    public void startDishes(String category) {
        // Create intent for the next activity
        Intent i = new Intent(this, DishesActivity.class);
        i.putExtra("CATEGORY", category);
        startActivity(i);
    }

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
            return false;
        }
    };

}

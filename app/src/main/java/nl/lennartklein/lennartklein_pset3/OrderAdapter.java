package nl.lennartklein.lennartklein_pset3;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static nl.lennartklein.lennartklein_pset3.OrderActivity.getOrder;

public class OrderAdapter extends ArrayAdapter<HashMap<String, String>> {

    private int resourceID;
    private Context mContext;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();

    TextView dID;
    ImageView dImage;
    TextView dName;
    TextView dPrice;
    TextView dAmount;
    ImageButton dRemove;
    ImageButton dAdd;

    OrderAdapter(Context context, int listResourceID, ArrayList<HashMap<String, String>> list) {
        super(context, 0, list);
        data = list;
        mContext = context;
        resourceID = listResourceID;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> dish = data.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        }

        // Lookup view for data population
        dID = convertView.findViewById(R.id.dish_id);
        dImage = convertView.findViewById(R.id.dish_image);
        dName = convertView.findViewById(R.id.dish_name);
        dPrice = convertView.findViewById(R.id.dish_price);
        dAmount = convertView.findViewById(R.id.dish_amount);
        dRemove = convertView.findViewById(R.id.button_minus);
        dAdd = convertView.findViewById(R.id.button_plus);

        // Populate the data into the template view using the data object
        dID.setText(dish.get("id"));
        new DownloadImage(dImage).execute(dish.get("image_url"));
        dName.setText(dish.get("name"));
        String sPrice = "$ " + String.valueOf(dish.get("price")) + 0;
        dPrice.setText(sPrice);
        dAmount.setText(dish.get("amount"));
        dRemove.setOnClickListener(new RemoveFromOrder());
        dAdd.setOnClickListener(new AddToOrder());

        // Return the completed view to render on screen
        return convertView;
    }

    private class AddToOrder implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Get all the siblings of this button
            ViewGroup row = (ViewGroup) v.getParent().getParent();
            int amount = 0;

            // Fetch the first TextView and use its text
            for (int i = 0; i < row.getChildCount(); i++) {
                View view = row.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String dish_id = textView.getText().toString();

                    // Get current amount from prefs
                    SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);
                    amount = prefs.getInt(dish_id, 0);

                    // Put new value in the saved order-preference
                    amount += 1;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(dish_id, amount);
                    editor.apply();

                    break;
                }
            }

            // Reset the My Order page
            getOrder(mContext, API.dishes);
        }
    }

    private class RemoveFromOrder implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Get all the siblings of this button
            ViewGroup row = (ViewGroup) v.getParent().getParent();

            // Fetch the first TextView and use its text
            for (int i = 0; i < row.getChildCount(); i++) {
                View view = row.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String dish_id = textView.getText().toString();

                    // Get current amount from prefs
                    SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);
                    int amount = prefs.getInt(dish_id, 0);

                    // Put new value in the saved order-preference
                    amount -= 1;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(dish_id, amount);
                    editor.apply();

                    // Show the new amount of this dish
                    dAmount.setText(String.valueOf(amount));
                    break;
                }
            }

            // Reset the My Order page
            getOrder(mContext, API.dishes);
        }
    }

}
package nl.lennartklein.lennartklein_pset3;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class DishAdapter extends ArrayAdapter<HashMap<String, String>> {

    private int resourceID;
    private Context mContext;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();

    DishAdapter(Context context, int listResourceID, ArrayList<HashMap<String, String>> list) {
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
        TextView dID = convertView.findViewById(R.id.dish_id);
        ImageView dImage = convertView.findViewById(R.id.dish_image);
        TextView dName = convertView.findViewById(R.id.dish_name);
        TextView dDesc = convertView.findViewById(R.id.dish_description);
        TextView dPrice = convertView.findViewById(R.id.dish_price);
        ImageButton dOrder = convertView.findViewById(R.id.dish_order);

        // Populate the data into the template view using the data object
        dID.setText(dish.get("id"));
        new DownloadImage(dImage).execute(dish.get("image_url"));
        dName.setText(dish.get("name"));
        dDesc.setText(dish.get("description"));
        String sPrice = "$ " + dish.get("price") + "0";
        dPrice.setText(sPrice);
        dOrder.setOnClickListener(new AddToOrder());

        // Return the completed view to render on screen
        return convertView;
    }

    private class AddToOrder implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Get all the siblings of this button
            ViewGroup row = (ViewGroup) v.getParent();

            // Fetch the first TextView and use its text
            for (int i = 0; i < row.getChildCount(); i++) {
                View view = row.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String dish_id = textView.getText().toString();
                    SharedPreferences prefs = mContext.getSharedPreferences("order", MODE_PRIVATE);
                    int amount = prefs.getInt(dish_id, 0);

                    // Put new value in the saved order-preference
                    amount += 1;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(dish_id, amount);
                    editor.apply();

                    // Give feedback
                    String success = mContext.getString(R.string.dish_ordered);
                    Toast.makeText(mContext, success, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

}
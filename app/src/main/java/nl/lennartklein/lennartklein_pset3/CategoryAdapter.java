package nl.lennartklein.lennartklein_pset3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<String> {

    private int resourceID;
    private ArrayList<String> data;

    CategoryAdapter(Context context, int listResourceID, ArrayList<String> list) {
        super(context, 0, list);
        data = list;
        resourceID = listResourceID;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        String item = data.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
        }
        // Lookup view for data population
        TextView categoryName = convertView.findViewById(R.id.category_name);

        // Populate the data into the template view using the data object
        categoryName.setText(item);

        // Return the completed view to render on screen
        return convertView;
    }
}
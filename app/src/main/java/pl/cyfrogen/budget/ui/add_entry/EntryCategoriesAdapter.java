package pl.cyfrogen.budget.ui.add_entry;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.models.Category;

public class EntryCategoriesAdapter extends ArrayAdapter<String> {

    private final List<Category> items;
    private final Context context;

    public EntryCategoriesAdapter(Context context, int resource,
                                  List objects) {
        super(context, resource, 0, objects);
        this.context = context;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.new_entry_type_spinner_row, parent, false);

        TextView textView = view.findViewById(R.id.item_category);
        ImageView iconImageView = view.findViewById(R.id.icon_imageview);

        iconImageView.setImageResource(items.get(position).getIconResourceID());
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(items.get(position).getIconColor()));
        textView.setText(items.get(position).getCategoryVisibleName(context));

        return view;
    }

    public int getItemIndex(String categoryID) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getCategoryID().equals(categoryID)) return i;
        }
        return -1;
    }
}


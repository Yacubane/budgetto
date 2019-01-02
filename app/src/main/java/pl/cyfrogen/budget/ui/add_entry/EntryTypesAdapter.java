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

public class EntryTypesAdapter extends ArrayAdapter<String> {
    private final List<EntryTypeListViewModel> items;
    private final Context context;

    public EntryTypesAdapter(Context context, int resource,
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

        iconImageView.setImageResource(items.get(position).iconID);
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(items.get(position).color));
        textView.setText(items.get(position).name);

        return view;
    }
}
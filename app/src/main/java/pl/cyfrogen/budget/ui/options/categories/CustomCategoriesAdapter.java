package pl.cyfrogen.budget.ui.options.categories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.util.CurrencyHelper;

public class CustomCategoriesAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private final Activity activity;
    Context context;

    public CustomCategoriesAdapter(Activity activity, List<Category> data, Context context) {
        super(context, R.layout.favorites_listview_row, data);
        this.context = context;
        this.activity = activity;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.custom_categories_listview_row, parent, false);

        Category category = getItem(position);

        TextView categoryNameTextView = listItem.findViewById(R.id.category_textview);
        ImageView iconImageView = listItem.findViewById(R.id.icon_imageview);

        iconImageView.setImageResource(category.getIconResourceID());
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));

        categoryNameTextView.setText(category.getCategoryVisibleName(getContext()));


        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditCustomCategoryActivity.class);
                intent.putExtra("category-id", category.getCategoryID());
                intent.putExtra("category-name", category.getCategoryVisibleName(getContext()));
                intent.putExtra("category-color", category.getIconColor());
                activity.startActivity(intent);
            }
        });
        return listItem;
    }


}

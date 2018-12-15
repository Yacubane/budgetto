package pl.cyfrogen.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemCategoriesListViewAdapter extends ArrayAdapter<CategoryModelHome> implements View.OnClickListener{

    private ArrayList<CategoryModelHome> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        public TextView txtSum;
        TextView txtName;
    }

    public ItemCategoriesListViewAdapter(ArrayList<CategoryModelHome> data, Context context) {
        super(context, R.layout.favorites_listview_row, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CategoryModelHome dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.favorites_listview_row, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.item_category);
            viewHolder.txtSum = convertView.findViewById(R.id.item_sum);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getCategoryName());
        viewHolder.txtSum.setText(dataModel.getCurrency().formatString(dataModel.getMoney()));
        return convertView;
    }
}

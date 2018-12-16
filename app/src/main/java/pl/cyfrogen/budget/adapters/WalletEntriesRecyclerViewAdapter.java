package pl.cyfrogen.budget.adapters;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.firebase.models.WalletEntry;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.models.Currency;
import pl.cyfrogen.budget.models.DefaultCategories;

public class WalletEntriesRecyclerViewAdapter extends RecyclerView.Adapter<WalletEntriesRecyclerViewAdapter.WalletEntryHolder> {

    private final String uid;
    private final FragmentActivity fragmentActivity;
    private ListDataSet<WalletEntry> walletEntries;


    public WalletEntriesRecyclerViewAdapter(FragmentActivity fragmentActivity, String uid) {
        this.fragmentActivity = fragmentActivity;
        this.uid = uid;

        WalletEntriesViewModelFactory.Model myViewModel = WalletEntriesViewModelFactory.getModel(uid, fragmentActivity);
        myViewModel.observe(fragmentActivity, new Observer<ListDataSet<WalletEntry>>() {

            @Override
            public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {
                walletEntries = walletEntryListDataSet;
                walletEntryListDataSet.notifyRecycler(WalletEntriesRecyclerViewAdapter.this);

            }
        });

    }

    @Override
    public WalletEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragmentActivity);
        View view = inflater.inflate(R.layout.history_listview_row, parent, false);
        return new WalletEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(WalletEntryHolder holder, int position) {
        String id = walletEntries.getIDList().get(position);
        WalletEntry walletEntry = walletEntries.getList().get(position);
        Category category = DefaultCategories.searchCategory(walletEntry.categoryID);
        holder.iconImageView.setImageResource(category.getIconResourceID());
        holder.iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));
        holder.categoryTextView.setText(category.getCategoryVisibleName(fragmentActivity));
        holder.nameTextView.setText(walletEntry.name);

        Date date = new Date(-walletEntry.timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        holder.dateTextView.setText(dateFormat.format(date));
        holder.moneyTextView.setText(Currency.DEFAULT.formatString(walletEntry.balanceDifference));
        holder.moneyTextView.setTextColor(ContextCompat.getColor(fragmentActivity,
                walletEntry.balanceDifference < 0 ? R.color.primary_text_expense : R.color.primary_text_income));

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDeleteDialog(id, uid, fragmentActivity);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (walletEntries == null) return 0;
        return walletEntries.getList().size();
    }

    private void createDeleteDialog(String id, String uid, Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Do you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("wallet-entries").child(uid).child("default").child(id).removeValue();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create().show();

    }

    public class WalletEntryHolder extends RecyclerView.ViewHolder {

        private final TextView dateTextView;
        private final TextView moneyTextView;
        private final TextView categoryTextView;
        private final TextView nameTextView;
        private final ImageView iconImageView;
        public View view;

        public WalletEntryHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            moneyTextView = itemView.findViewById(R.id.money_textview);
            categoryTextView = itemView.findViewById(R.id.category_textview);
            nameTextView = itemView.findViewById(R.id.name_textview);
            dateTextView = itemView.findViewById(R.id.date_textview);
            iconImageView = itemView.findViewById(R.id.icon_imageview);

        }
    }


}
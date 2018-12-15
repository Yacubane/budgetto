package pl.cyfrogen.budget;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.Operation;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class HistoryFragment extends BaseFragment {
    public static final CharSequence TITLE = "History";
    private RecyclerView historyRecyclerView;
    private WalletEntriesAdapter historyRecyclerViewAdapter;

    public static HistoryFragment newInstance() {

        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


        DatabaseReference walletEntriesReference = FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(getUid()).child("default"); //todo wallet-id is always default
        historyRecyclerViewAdapter = new WalletEntriesAdapter(getActivity().getApplicationContext(), walletEntriesReference);
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);
    }

    private static class WalletEntryHolder extends RecyclerView.ViewHolder {

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

    private class WalletEntriesAdapter extends RecyclerView.Adapter<WalletEntryHolder> {

        private Context context;
        private ListDataSet<WalletEntry> walletEntries;


        public WalletEntriesAdapter(final Context context, DatabaseReference ref) {
            this.context = context;

            WalletEntriesViewModelFactory.Model myViewModel = WalletEntriesViewModelFactory.getModel(getUid(), getActivity());
            myViewModel.observe(getActivity(), new Observer<ListDataSet<WalletEntry>>() {

                @Override
                public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {
                    walletEntries = walletEntryListDataSet;
                    if (walletEntryListDataSet.getLastOperation() == Operation.ITEM_INSERTED)
                        historyRecyclerView.smoothScrollToPosition(0);
                    walletEntryListDataSet.notifyRecycler(WalletEntriesAdapter.this);

                }
            });

        }

        @Override
        public WalletEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.history_listview_row, parent, false);
            return new WalletEntryHolder(view);
        }

        @Override
        public void onBindViewHolder(WalletEntryHolder holder, int position) {
            String id = walletEntries.getIDList().get(position);
            WalletEntry walletEntry = walletEntries.getList().get(position);
            CategoryModel categoryModel = DefaultCategoryModels.searchCategory(walletEntry.categoryID);
            holder.iconImageView.setImageResource(categoryModel.getIconResourceID());
            holder.iconImageView.setBackgroundTintList(ColorStateList.valueOf(categoryModel.getIconColor()));
            holder.categoryTextView.setText(categoryModel.getCategoryVisibleName(getContext()));
            holder.nameTextView.setText(walletEntry.name);

            Date date = new Date(-walletEntry.timestamp);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            holder.dateTextView.setText(dateFormat.format(date));
            holder.moneyTextView.setText(Currency.DEFAULT.formatString(walletEntry.balanceDifference));
            holder.moneyTextView.setTextColor(ContextCompat.getColor(context,
                    walletEntry.balanceDifference < 0 ? R.color.primary_text_expense : R.color.primary_text_income));

            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createDeleteDialog(id);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            if (walletEntries == null) return 0;
            return walletEntries.getList().size();
        }


    }

    private void createDeleteDialog(String id) {
        new AlertDialog.Builder(getContext())
                .setMessage("Do you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("wallet-entries").child(getUid()).child("default").child(id).removeValue();
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

}

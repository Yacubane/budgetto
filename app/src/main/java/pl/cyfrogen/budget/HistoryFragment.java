package pl.cyfrogen.budget;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModel;
import pl.cyfrogen.budget.firebase.WalletEntriesViewModelFactory;
import pl.cyfrogen.budget.models.WalletEntry;

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
                .child("wallet-entries").child(getUid()).child("default"); //todo wallet-id is always 1
        historyRecyclerViewAdapter = new WalletEntriesAdapter(getActivity().getApplicationContext(), walletEntriesReference);
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);
    }

    private static class WalletEntryHolder extends RecyclerView.ViewHolder {

        public TextView moneyTextView;
        public TextView categoryTextView;
        public TextView nameTextView;

        public WalletEntryHolder(View itemView) {
            super(itemView);

            moneyTextView = itemView.findViewById(R.id.money_textview);
            categoryTextView = itemView.findViewById(R.id.category_textview);
            nameTextView = itemView.findViewById(R.id.name_textview);

        }
    }

    private class WalletEntriesAdapter extends RecyclerView.Adapter<WalletEntryHolder> {

        private Context context;
        private List<WalletEntry> walletEntries;


        public WalletEntriesAdapter(final Context context, DatabaseReference ref) {
            this.context = context;

            WalletEntriesViewModel myViewModel = ViewModelProviders.of(getActivity(), new WalletEntriesViewModelFactory(getUid())).get(WalletEntriesViewModel.class);
            myViewModel.getDataSnapshotLiveData().observe(getActivity(), new Observer<ListDataSet<WalletEntry>>() {

                @Override
                public void onChanged(@Nullable ListDataSet<WalletEntry> walletEntryListDataSet) {
                    walletEntries = walletEntryListDataSet.getList();
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
            WalletEntry walletEntry = walletEntries.get(position);
            holder.categoryTextView.setText(walletEntry.categoryName);
            holder.nameTextView.setText(walletEntry.name);
            holder.moneyTextView.setText(walletEntry.balanceDifference+"");
        }

        @Override
        public int getItemCount() {
            if(walletEntries == null) return 0;
            return walletEntries.size();
        }



    }

}

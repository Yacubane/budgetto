package pl.cyfrogen.budget;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static class WalletEntriesAdapter extends RecyclerView.Adapter<WalletEntryHolder> {

        private Context context;
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;

        private List<String> walletEntriesIds = new ArrayList<>();
        private List<WalletEntry> walletEntries = new ArrayList<>();

        public WalletEntriesAdapter(final Context context, DatabaseReference ref) {
            this.context = context;
            databaseReference = ref;


            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    WalletEntry item = dataSnapshot.getValue(WalletEntry.class);

                    String key = dataSnapshot.getKey();

                    if (!walletEntriesIds.contains(key)) {
                        int insertedPosition;
                        if (previousChildName == null) {
                            walletEntries.add(0, item);
                            walletEntriesIds.add(0, key);
                            insertedPosition = 0;
                        } else {
                            int previousIndex = walletEntriesIds.indexOf(previousChildName);
                            int nextIndex = previousIndex + 1;
                            if (nextIndex == walletEntries.size()) {
                                walletEntries.add(item);
                                walletEntriesIds.add(key);
                            } else {
                                walletEntries.add(nextIndex, item);
                                walletEntriesIds.add(nextIndex, key);
                            }
                            insertedPosition = nextIndex;
                        }
                        notifyItemInserted(insertedPosition);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    WalletEntry item = dataSnapshot.getValue(WalletEntry.class);
                    String key = dataSnapshot.getKey();

                    if (walletEntriesIds.contains(key)) {
                        int index = walletEntries.indexOf(key);
                        WalletEntry oldItem = walletEntries.get(index);
                        walletEntries.set(index, item);
                        notifyItemChanged(index);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();

                    if (walletEntriesIds.contains(key)) {
                        int index = walletEntriesIds.indexOf(key);
                        WalletEntry item = walletEntries.get(index);

                        walletEntriesIds.remove(index);
                        walletEntries.remove(index);

                        notifyItemRemoved(index);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    WalletEntry item = dataSnapshot.getValue(WalletEntry.class);
                    String key = dataSnapshot.getKey();

                    int index = walletEntriesIds.indexOf(key);
                    walletEntries.remove(index);
                    walletEntriesIds.remove(index);
                    int newPosition;
                    if (previousChildName == null) {
                        walletEntries.add(0, item);
                        walletEntriesIds.add(0, key);
                        newPosition = 0;
                    } else {
                        int previousIndex = walletEntriesIds.indexOf(previousChildName);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == walletEntries.size()) {
                            walletEntries.add(item);
                            walletEntriesIds.add(key);
                        } else {
                            walletEntries.add(nextIndex, item);
                            walletEntriesIds.add(nextIndex, key);
                        }
                        newPosition = nextIndex;
                    }
                    notifyItemMoved(index, newPosition);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.orderByChild("timestamp").addChildEventListener(childEventListener);
            this.childEventListener = childEventListener;
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
            return walletEntries.size();
        }

        public void cleanupListener() {
            if (childEventListener != null) {
                databaseReference.removeEventListener(childEventListener);
            }
        }

    }

}

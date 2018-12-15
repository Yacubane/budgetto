package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class FirebaseQueryLiveData extends LiveData<ListDataSet<WalletEntry>> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private Query query;
    private MyValueEventListener listener = new MyValueEventListener();
    private final String uid;
    private String walletID = "default";

    ListDataSet<WalletEntry> walletEntriesLiveDataSet = new ListDataSet<>();
    private List<WalletEntry> walletEntries = walletEntriesLiveDataSet.getList();
    private ArrayList<String> walletEntriesIds = walletEntriesLiveDataSet.getIDList();

    public FirebaseQueryLiveData(String uid) {
        this.uid = uid;
        this.query = createQuery();
    }

    public Query createQuery() {
        return FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(uid).child(walletID).orderByChild("timestamp");
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addChildEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
    }

    public void setWalletID(String walletID) {
        this.walletID = walletID;
        query.removeEventListener(listener);
        walletEntriesLiveDataSet.clear();
        setValue(walletEntriesLiveDataSet);
        this.query = createQuery();
        listener = new MyValueEventListener();
        query.addChildEventListener(listener);
    }

    private class MyValueEventListener implements ChildEventListener {

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

                //notifyItemInserted(insertedPosition);
                walletEntriesLiveDataSet.setItemInserted(insertedPosition);
                setValue(walletEntriesLiveDataSet);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            WalletEntry item = dataSnapshot.getValue(WalletEntry.class);
            String key = dataSnapshot.getKey();

            if (walletEntriesIds.contains(key)) {
                int index = walletEntriesIds.indexOf(key);
                WalletEntry oldItem = walletEntries.get(index);
                walletEntries.set(index, item);
                //notifyItemChanged(index);
                walletEntriesLiveDataSet.setItemChanged(index);
                setValue(walletEntriesLiveDataSet);

            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (walletEntriesIds.contains(key)) {
                int index = walletEntriesIds.indexOf(key);
                if(index == -1) return;
                WalletEntry item = walletEntries.get(index);

                walletEntriesIds.remove(index);
                walletEntries.remove(index);

                //notifyItemRemoved(index);
                walletEntriesLiveDataSet.setItemRemoved(index);
                setValue(walletEntriesLiveDataSet);
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
            //notifyItemMoved(index, newPosition);
            walletEntriesLiveDataSet.setItemMoved(index, newPosition);
            setValue(walletEntriesLiveDataSet);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
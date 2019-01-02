package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryLiveDataSet<T> extends LiveData<FirebaseElement<ListDataSet<T>>> {
    private final Class<T> genericTypeClass;
    private Query query;
    private ValueEventListener listener;
    ListDataSet<T> liveDataSet;
    private List<T> liveDataSetEntries;
    private ArrayList<String> liveDataSetIndexes;

    public FirebaseQueryLiveDataSet(Class<T> genericTypeClass, Query query) {
        listener = new ValueEventListener();
        liveDataSet = new ListDataSet<>();
        liveDataSetEntries = liveDataSet.list;
        liveDataSetIndexes = liveDataSet.getIDList();
        setValue(new FirebaseElement<>(liveDataSet));
        this.genericTypeClass = genericTypeClass;
        this.query = query;
    }

    public void setQuery(Query query) {
        removeListener();
        liveDataSet.clear();
        setValue(new FirebaseElement<>(liveDataSet));
        this.query = query;
        setListener();
    }


    private void removeListener() {
        query.removeEventListener(listener);
    }

    private void setListener() {
        query.addChildEventListener(listener);
    }

    @Override
    protected void onActive() {
        setListener();
    }


    @Override
    protected void onInactive() {
        removeListener();
        liveDataSet.clear();
    }



    private class ValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            T item = dataSnapshot.getValue(genericTypeClass);

            String key = dataSnapshot.getKey();
            if (!liveDataSetIndexes.contains(key)) {
                int insertedPosition;
                if (previousChildName == null) {
                    liveDataSetEntries.add(0, item);
                    liveDataSetIndexes.add(0, key);
                    insertedPosition = 0;
                } else {
                    int previousIndex = liveDataSetIndexes.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == liveDataSetEntries.size()) {
                        liveDataSetEntries.add(item);
                        liveDataSetIndexes.add(key);
                    } else {
                        liveDataSetEntries.add(nextIndex, item);
                        liveDataSetIndexes.add(nextIndex, key);
                    }
                    insertedPosition = nextIndex;
                }

                liveDataSet.setItemInserted(insertedPosition);
                setValue(new FirebaseElement<>(liveDataSet));
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            T item = dataSnapshot.getValue(genericTypeClass);
            String key = dataSnapshot.getKey();

            if (liveDataSetIndexes.contains(key)) {
                int index = liveDataSetIndexes.indexOf(key);
                T oldItem = liveDataSetEntries.get(index);
                liveDataSetEntries.set(index, item);
                liveDataSet.setItemChanged(index);
                setValue(new FirebaseElement<>(liveDataSet));

            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (liveDataSetIndexes.contains(key)) {
                int index = liveDataSetIndexes.indexOf(key);
                if (index == -1) return;
                T item = liveDataSetEntries.get(index);

                liveDataSetIndexes.remove(index);
                liveDataSetEntries.remove(index);
                liveDataSet.setItemRemoved(index);
                setValue(new FirebaseElement<>(liveDataSet));
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            T item = dataSnapshot.getValue(genericTypeClass);
            String key = dataSnapshot.getKey();

            int index = liveDataSetIndexes.indexOf(key);
            if(index == -1) return;
            liveDataSetEntries.remove(index);
            liveDataSetIndexes.remove(index);
            int newPosition;
            if (previousChildName == null) {
                liveDataSetEntries.add(0, item);
                liveDataSetIndexes.add(0, key);
                newPosition = 0;
            } else {
                int previousIndex = liveDataSetIndexes.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == liveDataSetEntries.size()) {
                    liveDataSetEntries.add(item);
                    liveDataSetIndexes.add(key);
                } else {
                    liveDataSetEntries.add(nextIndex, item);
                    liveDataSetIndexes.add(nextIndex, key);
                }
                newPosition = nextIndex;
            }
            liveDataSet.setItemMoved(index, newPosition);
            setValue(new FirebaseElement<>(liveDataSet));


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            setValue(new FirebaseElement<>(databaseError));
            removeListener();
            setListener();
        }
    }
}
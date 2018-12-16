package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveDataElement<T> extends LiveData<FirebaseElement<T>> {
    private Query query;
    private ValueEventListener listener;


    public FirebaseQueryLiveDataElement(Class<T> genericTypeClass, Query query) {
        setValue(null);
        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                T item = dataSnapshot.getValue(genericTypeClass);
                setValue(new FirebaseElement<>(item));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setValue(new FirebaseElement<>(databaseError));
                removeListener();
                setListener();
            }
        };
        this.query = query;
    }

    private void removeListener() {
        query.removeEventListener(listener);
    }

    private void setListener() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onActive() {
        setListener();
    }


    @Override
    protected void onInactive() {
        removeListener();
    }

}
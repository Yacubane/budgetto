package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryLiveDataElement<T> extends LiveData<T> {
    private final Class<T> genericTypeClass;
    private Query query;
    private ValueEventListener listener;


    public FirebaseQueryLiveDataElement(Class<T> genericTypeClass, Query query) {
        setValue(null);
        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                T item = dataSnapshot.getValue(genericTypeClass);
                setValue(item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        this.genericTypeClass = genericTypeClass;
        this.query = query;
    }


    @Override
    protected void onActive() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(listener);
    }

}
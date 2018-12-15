package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class WalletEntriesViewModel extends ViewModel {

    private final FirebaseQueryLiveData<WalletEntry> liveData;

    public WalletEntriesViewModel(String uid) {
        liveData = new FirebaseQueryLiveData<>(WalletEntry.class, FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(uid).child("default").orderByChild("timestamp"));
    }

    @NonNull
    public LiveData<ListDataSet<WalletEntry>> getDataSnapshotLiveData() {
        return liveData;
    }
}
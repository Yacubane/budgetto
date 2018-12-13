package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import pl.cyfrogen.budget.models.WalletEntry;

public class WalletEntriesViewModel extends ViewModel {

    private final FirebaseQueryLiveData liveData;

    public WalletEntriesViewModel(String uid) {
        liveData = new FirebaseQueryLiveData(uid);
    }

    @NonNull
    public LiveData<ListDataSet<WalletEntry>> getDataSnapshotLiveData() {
        return liveData;
    }
}
package pl.cyfrogen.budget.firebase.viewmodels;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.FirebaseQueryLiveDataSet;
import pl.cyfrogen.budget.firebase.ListDataSet;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class WalletEntriesBaseViewModel extends ViewModel {
    protected final FirebaseQueryLiveDataSet<WalletEntry> liveData;
    protected final String uid;

    public WalletEntriesBaseViewModel(String uid, Query query) {
        this.uid=uid;
        liveData = new FirebaseQueryLiveDataSet<>(WalletEntry.class, query);
    }

    public void observe(LifecycleOwner owner, FirebaseObserver<FirebaseElement<ListDataSet<WalletEntry>>> observer) {
        observer.onChanged(liveData.getValue());
        liveData.observe(owner, new Observer<FirebaseElement<ListDataSet<WalletEntry>>>() {
            @Override
            public void onChanged(@Nullable FirebaseElement<ListDataSet<WalletEntry>> element) {
                if(element != null) observer.onChanged(element);
            }
        });
    }

    public void removeObserver(Observer<FirebaseElement<ListDataSet<WalletEntry>>> observer) {
        liveData.removeObserver(observer);
    }


}

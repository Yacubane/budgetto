package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class WalletEntriesViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    private WalletEntriesViewModelFactory(String uid) {
        this.uid = uid;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);
    }

    public static Model getModel(String uid, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new WalletEntriesViewModelFactory(uid)).get(Model.class);
    }

    public static class Model extends ViewModel {
        private final FirebaseQueryLiveDataSet<WalletEntry> liveData;

        public Model(String uid) {
            liveData = new FirebaseQueryLiveDataSet<>(WalletEntry.class, FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp"));
        }

        public void observe(LifecycleOwner owner, Observer<ListDataSet<WalletEntry>> observer) {
            observer.onChanged(liveData.getValue());
            liveData.observe(owner, observer);
        }

        public void removeObserver(Observer<ListDataSet<WalletEntry>> observer) {
            liveData.removeObserver(observer);
        }
    }
}
package pl.cyfrogen.budget.firebase.viewmodel_factories;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import pl.cyfrogen.budget.firebase.viewmodels.WalletEntriesBaseViewModel;

public class TopWalletEntriesViewModelFactory implements ViewModelProvider.Factory {
    private final Calendar endDate;
    private final Calendar startDate;
    private String uid;

    TopWalletEntriesViewModelFactory(String uid, Calendar startDate, Calendar endDate) {
        this.uid = uid;
        this.startDate=startDate;
        this.endDate=endDate;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);
    }

    public static Model getModel(String uid, Calendar startDate, Calendar endDate, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new TopWalletEntriesViewModelFactory(uid, startDate, endDate)).get(Model.class);
    }

    public static class Model extends WalletEntriesBaseViewModel {

        public Model(String uid) {
            super(uid, FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp"));
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp")
                    .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
        }
    }
}
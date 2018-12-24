package pl.cyfrogen.budget.firebase.viewmodel_factories;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

import pl.cyfrogen.budget.firebase.viewmodels.WalletEntriesBaseViewModel;

public class WalletEntriesHistoryViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    WalletEntriesHistoryViewModelFactory(String uid) {
        this.uid = uid;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);
    }

    public static Model getModel(String uid, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new WalletEntriesHistoryViewModelFactory(uid)).get(Model.class);
    }

    public static class Model extends WalletEntriesBaseViewModel {

        private Calendar endDate;
        private Calendar startDate;

        public Model(String uid) {
            super(uid, getDefaultQuery(uid));
        }

        private static Query getDefaultQuery(String uid) {
            return FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp").limitToFirst(500);
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            if (startDate != null && endDate != null) {
                liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                        .child("wallet-entries").child(uid).child("default").orderByChild("timestamp")
                        .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
            } else {
                liveData.setQuery(getDefaultQuery(uid));
            }
        }

        public boolean hasDateSet() {
            return startDate != null && endDate != null;
        }

        public Calendar getStartDate() {
            return startDate;
        }

        public Calendar getEndDate() {
            return endDate;
        }
    }
}
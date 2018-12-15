package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.Objects;

public class WalletEntriesViewModelFactory implements ViewModelProvider.Factory {
    private String uid;
    public WalletEntriesViewModelFactory(String uid) {
        this.uid = uid;

    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WalletEntriesViewModel(uid);
    }
}
package pl.cyfrogen.budget.firebase;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.Objects;

public class WalletEntriesViewModelFactory implements ViewModelProvider.Factory {
    private String mParam;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletEntriesViewModelFactory that = (WalletEntriesViewModelFactory) o;
        return Objects.equals(mParam, that.mParam);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mParam);
    }

    public WalletEntriesViewModelFactory(String param) {
        mParam = param;

    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WalletEntriesViewModel(mParam);
    }
}
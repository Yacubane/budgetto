package pl.cyfrogen.budget.firebase.viewmodel_factories;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.FirebaseQueryLiveDataElement;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.viewmodels.UserProfileBaseViewModel;

public class UserProfileViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    private UserProfileViewModelFactory(String uid) {
        this.uid = uid;

    }

    public static void saveModel(String uid, User user) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).setValue(user);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserProfileBaseViewModel(uid);
    }

    public static UserProfileBaseViewModel getModel(String uid, FragmentActivity activity) {
        return ViewModelProviders.of(activity, new UserProfileViewModelFactory(uid)).get(UserProfileBaseViewModel.class);
    }


}
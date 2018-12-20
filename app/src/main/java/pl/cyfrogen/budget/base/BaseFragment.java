package pl.cyfrogen.budget.base;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class BaseFragment extends Fragment {
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}

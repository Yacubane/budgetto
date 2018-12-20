package pl.cyfrogen.budget.base;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}

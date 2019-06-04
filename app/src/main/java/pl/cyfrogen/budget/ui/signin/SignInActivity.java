package pl.cyfrogen.budget.ui.signin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import pl.cyfrogen.budget.Links;
import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.ui.main.MainActivity;


public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView errorTextView;
    private SignInButton signInButton;
    private View progressView;
    private TextView privacyPolicyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        progressView = findViewById(R.id.progress_view);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                signInButton.setEnabled(false);
                errorTextView.setText("");
            }
        });

        privacyPolicyTextView = findViewById(R.id.privacy_policy_text_view);
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By signing in, you are indicating that you have read and agree to the ");
        spanTxt.append("privacy policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Links.PRIVACY_POLICY_LINK));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - "privacy policy".length(), spanTxt.length(), 0);
        privacyPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicyTextView.setText(spanTxt, TextView.BufferType.SPANNABLE);

        errorTextView = findViewById(R.id.error_textview);
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressView();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        showProgressView();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                hideProgressView();
                loginError("Google sign in failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            loginError("Firebase auth failed.");
                            hideProgressView();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            progressView.setVisibility(View.GONE);
            return;
        }
        showProgressView();
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    runTransaction(userReference);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loginError("Firebase fetch user data failed.");
                hideProgressView();
            }
        });


    }

    private void loginError(String text) {
        errorTextView.setText(text);
        signInButton.setEnabled(true);
    }

    private void runTransaction(DatabaseReference userReference) {
        showProgressView();
        userReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    mutableData.setValue(new User());
                    return Transaction.success(mutableData);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {
                if (committed) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    errorTextView.setText("Firebase create user transaction failed.");
                    hideProgressView();
                }
            }
        });
    }

    private void showProgressView() {
        progressView.setVisibility(View.VISIBLE);

    }

    private void hideProgressView() {
        progressView.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

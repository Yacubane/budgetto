package pl.cyfrogen.budget;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pl.cyfrogen.budget.libraries.GUIUtils;
import pl.cyfrogen.budget.libraries.OnRevealAnimationListener;
import pl.cyfrogen.budget.models.EntryCategory;
import pl.cyfrogen.budget.models.EntryType;
import pl.cyfrogen.budget.models.WalletEntry;

public class AddBudgetEntryActivity extends BaseActivity {

    private Button addEntryButton;
    private Spinner selectCategorySpinner;
    private EditText selectTypeSpinner;
    private Button chooseDateButton;
    private Calendar choosedDate;
    private DatabaseReference mDatabase;
    private View mFab;
    private View mRlContainer;
    private View mRlContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_entry);

        mFab = findViewById(R.id.activity_contact_fab);
        mRlContainer = findViewById(R.id.root_layout);
        mRlContainer2 = findViewById(R.id.root_layout2);
        mRlContainer2.setVisibility(View.INVISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        choosedDate = Calendar.getInstance();

        // addEntryButton = findViewById(R.id.add_budget_entry);
        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectTypeSpinner = findViewById(R.id.select_type_edittext);
        addEntryButton = findViewById(R.id.add_entry_button);
        chooseDateButton = findViewById(R.id.choose_date_button);
        setChooseDateButtonText();
        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });


        final ArrayList<EntryCategory> entryCategories = new ArrayList<>();
        final ArrayList<String> entryCategoriesIds = new ArrayList<>();

        final ArrayAdapter<EntryCategory> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, entryCategories);
        adapter.add(new EntryCategory("Test")); //todo remove
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(adapter);


        mDatabase.child("entry-categories").child(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EntryCategory comment = dataSnapshot.getValue(EntryCategory.class);
                entryCategoriesIds.add(dataSnapshot.getKey());
                entryCategories.add(comment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EntryCategory newComment = dataSnapshot.getValue(EntryCategory.class);
                String commentKey = dataSnapshot.getKey();
                int commentIndex = entryCategoriesIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    entryCategories.set(commentIndex, newComment);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
                int commentIndex = entryCategoriesIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    entryCategoriesIds.remove(commentIndex);
                    entryCategories.remove(commentIndex);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //todo save event listener and remove it on finish

        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWallet(100, choosedDate.getTime(),
                        selectCategorySpinner.getSelectedItem().toString(), selectTypeSpinner.getText().toString());
                //todo change balance difference
            }
        });


    }

    private void setChooseDateButtonText() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDateButton.setText(dataFormatter.format(choosedDate.getTime()));
    }

    public void addToWallet(long balanceDifference, Date entryDate, String entryCategory, String entryType) {
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").push().setValue(new WalletEntry(entryCategory, entryType, entryDate.getTime(), balanceDifference));
        finish();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        choosedDate.set(year, monthOfYear, dayOfMonth);
                        setChooseDateButtonText();

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(mRlContainer);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }



    private void animateRevealShow(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        GUIUtils.animateRevealShow(this, mRlContainer, mFab.getWidth() / 2, R.color.colorPrimary,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(600);
            mRlContainer2.startAnimation(animation);
            mRlContainer2.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(200);
        mRlContainer2.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                mRlContainer2.setVisibility(View.INVISIBLE);
                GUIUtils.animateRevealHide(AddBudgetEntryActivity.this, mRlContainer, R.color.colorPrimary, mFab.getWidth() / 2,
                        new OnRevealAnimationListener() {
                            @Override
                            public void onRevealHide() {
                                backPressed();

                            }

                            @Override
                            public void onRevealShow() {

                            }
                        });
            }
        });


    }



    private void backPressed() {
        super.onBackPressed();
    }

    private void setupExitAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementReturnTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

    }
}

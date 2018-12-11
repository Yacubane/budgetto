package pl.cyfrogen.budget;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddBudgetEntryActivity extends BaseActivity {

    private Button addEntryButton;
    private Spinner selectCategorySpinner;
    private Spinner selectTypeSpinner;
    private Button chooseDateButton;
    private Calendar choosedDate;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_entry);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        choosedDate = Calendar.getInstance();

        addEntryButton = findViewById(R.id.add_budget_entry);
        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectTypeSpinner = findViewById(R.id.select_type_spinner);
        chooseDateButton = findViewById(R.id.choose_date_button);
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDateButton.setText(dataFormatter.format(choosedDate.getTime()));
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

                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}

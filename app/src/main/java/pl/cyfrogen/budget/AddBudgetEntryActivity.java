package pl.cyfrogen.budget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.cyfrogen.budget.activityModels.CircullarRevealActivity;
import pl.cyfrogen.budget.firebase.models.EntryCategory;
import pl.cyfrogen.budget.firebase.models.WalletEntry;

public class AddBudgetEntryActivity extends CircullarRevealActivity {

    private Button addEntryButton;
    private Spinner selectCategorySpinner;
    private TextInputEditText selectNameEditText;
    private Calendar choosedDate;
    private DatabaseReference mDatabase;
    private TextInputEditText selectAmountEditText;
    private TextView chooseDayTextView;
    private TextView chooseTimeTextView;
    private Spinner selectTypeSpinner;

    public AddBudgetEntryActivity() {
        super(R.layout.activity_add_budget_entry, R.id.activity_contact_fab, R.id.root_layout, R.id.root_layout2);
    }

    @Override
    public void onInitialized(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        choosedDate = Calendar.getInstance();

        // addEntryButton = findViewById(R.id.add_budget_entry);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);
        Currency currency = Currency.DEFAULT;

        selectAmountEditText.setText(currency.formatString(0), TextView.BufferType.EDITABLE);
        selectAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    selectAmountEditText.removeTextChangedListener(this);


                    current = currency.formatString(Long.valueOf(getLongMoney(s)));
                    selectAmountEditText.setText(current);
                    selectAmountEditText.setSelection(current.length() -
                            (currency.isLeftFormatted() ? 0 : currency.getStringAddition().length()));

                    selectAmountEditText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectTypeSpinner = findViewById(R.id.select_type_spinner);
        addEntryButton = findViewById(R.id.add_entry_button);
        chooseTimeTextView = findViewById(R.id.choose_time_textview);
        chooseDayTextView = findViewById(R.id.choose_day_textview);

        EntryTypeArrayAdapter typeAdapter = new EntryTypeArrayAdapter(this,
                R.layout.new_entry_type_spinner_row, Arrays.asList(
                new EntryType("Expense", Color.parseColor("#ef5350"),
                        R.drawable.money_icon),
                new EntryType("Income", Color.parseColor("#66bb6a"),
                        R.drawable.money_icon)));

        selectTypeSpinner.setAdapter(typeAdapter);


        updateDate();
        chooseDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        chooseTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        final ArrayList<EntryCategory> entryCategories = new ArrayList<>();
        final ArrayList<String> entryCategoriesIds = new ArrayList<>();

        final List<CategoryModel> categoryModels = Arrays.asList(DefaultCategoryModels.getCategoryModels());


        EntryCategoryArrayAdapter categoryAdapter = new EntryCategoryArrayAdapter(this,
                R.layout.new_entry_type_spinner_row, categoryModels);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);


        mDatabase.child("entry-categories").child(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EntryCategory comment = dataSnapshot.getValue(EntryCategory.class);
                entryCategoriesIds.add(dataSnapshot.getKey());
                entryCategories.add(comment);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EntryCategory newComment = dataSnapshot.getValue(EntryCategory.class);
                String commentKey = dataSnapshot.getKey();
                int commentIndex = entryCategoriesIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    entryCategories.set(commentIndex, newComment);
                    categoryAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String commentKey = dataSnapshot.getKey();
                int commentIndex = entryCategoriesIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    entryCategoriesIds.remove(commentIndex);
                    entryCategories.remove(commentIndex);
                    categoryAdapter.notifyDataSetChanged();
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
                addToWallet(((selectTypeSpinner.getSelectedItemPosition() * 2) - 1) *
                                getLongMoney(selectAmountEditText.getText().toString()),
                        choosedDate.getTime(),
                        ((CategoryModel)selectCategorySpinner.getSelectedItem()).getCategoryID(),
                        selectNameEditText.getText().toString());
            }
        });


    }

    private long getLongMoney(CharSequence s) {
        String cleanString = s.toString().replaceAll("[^0-9]", "");
        return Long.valueOf(cleanString);

    }


    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDayTextView.setText(dataFormatter.format(choosedDate.getTime()));

        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("HH:mm");
        chooseTimeTextView.setText(dataFormatter2.format(choosedDate.getTime()));
    }

    public void addToWallet(long balanceDifference, Date entryDate, String entryCategory, String entryType) {
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").push().setValue(new WalletEntry(entryCategory, entryType, entryDate.getTime(), balanceDifference));
        finishWithAnimation();
    }

    private void pickTime() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                choosedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                choosedDate.set(Calendar.MINUTE, minute);
                updateDate();

            }
        }, choosedDate.get(Calendar.HOUR_OF_DAY), choosedDate.get(Calendar.MINUTE), true).show();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        choosedDate.set(year, monthOfYear, dayOfMonth);
                        updateDate();

                    }
                }, year, month, day).show();
    }

    public class EntryCategoryArrayAdapter extends ArrayAdapter<String> {

        private final List<CategoryModel> items;
        private final Context context;

        public EntryCategoryArrayAdapter(Context context, int resource,
                                         List objects) {
            super(context, resource, 0, objects);
            this.context = context;
            items = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        @Override
        public @NonNull
        View getView(int position, View convertView, ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        private View createItemView(int position, View convertView, ViewGroup parent) {
            final View view = LayoutInflater.from(context).inflate(R.layout.new_entry_type_spinner_row, parent, false);

            TextView textView = view.findViewById(R.id.item_category);
            ImageView iconImageView = view.findViewById(R.id.icon_imageview);

            iconImageView.setImageResource(items.get(position).getIconResourceID());
            iconImageView.setBackgroundTintList(ColorStateList.valueOf(items.get(position).getIconColor()));
            textView.setText(items.get(position).getCategoryVisibleName(context));

            return view;
        }
    }

    public class EntryTypeArrayAdapter extends ArrayAdapter<String> {

        private final List<EntryType> items;
        private final Context context;

        public EntryTypeArrayAdapter(Context context, int resource,
                                     List objects) {
            super(context, resource, 0, objects);
            this.context = context;
            items = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        @Override
        public @NonNull
        View getView(int position, View convertView, ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        private View createItemView(int position, View convertView, ViewGroup parent) {
            final View view = LayoutInflater.from(context).inflate(R.layout.new_entry_type_spinner_row, parent, false);

            TextView textView = view.findViewById(R.id.item_category);
            ImageView iconImageView = view.findViewById(R.id.icon_imageview);

            iconImageView.setImageResource(items.get(position).iconID);
            iconImageView.setBackgroundTintList(ColorStateList.valueOf(items.get(position).color));
            textView.setText(items.get(position).name);

            return view;
        }
    }


    private class EntryType {
        public final String name;
        public final int color;
        public final int iconID;

        public EntryType(String name, int color, int iconID) {
            this.name = name;
            this.color = color;
            this.iconID = iconID;
        }
    }
}

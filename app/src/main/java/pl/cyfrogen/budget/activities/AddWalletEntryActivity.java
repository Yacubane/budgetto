package pl.cyfrogen.budget.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.cyfrogen.budget.adapters.NewEntryCategoriesAdapter;
import pl.cyfrogen.budget.adapters.NewEntryTypesAdapter;
import pl.cyfrogen.budget.models.Category;
import pl.cyfrogen.budget.models.CurrencyHelper;
import pl.cyfrogen.budget.models.DefaultCategories;
import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.models.EntryCategory;
import pl.cyfrogen.budget.firebase.models.WalletEntry;
import pl.cyfrogen.budget.models.NewEntryTypeListViewModel;

public class AddWalletEntryActivity extends CircullarRevealActivity {

    private Button addEntryButton;
    private Spinner selectCategorySpinner;
    private TextInputEditText selectNameEditText;
    private Calendar choosedDate;
    private TextInputEditText selectAmountEditText;
    private TextView chooseDayTextView;
    private TextView chooseTimeTextView;
    private Spinner selectTypeSpinner;
    private CurrencyHelper currencyHelper;

    public AddWalletEntryActivity() {
        super(R.layout.activity_add_budget_entry, R.id.activity_contact_fab, R.id.root_layout, R.id.root_layout2);
    }

    @Override
    public void onInitialized(Bundle savedInstanceState) {
        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectTypeSpinner = findViewById(R.id.select_type_spinner);
        addEntryButton = findViewById(R.id.add_entry_button);
        chooseTimeTextView = findViewById(R.id.choose_time_textview);
        chooseDayTextView = findViewById(R.id.choose_day_textview);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);


        choosedDate = Calendar.getInstance();

        currencyHelper = CurrencyHelper.DEFAULT;
        setupAmountEditText();


        NewEntryTypesAdapter typeAdapter = new NewEntryTypesAdapter(this,
                R.layout.new_entry_type_spinner_row, Arrays.asList(
                new NewEntryTypeListViewModel("Expense", Color.parseColor("#ef5350"),
                        R.drawable.money_icon),
                new NewEntryTypeListViewModel("Income", Color.parseColor("#66bb6a"),
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

        final List<Category> categories = Arrays.asList(DefaultCategories.getCategories());
        NewEntryCategoriesAdapter categoryAdapter = new NewEntryCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWallet(((selectTypeSpinner.getSelectedItemPosition() * 2) - 1) *
                                convertAmountStringToLong(selectAmountEditText.getText().toString()),
                        choosedDate.getTime(),
                        ((Category) selectCategorySpinner.getSelectedItem()).getCategoryID(),
                        selectNameEditText.getText().toString());
            }
        });


    }

    private void setupAmountEditText() {
        selectAmountEditText.setText(currencyHelper.formatString(0), TextView.BufferType.EDITABLE);
        selectAmountEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().equals(current)) {
                    selectAmountEditText.removeTextChangedListener(this);
                    current = currencyHelper.formatString(convertAmountStringToLong(charSequence));
                    selectAmountEditText.setText(current);
                    selectAmountEditText.setSelection(current.length() -
                            (currencyHelper.isLeftFormatted() ? 0 : currencyHelper.getStringAddition().length()));

                    selectAmountEditText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private long convertAmountStringToLong(CharSequence s) {
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





}

package pl.cyfrogen.budget;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBudgetEntryActivity extends AppCompatActivity {

    private Button addEntryButton;
    private Spinner selectCategorySpinner;
    private Spinner selectTypeSpinner;
    private Button chooseDateButton;
    private Calendar choosedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_entry);

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

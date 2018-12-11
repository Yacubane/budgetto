package pl.cyfrogen.budget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

public class MainMenuActivity extends BaseActivity {

    private Button addBudgetEntryButton;
    private TextView actualBudgetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        addBudgetEntryButton = findViewById(R.id.add_budget_entry);
        addBudgetEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, AddBudgetEntryActivity.class));
            }
        });
        actualBudgetTextView = findViewById(R.id.actual_budget);

        FirebaseDatabase.getInstance().getReference().child("entry-categories")
                .child(getUid()).push().setValue(new EntryCategory("test"));


    }
}

package pl.cyfrogen.budget.ui.options.categories;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.base.BaseActivity;
import pl.cyfrogen.budget.exceptions.EmptyStringException;
import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.models.WalletEntryCategory;
import pl.cyfrogen.budget.firebase.viewmodel_factories.UserProfileViewModelFactory;

public class AddCustomCategoryActivity extends BaseActivity {

    private TextInputEditText selectNameEditText;
    private Button selectColorButton;
    private Button addCustomCategoryButton;
    private User user;
    private ImageView iconImageView;
    private int selectedColor = Color.parseColor("#000000");
    private TextInputLayout selectNameInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_category);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add custom category");

        UserProfileViewModelFactory.getModel(getUid(), this).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    AddCustomCategoryActivity.this.user = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });


    }

    private void dataUpdated() {
        if (user == null) return;
        iconImageView = findViewById(R.id.icon_imageview);
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        selectColorButton = findViewById(R.id.select_color_button);
        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker(AddCustomCategoryActivity.this,
                        (selectedColor >> 16) & 0xFF,
                        (selectedColor >> 8) & 0xFF,
                        (selectedColor >> 0) & 0xFF);
                colorPicker.show();

                Button okColor = colorPicker.findViewById(R.id.okColorButton);

                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedColor = colorPicker.getColor();
                        iconImageView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                        colorPicker.dismiss();
                    }
                });
            }
        });

        addCustomCategoryButton = findViewById(R.id.add_custom_category_button);
        addCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addCustomCategory(selectNameEditText.getText().toString(), "#" + Integer.toHexString(selectedColor));
                } catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                }


            }
        });
    }

    private void addCustomCategory(String categoryName, String categoryHtmlCode) throws EmptyStringException {
        if(categoryName == null || categoryName.length() == 0)
            throw new EmptyStringException("Entry name length should be > 0");

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("customCategories").push().setValue(
                new WalletEntryCategory(categoryName,  categoryHtmlCode));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}

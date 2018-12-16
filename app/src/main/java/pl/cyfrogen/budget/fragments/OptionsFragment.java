package pl.cyfrogen.budget.fragments;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.ArrayList;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.activities.SignInActivity;
import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.UserProfileViewModelFactory;
import pl.cyfrogen.budget.firebase.models.User;

public class OptionsFragment extends PreferenceFragmentCompat {
    public static final CharSequence TITLE = "Options";

    User user;

    ArrayList<Preference> preferences = new ArrayList<>();
    private Preference currencyPreference;

    public static OptionsFragment newInstance() {

        return new OptionsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);

        Field[] fields = R.string.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().startsWith("pref_key")) {
                try {
                    preferences.add(findPreference(getString((int) fields[i].get(null))));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Preference preference : preferences) {
            preference.setEnabled(false);
        }

        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {

            @Override
            public void onChanged(FirebaseElement<User> element) {
                if(!element.hasNoError()) return;
                OptionsFragment.this.user = element.getElement();
                dataUpdated();
            }
        });


        Preference myPref = findPreference("logout");
        myPref.setEnabled(false);
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
                return true;
            }
        });



    }

    private void dataUpdated() {
        for (Preference preference : preferences) {
            preference.setEnabled(true);
        }

        currencyPreference = findPreference(getString(R.string.pref_key_currency));
        currencyPreference.setSummary(user.currency.symbol);
        currencyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Info");
                View layout = getLayoutInflater().inflate(R.layout.set_currency_dialog, null);

                TextInputEditText currencyEditText = layout.findViewById(R.id.currency_edittext);
                currencyEditText.setText(user.currency.symbol);
                CheckBox showCurrencyOnLeft = layout.findViewById(R.id.show_currency_on_left_checkbox);
                showCurrencyOnLeft.setChecked(user.currency.left);
                CheckBox addSpaceCheckBox = layout.findViewById(R.id.add_space_currency_checkbox);
                addSpaceCheckBox.setChecked(user.currency.space);


                alert.setView(layout);
                alert.setCancelable(false);
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.currency.left = showCurrencyOnLeft.isChecked();
                        user.currency.space = addSpaceCheckBox.isChecked();
                        user.currency.symbol = currencyEditText.getText().toString();
                        saveUser(user);
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

                return false;
            }
        });

    }

    private void saveUser(User user) {
        UserProfileViewModelFactory.saveModel(getUid(), user);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}



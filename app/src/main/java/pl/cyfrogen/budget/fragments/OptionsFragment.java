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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.ArrayList;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.activities.AddWalletEntryActivity;
import pl.cyfrogen.budget.activities.SignInActivity;
import pl.cyfrogen.budget.firebase.FirebaseElement;
import pl.cyfrogen.budget.firebase.FirebaseObserver;
import pl.cyfrogen.budget.firebase.UserProfileViewModelFactory;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.models.CurrencyHelper;

public class OptionsFragment extends PreferenceFragmentCompat {
    public static final CharSequence TITLE = "Options";

    User user;

    ArrayList<Preference> preferences = new ArrayList<>();

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
                if (!element.hasNoError()) return;
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

        Preference currencyPreference = findPreference(getString(R.string.pref_key_currency));
        currencyPreference.setSummary(user.currency.symbol);
        currencyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Set currency");
                View layout = getLayoutInflater().inflate(R.layout.set_currency_dialog, null);

                TextInputEditText currencyEditText = layout.findViewById(R.id.currency_edittext);
                currencyEditText.setText(user.currency.symbol);
                CheckBox showCurrencyOnLeft = layout.findViewById(R.id.show_currency_on_left_checkbox);
                showCurrencyOnLeft.setChecked(user.currency.left);
                CheckBox addSpaceCheckBox = layout.findViewById(R.id.add_space_currency_checkbox);
                addSpaceCheckBox.setChecked(user.currency.space);

                alert.setView(layout);
                alert.setNegativeButton("Cancel", null);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.currency.left = showCurrencyOnLeft.isChecked();
                        user.currency.space = addSpaceCheckBox.isChecked();
                        user.currency.symbol = currencyEditText.getText().toString();
                        saveUser(user);
                    }
                });
                alert.create().show();
                return true;
            }
        });


        Preference firstWeekDayPreference = findPreference(getString(R.string.pref_key_first_week_day));
        firstWeekDayPreference.setSummary(getDayString(user.userSettings.dayOfWeekStart));
        firstWeekDayPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Set first week day:");
                View layout = getLayoutInflater().inflate(R.layout.set_first_day_of_week_dialog, null);
                RadioGroup radioGroup = layout.findViewById(R.id.radio_group);
                ((RadioButton) radioGroup.getChildAt(user.userSettings.dayOfWeekStart)).setChecked(true);
                alert.setView(layout);
                alert.setNegativeButton("Cancel", null);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = radioGroup.indexOfChild(layout.findViewById(radioGroup.getCheckedRadioButtonId()));
                        user.userSettings.dayOfWeekStart = index;
                        saveUser(user);
                    }
                });
                alert.create().show();
                return true;
            }
        });


        Preference firstMonthDayPreference = findPreference(getString(R.string.pref_key_first_month_day));
        firstMonthDayPreference.setSummary("" + (user.userSettings.dayOfMonthStart + 1));
        firstMonthDayPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Set first month day:");
                View layout = getLayoutInflater().inflate(R.layout.set_first_day_of_month_dialog, null);
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                editText.setText("" + (user.userSettings.dayOfMonthStart + 1));
                alert.setView(layout);
                alert.setNegativeButton("Cancel", null);
                alert.setPositiveButton("OK", null);
                AlertDialog alertDialog = alert.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                try {
                                    int number = Integer.parseInt(editText.getText().toString());
                                    if (number <= 0 || number >= 29) {
                                        editText.setError("Number must be in 1-29 range");
                                    } else {
                                        user.userSettings.dayOfMonthStart = number - 1;
                                        saveUser(user);
                                        alertDialog.dismiss();
                                    }
                                } catch (NumberFormatException e) {
                                    editText.setError("You must write number");
                                }

                            }
                        });
                    }
                });
                alertDialog.show();
                return true;
            }
        });


        {
            Preference counterTypePreference = findPreference(getString(R.string.pref_key_counter_type));
            View layout = getLayoutInflater().inflate(R.layout.set_counter_type_dialog, null);
            RadioGroup radioGroup = layout.findViewById(R.id.radio_group);
            counterTypePreference.setSummary(((RadioButton) radioGroup.getChildAt(user.userSettings.homeCounterType)).getText());
            counterTypePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Set counter type:");

                    ((RadioButton) radioGroup.getChildAt(user.userSettings.homeCounterType)).setChecked(true);
                    alert.setView(layout);
                    alert.setNegativeButton("Cancel", null);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int index = radioGroup.indexOfChild(layout.findViewById(radioGroup.getCheckedRadioButtonId()));
                            user.userSettings.homeCounterType = index;
                            saveUser(user);
                        }
                    });
                    alert.create().show();
                    return true;
                }
            });

        }

        {
            Preference counterTypePreference = findPreference(getString(R.string.pref_key_counter_period));
            View layout = getLayoutInflater().inflate(R.layout.set_counter_period_dialog, null);
            RadioGroup radioGroup = layout.findViewById(R.id.radio_group);
            counterTypePreference.setSummary(((RadioButton) radioGroup.getChildAt(user.userSettings.homeCounterPeriod)).getText());
            counterTypePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Set counter period:");
                    ((RadioButton) radioGroup.getChildAt(user.userSettings.homeCounterPeriod)).setChecked(true);
                    alert.setView(layout);
                    alert.setNegativeButton("Cancel", null);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int index = radioGroup.indexOfChild(layout.findViewById(radioGroup.getCheckedRadioButtonId()));
                            user.userSettings.homeCounterPeriod = index;
                            saveUser(user);
                        }
                    });
                    alert.create().show();
                    return true;
                }
            });

        }

        Preference limitPreference = findPreference(getString(R.string.pref_key_limit));
        limitPreference.setSummary(CurrencyHelper.formatCurrency(user.currency, user.userSettings.limit));
        limitPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Set limit:");
                View layout = getLayoutInflater().inflate(R.layout.set_limit_dialog, null);
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                AddWalletEntryActivity.setupAmountEditText(editText, user);
                alert.setView(layout);
                alert.setNegativeButton("Cancel", null);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.userSettings.limit = AddWalletEntryActivity.convertAmountStringToLong(editText.getText().toString());
                        saveUser(user);
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });


    }

    private String getDayString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
        }
        return "";
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



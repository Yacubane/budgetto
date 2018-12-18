package pl.cyfrogen.budget.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public Currency currency = new Currency("$", true, true);
    public UserSettings userSettings = new UserSettings();
    public Wallet wallet = new Wallet();

    public User() {

    }
}
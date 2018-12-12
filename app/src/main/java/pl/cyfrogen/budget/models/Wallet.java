package pl.cyfrogen.budget.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Wallet {

    public String name;
    public String currency;

    public Wallet() {

    }

    public Wallet(String name, String currency) {
        this.name = name;
        this.currency = currency;
    }

}
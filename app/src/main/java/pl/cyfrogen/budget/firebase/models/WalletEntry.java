package pl.cyfrogen.budget.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class WalletEntry {

    public String categoryName;
    public String name;
    public long timestamp;
    public long balanceDifference;
    public WalletEntry() {

    }

    public WalletEntry(String categoryName, String name, long timestamp, long balanceDifference) {
        this.categoryName = categoryName;
        this.name = name;
        this.timestamp = -timestamp;
        this.balanceDifference = balanceDifference;
    }

}
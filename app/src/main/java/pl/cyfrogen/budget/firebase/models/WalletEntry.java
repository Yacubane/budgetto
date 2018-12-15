package pl.cyfrogen.budget.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class WalletEntry {

    public String categoryID;
    public String name;
    public long timestamp;
    public long balanceDifference;
    public WalletEntry() {

    }

    public WalletEntry(String categoryID, String name, long timestamp, long balanceDifference) {
        this.categoryID = categoryID;
        this.name = name;
        this.timestamp = -timestamp;
        this.balanceDifference = balanceDifference;
    }

}
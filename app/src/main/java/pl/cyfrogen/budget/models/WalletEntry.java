package pl.cyfrogen.budget.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class WalletEntry {

    public String categoryID;
    public String typeID;
    public long timestamp;
    public long balanceDifference;
    public WalletEntry() {

    }

    public WalletEntry(String categoryID, String typeID, long timestamp, long balanceDifference) {
        this.categoryID = categoryID;
        this.typeID = typeID;
        this.timestamp = timestamp;
        this.balanceDifference = balanceDifference;

    }

}
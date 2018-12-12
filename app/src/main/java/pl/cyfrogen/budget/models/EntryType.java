package pl.cyfrogen.budget.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class EntryType {

    public String type;

    public EntryType() {

    }

    public EntryType(String category) {
        this.type = category;
    }

    @Override
    public String toString() {
        return type;
    }

}
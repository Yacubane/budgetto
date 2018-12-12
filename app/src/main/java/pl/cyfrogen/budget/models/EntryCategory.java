package pl.cyfrogen.budget.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class EntryCategory {

    public String category;

    public EntryCategory() {

    }

    public EntryCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category;
    }

}
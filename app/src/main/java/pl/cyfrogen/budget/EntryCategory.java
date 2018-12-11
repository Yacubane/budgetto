package pl.cyfrogen.budget;


import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
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
package pl.cyfrogen.budget.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public int dayOfMonthStart;
    public int dayOfWeekStart;
    public long limit;
    public int homeCounterType;
    public User() {

    }
}
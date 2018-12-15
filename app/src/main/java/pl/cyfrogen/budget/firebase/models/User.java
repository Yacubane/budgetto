package pl.cyfrogen.budget.firebase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    int dayOfMonthStart;
    int dayOfWeekStart;
    long limit;
    int homeCounterType;
    public User() {

    }
}
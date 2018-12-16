package pl.cyfrogen.budget.firebase.models;

import java.util.Calendar;

public class UserSettings {
    public static final int HOME_COUNTER_TYPE_COMPARE_INCOME_EXPENSE = 0;
    public static final int HOME_COUNTER_TYPE_SHOW_LIMIT= 1;

    public int dayOfMonthStart = 1;
    public int dayOfWeekStart = Calendar.MONDAY;
    public long limit;
    public int homeCounterType = UserSettings.HOME_COUNTER_TYPE_COMPARE_INCOME_EXPENSE;
    public UserSettings() {

    }

}

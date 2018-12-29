package pl.cyfrogen.budget.models;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.models.WalletEntryCategory;

public  class DefaultCategories {
    private static Category[] categories = new Category[]{
            new Category(":others", "Others", R.drawable.category_default, Color.parseColor("#455a64")),
            new Category(":clothing", "Clothing", R.drawable.category_clothing, Color.parseColor("#d32f2f")),
            new Category(":food", "Food", R.drawable.category_food, Color.parseColor("#c2185b")),
            new Category(":gas_station", "Fuel", R.drawable.category_gas_station, Color.parseColor("#7b1fa2")),
            new Category(":gaming", "Gaming", R.drawable.category_gaming, Color.parseColor("#512da8")),
            new Category(":gift", "Gift", R.drawable.category_gift, Color.parseColor("#303f9f")),
            new Category(":holidays", "Holidays", R.drawable.category_holidays, Color.parseColor("#1976d2")),
            new Category(":home", "Home", R.drawable.category_home, Color.parseColor("#0288d1")),
            new Category(":kids", "Kids", R.drawable.category_kids, Color.parseColor("#0097a7")),
            new Category(":pharmacy", "Pharmacy", R.drawable.category_pharmacy, Color.parseColor("#00796b")),
            new Category(":repair", "Repair", R.drawable.category_repair, Color.parseColor("#388e3c")),
            new Category(":shopping", "Shopping", R.drawable.category_shopping, Color.parseColor("#689f38")),
            new Category(":sport", "Sport", R.drawable.category_sport, Color.parseColor("#afb42b")),
            new Category(":transfer", "Transfer", R.drawable.category_transfer, Color.parseColor("#fbc02d")),
            new Category(":transport", "Transport", R.drawable.category_transport, Color.parseColor("#ffa000")),
            new Category(":work", "Work", R.drawable.category_briefcase, Color.parseColor("#f57c00")),


    };

    public static Category createDefaultCategoryModel(String visibleName) {
        return new Category("default", visibleName, R.drawable.category_default,
                Color.parseColor("#26a69a"));
    }


    public static Category[] getDefaultCategories() {
        return categories;
    }
}

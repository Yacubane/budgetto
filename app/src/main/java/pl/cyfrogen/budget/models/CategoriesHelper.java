package pl.cyfrogen.budget.models;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.firebase.models.User;
import pl.cyfrogen.budget.firebase.models.WalletEntryCategory;

public class CategoriesHelper {
    public static Category searchCategory(User user, String categoryName) {
        for(Category category : DefaultCategories.getDefaultCategories()) {
            if(category.getCategoryID().equals(categoryName)) return category;
        }
        for(Map.Entry<String, WalletEntryCategory> entry : user.customCategories.entrySet()) {
            if(entry.getKey().equals(categoryName)) {
                return new Category(categoryName, entry.getValue().visibleName, R.drawable.category_default, Color.parseColor(entry.getValue().htmlColorCode));
            }
        }
        return DefaultCategories.createDefaultCategoryModel("Others");
    }

    public static List<Category> getCategories(User user) {
        ArrayList<Category> categories = new ArrayList<>(Arrays.asList(DefaultCategories.getDefaultCategories()));
        for(Map.Entry<String, WalletEntryCategory> entry : user.customCategories.entrySet()) {
            String categoryName = entry.getKey();
            categories.add(new Category(categoryName, entry.getValue().visibleName, R.drawable.category_default, Color.parseColor(entry.getValue().htmlColorCode)));
        }
        return categories;
    }

    public static List<Category> getCustomCategories(User user) {
        ArrayList<Category> categories = new ArrayList<>();
        for(Map.Entry<String, WalletEntryCategory> entry : user.customCategories.entrySet()) {
            String categoryName = entry.getKey();
            categories.add(new Category(categoryName, entry.getValue().visibleName, R.drawable.category_default, Color.parseColor(entry.getValue().htmlColorCode)));
        }
        return categories;
    }
}

package pl.cyfrogen.budget;

import android.graphics.Color;

public  class DefaultCategoryModels {
    private static CategoryModel[] categoryModels = new CategoryModel[]{
            new CategoryModel(":clothing", "Clothing", R.drawable.category_clothing, Color.parseColor("#FF0000")),
            new CategoryModel(":food", "Food", R.drawable.category_food, Color.parseColor("#FF0000")),
            new CategoryModel(":gas_station", "Fuel", R.drawable.category_gas_station, Color.parseColor("#FF0000")),
            new CategoryModel(":gaming", "Gaming", R.drawable.category_gaming, Color.parseColor("#FF0000")),
            new CategoryModel(":gift", "Gift", R.drawable.category_gift, Color.parseColor("#FF0000")),
            new CategoryModel(":holidays", "Holidays", R.drawable.category_holidays, Color.parseColor("#FF0000")),
            new CategoryModel(":home", "Home", R.drawable.category_home, Color.parseColor("#FF0000")),
            new CategoryModel(":kids", "Kids", R.drawable.category_kids, Color.parseColor("#FF0000")),
            new CategoryModel(":pharmacy", "Pharmacy", R.drawable.category_pharmacy, Color.parseColor("#FF0000")),
            new CategoryModel(":repair", "Repair", R.drawable.category_repair, Color.parseColor("#FF0000")),
            new CategoryModel(":shopping", "Shopping", R.drawable.category_shopping, Color.parseColor("#FF0000")),
            new CategoryModel(":sport", "Sport", R.drawable.category_sport, Color.parseColor("#FF0000")),
            new CategoryModel(":transfer", "Transfer", R.drawable.category_transfer, Color.parseColor("#FF0000")),
            new CategoryModel(":transport", "Transport", R.drawable.category_transport, Color.parseColor("#FF0000")),
            new CategoryModel(":work", "Work", R.drawable.category_briefcase, Color.parseColor("#FF0000")),

    };

    public static CategoryModel createDefaultCategoryModel(String visibleName) {
        return new CategoryModel("default", visibleName, R.drawable.category_default,
                Color.parseColor("#00897b"));
    }

    public static CategoryModel searchCategory(String categoryName) {
        for(CategoryModel categoryModel : categoryModels) {
            if(categoryModel.getCategoryID().equals(categoryName)) return categoryModel;
        }
        return createDefaultCategoryModel(categoryName);
    }

    public static CategoryModel[] getCategoryModels() {
        return categoryModels;
    }
}

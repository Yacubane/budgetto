package pl.cyfrogen.budget;

import android.graphics.Color;

public  class DefaultCategoryModels {
    private static CategoryModel[] categoryModels = new CategoryModel[]{
            new CategoryModel(":others", "Others", R.drawable.category_default, Color.parseColor("#455a64")),
            new CategoryModel(":clothing", "Clothing", R.drawable.category_clothing, Color.parseColor("#d32f2f")),
            new CategoryModel(":food", "Food", R.drawable.category_food, Color.parseColor("#c2185b")),
            new CategoryModel(":gas_station", "Fuel", R.drawable.category_gas_station, Color.parseColor("#7b1fa2")),
            new CategoryModel(":gaming", "Gaming", R.drawable.category_gaming, Color.parseColor("#512da8")),
            new CategoryModel(":gift", "Gift", R.drawable.category_gift, Color.parseColor("#303f9f")),
            new CategoryModel(":holidays", "Holidays", R.drawable.category_holidays, Color.parseColor("#1976d2")),
            new CategoryModel(":home", "Home", R.drawable.category_home, Color.parseColor("#0288d1")),
            new CategoryModel(":kids", "Kids", R.drawable.category_kids, Color.parseColor("#0097a7")),
            new CategoryModel(":pharmacy", "Pharmacy", R.drawable.category_pharmacy, Color.parseColor("#00796b")),
            new CategoryModel(":repair", "Repair", R.drawable.category_repair, Color.parseColor("#388e3c")),
            new CategoryModel(":shopping", "Shopping", R.drawable.category_shopping, Color.parseColor("#689f38")),
            new CategoryModel(":sport", "Sport", R.drawable.category_sport, Color.parseColor("#afb42b")),
            new CategoryModel(":transfer", "Transfer", R.drawable.category_transfer, Color.parseColor("#fbc02d")),
            new CategoryModel(":transport", "Transport", R.drawable.category_transport, Color.parseColor("#ffa000")),
            new CategoryModel(":work", "Work", R.drawable.category_briefcase, Color.parseColor("#f57c00")),


    };

    public static CategoryModel createDefaultCategoryModel(String visibleName) {
        return new CategoryModel("default", visibleName, R.drawable.category_default,
                Color.parseColor("#26a69a"));
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

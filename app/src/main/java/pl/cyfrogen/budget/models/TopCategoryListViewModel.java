package pl.cyfrogen.budget.models;

public class TopCategoryListViewModel {
    private long money;
    private final CurrencyHelper currencyHelper;
    private final Category category;
    private String categoryName;

    public TopCategoryListViewModel(Category category, String categoryName, CurrencyHelper currencyHelper, long money) {
        this.category = category;
        this.categoryName = categoryName;
        this.currencyHelper = currencyHelper;
        this.money = money;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public CurrencyHelper getCurrencyHelper() {
        return currencyHelper;
    }

    public long getMoney() {
        return money;
    }

    public Category getCategory() {
        return category;
    }
}

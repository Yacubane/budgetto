package pl.cyfrogen.budget;

public class CategoryModelHome {
    private long money;
    private final Currency currency;
    private final CategoryModel categoryModel;
    private String categoryName;
    private long sum;

    public CategoryModelHome(CategoryModel categoryModel, String categoryName, Currency currency, long money) {
        this.categoryModel = categoryModel;
        this.categoryName = categoryName;
        this.currency = currency;
        this.money = money;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public long getMoney() {
        return money;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void addMoney(long balanceDifference) {
        money += balanceDifference;
    }
}

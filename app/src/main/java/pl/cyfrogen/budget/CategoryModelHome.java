package pl.cyfrogen.budget;

public class CategoryModelHome {
    private final long money;
    private final Currency currency;
    private String categoryName;
    private long sum;

    public CategoryModelHome(String categoryName, Currency currency, long money) {
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
}

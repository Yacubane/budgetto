package pl.cyfrogen.budget.models;

import pl.cyfrogen.budget.firebase.models.Currency;

public class CurrencyHelper {
    private final String name;
    private final boolean left;

    public CurrencyHelper(String name, boolean left) {
        this.left = left;
        this.name = name;
    }

    public String formatString(long money) {
        long absMoney = Math.abs(money);
        return (left ? name : "") + (money < 0 ? "-" : "") +
                (absMoney / 100) + "." + (absMoney % 100 < 10 ? "0" : "") +  (absMoney % 100)  +
                (left ? "" : name);
    }

    public static String formatCurrency(Currency currency, long money) {
        long absMoney = Math.abs(money);
        return (currency.left ? ((currency.space ? "" : " ") + currency.symbol): "") +
                (money < 0 ? "-" : "") +
                (absMoney / 100) + "." +
                (absMoney % 100 < 10 ? "0" : "") +
                (absMoney % 100)  +
                (currency.left ? "" : ((currency.space ? " " : "") + currency.symbol));
    }

    public String getStringAddition() {
        return name;
    }

    public boolean isLeftFormatted() {
        return left;
    }
}

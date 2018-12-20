package pl.cyfrogen.budget.util;

import pl.cyfrogen.budget.firebase.models.Currency;

public class CurrencyHelper {
    public static String formatCurrency(Currency currency, long money) {
        long absMoney = Math.abs(money);
        return (currency.left ? (currency.symbol + (currency.space ? " " : "")): "") +
                (money < 0 ? "-" : "") +
                (absMoney / 100) + "." +
                (absMoney % 100 < 10 ? "0" : "") +
                (absMoney % 100)  +
                (currency.left ? "" : ((currency.space ? " " : "") + currency.symbol));
    }

}

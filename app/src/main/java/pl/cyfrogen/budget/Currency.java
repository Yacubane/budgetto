package pl.cyfrogen.budget;

public class Currency {
    public static final Currency USD = new Currency("$", true);
    public static final Currency PLN = new Currency("zl", false);
    public static final Currency DEFAULT = PLN;

    private final String name;
    private final boolean left;

    public Currency(String name, boolean left) {
        this.left = left;
        this.name = name;
    }

    public String formatString(long money) {
        long absMoney = Math.abs(money);
        return (left ? name : "") + (money < 0 ? "-" : "") +
                (absMoney / 100) + "." + (absMoney % 100 < 10 ? "0" : "") +  (absMoney % 100)  +
                (left ? "" : name);
    }

    public String getStringAddition() {
        return name;
    }

    public boolean isLeftFormatted() {
        return left;
    }
}

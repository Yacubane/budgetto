package pl.cyfrogen.budget;

public class Currency {
    public static final Currency USD = new Currency(" $");
    private final String name;

    public Currency(String name) {
        this.name = name;
    }

    public String formatString(long money) {
        if (money % 100 < 10)
            return (money / 100) + "." + (money % 100) + "0" + name;
        else
            return (money / 100) + "." + (money % 100) + name;
    }
}

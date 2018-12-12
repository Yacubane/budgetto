package pl.cyfrogen.budget;

public class Currency {
    public static final Currency USD = new Currency("USD");
    private final String name;

    public Currency(String name) {
        this.name = name;
    }

    public String formatString(long money) {
        return (money / 100) + "," + (money % 100) + name;
    }
}

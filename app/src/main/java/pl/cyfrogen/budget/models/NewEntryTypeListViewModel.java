package pl.cyfrogen.budget.models;

public class NewEntryTypeListViewModel {
    public final String name;
    public final int color;
    public final int iconID;

    public NewEntryTypeListViewModel(String name, int color, int iconID) {
        this.name = name;
        this.color = color;
        this.iconID = iconID;
    }
}

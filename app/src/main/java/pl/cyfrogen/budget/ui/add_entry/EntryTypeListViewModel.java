package pl.cyfrogen.budget.ui.add_entry;

public class EntryTypeListViewModel {
    public final String name;
    public final int color;
    public final int iconID;

    public EntryTypeListViewModel(String name, int color, int iconID) {
        this.name = name;
        this.color = color;
        this.iconID = iconID;
    }
}

package pl.cyfrogen.budget.models;

import android.content.Context;

import java.util.Objects;

public class Category {
    private final String id;
    private String visibleName;
    private final int iconResourceID;
    private final int backgroundColor;
    private int visibleNameResourceID;

    public Category(String id, int visibleNameResourceID, int iconResourceID, int backgroundColor) {
        this.id = id;
        this.visibleNameResourceID = visibleNameResourceID;
        this.iconResourceID = iconResourceID;
        this.backgroundColor = backgroundColor;
    }

    public Category(String id, String visibleName, int iconResourceID, int backgroundColor) {
        this.id = id;
        this.visibleName = visibleName;
        this.iconResourceID = iconResourceID;
        this.backgroundColor = backgroundColor;
    }

    public String getCategoryID() {
        return id;
    }

    public String getCategoryVisibleName(Context context) {
        if (visibleName != null)
            return visibleName;
        return context.getResources().getString(visibleNameResourceID);
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public int getIconColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return getCategoryID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, visibleName, iconResourceID, backgroundColor, visibleNameResourceID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return iconResourceID == that.iconResourceID &&
                backgroundColor == that.backgroundColor &&
                visibleNameResourceID == that.visibleNameResourceID &&
                Objects.equals(id, that.id) &&
                Objects.equals(visibleName, that.visibleName);
    }


}

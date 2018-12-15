package pl.cyfrogen.budget;

import android.content.Context;
import android.graphics.Color;

import java.util.Objects;

public class CategoryModel {
    private final String id;
    private String visibleName;
    private final int iconResourceID;
    private final int backgroundColor;
    private int visibleNameResourceID;

    public CategoryModel(String id, int visibleNameResourceID, int iconResourceID, int backgroundColor) {
        this.id = id;
        this.visibleNameResourceID = visibleNameResourceID;
        this.iconResourceID = iconResourceID;
        this.backgroundColor = backgroundColor;
    }

    public CategoryModel(String id, String visibleName, int iconResourceID, int backgroundColor) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryModel that = (CategoryModel) o;
        return iconResourceID == that.iconResourceID &&
                backgroundColor == that.backgroundColor &&
                visibleNameResourceID == that.visibleNameResourceID &&
                Objects.equals(id, that.id) &&
                Objects.equals(visibleName, that.visibleName);
    }


}

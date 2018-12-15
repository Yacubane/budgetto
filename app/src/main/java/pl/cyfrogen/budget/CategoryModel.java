package pl.cyfrogen.budget;

import android.content.Context;
import android.graphics.Color;

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
}

package pl.cyfrogen.budget.firebase;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListDataSet<T> {
    public enum Operation {
        ITEM_CHANGED, ITEM_INSERTED, ITEM_REMOVED, ITEM_MOVED, ITEMS_CLEARED, NOTHING;
    }

    private ArrayList<String> ids = new ArrayList<>();
    List<T> list;
    private Operation lastOperation = Operation.NOTHING;
    private int index;
    private int index2;

    public ListDataSet() {
        list = new ArrayList<>();
    }


    public List<T> getList() {
        return list;
    }

    public void setItemChanged(int index) {
        this.index=index;
        lastOperation = Operation.ITEM_CHANGED;
    }

    public void setItemInserted(int insertedPosition) {
        this.index=insertedPosition;
        lastOperation = Operation.ITEM_INSERTED;
    }

    public void setItemMoved(int index, int newPosition) {
        this.index=index;
        this.index2=newPosition;
        lastOperation = Operation.ITEM_MOVED;
    }

    public void setItemRemoved(int index) {
        this.index=index;
        lastOperation = Operation.ITEM_REMOVED;
    }
    public void notifyRecycler (RecyclerView.Adapter recyclerViewAdapter) {
        switch (lastOperation) {
            case ITEM_CHANGED:
                recyclerViewAdapter.notifyItemChanged(index);
                break;
            case ITEM_INSERTED:
                recyclerViewAdapter.notifyItemInserted(index);
                break;
            case ITEM_REMOVED:
                recyclerViewAdapter.notifyItemRemoved(index);
                break;
            case ITEM_MOVED:
                recyclerViewAdapter.notifyItemMoved(index, index2);
                break;
            case ITEMS_CLEARED:
                recyclerViewAdapter.notifyDataSetChanged();
                break;
        }
    }

    public void clear() {
        list.clear();
        ids.clear();
        lastOperation = Operation.ITEMS_CLEARED;
    }

    public Operation getLastOperation() {
        return lastOperation;
    }

    public ArrayList<String> getIDList() {
        return ids;
    }

}

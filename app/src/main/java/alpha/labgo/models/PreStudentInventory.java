package alpha.labgo.models;

import java.util.ArrayList;

public class PreStudentInventory {
    private ArrayList<ArrayList<BorrowedItem>> mStudentItems;
    private ArrayList<String> mGtids;

    public PreStudentInventory(
            ArrayList<ArrayList<BorrowedItem>> studentItems,
            ArrayList<String> gtids) {
        this.mGtids = gtids;
        this.mStudentItems = studentItems;
    }

    public ArrayList<String> getGtids() {
        return mGtids;
    }

    public ArrayList<ArrayList<BorrowedItem>> getStudentItems() {
        return mStudentItems;
    }
}

package alpha.labgo.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentInventory extends ExpandableGroup<BorrowedItem>{

    private String mGtid;
    private int mQuantity;
    ArrayList<BorrowedItem> mBorrowedItems;

    public StudentInventory(String studentName, String gtid, ArrayList<BorrowedItem> borrowedItems) {
        super(studentName, borrowedItems);
        this.mGtid = gtid;
        this.mQuantity = borrowedItems.size();
        this.mBorrowedItems = borrowedItems;
    }

    public String getGtid() {
        return mGtid;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void addItem(BorrowedItem newItem) {
        mBorrowedItems.add(newItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StudentInventory)) {
            return false;
        }

        StudentInventory studentInventory = (StudentInventory) obj;
        return getGtid() == studentInventory.getGtid();
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getGtid());
    }
}

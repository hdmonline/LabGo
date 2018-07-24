package alpha.labgo.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentInventory implements Parent<BorrowedItem> {

    private String mName;
    private String mGtid;
    private int mQuantity;
    private List<BorrowedItem> mBorrowedItems;

    public StudentInventory(String studentName, String gtid, ArrayList<BorrowedItem> borrowedItems) {
        this.mName = studentName;
        this.mGtid = gtid;
        this.mQuantity = borrowedItems.size();
        this.mBorrowedItems = borrowedItems;
    }

    public String getGtid() {
        return mGtid;
    }

    public void setGtid(String gtid) {
        this.mGtid = gtid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void addItem(BorrowedItem newItem) {
        mBorrowedItems.add(newItem);
    }

    public void setItems(List<BorrowedItem> borrowedItems) {
        this.mBorrowedItems = borrowedItems;
    }

    public List<BorrowedItem> getItems() {
        return mBorrowedItems;
    }

    public BorrowedItem getItem(int position) {
        return mBorrowedItems.get(position);
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof StudentInventory)) {
//            return false;
//        }
//
//        StudentInventory studentInventory = (StudentInventory) obj;
//        return getGtid() == studentInventory.getGtid();
//    }
//
//    @Override
//    public int hashCode() {
//        return Integer.parseInt(getGtid());
//    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    @Override
    public List<BorrowedItem> getChildList() {
        return mBorrowedItems;
    }
}

package alpha.labgo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class describes the borrowed item
 * Implementing {@link Parcelable} to be used for {@link com.thoughtbot.expandablerecyclerview.models.ExpandableGroup}
 */
public class BorrowedItem implements Parcelable {

    private String mItemImage;
    private String mItemName;
    private String mItemDescription;
    private String mCheckOutTime;

    /**
     * Default constructor
     */
    public BorrowedItem() {

    }

    /**
     * Constructor
     *
     * @param image
     * @param name
     * @param description
     * @param time
     */
    public BorrowedItem(String image, String name, String description, String time) {
        this.mItemImage = image;
        this.mItemName = name;
        this.mItemDescription = description;
        this.mCheckOutTime = time;
    }

    protected BorrowedItem(Parcel in) {
        mItemImage = in.readString();
        mItemName = in.readString();
        mItemDescription = in.readString();
        mCheckOutTime = in.readString();
    }

    public static final Creator<BorrowedItem> CREATOR = new Creator<BorrowedItem>() {
        @Override
        public BorrowedItem createFromParcel(Parcel in) {
            return new BorrowedItem(in);
        }

        @Override
        public BorrowedItem[] newArray(int size) {
            return new BorrowedItem[size];
        }
    };

    public String getItemImage() {
        return mItemImage;
    }

    public String getCheckOutTime() {
        return mCheckOutTime;
    }

    public String getItemName() {
        return mItemName;
    }

    public String getItemDescription() {
        return mItemDescription;
    }

    public void setItemImage(String itemImage) {
        this.mItemImage = itemImage;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.mCheckOutTime = checkOutTime;
    }

    public void setItemDescription(String itemDescription) {
        this.mItemDescription = itemDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mItemName);
        parcel.writeString(mItemDescription);
        parcel.writeString(mCheckOutTime);
        parcel.writeString(mItemImage);
    }
}

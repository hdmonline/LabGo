package alpha.labgo.models;

public class BorrowedItem {

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
}
